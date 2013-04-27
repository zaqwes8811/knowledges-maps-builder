# coding: utf-8
'''
Created on 27.04.2013

@author: кей
'''

# units
from crawlers import get_target
from scriber import scribe_index


def printer(msg):
    print msg

if __name__=='__main__':
    def main():
        rpt = []
        # Запускаем крулер
        target_fname = 'targets/test_crawler_schedule.txt'
        spider_target_fname = 'targets/test_spider_extr_target.txt'
        target = get_target(target_fname, spider_target_fname)
        # TODO(zaqwes): может задание в json сохранять для передачи разметчику, а то
        #   при любых ошибках нужно заново запускать поиск
        
        rpt = scribe_index(target, spider_target_fname)
        return rpt
          
    # Runner()  
    rpt = main()
    if rpt:
        map(printer, rpt)
    
        
    print 'Done'
