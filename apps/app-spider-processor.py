# coding: utf-8
'''
Created on 22.04.2013

@author: Igor
'''
import spiders_processors.docs_spider as docs_spider


        
def printer(msg):
    print msg

if __name__=='__main__':
    
    
    result = docs_spider.get_docs()
    map(printer, result)

    print 'Done'
