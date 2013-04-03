# coding: utf-8
'''
Created on 03.04.2013

@author: кей
'''
import re

from business.nlp_components._content_filters import _purge_one_sentence
from business.nlp_components.filters import is_key_enabled

# Processors
def processListContentSentences(list_s_sentences, tokenizer):#void
    """ Получает список предложений и заполняет ветку индекса. """
    one_node_hash = {}
    summ_sents_len = None
    count_sents = None
    if tokenizer:
        count_sents = len(list_s_sentences) 
        summ_sents_len = 0
    
    # Обработка
    #def _process_one_sentence(sentence):
    for  sentence in list_s_sentences:
        if sentence:
            pure_sentence = _purge_one_sentence(sentence)

            # Лексические единицы одного предложения
            set_words = pure_sentence.split(' ')
            if tokenizer:
                summ_sents_len += len(set_words)
            
            for at in set_words:
                if at != ' ' and at:
                    # Темперь пропускаем через стеммер
                    stemmed_key = at
                    if stemmed_key in one_node_hash:
                        one_node_hash[stemmed_key]['N'] += 1
                        one_node_hash[stemmed_key]['S'].add(at)
                    else:
                        # Первое включение
                        if is_key_enabled(stemmed_key):                          
                            # Все что соответсвует слову
                            one_node_hash[stemmed_key] = {}
                            
                            # Формируем структуру, соотв. одному корню слова
                            one_node_hash[stemmed_key]['N'] = 1
                            one_node_hash[stemmed_key]['S'] = set()
                            one_node_hash[stemmed_key]['S'].add(at)
                            
    #map(_process_one_sentence, list_s_sentences)
        
                            
    return one_node_hash, (count_sents, summ_sents_len)
