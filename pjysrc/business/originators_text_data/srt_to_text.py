# coding: utf-8
'''
Created on 20.03.2013

@author: кей
'''
# Sys

# Other
import re
import dals.os_io.io_wrapper as dal

def is_content_nums(string):
    pattern = '^\d*?$'
    result = re.finditer(pattern, string)
    for match in result :
        #s = match.group()
        return True
    return False

def _split_to_sentences(one_line):
    """ Сделано супер просто. Но реально алгоритмы не таки простые. """
    _kInsertConst = '@@@@'
    one_line_with_inserts = ''
    for at in one_line:
        one_line_with_inserts += at
        if at == '.' or at == '!' or at == '?' or at == ']':
            one_line_with_inserts += _kInsertConst
            
    # Чистый контент - набор предложений
    sentences_lst = one_line_with_inserts.split(_kInsertConst)
    return sentences_lst

def get_list_content_items_from_str(url):
    """ Тотлько для субтитров. """
    result = _split_to_sentences(srt_to_text_line(url))
    return result

def srt_to_text_line(url):
    """ Тотлько для субтитров. """
    sets = dal.get_utf8_template()
    sets['name'] = url
        
    readed_lst = dal.file2list(sets)
    purged_lst = list()
    if readed_lst:
        for at in readed_lst:
            at_copy = at.replace('\r','')
            at_copy = at_copy.replace('\n','')
            if at_copy:
                if not '-->' in at_copy:
                    if not is_content_nums(at_copy):
                        at_copy = at_copy.replace('<i>','')
                        at_copy = at_copy.replace('</i>','')
                        purged_lst.append(at_copy)
    
    # Теперь нужно разить на предложения
    one_line = ' '.join(purged_lst)
    
    return one_line
