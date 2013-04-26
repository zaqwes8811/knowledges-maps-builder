#-*- coding: utf-8 -*-
""" 
    tested :
        python2.7
        jython2.5
            читает и пишет правильно, но тип текущей локали определят
                не правильно, поэтому выводит на консоль неправильно
    connect : import usaio.io_wrapper
"""
import codecs

""" 
    Класс для работы с файлами на компьютере
"""

class LocalHostException(Exception):
    pass

class File():
    _fhandle = None
    
    """ Инициализация дискриптора. Передача по ссылке! """
    def __init__(self, fhandle):

        self._fhandle = fhandle
    def __del__(self):
        self._fhandle.close()  # TODO(zaqwes): иногда выдает ошибк (jython)
    
    def to_list(self):
        try:
            list_lines = self._fhandle.readlines() 
             
            # Удалить переводы строк
            result = []
            for at in list_lines:
                result.append(at.replace('\r', '').replace('\n', ''))
            return result
        
        except UnicodeDecodeError, e:
            print 'UnicodeDecodeError', e
            return None
            
        except IOError, e:
            print 'IOError : ', e
            return None
    
    def write(self, str):
        try:
            try : 
                self._fhandle.write(str)
            except UnicodeEncodeError, e:
                print 'UnicodeEncodingError:', e
        except IOError, e:
            print 'IOError : ', e
        
""" Выдает файловые объекты """
def FabricOpen(settings):
    fname = settings['name']
    how_open = settings['how_open']
    xcoding = settings['coding']
    try:
        # создаем реальный файловый объект и передаем его обертке
        f = codecs.open(fname, how_open, encoding=xcoding)
        wrapper = File(f)

        return wrapper
    
    # Скорее всего путь не тот
    except IOError, e:
        print 'IOError : ', e
        return None


""" 
    Запись в файл ну формате 1251 c windews end line(\r\n)
    на вхоже список строк в unicode endline \n
"""

def write_file_from_list(targetFname, listLines):
    sum = ''
    for at in listLines:
        sum+=at[:-1]+'\r\n'
    import codecs
    f = codecs.open(targetFname, 'wb', encoding='cp1251')
    f.write(sum)
    f.close
    
def get_utf8_template():
    return {'name':  'fname', 'how_open': 'r', 'coding': 'utf_8'}

def file2list_int(sets):
    readed_list = file2list(sets)
    result = []
    try:
        for at in readed_list:
            result.append(int(at))
    except ValueError as e:
        print 'ValueError', e
        result = None
        
    return result
        
def file2list(sets):
    """ """
    file = FabricOpen(sets)
    listLines = None
    if file != None:
        # читаем все 
        listLines = file.to_list()
        #print listLines
        #if listLines != None:
            #for at in listLines:
                #print at
                #.encode('cp866')    # for jython
    return listLines

def list2file(sets, lst):
    file = FabricOpen(sets)
    if file != None and lst != None:
        file.write('\r\n'.join(lst))
    else :
        print "list2file error occure"

def app_str(sets, string):
    file = FabricOpen(sets)
    if file != None:
        file.write(string+'\r\n')
    else :
        print "app_str error occure"
        
def write_result_file(result_list, fname):
    sets = get_utf8_template()
    sets['how_open'] = 'w'
    sets['name'] = fname
    list2file(sets, result_list)
    
def read_utf_txt_file(fname):
    sets = get_utf8_template()
    sets['name'] = fname
    return file2list(sets) 
    
    
if __name__=='__main__':
    
    print 'Done'
