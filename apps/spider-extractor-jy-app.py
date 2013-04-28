# coding: utf-8
'''
Created on 22.04.2013

@author: кей
'''
import traceback
try:
    import sys
    import json
    
    sys.path.append('../src')
    sys.path.append('../no-jython-libs')
    sys.path.append('D:/work-libs/java/tika-app-1.3.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/resources.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/rt.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/jsse.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/jce.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/charsets.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/ext/dnsns.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/ext/localedata.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/ext/sunjce_provider.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/ext/sunmscapi.jar')
    sys.path.append('C:/Program Files (x86)/Java/jre6/lib/ext/sunpkcs11.jar')
    
    # Java
    import java.text.BreakIterator as BreakIterator
    import java.util.Locale as Locale
    import java.lang.System as System
    
    # App
    import dals.local_host.local_host_io_wrapper as dal
    from nlp_components import split_to_sentents
    from app_utils import printer
    from crosscuttings import tools
    
    # units
    from crawlers import get_node_name
    from crawlers import get_url
    from crawlers import get_path_tasks
    from crawlers import kKeyIndexName
    
    # ToText convertors
    from spiders_extractors.tika_wrapper import TextExtractorFromOdtDocPdf

except:
    formatted_lines = traceback.format_exc().splitlines()
    err_msg = '\n'.join(formatted_lines) 
    print err_msg
    print sys.path
    var = raw_input("Press any key.")
    exit()
map(printer, sys.path)

def main(spider_target_fname):
    # Задание получено в предыдущий сериях
    rpt = []

    # Задание загружаем из файла
    json_target, err = dal.read_utf_file_to_list_lines(spider_target_fname+'.json')
    if not json_target:
        print 'Failure occure: ', err[1]
        var = raw_input("Press any key.")
        print 'Quit'
        exit()

    target = json.loads(' '.join(json_target))
    
    # Рельные задание с именами файлов и узлов
    nodes_and_urls, err = get_path_tasks(spider_target_fname+'.txt')
    def get_node_and_url(line):
        node = get_node_name(line)
        url = get_url(line)
        return (node, url)
        
    nodes_and_urls_pkt = map(get_node_and_url, nodes_and_urls)
    
    # Можно переводить в текст   
    for pair in nodes_and_urls_pkt:
        # Почему-то нужно создавать каждый раз!
        extractor = TextExtractorFromOdtDocPdf()
        node = pair[0]
        url = pair[1]
        index_name = target[kKeyIndexName]
        app_folder, index_path, index_root, tmp_root = tools.get_pathes_complect(index_name)
        path_to_index = tmp_root
        path_to_tmp_node = tmp_root+'/'+node
        result, err = extractor.process(url, path_to_tmp_node)
        print result, err
        if err[0]:
            rpt.append(err[1])  
        break 
        
    return rpt

    
if __name__=='__main__':
    spider_target_fname = 'targets/spider_extractor_target'
    rpt = main(spider_target_fname)
    if rpt:
        print 'Rpt:'
        map(printer, rpt)
    print 'Done'
