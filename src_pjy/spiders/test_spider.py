# coding: utf-8
'''
Created on 10.04.2013

@author: кей
'''
""" 
import logging
logger = logging.getLogger('ftpuploader')
hdlr = logging.FileHandler('ftplog.log')
formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
hdlr.setFormatter(formatter)
logger.addHandler(hdlr)
logger.setLevel(logging.INFO)
FTPADDR = "some ftp address"
"""

import unittest
import re

# 
import dals.os_io.io_wrapper as dal

def parser_target_for_spider(target_fname):
    sets = dal.get_utf8_template()
    sets['name'] = target_fname
    list_lines, err = dal.efile2list(sets)
    if err[0]:
        return None, err
    
    # Utils
    remove_forward_and_back_spaces = lambda line: \
        re.sub("^\s+|\n|\r|\s+$", '', line) if line else None

    
    # Можно обрабатывать
    list_without_comments = map(
            lambda line: remove_forward_and_back_spaces(line.split('#')[0]), 
            list_lines)
    
    # Удаление пустых строк
    result_job_list = []
    map(lambda line: result_job_list.append(line) if line else None, list_without_comments)
    
    is_node = lambda line: True if '[' in line and ']' in line else False
    
    # В первой информационной строке должно быть имя узла
    if not is_node(result_job_list[0]):
        return None, [2, 'Неверный формат файла - первое имя узла должно быть до адресов.']
    
    get_node_name = lambda src_node_name: remove_forward_and_back_spaces(
                                        src_node_name.replace('[', '').replace(']', ''))
    
    current_node = get_node_name(result_job_list[0])
    for at in result_job_list:
        if is_node(at):
            current_node = get_node_name(at)
        else:
            print (current_node, at)
    
    
    # Tmp
    return "", [0, '']

class Test(unittest.TestCase):


    def test_parser_target_bad_file(self):
        target_fname = 'test_spider_target.txt_f'
        result, err = parser_target_for_spider(target_fname)
        self.assertIsNone(result, "File no exist")
        
    def test_parser_target(self):
        target_fname = 'test_data/test_spider_target.txt'
        result, err = parser_target_for_spider(target_fname)
        self.assertIsNotNone(result, "File exist")
        
    def test_parser_target_bad_format(self):
        target_fname = 'test_data/test_spider_target_bad.txt'
        result, err = parser_target_for_spider(target_fname)
        self.assertEqual(err[0], 2, "Ошибка форматирование файла задания")


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()