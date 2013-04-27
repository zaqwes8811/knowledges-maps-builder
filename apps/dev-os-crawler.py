# coding: utf-8
'''
Created on 26.04.2013

@author: Igor
'''
# Sys
import os#.path.exists

# DAL
import dals.local_host.local_host_io_wrapper as local_dal

# Exist API
import crawlers.dirs_walker as os_walker

# crosscuttings
import crosscuttings.tools as tools 

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
    
    print 'Done'
