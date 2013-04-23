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
    fname = '../apps/out.txt'
    data = read_utf_txt_file(fname)
    data_for_processing = json.loads(data[0])
    
    for node in data_for_processing:
        list_for_sort = []
        node_data = data_for_processing[node]
        print 'Node name', node
        summ_words = 0
        for it in node_data:
            list_for_sort.append((node_data[it], it))
            summ_words += node_data[it]
        # Сортируем список
        list_for_sort = sorted(
                list_for_sort, 
                key=lambda record: record[0],
                reverse=True)
        partina_summ = 0
        index = 0
        for ptr in list_for_sort:
            #print '  Count: ', ptr[0], 'Word: ', ptr[1]
            partina_summ += ptr[0]
            index += 1
   
            if index > 0.2*len(list_for_sort):
                break
            
            #if partina_summ > 0.8*summ_words:
            #    break
            
        print 'Уникальных слов в индексе: ', len(list_for_sort)
        print 'Отобрано Оценка High (20% числа уникальных слов): ', index
        print 'Отбошено Оценка Low (80% числа уникальных слов): ', len(list_for_sort)-index
        print 'Они составляют 80% И 20% от общего числа слов в частотном индексе: ', partina_summ
        print 'Сумма всех частот в индексе - число рассматрив. слов в тексте 100%:', summ_words
        print
        
        """def get_y_axis(data):
            y = []
            for it in data:
                y.append(it[0]) # Только частота
            return y
        y_axis = get_y_axis(data_for_processing[at])
        x = range(len(y_axis))
        print y_axis
        p, = plot(x, y_axis)#, label='Node name : '+at)
        """
    #legend( loc='upper left' )
        
    #ylabel('The number of mentions of the word')
    #xlabel('Inndex of word')
    #grid()
    #show()
    print 'Done'
