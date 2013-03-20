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
    pass

def main():
    
    # Получаем пути к субтитрам
    files = get_addrs()
    
    # Хранилище индекса
    content_item_name = 'Iron Man AA'
    Index = {content_item_name:{}}  # плохо возвращат хэндл на внутр. объекта
    
    # Получить индекс 
    for fname in files:
        # Выделяем единицы контента в список
        sentences_lst = get_list_content_items_from_str(fname)
        
        # Заполняем индекс
        # Разделить на наполнитель индекса и индекс?
        orginator.process_list_content_sentences(
                sentences_lst, 
                Index[content_item_name])
        
        # Второй вариант, вернуть ветку индекса и потом уже загрузить
        # в сам индекс - избыточность, но класс индекса может проверить
        # валидность этой ветки
    
    # Сохраняем в индексе
    branch = Index[content_item_name]
    for at in branch:
        if branch[at]['num'] > 1:
            print branch[at]['num'], at
            
    to_file = [json.dumps(Index, sort_keys=True, indent=2)]
    
    settings = {
        'name':  'extracted_words.json', 
        'howOpen': 'w', 
        'coding': 'cp866' }
        
    dal.list2file(settings, to_file)

if __name__=='__main__':
    main()

