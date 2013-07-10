# encoding: utf8


from numpy import arange
from pylab import plot
from pylab import show
import random



COUNT_POINTS = 10;
fx = arange(COUNT_POINTS)*1.0
fx /= sum(fx)

Fxi = 0
Fx = []
for i in fx:
    Fx.append(Fxi)
    Fxi += i
    
plot(Fx, "-v")
plot(fx, "-v")
    
# Обратная
#print random.random()
    
show()

