# coding: utf-8
'''
Created on 10.04.2013

@author: кей
'''
import unittest

# 
import dals.os_io.io_wrapper as dal

def parser_target_for_spider(target_fname):
    sets = dal.get_utf8_template()
    sets['name'] = target_fname
    print target_fname
    list_lines, err = dal.efile2list(sets)
    if err[0]:
        return None, err

class Test(unittest.TestCase):


    def testName(self):
        target_fname = 'test_spider_target.txt_f'
        result, err = parser_target_for_spider(target_fname)
        self.assertIsNone(result, "File no exist")


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()