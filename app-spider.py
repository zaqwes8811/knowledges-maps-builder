# coding: utf-8
'''
Created on 11.04.2013

@author: кей
'''

# Other

# App
from spiders import base_spider
from spiders import parser_target_for_spider
from spiders import check_availabel_resourses
from crosscuttings import tools

def printer(msg):
    print msg
    
def run_other():
    pass

def main():
    
    # Инициализируем паука
    configuration = tools.get_app_cfg()
    kSpiderTargetsPath = configuration['App']['Spider']['targets_folder']
    
    # Запускаем паука
    # Проверяем файл целей
    target_name = kSpiderTargetsPath+'iron_man_aa_target.txt'
    parse_file_rpt = []
    for at in parser_target_for_spider(target_name):
        result, err_code, rpt = at
        if rpt:
            parse_file_rpt.append(rpt)
            print at[0]
    if parse_file_rpt:
        # Есть замечания
        print 'Rpt:'
        map(printer, parse_file_rpt)
        
        
        # Несколько раз спрашиваем
        print "Исправльте ошибки и попробуйте еще раз."
        cmd = str(raw_input('Продолжить?[y/n]'))
        if cmd == 'n':
            return
        else:
            print 'Комманда не опознана'
            return
    
    # TODO(zaqwes): базовая проверка целостности задания
    #   это нужно для того, чтобы не запускать все с нуля
    #
    # - доступны ли url
    # - все ли преобразователи найдены
    # - ...
    # target_check_rpt = ...
    all_right, rpt = check_availabel_resourses(target_name)
    if not all_right:
        print 'Rpt:'
        map(printer, rpt)
        
        print "Исправльте ошибки и попробуйте еще раз."
        cmd = str(raw_input('Продолжить?[y/n]'))
        if cmd == 'n':
            return
        else:
            print 'Комманда не опознана'
            return
            
    
    # TODO(zaqwes): сделать отчет по преобразованию
    #rpt = base_spider(target_name)
    #map(printer, rpt)
    
    
if __name__=='__main__':
    main()
    print 'Done'