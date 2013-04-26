# coding: utf-8
'''
Created on 26.04.2013

@author: Igor
'''

# DAL
import dals.local_host.io_wrapper as os_dal

# Exist API
import crawlers.dirs_walker as os_walker

if __name__=='__main__':
    target_fname = 'target/test_crawler_schedule.txt'
    
    def printer(msg):
        print msg
    
    print 'Done'
