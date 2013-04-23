# coding: utf-8
''' 

Происходит дублирование информации - потери в памяти, но это позволяет
  запускать задачи параллельно
'''

# Other
#from org.math.plot import *
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

import spiders_processors.docs_spider as docs_spider

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

def fake_crawler_zero():
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
    return readed_data

def fake_crawler_one():
    list_files = [
            '../statistic_data/srts/Stenf Algs part I/5 - 1 - Quicksort- Overview (12 min).srt',
            '../statistic_data/srts/Stenf Algs part I/5 - 3 - Correctness of Quicksort [Review - Optional] (11 min).srt',
            '../statistic_data/srts/Stenf Algs part I/5 - 3 - Correctness of Quicksort [Review - Optional] (11 min).srt', 
            '../statistic_data/srts/Stenf Algs part I/5 - 2 - Partitioning Around a Pivot (25 min).srt',
            '../statistic_data/srts/Stenf Algs part I/5 - 1 - Quicksort- Overview (12 min).srt']
        
    # Запускаем spider-processor
    readed_data = {}
    i = 0
    for at in list_files:
        node_name = at.split('/')[-1][:-3]+'_N'+str(i)
        readed_data[node_name] = [at]
        i += 1

    return readed_data
    
def get_scheme_actions_srt(readed_data):
    jobs = plan_to_jobs_convertor_simpler(readed_data)
    
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
    # Запускаем краулер
    print 'Run crawler'
    #result_crawler = docs_spider.get_docs()#fake_crawler_one()
    
    print 'Get task plan.'
    jobs = docs_spider.get_docs()#get_scheme_actions_srt(result_crawler)
    map(printer, jobs)

    print 'Begin Map stage. Wait please...'
    map_stage_results = map(mapper, jobs)
    
    # Suffle stage
    print 'Begin Suffle stage. Wait please...'
    suffle_stage_results = suffler(map_stage_results)
      
          
    # Reduce
    result_reduce = {}
    for at in suffle_stage_results:
        
        one_node = suffle_stage_results[at]
        
        # Проверка слияния
        result = base_reducer(one_node)
        
        node_index = result[0][0]
        axises = []
        for jat in node_index:
            axises.append((node_index[jat]['N'], jat))
            
        src_list = sorted(
                axises, 
                key=lambda record: record[0],
                reverse=True) 
        tmp = []
        for it in src_list:
            tmp.append({it[0]: it[1]})
        result_reduce[at] = tmp

    # Result MapReduce
    json_result = json.dumps(result_reduce)
    fname = 'indexes/first_index.json'
    write_result_file([json_result], fname)
    print json_result
    
    #map(printer, result_reduce.items())

if __name__=='__main__':
    print 'Begin'
    main()
    
    
  
    """
    double[] x = ...
    double[] y = ...
    
    # create your PlotPanel (you can use it as a JPanel)
    """
    #plot = Plot2DPanel()
    """
    # add a line plot to the PlotPanel
    plot.addLinePlot("my plot", x, y);
    
    # put the PlotPanel in a JFrame, as a JPanel
    JFrame frame = new JFrame("a plot panel");
    frame.setContentPane(plot);
    frame.setVisible(true);"""
  
    print 'Done'
    
    #print json.dumps(main)  # NO WAY
    
    

