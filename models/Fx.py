# encoding: utf8


from numpy import arange
from pylab import plot
from pylab import show
import random
import math

import cProfile

import matplotlib.pyplot as plt
import numpy as np
from numpy import exp
from numpy import log
from numpy import abs

class Range(object):
    def contains(self, c):
        pass
    
class ClosedOpen(Range):
    _a = None
    _b = None
    _id = None
    def __init__(self, a, b, id):
        self._a = a
        self._b = b
        self._id = id
    def get_id(self):
        return self._id
    
    def get_a(self):
        return self._a
    
    def get_b(self):
        return self._b
        
    def contains(self, c):
        return self._a < c and c <= self._b
    
    def __str__(self):
        return "("+str(self._a)+", "+str(self._b)+"]"
    def __repr__(self):
        return self.__str__()

def find(ranges, n, value):
    if n != 1:
        return ranges[0].get_a() < value and value <= ranges[n-1].get_b()  
    else:
        return ranges[0].contains(value)
        
def splitter(ranges, n, value):
    # Останавливаем ветку
    """
    #@SLOW - вызывается всегда!
    finded = False
    if n != 1:
        finded = ranges[0].get_a() < value and value <= ranges[n-1].get_b()  
    else:
        finded = ranges[0].contains(value)
    #@SLOW
    """
    finded = find(ranges, n, value)
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
            
def get_near_uniform_recursive(code_book, size_code_book, max_value, ranges=None):
    value = random.random()*max_value
    result = splitter(ranges, size_code_book, value) 
    return result[1]. get_id()

            
def get_near_uniform_linear(code_book, max_value, ranges=None, tmp=None):
    value = random.random()*max_value
    vector = 0
    for code in code_book:
        if value <= code:
            break
        vector += 1
    #print "Cluster", vector, "Sample", value 
    return vector

def develop():
    COUNT_POINTS = 100;
    fx = arange(COUNT_POINTS)
    fx = fx[::-1]
    
   
    Fxi = 0
    code_book = []
    for i in (fx):
        Fxi += i
        tmp = Fxi
        code_book.append(tmp)

    #print fx
    #code_book = code_book/sum(code_book)
    """
    plot(fx, "-o")   
    plot(code_book, "-v")
    show()"""
       
    #"""
    # Обратная. Как нагенерить ключей?
    size_experiment = 100000
    experiment = arange(size_experiment)*1.0
    max_value = max(code_book)
    ranges = []
    ranges.append(ClosedOpen(0, code_book[0], 0))
    axis = range(COUNT_POINTS-1)
    for i in axis:
        ranges.append(ClosedOpen(code_book[i], code_book[i+1], i+1))
    ranges = tuple(ranges)    
    for i in range(size_experiment):
        experiment[i] = get_near_uniform_linear(code_book, COUNT_POINTS, max_value, ranges)
    """
    x = experiment
    hist, bins = np.histogram(x, bins = COUNT_POINTS)
    width = 0.7*(bins[1]-bins[0])
    center = (bins[:-1]+bins[1:])/2
    plt.bar(center, hist, align = 'center', width = width)
    plt.show()
    #"""
    
if __name__=="__main__":
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
  
    #code_book = [1, 3, 4, 7, 9]
    #for i in range(10000):
    #    get_near_uniform(code_book)
    #develop()
    cProfile.run("develop()")
    print "Done"


