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
    _index_root = None
    _branch_cash = None  # ядро системы - кэш
    _current_branch = None
    
    _kForwardIndexName = 'forward_index.json'
    
    def _get_real_branch_name(self):
        real_branch_name = self._index_root+'/'+self._current_branch.replace(' ', '$$')
        return real_branch_name
    
    def __init__(self, index_root, init_branch=None):
        self._index_root = index_root
      
    def assign_branch(self, branch_name):
        """ Соединяет курсор с узлом. Если узла нет, создается."""
        self._current_branch = branch_name
        self._branch_cash = {}
        findex_name = self._get_real_branch_name()+'/'+self._kForwardIndexName
        sets = dal.get_utf8_template()
        sets['name'] = findex_name
        try:
            os.mkdir(self._get_real_branch_name())
            sets['howOpen'] = 'w'
            dal.list2file(sets, ["{}"])
        except OSError as e:
            print 'Branch is exist'  
            # Загружаем индекс 
            readed_list = dal.file2list(sets)
            branch_in_json = ' '.join(readed_list)
            
            # TODO(zaqwes): долгая операция(несколько секунд), как быть?
            self._branch_cash = json.loads(branch_in_json)
    
    def print_branch(self, branch_name):
        branch = self._branch_cash
        for at in branch:
            if branch[at] > 1:
                print branch[at], at
                  
    def save_branch_cash(self):
        to_file = [json.dumps(self._branch_cash, sort_keys=True, indent=2)]
        sets = dal.get_utf8_template()
        sets['name'] = self._get_real_branch_name()+'/'+self._kForwardIndexName
        sets['howOpen'] = 'w'
        dal.list2file(sets, to_file)
        
    #def _load_branch_in_cash(self):
    #    pass
    
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
                            self._branch_cash[at] += 1
                        else:
                            # Первое включение
                            if _is_key_enabled(at):
                                self._branch_cash[at] = 1 #, 'sents':[one_sentence]}
        self.save_branch_cash()

if __name__ == '__main__':
    #run()
    pass