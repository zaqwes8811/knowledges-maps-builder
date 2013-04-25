#-*- coding: utf-8 -*-
""" Нужно получить все
    
    thinks : walk and regex
    
    http://docs.python.org/2/tutorial/errors.html
"""
import os
slash = '\\'

import sys, traceback
 
class CrawlerException(Exception):
    def __init__(self, value):
        self.value = value
        
    def __str__(self):
        return str(self.value)

def _check_extension(string, extension_list, ignored_extentions):
    """ может быть ошибка, хотя маловероятна. Точка вероятность повышает 
    
    TODO(zaqwes): проверка заперщенных расширений
    """
    for k in extension_list:
        if '.'+k == string[-len(k)-1:].lower():
            return True
    return False

def find_files_down_tree(root, extension_list, ignored_extentions=None, ignored_dirs=None):
    """ Получить список файлов заданных типов с полными путями

    Args:
        ignoreList
            1. пути - папки
            2. расширения, которые похожи на разрешенные
            3. целые файлы (с путем(1 шт) и без(может быть много))
            4. регулярные выражения - подстроки
        
    troubles testing :
        разные типы данных - возвр. знач. и сообщение - но нужно 
            принимать из функции два значения
    """
    
    def on_error_walk(err):      
        # TODO: сделать свой класс обработки ошибок
        raise CrawlerException(err)
    
    result_list = list('')
    # получаем объект для обхода
    # Если корня нет исключение генерируется при доступе
    try:
        getted_list = os.walk(root, onerror=on_error_walk)
        for root, dirs, files in getted_list:
            if files:
                for name in files:
                    if _check_extension(name, extension_list, ignored_extentions):
                        bResult = True
                        if ignored_dirs:
                            for it in ignored_dirs:
                                if it in root:
                                    bResult = False
                            
                        if bResult:
                            result_list.append(root+slash+name)
    except CrawlerException as e:
        return None, (1, str(e))

    # возвращаем что насобирали
    return result_list, (0, '')    # может в питоне и нече, но вообеще-то...?
        # вобщем нужно подумать над обработкой ошибок


""" How use it """
if __name__ == '__main__':
    root = 'ds'
    extension_list = ['py']
    
    # Ignore
    ignore_extentions = ['pyc']
    
    # поиск
    result_list, err = find_files_down_tree(root, extension_list)
    print unicode(str(unicode(str(err[1]), 'cp1251')), 'utf8')
    
    def printer(msg):
        print msg

        
    # список получен, можно его обработать
    # в принципе можно передать указатель на функцию обработки
    #map(printer, result_list)
    
    print 'Done'



