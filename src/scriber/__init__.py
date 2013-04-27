# coding: utf-8
""" Размечает инфраструктуру для приложения, а так же проверяет структуру

Пока все хранится в файлах и папках

"""
# Sys
import os

# crosscuttings
import crosscuttings.tools as tools 

from crawlers import kKeyIndexName
from crawlers import get_path_tasks

import dals.local_host.local_host_io_wrapper as dal

import app_utils as util

def scribe_index(target, spider_target_fname):
    rpt = []
    # Запускаем разметчик
    index_name = target[kKeyIndexName]
    app_folder, index_path, index_root, tmp_root = tools.get_pathes_complect(index_name)
    
    
    
    print 'App folder root -', app_folder
    print 'Index root -', index_path
    
    if not os.path.exists(app_folder):
        os.makedirs(app_folder)
        
    # Директория существует
    if not os.path.exists(tmp_root):
        os.makedirs(tmp_root)
    else:
        # TODO(zaqwes): плохо что если переименовывать, то нужно заново
        #   запускать поиск
        rpt.append('Error : Index exist. In current version it no '+
                   'enabled. Remove it or rename in target.')
        rpt.append('  Name - '+tmp_root)
        return rpt  # Только в разработческой версии
        
    # Директория существует
    if not os.path.exists(index_root):
        os.makedirs(index_root)
    else:
        # TODO(zaqwes): плохо что если переименовывать, то нужно заново
        #   запускать поиск
        rpt.append('Error : Index exist. In current version it no '+
                   'enabled. Remove it or rename in target.')
        rpt.append('  Name - '+index_root)
        return rpt  # Только в разработческой версии
            
    # Проверяем папку индексов
    
    # Нужно ли делать разметку по узлам во временной папке, да, нужно
    #   если анализировать прочими средствами        
    # Размечаем структуру узлов
    def get_one_node(line):
        line = line.split('*')[0]
        line = line.replace('[','')
        node = util.remove_forward_and_back_spaces(line.replace(']',''))
        return node

    jobs_list, err =  get_path_tasks(spider_target_fname)
    if err[0]:
        rpt.append(err)
    #map(util.printer, jobs_list)
    nodes = map(get_one_node, jobs_list)
    for node in nodes:
        path_to_node_tmp = tmp_root+'/'+node
        index_node = index_root+'/'+node
        if not os.path.exists(path_to_node_tmp):
            os.makedirs(path_to_node_tmp)
        if not os.path.exists(index_node):
            os.makedirs(index_node)
    return rpt
