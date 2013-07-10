# encoding: utf8


from numpy import arange
from pylab import plot
from pylab import show



COUNT_POINTS = 23;
fx = arange(COUNT_POINTS)

Fxi = 0
Fx = []
for i in fx:
    Fx.append(Fxi)
    Fxi += i
    
for j in range(4):
    Fx.append(Fxi)
    
plot(Fx, "-v")
show()

