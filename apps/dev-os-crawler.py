# coding: utf-8
'''
Created on 26.04.2013

@author: Igor
'''

# DAL
import dals.local_host.local_host_io_wrapper as local_dal

# Exist API
import crawlers.dirs_walker as os_walker

# App
import app_utils as util

# crosscuttings
import crosscuttings.tools as tools 

if __name__=='__main__':
    target_fname = 'targets/test_crawler_schedule.txt'
    
    def printer(msg):
        print msg
        
    def get_target_object(raw_target):
        # TODO(zaqwes): очистить от комментов
        target = {}
        def process_one_line(line):
            ptr = line.find(':')
            key = util.remove_forward_and_back_spaces(line[0:ptr])
            target[key] = util.remove_forward_and_back_spaces(line[ptr+1:])
        
        map(process_one_line, raw_target)
        
        # Подчищаем
        # Расширения
        list_ext = target['extention list'].split(' ')
        target['extention list'] = util.remove_fandb_spaces_in_tuple(tuple(list_ext))
        
        # Точки входа
        
        return target
        
    raw_target, err = local_dal.read_utf_file_to_list_lines(target_fname)
    target = get_target_object(raw_target)
    
    map(printer, target.items())
    
    # Проверяем задание
    path_to_settings = "App/Spider extractor/auto_detected_extention"
    print tools.get_app_cfg_by_path(path_to_settings)
    
    print 'Done'
