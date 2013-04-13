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

def main():
    
    # Инициализируем паука
    configuration = tools.get_app_cfg()
    kSpiderTargetsPath = configuration['App']['Spider']['targets_folder']
    
    # Запускаем паука
    # Проверяем файл целей
    target_name = kSpiderTargetsPath+'iron_man_aa_target.txt'
    while True:
        parse_file_rpt = []
        for at in parser_target_for_spider(target_name):
            result, err_code, rpt = at
            if rpt:
                parse_file_rpt.append(rpt)
                print at
        if not parse_file_rpt:
            break
        
        # Есть замечания
        print 'Rpt:'
        map(printer, parse_file_rpt)
        
        cmd = str(raw_input('Файл отред. можно продолжать/Выход [y/q]'))
        
        # Несколько раз спрашиваем
        if cmd == 'y':
            continue
        elif cmd == 'q':
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
            
    
    # TODO(zaqwes): сделать отчет по преобразованию
    #rpt = base_spider(target_name)
    #map(printer, rpt)
    
    
if __name__=='__main__':
    main()
    print 'Done'