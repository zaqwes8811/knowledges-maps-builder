# coding: utf-8
# Std
import re

def remove_forward_and_back_spaces(line):
    if line:
        return re.sub("^\s+|\n|\r|\s+$", '', line)
    else:
        return None
    
def remove_fandb_spaces_in_tuple(src):
    tmp = []
    for at in src:
        tmp.append(remove_forward_and_back_spaces(at))
        
    return tuple(tmp)