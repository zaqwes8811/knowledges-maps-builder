# coding: utf-8
'''
Created on 26.04.2013

@author: Igor
'''
# DAL
import dals.local_host.local_host_io_wrapper as local_dal

# Exist API
import crawlers.dirs_walker as os_walker
from crawlers import check_crawler_target
from crawlers import get_target_object
from crawlers import fill_target_for_spider
from crawlers import kKeyTargetExts# = 'extention list'
from crawlers import kKeyIgnoredDir# = 'ignored dir'
from crawlers import kKeyRoot# = 'root'


if __name__=='__main__':
    target_fname = 'targets/test_crawler_schedule.txt'
    
    def printer(msg):
        print msg

    raw_target, err = local_dal.read_utf_file_to_list_lines(target_fname)
    target = get_target_object(raw_target)
    
    map(printer, target.items())

    rpt = check_crawler_target(target)
    print 
    print 'Rpt:'
    map(printer, rpt)
    
    print '\nResult target:'
    map(printer, target.items())
    
    # Можно передавать краулеру на посик файлов - DataIsSafe
    # поиск
    print 
    print 'Begin finding'
    roots = target[kKeyRoot]
    extension_list = target[kKeyTargetExts]
    result_list, err = os_walker.find_files_down_tree_roots(roots, extension_list)
    if err[0]:
        print err[1]
        
    # Разбираем не узлы
    target_for_spider, rpt = fill_target_for_spider(result_list)
    print 
    print 'Rpt:'
    map(printer, rpt)
    
    fname = 'targets/test_spider_extr_target.txt'
    local_dal.write_result_file(target_for_spider, fname)
    
    print 'Done'
