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
from nlp_components.tokenizers import roughly_split_to_sentences
from MapReduce.mappers import mapper
from MapReduce.mappers import mapper_real
from MapReduce.sufflers import suffler
from MapReduce.reduces import base_reducer 
from MapReduce.reduces import base_merge


# Преобразователи ресурса в текст
from std_to_text_convertors.srt_to_text import std_srt_to_text_line
from dals.os_io.io_wrapper import list2file
from dals.os_io.io_wrapper import get_utf8_template

def printer(item):
    print item
    
def plan_to_jobs_convertor(scheme):
    result = []
    for at in scheme:
        slice = scheme[at]
        for it in slice:
            it.append(at)
            result.append(it)
    return result

def get_scheme_actions():
    # TODO(zaqwes): Во что сереализуется указатель на функцию - Нельзя его сереализовать
    # Можно подставить имя
    
    """def one_node_action_fake():
        content_pkge = \
            [
                [ 
                std_srt_to_text_line, 
                roughly_split_to_sentences],  # Дробитель контекста
                [, 
                std_srt_to_text_line, 
                roughly_split_to_sentences]
             ]
        return content_pkge
    
    def one_node_action_fake2():
        content_pkge = \
            [
                [
                std_srt_to_text_line, 
                roughly_split_to_sentences],  # Дробитель контекста
                [
                std_srt_to_text_line, 
                roughly_split_to_sentences],
                [
                std_srt_to_text_line, 
                roughly_split_to_sentences]
             ]
        return content_pkge"""
    
    node_name1 = 'Stenf. courses I'
    node_name2 = 'Stenf. courses II'
    readed_data = {node_name1:[], node_name2:[]}
    
    'statistic_data/srts/Stenf Algs part I/5 - 1 - Quicksort- Overview (12 min).srt'
    'statistic_data/srts/Stenf Algs part I/5 - 3 - Correctness of Quicksort [Review - Optional] (11 min).srt'
    
    
    
    'statistic_data/srts/Stenf Algs part I/5 - 3 - Correctness of Quicksort [Review - Optional] (11 min).srt' 
    'statistic_data/srts/Stenf Algs part I/5 - 2 - Partitioning Around a Pivot (25 min).srt'
    'statistic_data/srts/Stenf Algs part I/5 - 1 - Quicksort- Overview (12 min).srt'
                    
    
    
    """readed_data = {
          node_name1: one_node_action_fake(),
          node_name2: one_node_action_fake2()}"""
    return readed_data



def main():
    print 'Get task plan.'
    scheme = get_scheme_actions()
    
    """
    print 'Split task to job.'
    jobs = plan_to_jobs_convertor(scheme)
    map(printer, jobs)
    
    mappers = [mapper, mapper_real]
    for mappr in mappers:
        print 'Begin Map stage. Wait please...'
        map_stage_results = map(mappr, jobs)
        #map(printer, map_stage_results)
        #print map_stage_results[1][1]
        #top_index = map_stage_results[1][1]
        #for at in top_index:
        #    print at, ' : ', top_index[at]['S'], ' : ', top_index[at]['N']
        
        # Suffle stage
        print 'Begin Suffle stage. Wait please...'
        suffle_stage_results = suffler(map_stage_results)
                
        # Reduce
        for at in suffle_stage_results:
            one_node = suffle_stage_results[at]
            #print one_node[1][0]
            
            # Проверка слияния
            result = base_reducer(one_node)
            
            node_index = result[0][0]
            axises = []
            for jat in node_index:
                axises.append((node_index[jat]['N'], jat))
                
            src_list = sorted(
                              axises, 
                              #key=lambda record: record[0],
                              reverse=True) 
            tmp = []
            for jat in src_list:
                print jat
                tmp.append(jat[0])
                
            print range(len(tmp))
            print tmp
            #plot(range(len(tmp)), tmp)
            
            
            
            break
    
            #sets = get_utf8_template()
            #sets['name'] = 'tmp.json'
            #sets['howOpen'] = 'w'
            #list2file(sets, [json.dumps(suffle_stage_results, sort_keys=True, indent=2)])
    #grid()
    #show()
    """

if __name__=='__main__':
    print 'Begin'
    main()
    print 'Done'
    
    #print json.dumps(main)  # NO WAY
    
    

