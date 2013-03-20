# coding: utf-8

# Other
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

class Index(object):
    _own_index = {}  # ядро системы
    
    def do_branch(self, branch_name):
        # ! Провеить на входимость, иначе затрет!
        self._own_index = {branch_name:{}}
    
    def get_branch(self, branch_name):
        return self._own_index[branch_name]
    
    def print_branch(self, branch_name):
        branch = self._own_index[branch_name]
        for at in branch:
            if branch[at]['num'] > 1:
                print branch[at]['num'], at
                
    def get_map(self):
        print
        print "Index map"
        for at in self._own_index:
            print at
    
    def save(self):
        to_file = [json.dumps(self._own_index, sort_keys=True, indent=2)]
        sets = dal.get_utf8_template()
        sets['name'] = 'index.json'
        sets['howOpen'] = 'w'
        dal.list2file(sets, to_file)

def main():
    # Получаем пути к субтитрам
    # Сборщик контента - отдельный объект!
    content_item_name = 'Iron Man AA'
    files = get_addrs()
    
    # Хранилище индекса
    index = Index()
    index.do_branch(content_item_name)

    # Получить индекс 
    for fname in files:
        # Выделяем единицы контента в список
        sentences_lst = get_list_content_items_from_str(fname)
        
        # Заполняем индекс
        # Разделить на наполнитель индекса и индекс?
        # Index = {content_item_name:{}}  # плохо возвращат хэндл на внутр. объекта
    
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
    index.save()

if __name__=='__main__':
    main()

