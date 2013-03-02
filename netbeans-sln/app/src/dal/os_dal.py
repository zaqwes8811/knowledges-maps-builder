# coding: utf-8

# Other
import uasio.os_io.io_wrapper as iow

def read_file_and_purge_content(fname):
    settings = {
        'name':  fname, 
        'howOpen': 'r', 
        'coding': 'cp866' }
        
    readed_lst = iow.file2list(settings)
    purged_lst = list()
    for at in readed_lst:
        at_copy = at.replace('\r','')
        at_copy = at_copy.replace('\n','')
        if at_copy:
            if not '-->' in at_copy:
                if not _is_content_nums(at_copy):
                    at_copy = at_copy.replace('<i>','')
                    at_copy = at_copy.replace('</i>','')
                    purged_lst.append(at_copy)
    
    # Теперь нужно разить на предложения
    one_line = ' '.join(purged_lst)
    return one_line