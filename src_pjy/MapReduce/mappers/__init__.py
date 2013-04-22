# coding: utf-8
from nlp_components.content_items_processors import process_list_content_sentences
from nlp_components.content_items_processors import process_list_content_sentences_real

import dals.os_io.io_wrapper as dal
# NO DRY!!
def read_utf_txt_file(fname):
    sets = dal.get_utf8_template()
    sets['name'] = fname
    return dal.file2list(sets) 

def mapper(job):
    """ [node_name, .., .., .., ]"""
    url = job[1]
    node_name = job[0]

    # Получем текст
    list_content_items = read_utf_txt_file(url)
    
    # Теперь можно составлять индекс
    index, (count_sents, summ_sents_len) = process_list_content_sentences(
                list_content_items)

    parallel_pkg = (node_name, index, [count_sents, summ_sents_len], url)
    return parallel_pkg

def mapper_real(job):
    """ [node_name, .., .., .., ]"""
    url = job[0]
    text_extractor = job[1]
    tokenizer = job[2]
    node_name = job[3]

    # Получем текст
    text = text_extractor(url)
    
    # Токенизация
    lits_content_items = []
    if tokenizer:
        lits_content_items = tokenizer(text)
    else:
        lits_content_items = [text]
    
    # Теперь можно составлять индекс
    index, (count_sents, summ_sents_len) = \
            process_list_content_sentences_real(
                lits_content_items,
                tokenizer)

    parallel_pkg = (node_name, index, [count_sents, summ_sents_len], url)
    return parallel_pkg








