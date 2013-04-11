# coding: utf-8
'''
Created on 11.04.2013

@author: кей
'''

# Other

# App
from spiders import base_spider
from crosscuttings import tools

if __name__=='__main__':
    
    # Инициализируем паука
    configuration = tools.get_app_cfg()
    kSpiderTargetsPath = configuration['App']['Spider']['targets_folder']
    
    # Запускаем паука
    target_name = 'iron_man_aa_target.txt'
    result, err = base_spider(kSpiderTargetsPath+target_name)
    print (result, err)