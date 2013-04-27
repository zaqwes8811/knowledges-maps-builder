# coding: utf-8
'''
Created on 22.04.2013

@author: Igor
'''
import spiders_processors.docs_spider as docs_spider


        
def printer(msg):
    print msg

if __name__=='__main__':
    
    
    result = docs_spider.get_docs()
    map(printer, result)
    
        
    """
    # Каждый файл отдельно
    #
    index_file = 0
    result_ = []
    for txt_fname in list_targets:
        print 'Process file:', txt_fname
        file_content_in_list = read_utf_txt_file(txt_fname)
        meta = file_content_in_list[0]
        content = file_content_in_list[1:]
        
        node_name = txt_fname.split('/')[-1]+'_N'
        #print node_name
        meta_info = json.loads(meta)
        meta_info['node_name'] = node_name
        meta = json.dumps(meta_info)
        #print meta
        
        # Пишем заголовок
        result =[meta, '']
        
        # Токенизируем контент
        split_to_sentents(content, result)
        
        # Записываем результаты
        path_to_tmp_files = 'result_folder/'
        url = path_to_tmp_files+node_name+str(index_file)+'.txt'
        write_result_file(result, url)
        index_file += 1
        
        result_.append((node_name, url))"""
    #return result_

    print 'Done'
