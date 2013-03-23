# coding: utf-8
""" Анализирует файлы с субтитрами и ищет частоту употребления слов.
    Каждому слову сопоставлет приложение или нескольно.
    
    Формат хранения данных пусть пока будет json
    
    TODO: Оптимизировать хранение контекста
    TODO: Ужесточить фильтрацию ключей
"""

# Sys

# Other - не все входят в поставку jython

# App
from business.to_text import is_content_nums 
from business.originator_frequency_index._content_filters import _purge_one_sentence

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


def process_list_content_sentences(sentences_lst, IndexCursor):
    # Обработка
    for one_sentence in sentences_lst:
        if one_sentence:
            pure_sentence = _purge_one_sentence(one_sentence)
            
            # Лексические единицы одного предложения
            set_words = pure_sentence.split(' ')
            for at in set_words:
                if at != ' ' and at:
                    if at in IndexCursor:
                        #if IndexCursor[at]['num'] < _kCountContentItems+1:
                        #    IndexCursor[at]['sents'].append(one_sentence)
                        IndexCursor[at]['num'] += 1
                    else:
                        # Первое включение
                        if _is_key_enabled(at):
                            IndexCursor[at] = {'num':1 }#, 'sents':[one_sentence]}



if __name__ == '__main__':
    #run()
    pass