# coding: utf-8
# Sys
import os

# Other
from dals.os_io.io_wrapper import list2file
from dals.os_io.io_wrapper import get_utf8_template

# App
import crosscuttings.tools as tools
from spiders._utils import parser_target_for_spider

# Convertors but how custome 
from spiders.std_to_text_convertors.srt_to_text import srt_to_text_line
# std_to_text_map =
# custom_to_text_map =

def _do_tmp_node_folder(node_name, tmp_dir_path):
    try:
        os.mkdir(tmp_dir_path+node_name)
    except WindowsError as e:
        pass
    return tmp_dir_path+node_name+'/'
    
def _save_temp_file(fname, text_content):
    sets = get_utf8_template()
    sets['name'] = fname 
    sets['howOpen'] = 'w'
    list2file(sets, text_content)
    
def text_extracte(url):
    result = ['url: '+url]
    result.append('')
    
    # Сам контент
    # TODO(zaqwes): url for GET может быть разным
    # TODO(zaqwes): Костыль - подходит только для файлов файловой системы
    #   причем пока только текстовых
    extention = url.split('.')[-1]  
    
    text_content = ''
    if extention == 'srt':
        text_content = srt_to_text_line(url)
    else:
        print 'Error: No implemented. Recognize only *.srt files. It *.'+extention
    
    result.append(text_content)
    return result

def base_spider(target_fname):
    """ 
    
    Danger:
        Узел записывается поверх, а не добавляется, временные файлы затираются
        Хорошо бы вообще очистить временную папку. Пусть целевой файл паука
        создает все заново.
        
        Пока все
    """
    rpt = []
    target_generator = parser_target_for_spider(target_fname)

    for at in target_generator:
        if at[0]:
            tmp_dir_path = tools.get_app_cfg()['App']['Spider']['intermedia_storage']
            node_name, url, file_idx = at
            
            # Строем папку
            path_to_node = _do_tmp_node_folder(node_name, tmp_dir_path)
            
            # Можно заполнять контентом
            text_content = text_extracte(url)
            
            # Пишем во временный файл
            tmp_fname = path_to_node+'/tmp'+str(file_idx)+'.txt'
            _save_temp_file(tmp_fname, text_content)
            
            rpt.append('ok')

        else:
            rpt.append('failure')
    return rpt
        