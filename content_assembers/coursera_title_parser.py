#-*- coding: utf-8 -*-

import sys
sys.path.append('D:/github-release')

import json

# Other
import uasio.os_io.io_wrapper as iow

# App
import _http_requester as http_request

def _split_url(url):
    """ http://www.dessci.com/en/products/mathplayer/ to 
    www.dessci.com /en/products/mathplayer/ - для httplib
    """
    url_copy = url.replace('https://','')
    url_copy = url_copy.replace('http://','')
    
    head = url_copy.split('/')[0]
    path = url_copy[len(head):]
    return head+' '+path
    
#def _iteration():
    
                
def _fill_tree_dict(lst, levels, level_pos, result_dict):
    #result_dict = {}
    if level_pos == len(levels)-1:
        return
    
    foldered_list = ('@@@'.join(lst)).split(levels[level_pos])
    for it in foldered_list:
        splitted = it.split('@@@')
        if splitted[0]:
            # заполняем
            #result_dict[splitted[0]] = 
            print splitted[1:]
         
    # Переходим на следующий ярус
    level_pos += 1
    #return result_dict

def main():
    fname = 'lessions_names.txt'
    fname = 'lessions_html_code.txt'
    
    sets = iow.get_utf8_template()
    sets['name'] = fname
    
    i = 0
    result_dict = []
    readed_list = iow.file2list(sets)
   
    for at in readed_list:
        # Предварительная фильтарция
        at = at.replace('</a>', '')
        
        # Ссылки с содержанием
        if 'pdf' in at or '&format=srt' in at:
            at_copy = at.replace('    <a target="_new" href=', '')
            result_dict.append('link_to_get '+_split_url(at_copy))

        # Темы
        if 'min)' in at and 'div' not in at:
            result_dict.append('name_content '+at)
            
        # Части-недели
        if '(Week' in at:
            at_copy_list = at.split('&nbsp;')[1].split('</h3>')[0]
            result_dict.append('folder '+at_copy_list)
        i += 1
    
    # теперь нужно запаковать в словарь
    levels = ['folder', 'name_content', 'link_to_get']
    result = {}
    result = _fill_tree_dict(result_dict, levels, 0, result)
    
    # сохраняем результаты в файл
    to_file = [json.dumps(result, sort_keys=True, indent=2)]
    
    settings = {
        'name':  'extracted_from_html.json', 
        'howOpen': 'w', 
        'coding': 'cp866' }
        
    iow.list2file(settings, to_file)
    
if __name__=='__main__':
    main()
    
    print 'Done'
