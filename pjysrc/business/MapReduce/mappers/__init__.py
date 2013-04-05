# coding: utf-8
from business.nlp_components.content_items_processors import process_list_content_sentences

def mapper(job):
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
            process_list_content_sentences(
                lits_content_items,
                tokenizer)

    parallel_pkg = (node_name, index, [count_sents, summ_sents_len], url)
    return parallel_pkg