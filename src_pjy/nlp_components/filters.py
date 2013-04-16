# coding: utf-8
'''
Created on 03.04.2013

@author: кей
'''
import re

def is_key_enabled(key_value):
    if key_value == 'and' or \
            is_content_nums(key_value) or \
            key_value == 'a' or \
            '(' in key_value or \
            ')' in key_value or \
            '(' in key_value or \
            len(key_value) < 2 or \
            False:
        return False
    return True

def is_content_nums(string):
    pattern = '^\d*?$'
    result = re.finditer(pattern, string)
    for match in result :
        return True
    return False
