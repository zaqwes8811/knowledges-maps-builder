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


# Java
import java.text.BreakIterator as BreakIterator
import java.util.Locale as Locale
import org.apache.tika.language.ProfilingWriter as ProfilingWriter


# Преобразователи ресурса в текст
from std_to_text_convertors.srt_to_text import std_srt_to_text_line
from dals.os_io.io_wrapper import list2file
from dals.os_io.io_wrapper import get_utf8_template
import dals.os_io.io_wrapper as dal

# No DRY!!!
def split_to_sentents(text, result_list):
    """ 
    
    Danger:
        Почему-то между буквами добавляются пробелы
    
    """
    bi = BreakIterator.getSentenceInstance();
    bi.setText(text)
    index = 0
    writer = ProfilingWriter()  # для определения языка
    while bi.next() != BreakIterator.DONE:
        sentence = text[index:bi.current()]
        writer.append(sentence)
        result_list.append(sentence)
        index = bi.current()
        
    # Статистику накопили и теперь определяем язык
    identifier = writer.getLanguage();
    lang = identifier.getLanguage()
    return lang
        
def write_result_file(result_list, fname):
    sets = dal.get_utf8_template()
    sets['howOpen'] = 'w'
    sets['name'] = fname
    dal.list2file(sets, result_list)
    
def read_utf_txt_file(fname):
    sets = dal.get_utf8_template()
    sets['name'] = fname
    return dal.file2list(sets) 


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

def plan_to_jobs_convertor_simpler(scheme):
    result = []
    j = 0
    for at in scheme:
        slice = scheme[at]
        for it in slice:
            j += 1
            result.append((at, it, j))
    return result

def get_scheme_actions():
    # TODO(zaqwes): Во что сереализуется указатель на функцию - Нельзя его сереализовать
    # Можно подставить имя
    """ 
    [node, url, std_srt_to_text_line, roughly_split_to_sentences]
    """
        
    # Запускаем spider-processor
    node_name1 = 'Stenf. courses I'
    node_name2 = 'Stenf. courses II'
    readed_data = {node_name1:[], node_name2:[]}
    
    # Как бы результат работы spider-extractor and crawler
    node1_urls = [
            '../statistic_data/srts/Stenf Algs part I/5 - 1 - Quicksort- Overview (12 min).srt',
            '../statistic_data/srts/Stenf Algs part I/5 - 3 - Correctness of Quicksort [Review - Optional] (11 min).srt']
    
    
    node2_urls = [
            '../statistic_data/srts/Stenf Algs part I/5 - 3 - Correctness of Quicksort [Review - Optional] (11 min).srt', 
            '../statistic_data/srts/Stenf Algs part I/5 - 2 - Partitioning Around a Pivot (25 min).srt',
            '../statistic_data/srts/Stenf Algs part I/5 - 1 - Quicksort- Overview (12 min).srt']
    
    readed_data[node_name1] = node1_urls
    readed_data[node_name2] = node2_urls
    
    jobs = plan_to_jobs_convertor_simpler(readed_data)
   # map(printer, jobs)

    # Запускаем spider-processor
    
    def spider_str_processor(job):
        metadata = {'node_name':job[0]}
        node_name = job[0]
        url = job[1]
        number = job[2]
        metadata['url'] = url
        
        result = ['meta', '']
        # Очищаем файлы
        purged_content_file = std_srt_to_text_line(url)
        
        # делем не предложения и определяем язык
        lang = split_to_sentents(purged_content_file, result)
        metadata['lang'] = lang
        
        result[0] = json.dumps(metadata)
        path_to_file = 'tmp/'+node_name+'_N'+str(number)+'.txt'
        write_result_file(result, path_to_file)
        return (node_name, path_to_file)
        
    initial_jobs = map(spider_str_processor, jobs)
    return initial_jobs             

def main():
    print 'Get task plan.'
    jobs = get_scheme_actions()
    map(printer, jobs)

    print 'Begin Map stage. Wait please...'
    map_stage_results = map(mapper, jobs)
    for at in map_stage_results:
        print at[-1]
    #map(printer, map_stage_results)
    #print map_stage_results[1][1]
    top_index = map_stage_results[1][1]
    for at in top_index:
        #print at, ' : ', top_index[at]['S'], ' : ', top_index[at]['N']
        pass
    
    """
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
            
            
            
            break"""
    
            #sets = get_utf8_template()
            #sets['name'] = 'tmp.json'
            #sets['howOpen'] = 'w'
            #list2file(sets, [json.dumps(suffle_stage_results, sort_keys=True, indent=2)])
    #grid()
    #show()
    #"""

if __name__=='__main__':
    print 'Begin'
    main()
    print 'Done'
    
    #print json.dumps(main)  # NO WAY
    
    

