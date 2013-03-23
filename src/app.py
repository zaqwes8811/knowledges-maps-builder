# coding: utf-8

# Other
import os
import json
import dals.os_io.io_wrapper as dal

# App
import business.originator_frequency_index.orginator as orginator
from business.to_text import get_list_content_items_from_str 

def get_addrs():
    files = [
            '../statistic_data/srts/Iron Man AA/Iron Man02x26.srt', 
            '../statistic_data/srts/Iron Man AA/Iron1and8.srt']
    return files

class IndexCursor(object):
    """ Итератрор индекса. Работает с одной веткой, но знает 
        конерь хранилища и его план
        
        Thinks:
            Как будет происходит первое подкл. к индексу? Мы не знаем
            есть ли ветки
    """
    _index_root = ''
    _own_index = {}  # ядро системы - кэш
    _current_branch = None
    
    def _get_real_branch_name(self):
        return self._
    
    def __init__(self, index_root, init_branch=None):
        self._index_root = index_root
    
    def load_index(self):
        pass
    
    def assign_branch(self, branch_name):
        self._current_branch = branch_name
    
    def do_branch(self, branch_name):
        # ! Провеить на входимость, иначе затрет!
        self._own_index = {branch_name:{}}
        try:
            os.mkdir(self._index_root+'/'+branch_name.replace(' ', '$$'))
        except OSError as e:
            print 'Branch is exist', e
            
    def get_branch(self, branch_name):
        return self._own_index[branch_name]
    
    def print_branch(self, branch_name):
        branch = self._own_index[branch_name]
        for at in branch:
            if branch[at]['num'] > 1:
                print branch[at]['num'], at
                
    def get_map(self):
        print
        print "IndexCursor map"
        for at in self._own_index:
            print at
    
    def save_branch(self, branch_name):
        to_file = [json.dumps(self._own_index, sort_keys=True, indent=2)]
        sets = dal.get_utf8_template()
        sets['name'] = 'index.json'
        sets['howOpen'] = 'w'
        dal.list2file(sets, to_file)
        
    def save_all(self):
        pass
    
    def get_list_nodes(self):
        list_nodes = os.listdir(self._index_root)
        result = []
        for at in list_nodes:
            result.append(at.replace('$$', ' '))
        return result

def main():
    # Просматриваем индекс
    index_root = 'indexes'
    index = IndexCursor(index_root)
    list_nodes = index.get_list_nodes()
    
    #index.do_branch(content_item_name)
    
    # Получаем пути к субтитрам
    # Сборщик контента - отдельный объект!
    content_item_name = 'Iron Man AA'
    files = get_addrs()
    
    
    '''
    # Получить индекс 
    print 'Process files. Wait please...'
    for fname in files:
        # Выделяем единицы контента в список
        sentences_lst = get_list_content_items_from_str(fname)
        
        # Заполняем индекс
        # Разделить на наполнитель индекса и индекс?
        # IndexCursor = {content_item_name:{}}  # плохо возвращат хэндл на внутр. объекта
    
        orginator.process_list_content_sentences(
                sentences_lst, 
                index.get_branch(content_item_name))
        
        # Второй вариант, вернуть ветку индекса и потом уже загрузить
        # в сам индекс - избыточность, но класс индекса может проверить
        # валидность этой ветки
    
    # Выводим
    index.print_branch(content_item_name)
    index.get_map()
    
    # Сохраняем в индексе  
    index.save_branch(None)'''
    
    

if __name__=='__main__':
    print 'Begin'
    main()
    print 'Done'

