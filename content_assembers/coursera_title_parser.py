#-*- coding: utf-8 -*-

import sys
sys.path.append('D:/github-release')

import json

# Other
import uasio.os_io.io_wrapper as iow

# App
import _http_requester as http_request

def _split_url(url):
    url_copy = url.replace('https://','')
    url_copy = url_copy.replace('http://','')
    
    head = url_copy.split('/')[0]
    return head#url_copy

if __name__=='__main__':
    fname = 'lessions_names.txt'
    fname = 'lessions_html_code.txt'
    
    sets = iow.get_utf8_template()
    sets['name'] = fname
    
    i = 0
    result_dict = {}
    readed_list = iow.file2list(sets)
   
    for at in readed_list:
        if 'pdf' in at or '&format=srt' in at:
            at_copy = at.replace('    <a target="_new" href=', '')
            result_dict[i] = _split_url(at_copy)

        if 'min)' in at and 'div' not in at:
            result_dict[i] = at
            
        if '(Week' in at:
            at_copy_list = at.split('&nbsp;')[1].split('</h3>')[0]
            print at_copy_list
            result_dict[i] = at_copy_list
        i += 1
    
    # сохраняем результаты в файл
    to_file = [json.dumps(result_dict, sort_keys=True, indent=2)]
    
    settings = {
        'name':  'extracted_from_html.json', 
        'howOpen': 'w', 
        'coding': 'cp866' }
        
    iow.list2file(settings, to_file)
    
    print 'Done'
