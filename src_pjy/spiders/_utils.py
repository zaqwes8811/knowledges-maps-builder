# coding: utf-8
'''
Created on 11.04.2013

@author: кей
'''
# Sys
import re

# Own
import dals.os_io.io_wrapper as dal

def _parse_target_params(str_params):
    rpt = []
    if str_params.count('[') != str_params.count(']'):
        return None, 1, "\tError: [Count '[' != count ']']"

    if str_params.count(':') != str_params.count('[') or \
        str_params.count(':') != str_params.count(']'):
        return None, 1, "\tError: [Format param - [anything : something]]"

    if 'std' in str_params:
        return '', 1, "Test rpt"
    else:
        return '', 0, None

    
def _is_node(line):
    line = remove_forward_and_back_spaces(line)
    if line[0] == '[' and line[-1] == ']':
        return True
    else:
        return False
    
def get_node_name(src_node_name): 
    return remove_forward_and_back_spaces(src_node_name.replace('[', '').replace(']', ''))
    
def remove_forward_and_back_spaces(line):
    if line:
        return re.sub("^\s+|\n|\r|\s+$", '', line)
    else:
        return None

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
        rpt = err[1]
        yield None, 1, rpt
        return
   
    # Можно обрабатывать
    list_without_comments = map(
            lambda line: remove_forward_and_back_spaces(line.split('#')[0]), 
            list_lines)
    
    # Удаление пустых строк
    result_job_list = []
    map(lambda line: result_job_list.append(line) if line else None, list_without_comments)

    # В первой информационной строке должно быть имя узла
    if not _is_node(result_job_list[0]):
        rpt = 'target_fname: '+target_fname+'. Неверный формат файла - первое имя узла должно быть до адресов.'
        code_err = 2
        yield None, code_err, rpt
        return
    
    current_node = get_node_name(result_job_list[0])
    i = 0

    for at in result_job_list:
        if _is_node(at):
            current_node = get_node_name(at)
            i = 0
        else:
            i += 1
            # Выделяем обработчик
            pos_first_settings_item = at.find('[')
            if pos_first_settings_item != -1:
                params = at[pos_first_settings_item:]
                url =  remove_forward_and_back_spaces(
                        at[:pos_first_settings_item])
                params, code_err, rpt = _parse_target_params(params)
                if code_err != 0 and rpt:
                    rpt = 'Name node: ['+current_node+']\nUrl: ['+url+']\n\t'+rpt+''
                yield (current_node, url, i, params), 0, rpt
            else:
                rpt = None
                yield (current_node, url, i, None), 0, rpt
                
            
            
            
            
            
