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
from business.nlp_components.tokenizers import roughly_split_to_sentences
from business.nlp_components.word_splitter import processListContentSentences

# Преобразователи ресурса в текст
from business.originators_text_data.srt_to_text import srt_to_text_line

def printer(item):
    print item

def get_scheme_actions():
    # TODO(zaqwes): Во что сереализуется указатель на функцию - Нельзя его сереализовать
    # Можно подставить имя
    
    def one_node_action_fake():
        content_pkge = \
            [
                ['../../../statistic_data/srts/Iron Man AA/Iron Man02x26.srt', 
                srt_to_text_line, 
                roughly_split_to_sentences],  # Дробитель контекста
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

def mapper(full_job):
    """ (node_name, [[], [], []])"""
    node_name, list_jobs = full_job
    result = []
    def process_url(one_job):
        url = one_job[0]
        text_extractor = one_job[1]
        tokenizer = one_job[2]
        
        # Получем текст
        text = text_extractor(url)
        
        # Токенизация
        need_add_content_to_index = False
        lits_content_items = []
        if tokenizer:
            lits_content_items = tokenizer(text)
            need_add_content_to_index = True
        else:
            lits_content_items = [text]
        
        # Теперь можно составлять индекс
        index = processListContentSentences(lits_content_items)
        map(printer, index.items())
        
        """parallel_pkg = (
                node_name,
                text[:20], # текс для дальнейшей обработки
                ,  # просто перепаковка для транзита
                url  # Для обратного индекса
                )  # Имя будущего узла для Suffle-части
        """
        parallel_pkg = (node_name)
        result.append(parallel_pkg)
     
    # Делаем "работы"   
    map(process_url, list_jobs)
    return result

def main():
    
    # План действий
    readed_data = get_scheme_actions()
    
    # Map stage
    print 'Process files. Wait please...'
    job_items = map(mapper, readed_data.items())
    map(printer, job_items)
    print 'Extract text done.'
    print
    print 'Plotting results...'
    
    # Suffle stage
    
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
    
    

