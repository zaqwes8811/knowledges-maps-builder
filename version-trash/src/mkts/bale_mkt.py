# type-mkt: interfaces
""" 
TODO(zaqwes): Move from here
Словарь макетов: 
  String - абстрактная строка
  Int(int) - целое число 
  
! Комментарии расставлять как показано
под именами классов и интерфейсов. Это используется при разборе
макета

Делемма:
  Для Python тип не важен, для Java важен, как быть с макетом?
    Ровнять по Java?
    
  Так же по другому происходит комментирование.
  
class Name  !! no comments

/*(? это предположение)* /!! в длинных комментах
"""

"""
  Dict:
    ресурс - файл, сокет, ресурс по ссылке, cgi-скрипт, etc.

  Use cases:
    @use case 
    @use case 
    @use case 

  Notes:
    Данные можно получать и прямо по ссылкам, и по готовому тексту
    
    Так как макет интерфейсный, то по всем java классам и интерфейсам
      должны генерится отдельные файлы

  Thinks:
    Есть ли в Java деструкторы или финалайзеры?

    Есть ли смысл в разделении захвата ресурса и преобразовании? Пока
    не используется преобразование по частям. А если использовать по частям?
    
    Наследование интерфейсов
      http:#www.javainception.ru/index.php/interfeisi/interfeisi/lmnozhestvennoe-nasledovanier-v-java.html
      http:#www.javaportal.ru/java/articles/mnj.html - in depth

    Перенести навигационную часть в dals or not? В dals конкретные запросы, или карты тоже, 
      там сейчас есть dir_walker.py,
      и он вполне ценен и, кажется, на месте. Наверное стоит. Но пример множественного наследования
      можно оставить. 

    Что если объединить обработку и навигацию? Появляется избыточность, если обработка данных не нужна
    Что если разделить? Возможно будет возникать повторное открытие ресурсов
    А что если использовать множественное наследование интерфейсов?
  
  Troubles:
    Возможно требуется двойной (и более) захват ресурса, что может быть накладно.
      Это особенно сильно проявляется при делении на получение карты ресурса и его обработку.
      Может таки объединить классы, тогда в алгоритм может быть таким:
        1. Захват вершины
        2. Получение карты ресурса
        3. "Ходим" по карте и что-то делаем
        4. Отпускаем ресурс

  Usige:
    Java:
    interface IAll extend/*(? это предположение)* / strange_things.ExtractorTextFromResource, strange_things.ExtracitorResourcePlan
    class MixedClass2 implements strange_things.ExtractorTextFromResource, strange_things.ExtracitorResourcePlan
"""


interface strange_things.ExtracitorResourcePlan
    """ Как бы навигационная часть """
    # Спорный вызов. Как он будет использоваться при множественном наследовании?
    # Захватывает точку входа в ресурс (например, файл базы данных)
    strange_things.ErrorContainer captureRoot(String urlRoot)
    # Error _freeRoot();  # должен вызывать в деструкторе

    # @param url информация о том, как можно добраться до вершины ресурса
    # @return карта, по которой можно передвигаться
    strange_things.GraphToText getPlan(String url, strange_things.ErrorContainer err)

    strange_things.GraphToText getPlanFromText(String url, strange_things.ErrorContainer err)

    # @return просто список "адресов"
    # Обобщить дробилку с рекурсией! Она использовалась для разбора страницы Курсеры
    List<String> getListAddresses(String url, strange_things.ErrorContainer err)

    # Выделяем адреса без подключения, у нас есть текст, который доставерно
    #   отражает план ресурса
    List<String> extractListAddressesFromText(String text, strange_things.ErrorContainer err)


interface strange_things.ExtractorTextFromResource
    """ Соединяется с ресурсом и преобразовывает его в текст.
      Thinks:
        Не забывать освободить ресурсы(о реализации), но как быть при множ. наследовании
    """
    # Выделяет весь текст из ресурса по некоторому адресу
    # @param url в переменной все данные, чтобы ресурс мог быть открыт
    String extract(String url, strange_things.ErrorContainer err)

    # Получить из ресурса сразу список единиц контента - предложений, например
    List<String> contentItemsToList(String url, strange_things.ErrorContainer err)

interface strange_things.TextToText
    # @param text зашумленнй текст, например html код
    # @return чистый текст
    # ! не возможно разрешить перегрузку
    String testToText(String text, strange_things.ErrorContainer err)

    # Получить сразу список единиц контента
    List<String> contentItemsToList(String text, strange_things.ErrorContainer err)


interface strange_things.ErrorContainer
    """ 
      Thinks:
        Как я понял в Java нельзя передать примитив по ссылке, исключениями для обработки ошибок
          пользоваться не хочу - исключения для исключительных ситуаций.
          
        Можно как-то вернуть tuple, но похоже с python это не склеить
    """
    String what()
    int getErrCode()

