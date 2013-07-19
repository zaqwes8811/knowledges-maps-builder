# encoding: utf8
# Кодовой книгой кажется будет сама инверсная функция.
# На первой стадии нужно перевести точку равномерного в неравн.
#   и только потом кластерзовать. Можно линейной интерполяцией части кривой (возможно
#   это скомпенсирует прочите вычислительные издержки).
#   по кодовой книге всегда будет известно, хотя... ключ тоже нужно найти
# Кривую можно аппроксимировать, а потом скэшировать.
# Обработка кодовой книги. Еще ключи даже при интерполяции должны быть целыми
#   т.к. у нас look-up таблица.
# 
# А не будет ли после нахождения кодового слова в исходном распределении эквивалентно
#   нахождению того что нам нужно? Нет, кажется. Все равно отсчет нужно перевести.
#   При таком подходе похоже опять выйдет равромерное распределение, но для точек.
#   Хотя точки равноудалены в реальности!
#
# Возможно пригодится rand(N) Она дает точные ключи.
# Что бы не интерполировать можно добавить точек в найденное распределение.
#   Тогда при интегрировании мы получим сразу интерполяцию. DSP-интерп. перед интегрированием.
#
# Пока делать только модель. Ее нужно хорошо тестировать.
#
# Хорошо бы все сделать в целых числах.
#
# Как вставлять отсчеты - точки станут не дробные по оси Ox. Хотя может эти и не проблема.
#
# O(n) - ?
#
# (...N1] (N1...N2] ...
import random
import math
import cProfile

from numpy import arange
from numpy import histogram
from pylab import plot
from pylab import show
from pylab import bar

import fake
 
def find_tuple(ranges, n, value):
    if n != 1:
        return ranges[0][0] < value and value <= ranges[n-1][1]  
    else:
        return ranges[0][0] < value and value <= ranges[0][1]
        
def splitter(ranges, n, value):
    # Останавливаем ветку
    finded = find_tuple(ranges, n, value)
    if not finded:
        return False, None 
    # Выход из рекурсии когда остался один объект и он искомый 
    if n == 1:
        return finded, ranges[0] 
    else:
        one_size = n/2  # Округляется в меньшую
        two_size = n - one_size
        tree_result = splitter(ranges[:one_size], one_size, value)
        if not tree_result[0]:
            tree_result = splitter(ranges[one_size:], two_size, value)
        return tree_result
            
def get_code_word(ranges, size_code_book=None, max_value=None):
    INTERVAL_POS = 1
    IDX_POS = 2
    value = random.random()*max_value
    result = splitter(ranges, size_code_book, value)
    return result[INTERVAL_POS][IDX_POS]


def make_Fx(fx):
    Fxi = 0
    code_book = []
    for i in (fx):
        Fxi += i
        tmp = Fxi
        code_book.append(tmp)
    return code_book

def make_ranges(code_book):
    """ (low, high, idx) """
    COUNT_POINTS = len(code_book)
    ranges = []
    axis = range(COUNT_POINTS-1)
    ranges.append((0, code_book[0], 0))
    for i in axis:
        ranges.append((code_book[i], code_book[i+1], i+1))
    ranges = tuple(ranges) 
    return ranges

def main():
    fx = fake.get_fake_fx()
    
    # Функция распределения
    Fx = make_Fx(fx)
    MAX_VALUE = max(Fx)

    # Интервалы кодовой книги
    code_book = make_ranges(Fx)
    COUNT_POINTS = len(code_book)
    
    # Сам эксперимент
    size_experiment = 10000
    experiment = arange(size_experiment)*1.0    
    for i in range(size_experiment):
        experiment[i] = get_code_word(code_book, COUNT_POINTS, MAX_VALUE)

    """
    x = experiment
    hist, bins = histogram(x, bins = COUNT_POINTS)
    width = 0.7*(bins[1]-bins[0])
    center = (bins[:-1]+bins[1:])/2
    bar(center, hist, align = 'center', width = width)
    show()
    #"""
 

   
def test():
    code_book = [1, 3, 4, 7, 9]
    ranges = make_ranges(code_book)
    for i in range(10):
        get_code_word(ranges, code_book, len(code_book), max(code_book)) 
            
if __name__=="__main__":
    #test()
    #main()
    cProfile.run("main()")
    print "Done"


