# coding: utf-8
''' 

Происходит дублирование информации - потери в памяти, но это позволяет
  запускать задачи параллельно
'''

# Other
#from pylab import plot
#from pylab import show
#from pylab import grid
import json

# App
#import business.originator_frequency_index.orginator as orginator
#from business.to_text import get_list_content_items_from_str 

# Преобразователи ресурса в текст
from business.originators_text_data.srt_to_text import srt_to_text_line

def get_scheme_actions():
    # TODO(zaqwes): Во что сереализуется указатель на функцию - Нельзя его сереализовать
    # Можно подставить имя
    
    def one_node_action_fake():
        content_pkge = \
            [
                ['../../../statistic_data/srts/Iron Man AA/Iron Man02x26.srt', 
                srt_to_text_line, 
                None],  # Дробитель контекста
                ['../../../statistic_data/srts/Iron Man AA/Iron1and8.srt', 
                srt_to_text_line, 
                None]
             ]
        return content_pkge
    
    node_name1 = 'Iron Man AA1'
    node_name2 = 'Iron Man AA2'
    readed_data = {
          node_name1: one_node_action_fake(),
          node_name2: one_node_action_fake()}
    return readed_data

def mapper(url):
    """ (node_name, [[], [], []])"""
    node_name = url[0]
    result = []
    list_jobs = url[1]
    def process_url(one_job):
        url = one_job[0]
        
        # Получем текст
        text_extractor = one_job[1]
        text = text_extractor(url)
        
        # Теперь можно составлять индекс
        tokenizer = one_job[2]
        parallel_pkg = [
                text, # текс для дальнейшей обработки
                tokenizer,  # просто перепаковка для транзита
                node_name,  # Имя будущего узла для Suffle-части
                url]  # Для обратного индекса
        result.append(parallel_pkg)
     
    # Делаем "работы"   
    map(process_url, list_jobs)
    return result

def main():
    # План действий
    readed_data = get_scheme_actions()

    
    # Получить индекс 
    print 'Process files. Wait please...'
    map_result = map(mapper, readed_data.items())
    print 'Extract text done.'
    print
    print 'Plotting results...'
    
    #def printer(value):
    #    for at in value:
    #        for it in value[at]:
    #            print it    
    #map(printer, map_result)
    
    # Выводим
    #index.print_branch(content_item_name)
    '''freq = []
    sorted_findex = index.get_sorted_forward_idx()
    for at in sorted_findex:
        print at
        freq.append(at[1])
    
    x = range(len(freq))'''
    #plot(x, freq)
    #grid()
    #show()
    
    # Сохраняем в индексе  
    #index.save_branch()
    #import sys
    #print sys.argv[0]

if __name__=='__main__':
    print 'Begin'
    main()
    print 'Done'
    
    #print json.dumps(main)  # NO WAY
    
    

