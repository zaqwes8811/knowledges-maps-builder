# encoding: utf8


from numpy import arange
from pylab import plot
from pylab import show
import random
import math

import matplotlib.pyplot as plt
import numpy as np

COUNT_POINTS = 20;
fx = arange(COUNT_POINTS)*1.0
fx /= sum(fx)

Fxi = 0
Fx = []
for i in fx:
    Fxi += i
    Fx.append(Fxi)
    
    
"""plot(Fx, "-v")
plot(fx, "-v")
show() """   


invFx = {}
for value in range(len(Fx)):
    key = int(math.floor(Fx[value]*COUNT_POINTS*COUNT_POINTS))
    invFx[key] = value
       

# Обратная. Как нагенерить ключей?
experiment = []
for i in range(10000):
    key = int(math.floor(random.random()*COUNT_POINTS*COUNT_POINTS))
    if key in invFx:
        experiment.append(invFx[key])
#"""
x = experiment
hist, bins = np.histogram(x,bins = 50)
width = 0.7*(bins[1]-bins[0])
center = (bins[:-1]+bins[1:])/2
plt.bar(center, hist, align = 'center', width = width)
plt.show()
#"""
    


