# coding: utf-8
'''
Created on 22.04.2013

@author: кей
'''
import json
import dals.os_io.io_wrapper as dal

from pylab import *

def write_result_file(result_list, fname):
    sets = dal.get_utf8_template()
    sets['howOpen'] = 'w'
    sets['name'] = fname
    dal.list2file(sets, result_list)
    
def read_utf_txt_file(fname):
    sets = dal.get_utf8_template()
    sets['name'] = fname
    return dal.file2list(sets) 

if __name__=='__main__':
    fname = '../apps/indexes/first_index.json'
    data = read_utf_txt_file(fname)
    data_for_processing = json.loads(data[0])
    for at in data_for_processing:
        def get_y_axis(data):
            y = []
            for it in data:
                y.append(it[0]) # Только частота
            return y
        y_axis = get_y_axis(data_for_processing[at])
        x = range(len(y_axis))
        print y_axis
        p, = plot(x, y_axis)#, label='Node name : '+at)
        
    #legend( loc='upper left' )
        
    ylabel('The number of mentions of the word')
    xlabel('Inndex of word')
    grid()
    show()
    print 'Done'
