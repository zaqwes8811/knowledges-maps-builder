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
from spiders_extractors.tika_wrapper import read_utf_txt_file
from spiders_extractors.tika_wrapper import write_result_file
from spiders_extractors.tika_wrapper import printer

def split_to_sentents(text_list, result_list):
    text = ' '.join(text_list)
    bi = BreakIterator.getSentenceInstance();
    bi.setText(text)
    index = 0
    while bi.next() != BreakIterator.DONE:
        sentence = text[index:bi.current()]
        #System.out.println("Sentence: " + sentence)
        result_list.append(sentence)
        index = bi.current();

def get_docs():
    # Получает задание
    path_to_targets = 'spider-targets/'
    target_file = path_to_targets+'_test_task.txt'
    list_targets = read_utf_txt_file(target_file)
    #map(printer, list_targets)
    
    # Каждый файл отдельно
    #
    index_file = 0
    result_ = []
    for txt_fname in list_targets:
        print 'Process file:', txt_fname
        file_content_in_list = read_utf_txt_file(txt_fname)
        meta = file_content_in_list[0]
        content = file_content_in_list[1:]
        
        node_name = txt_fname.split('/')[-1]+'_N'
        print node_name
        meta_info = json.loads(meta)
        meta_info['node_name'] = node_name
        meta = json.dumps(meta_info)
        print meta
        
        # Пишем заголовок
        result =[meta, '']
        
        # Токенизируем контент
        split_to_sentents(content, result)
        
        # Записываем результаты
        path_to_tmp_files = 'result_folder/'
        url = path_to_tmp_files+node_name+str(index_file)+'.txt'
        write_result_file(result, url)
        index_file += 1
        
        result_.append((node_name, url))
    return result_
