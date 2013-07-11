# encoding: utf8


from numpy import arange
from pylab import plot
from pylab import show
import random
import math

import matplotlib.pyplot as plt
import numpy as np
from numpy import exp
from numpy import log
from numpy import abs

def get_near_uniform(code_book):
    value = random.random()*max(code_book)
    vector = 0
    for code in code_book:
        if value <= code:
            break
        vector += 1
    #print "Cluster", vector, "Sample", value 
    return vector

def develop():
    lam = 2.0
    COUNT_POINTS = 100;
    x = arange(COUNT_POINTS)*1.0
    x = x/max(x)*2
    dx = x[1]-x[0]
    fx = lam*exp(-lam*x)
   
    Fxi = 0
    code_book = []
    for i in (fx):
        Fxi += i*dx
        tmp = Fxi-dx
        code_book.append(tmp)

    #plot(x, fx)   
    #plot(x, code_book, "-v")
    #show()
       
    
    # Обратная. Как нагенерить ключей?
    experiment = arange(10000)*1.0
    for i in range(10000):
        experiment[i] = get_near_uniform(code_book)
    
    print experiment
    x = experiment
    hist, bins = np.histogram(x,bins = 150)
    width = 0.7*(bins[1]-bins[0])
    center = (bins[:-1]+bins[1:])/2
    plt.bar(center, hist, align = 'center', width = width)
    plt.show()

    
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
    code_book = [1, 2, 4]
    
    
    #get_near_uniform(code_book)
    develop()
    #for i in range(100):
    #    get_near_uniform(code_book)
    print "Done"


