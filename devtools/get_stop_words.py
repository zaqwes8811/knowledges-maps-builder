# coding: utf-8
'''
Created on 11.05.2013

@author: кей
'''

from dals.local_host.local_host_io_wrapper import file2list
from dals.local_host.local_host_io_wrapper import get_utf8_template

if __name__=="__main__":
    sets = get_utf8_template()
    sets['name'] = '../info/preposition_ru.txt'
    readed = file2list(sets)
    print readed[0]
    print len(readed[0][0].split(" "))
    print len(set(readed[0][0].split(" ")))
    print 'Done'
