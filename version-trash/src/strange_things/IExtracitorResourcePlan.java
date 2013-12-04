package strange_things;// Import
import java.util.List;


public interface /*class*/ IExtracitorResourcePlan /* implement strange_things.IExtracitorResourcePlan */ {
    /** Как бы навигационная часть */
    // Спорный вызов. Как он будет использоваться при множественном наследовании?
    // Захватывает точку входа в ресурс (например, файл базы данных)
    IErrorContainer captureRoot(String urlRoot);//hided {}
    // Error _freeRoot();  // должен вызывать в деструкторе

    // @param url информация о том, как можно добраться до вершины ресурса
    // @return карта, по которой можно передвигаться
    IGraphToText getPlan(String url, IErrorContainer err);//hided {}

    IGraphToText getPlanFromText(String url, IErrorContainer err);//hided {}

    // @return просто список "адресов"
    // Обобщить дробилку с рекурсией! Она использовалась для разбора страницы Курсеры
    List<String> getListAddresses(String url, IErrorContainer err);//hided {}

    // Выделяем адреса без подключения, у нас есть текст, который доставерно
    //   отражает план ресурса
    List<String> extractListAddressesFromText(String text, IErrorContainer err);//hided {}
}  // strange_things.IExtracitorResourcePlan