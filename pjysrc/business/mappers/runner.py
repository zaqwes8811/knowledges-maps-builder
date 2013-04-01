# coding: utf-8

# Other
#from pylab import plot
#from pylab import show
#from pylab import grid
import json

# App
#import business.originator_frequency_index.orginator as orginator
#from business.to_text import get_list_content_items_from_str 
from business.originators_text_data.srt_to_text import srt_to_text_line

def get_scheme_actions():
    # TODO(zaqwes): Во что сереализуется указатель на функцию - Нельзя его сереализовать
    # Можно подставить имя
    
    def one_node_action_fake():
        files = [
                 ['../../../statistic_data/srts/Iron Man AA/Iron Man02x26.srt', 
                    srt_to_text_line, None],
                 ['../../../statistic_data/srts/Iron Man AA/Iron1and8.srt', 
                    srt_to_text_line, None]]
        return files
    
    node_name1 = 'Iron Man AA1'
    node_name2 = 'Iron Man AA2'
    readed_data = {
          node_name1: one_node_action_fake(),
          node_name2: one_node_action_fake()}
    return readed_data

def mapper(url):
    key = url[0]
    result = {key: []}
    def print_pathes(item):
        path = item[0]
        
        # Получем текст
        text = item[1](path)
        
        # теперь можно раздробить на единицы контента
        #content_items = []
        #if item[2]:
            #content_items = item[2](text)
        #else:
            #content_items = [text]

        # Теперь можно составлять индекс
        result[key].append([text, item[2]])
        
        
    map(print_pathes, url[1])
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
    
    def printer(value):
        for at in value:
            for it in value[at]:
                print it    
    map(printer, map_result)
    
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
    
    

