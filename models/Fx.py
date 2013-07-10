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

def develop():
    lam = 2.0
    COUNT_POINTS = 100;
    
    x = arange(COUNT_POINTS)*1.0
    x = x/max(x)*5
    dx = x[1]-x[0]
    y = arange(COUNT_POINTS)*1.0
    y /= max(y)
    
    #fx = arange(COUNT_POINTS)*1.0
    fx = lam*exp(-lam*x)
    #fx = 1*exp(-1.0*x)
    
    #fx /= sum(fx)
    
    Fxi = 0
    
    Fxe = 1-exp(-lam*x)
    Fx = []
    for i in (fx):
        print i
        Fxi += i*dx
        tmp = Fxi-dx
        Fx.append(tmp)
        
    invFx = {}
    for value in range(len(Fx)):
        key = int(Fx[value]*100.0)
        invFx[key] = value
        
    print invFx 
    print log(np.e) 
    plot(x, fx)   
    plot(x, Fx, "-v")
    #plot(x, Fxe, "-v")
    #plot(y, -1/lam*log(1-y), "-rv")
    show()
       
    """
    # Обратная. Как нагенерить ключей?
    experiment = []
    for i in range(10000):
        experiment.append(-log(random.random()))
    
    x = experiment
    hist, bins = np.histogram(x,bins = 50)
    width = 0.7*(bins[1]-bins[0])
    center = (bins[:-1]+bins[1:])/2
    plt.bar(center, hist, align = 'center', width = width)
    plt.show()
    """
    
if __name__=="__main__":
    # Кодовой книгой кажется будет сама инверсная функция.
    # Обработка кодовой книги.
    #
    # O(n) - ?
    code_book = [1, 2, 4]
    def get_near_uniform(code_book):
        value = random.random()*max(code_book)
        print "Sample", value
        deltas = []
        min_word= 0
        min_delta= 0
        for code in code_book:
            if code <= min_word:
                min_word = code
            deltas.append(abs(code-value))
        print min(deltas)
    
    get_near_uniform(code_book)
    
    print "Done"


