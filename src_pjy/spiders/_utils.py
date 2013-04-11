# coding: utf-8
'''
Created on 11.04.2013

@author: кей
'''
# Sys
import re

# Own
import dals.os_io.io_wrapper as dal

def parser_target_for_spider(target_fname):
    """ 
    
    Thinks:
        А что если файл пустой?
        
        
    TODO:
        Сделать кастомизацию преобразоватлелей в текст
    """
    sets = dal.get_utf8_template()
    sets['name'] = target_fname
    list_lines, err = dal.efile2list(sets)
    if err[0]:
        yield None, err
        return
    
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
        yield None, [2, 'Неверный формат файла - первое имя узла должно быть до адресов.']
        return
    
    get_node_name = lambda src_node_name: remove_forward_and_back_spaces(
                                        src_node_name.replace('[', '').replace(']', ''))
    
    current_node = get_node_name(result_job_list[0])
    i = 0
    for at in result_job_list:
        if is_node(at):
            current_node = get_node_name(at)
            i = 0
        else:
            i += 1
            yield (current_node, at, i)
