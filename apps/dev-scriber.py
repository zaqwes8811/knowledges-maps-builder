# coding: utf-8
'''
Created on 27.04.2013

@author: кей
'''
# Sys
import os

# crosscuttings
import crosscuttings.tools as tools 

import dals.local_host.local_host_io_wrapper as dal

import app_utils as util

# units
from crawlers import get_target
from crawlers import kKeyIndexName

def printer(msg):
    print msg

if __name__=='__main__':
    def main():
        rpt = []
        # Запускаем крулер
        target_fname = 'targets/test_crawler_schedule.txt'
        spider_target_fname = 'targets/test_spider_extr_target.txt'
        target = get_target(target_fname, spider_target_fname)
        # TODO(zaqwes): может задание в json сохранять для передачи разметчику, а то
        #   при любых ошибках нужно заново запускать поиск
        
        # Запускаем разметчик
        index_name = target[kKeyIndexName]
        path = 'App/Scriber/app_folder'
        app_folder = tools.get_app_cfg_by_path(path)
        
        # Производные пути
        index_path = app_folder+'/'+index_name
        index_root = index_path+'/index'
        tmp_root = index_path+'/tmp'
        
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
            
        jobs_list, err =  dal.read_utf_file_to_list_lines(spider_target_fname)
        nodes = map(get_one_node, jobs_list)
        for node in nodes:
            path_to_node_tmp = tmp_root+'/'+node
            index_node = index_root+'/'+node
            if not os.path.exists(path_to_node_tmp):
                os.makedirs(path_to_node_tmp)
            if not os.path.exists(index_node):
                os.makedirs(index_node)
          
    # Runner()  
    rpt = main()
    if rpt:
        map(printer, rpt)
    
        
    print 'Done'
