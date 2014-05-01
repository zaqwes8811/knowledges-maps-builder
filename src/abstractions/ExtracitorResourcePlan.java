package abstractions;// Import
import java.util.List;


public interface /*class*/ ExtracitorResourcePlan /* implement abstractions.ExtracitorResourcePlan */ {
    /** Как бы навигационная часть */
    // Спорный вызов. Как он будет использоваться при множественном наследовании?
    // Захватывает точку входа в ресурс (например, файл базы данных)
    ErrorContainer captureRoot(String urlRoot);//hided {}
    // Error _freeRoot();  // должен вызывать в деструкторе

    // @param url информация о том, как можно добраться до вершины ресурса
    // @return карта, по которой можно передвигаться
    GraphToText getPlan(String url, ErrorContainer err);//hided {}

    GraphToText getPlanFromText(String url, ErrorContainer err);//hided {}

    // @return просто список "адресов"
    // Обобщить дробилку с рекурсией! Она использовалась для разбора страницы Курсеры
    List<String> getListAddresses(String url, ErrorContainer err);//hided {}

    // Выделяем адреса без подключения, у нас есть текст, который доставерно
    //   отражает план ресурса
    List<String> extractListAddressesFromText(String text, ErrorContainer err);//hided {}
}  // abstractions.ExtracitorResourcePlan