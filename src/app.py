# coding: utf-8

# App
import business.originator_frequency_index.orginator as orginator
from business.to_text import get_list_content_items_from_str 
from business.originator_frequency_index.orginator import IndexCursor

def get_addrs():
    files = [
            '../statistic_data/srts/Iron Man AA/Iron Man02x26.srt', 
            '../statistic_data/srts/Iron Man AA/Iron1and8.srt']
    return files



def main():
    # Просматриваем индекс
    index_root = 'indexes'
    index = IndexCursor(index_root)
    list_nodes = index.get_list_nodes()
    print list_nodes
    
    # Типа выбрали ветку
    content_item_name = 'Iron Man AA'
    
    index.assign_branch(content_item_name)
    
    # Получаем пути к субтитрам
    # Сборщик контента - отдельный объект!
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

