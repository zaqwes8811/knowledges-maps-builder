'''
Created on 20.03.2013

@author: кей
'''
# Sys


# Other
import re
import dals.os_io.io_wrapper as iow

def is_content_nums(string):
    pattern = '^\d*?$'
    result = re.finditer(pattern, string)
    for match in result :
        #s = match.group()
        return True
    return False

def get_list_content_items_from_str(url):
    """ Тотлько для субтитров. """
    _kInsertConst = '@@@@'
    settings = {
        'name':  url, 
        'howOpen': 'r', 
        'coding': 'cp866' }
        
    readed_lst = iow.file2list(settings)
    purged_lst = list()
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
    one_line_with_inserts = ''
    for at in one_line:
        one_line_with_inserts += at
        if at == '.' or at == '!' or at == '?' or at == ']':
            one_line_with_inserts += _kInsertConst
            
    # Чистый контент - набор предложений
    sentences_lst = one_line_with_inserts.split(_kInsertConst)
    return sentences_lst
