# coding: utf-8
'''
Created on 22.04.2013

@author: кей
'''
import json

# Java
import java.text.BreakIterator as BreakIterator
import java.util.Locale as Locale
import java.lang.System as System

# App
import dals.local_host.local_host_io_wrapper as dal
from nlp_components import split_to_sentents
from app_utils import printer

# units
from crawlers import get_node_name
from crawlers import get_url

# ToText convertors
from spiders_extractors.tika_wrapper import TextExtractorFromOdtDocPdf

def main():
    rpt = []
    # Задание получено в предыдущий сериях
    spider_target_fname = 'targets/spider_extractor_target'
    
    # Задание загружаем из файла
    json_target, err = dal.read_utf_file_to_list_lines(spider_target_fname+'.json')
    if not json_target:
        print 'Failure occure: ', err[1]
        var = raw_input("Press any key.")
        print 'Quit'
        exit()

    target = json.loads(' '.join(json_target))
    
    # Рельные задание с именами файлов и узлов
    nodes_and_urls, err = dal.read_utf_file_to_list_lines(spider_target_fname+'.txt')
    def get_node_and_url(line):
        node = get_node_name(line)
        url = get_url(line)
        return (node, url)
        
    nodes_and_urls_pkt = map(get_node_and_url, nodes_and_urls)
    
    # Можно переводить в текст
    

    
if __name__=='__main__':
    main()
    print 'Done'
