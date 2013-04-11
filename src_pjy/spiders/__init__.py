# coding: utf-8
# Sys
import os

# App
import crosscuttings.tools as tools
from spiders._utils import parser_target_for_spider

def base_spider(target_fname):
    target_generator = parser_target_for_spider(target_fname)
    
    tmp_dir_path = tools.get_app_cfg()['App']['Spider']['intermedia_storage']
    for at in target_generator:
        if at[0]:
            # Строем папку
            node_name = at[0]
            try:
                os.mkdir(tmp_dir_path+node_name)
            except WindowsError as e:
                pass
            
            # Можно заполнять контентом
            print at
        else:
            return at
    return '', [0,'']
        