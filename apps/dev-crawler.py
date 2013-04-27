# coding: utf-8
'''
Created on 26.04.2013

@author: Igor
'''

from crawlers import get_target

def printer(msg):
    print msg

if __name__=='__main__':
    target_fname = 'targets/test_crawler_schedule.txt'
    spider_target_fname = 'targets/test_spider_extr_target.txt'
    target = get_target(target_fname, spider_target_fname)

    print 'Done'
