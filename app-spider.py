# coding: utf-8
'''
Created on 11.04.2013

@author: кей
'''

from spiders import base_spider


kSpiderTargetsPath = './app-spider-targets'

if __name__=='__main__':
    target_name = 'iron_man_aa_target.txt'
    result, err = base_spider(target_name)