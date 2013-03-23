# coding: utf-8
""" Анализирует файлы с субтитрами и ищет частоту употребления слов.
    Каждому слову сопоставлет приложение или нескольно.
    
    Формат хранения данных пусть пока будет json
    
    TODO: Оптимизировать хранение контекста
    TODO: Ужесточить фильтрацию ключей
"""

# Sys

# Other - не все входят в поставку jython
import os
import json
import dals.os_io.io_wrapper as dal

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

class IndexCursor(object):
    """ Итератрор индекса. Работает с одной веткой, но знает 
        конерь хранилища и его план
        
        Thinks:
            Как будет происходит первое подкл. к индексу? Мы не знаем
            есть ли ветки
    """
    _index_root = ''
    _branch_cash = {}  # ядро системы - кэш
    _current_branch = None
    
    def _get_real_branch_name(self):
        #return self._
        pass
    
    def __init__(self, index_root, init_branch=None):
        self._index_root = index_root
      
    def assign_branch(self, branch_name):
        """ Соединяет курсор с узлом. Если узла нет, создается."""
        self._current_branch = branch_name
        
        try:
            os.mkdir(self._index_root+'/'+branch_name.replace(' ', '$$'))
        except OSError as e:
            print 'Branch is exist', e    
            # Загружаем индекс 
    
    def print_branch(self, branch_name):
        branch = self._branch_cash[branch_name]
        for at in branch:
            if branch[at]['num'] > 1:
                print branch[at]['num'], at
                
    def get_map(self):
        print
        print "IndexCursor map"
        for at in self._branch_cash:
            print at
    
    def save_branch(self, branch_name):
        to_file = [json.dumps(self._branch_cash, sort_keys=True, indent=2)]
        sets = dal.get_utf8_template()
        sets['name'] = 'index.json'
        sets['howOpen'] = 'w'
        dal.list2file(sets, to_file)
    
    def get_list_nodes(self):
        list_nodes = os.listdir(self._index_root)
        result = []
        for at in list_nodes:
            result.append(at.replace('$$', ' '))
        return result
    
    # Processors
    def process_list_content_sentences(self, sentences_lst):
        """ Получает список предложений и заполняет ветку индекса. """
        # Обработка
        for one_sentence in sentences_lst:
            if one_sentence:
                pure_sentence = _purge_one_sentence(one_sentence)
                
                # Лексические единицы одного предложения
                set_words = pure_sentence.split(' ')
                for at in set_words:
                    if at != ' ' and at:
                        if at in self._branch_cash:
                            #if IndexCursor[at]['num'] < _kCountContentItems+1:
                            #    IndexCursor[at]['sents'].append(one_sentence)
                            self._branch_cash[at]['num'] += 1
                        else:
                            # Первое включение
                            if _is_key_enabled(at):
                                self._branch_cash[at] = {'num':1 }#, 'sents':[one_sentence]}


if __name__ == '__main__':
    #run()
    pass