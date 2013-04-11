# coding: utf-8
'''
Created on 11.04.2013

@author: кей
'''

# Other

# App
from spiders import base_spider
from crosscuttings import tools

def printer(msg):
    print msg

if __name__=='__main__':
    
    # Инициализируем паука
    configuration = tools.get_app_cfg()
    kSpiderTargetsPath = configuration['App']['Spider']['targets_folder']
    
    # Запускаем паука
    target_name = 'iron_man_aa_target.txt'
    
    # TODO(zaqwes): базовая проверка целостности задания
    #   это нужно для того, чтобы не запускать все с нуля
    # target_check_rpt = ...
    
    # TODO(zaqwes): сделать отчет по преобразованию
    rpt = base_spider(kSpiderTargetsPath+target_name)
    map(printer, rpt)