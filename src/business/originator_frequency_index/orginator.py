#-*- coding: utf-8 -*-
""" Анализирует файлы с субтитрами и ищет частоту употребления слов.
    Каждому слову сопоставлет приложение или нескольно.
    
    Формат хранения данных пусть пока будет json
    
    TODO: Оптимизировать хранение контекста
    TODO: Ужесточить фильтрацию ключей
"""

# Sys

# Other - не все входят в поставку jython
import json
import dals.os_io.io_wrapper as iow

# App
from business.to_text import is_content_nums 
from business.to_text import get_list_content_items_from_str 
from business.originator_frequency_index.content_filters import _purge_one_sentence

_kCountContentItems = 3

def insert(original, new, pos):
    '''Inserts new inside original at pos.'''
    return original[:pos] + new + original[pos:] 
  
def _is_key_enabled(key_value):
    if key_value == 'and' or \
            is_content_nums(key_value) or \
            key_value == 'a' or \
            False:
        return False
    return True


 

if __name__ == '__main__':
    files = ['srts/Iron Man02x26.srt', 'srts/Iron1and8.srt']
    result = {'it':{'num':0, 'sents':[]}}
    for fname in files:
        
        # Выделяем единицы контента в список
        sentences_lst = get_list_content_items_from_str(fname)
        
        """ Далее индексопостроитель. """
        
        # Обработка
        for one_sentence in sentences_lst:
            if one_sentence:
                pure_sentence = _purge_one_sentence(one_sentence)
                
                # Лексические единицы одного предложения
                set_words = pure_sentence.split(' ')
                for at in set_words:
                    if at != ' ':
                        if at:
                            if at in result:
                                if result[at]['num'] < _kCountContentItems+1:
                                    result[at]['sents'].append(one_sentence)
                                result[at]['num'] += 1
                            else:
                                if _is_key_enabled(at):
                                    result[at] = {'num':1, 'sents':[one_sentence]}
    
    
    # Сохраняем в индексе
    to_file = [json.dumps(result, sort_keys=True, indent=2)]
    
    settings = {
        'name':  'extracted_words.json', 
        'howOpen': 'w', 
        'coding': 'cp866' }
        
    iow.list2file(settings, to_file)