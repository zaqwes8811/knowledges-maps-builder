# coding: utf-8

# Other
#from pylab import plot
#from pylab import show
#from pylab import grid

# App
#import business.originator_frequency_index.orginator as orginator
#from business.to_text import get_list_content_items_from_str 
from business.originators_text_data.srt_to_text import srt_to_text_line

def get_addrs():
    files = [
             ['../../../statistic_data/srts/Iron Man AA/Iron Man02x26.srt', 
                srt_to_text_line],
            ['../../../statistic_data/srts/Iron Man AA/Iron1and8.srt', srt_to_text_line]]
    return files

def mapper(url):
    key = url[0]
    def print_pathes(item):
        path = item[0]
        
        # Получем текст
        text = item[1](path)

        
    map(print_pathes, url[1])

def main():
    # Просматриваем индекс
   
    # Заготовка путей
    node_name1 = 'Iron Man AA1'
    node_name2 = 'Iron Man AA2'
    
    readed_data = {
          node_name1: get_addrs(),
          node_name2: get_addrs()}

    
    # Получить индекс 
    print 'Process files. Wait please...'
    print map(mapper, readed_data.items())
    
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
    
    

