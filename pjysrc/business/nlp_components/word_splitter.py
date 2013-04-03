# coding: utf-8
'''
Created on 03.04.2013

@author: кей
'''
import re

from business.nlp_components._content_filters import _purge_one_sentence
from business.nlp_components.filters import _is_key_enabled

# Processors
def processListContentSentences(list_s_sentences):#void
    """ Получает список предложений и заполняет ветку индекса. """
    _branch_cash = {}
    
    # Обработка
    for one_sentence in list_s_sentences:
        if one_sentence:
            pure_sentence = _purge_one_sentence(one_sentence)
            
            # Лексические единицы одного предложения
            set_words = pure_sentence.split(' ')
            for at in set_words:
                if at != ' ' and at:
                    if at in _branch_cash:
                        _branch_cash[at] += 1
                    else:
                        # Первое включение
                        if _is_key_enabled(at):
                            _branch_cash[at] = 1
                            
    return _branch_cash
