# coding: utf-8
'''
    TODO(zaqwes): Как быть если запуск многопоточный, а файл включен в несколько узлов?
'''
# Sys

# Other
import re
import dals.os_io.io_wrapper as dal

from  business.nlp_components.filters import is_content_nums

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
