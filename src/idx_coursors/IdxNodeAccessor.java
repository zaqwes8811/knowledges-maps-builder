package idx_coursors;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import common.Util;
import common.annotations.Immutable;
import crosscuttings.AppConstants;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.annotation.concurrent.Immutable

// Вообще это геттер-сеттер к базе данных. Это синглетон - нет не синглетон.
//
// И он должен быть многопоточным. И еще желательно межпроцессозащищенным.
//
// @NoThreadSafe
//   Доступ к файлу. Хотя возможно просто будет занят при открытии.
//
// Для ошибок будет использоваться не Optional а исклюения
// TODO(zaqwes): ??
// TODO(zaqwes): TOTH: Узлов может быть несколько, поэтому создавать объект нужно. Методы доступа должны
//   не статические
//
// TODO(zaqwes): TOTH: Файл тоже ведь состояние объекта?
//
// TODO(zaqwes): А если это будет доступ к базе данных, а не файлам, то нужно ли здесь кэширование
//   тогда наверное можно переопределять методы доступа, а остальное оставлять
//
// Ввиду того, что аксессоры должны быть уникальными в системе, вне зависимости
//   от того, что они только для чтения или для чтения и записи, один класс
//   создает и те и другие, но контроллирует их общее создание объектов
public class IdxNodeAccessor {
  //private static Set<String> ids;  // Еще не подключено, но будет
  public final static String SORTED_IDX_FILENAME = "sorted.txt";

  public static ImmutableNodeMeansOfAccess of(String pathToNode)
      throws NodeNoFound, NodeAlreadyExist, CorruptNode {
    try {
      // TODO(zaqwes): Запрещать создавать объекты с одинаковыми именами узлов!
      // Несмотря на то, что класс внутренний и его конструктор закрыт, мы можем здесь его вызывать.
      return new ImmutableOnFiles(pathToNode);
    } catch (IOException e) {
      CorruptNode c = new CorruptNode();
      c.initCause(e);
      throw c;
    } catch (JsonSyntaxException e) {
      // Ошибка разбора JSON данных
      CorruptNode c = new CorruptNode();
      c.initCause(e);
      throw c;
    }
  }

  @Immutable
  private static class ImmutableOnFiles implements ImmutableNodeMeansOfAccess {
    private final String PATH_TO_NODE;

    // TODO(zaqwes): TOTH: Если поле финальное, то если его нет, то объекта тоже нет! Если использовать
    //   проверяемые исключения, то ну никак нельзя ссылку вынести за пределы try? Нет можно.
    private final ImmutableList<String> CASH_SORTED_NODE_IDX;  // Просто приравнять нельзя, нужно копировать!
    //private final ImmutableList<String> DESCRIPTIONS_NODE_IDX;
    //   Иначе будет хранится not-immutable ссылка!

    // TODO(zaqwes): TOTH: Путь проверять на существование в конструкторе, или потом.
    private ImmutableOnFiles(String pathToNode) throws NodeNoFound, IOException {
      // Есть ли путь к узлу
      if (!new File(pathToNode).exists()) {
        throw new NodeNoFound(pathToNode);
      }
      PATH_TO_NODE = pathToNode;

      //if (true) throw new NodeNoFound(pathToNode);  // Вот что будет с последующими константами
      //   по идее такой объект использовать нельзя, но при создании он, если была задана ссылка заране
      //   ссылка станет нулевой и при вызове unchecked, в этом случае пользоваться Optional.

      // Проверяем все ли файлы.

      // Загружаем кэши - здесь загружаем все!
      CASH_SORTED_NODE_IDX = ImmutableList.copyOf(
        readSortedIdx(Joiner.on(AppConstants.PATH_SPLITTER)
          .join(PATH_TO_NODE, SORTED_IDX_FILENAME)));

      // Проверяем чтобы размеры подидексов были равны размеру сортированного

    }

    // TODO(zaqwes): Скорее всего оверхед, т.к. как понимаю замок один на класс
    // TODO(zaqwes): Перенести код явно в конструктор!
    private synchronized ImmutableList<String> readSortedIdx(String filename) throws IOException {
        // !Критическая секция. Возможно, если кто-то другой его открыл, то он будет занят, но
        //   Возможно критическая секция упрощает доступ к файлу из разных потоков.
        String sortedIdxJson = Util.fileToString(filename).get();
        // !Критическая секция

        List<String> sortedIdxCash = (new Gson().fromJson(sortedIdxJson,
          new TypeToken<ArrayList<String>>() {}.getType()));
        return ImmutableList.copyOf(sortedIdxCash);
    }

    private ImmutableMap<String, List<Integer>> readSentencesKeys(String filename) throws IOException {
        // !Критическая секция. Возможно, если кто-то другой его открыл, то он будет занят, но
        //   Возможно критическая секция упрощает доступ к файлу из разных потоков.
        String sortedIdxJson = Util.fileToString(filename).get();
        // !Критическая секция

        Map<String, List<Integer>> sortedIdxCash = (new Gson().fromJson(sortedIdxJson,
          new TypeToken<HashMap<String, List<Integer>>>() {}.getType()));
        return ImmutableMap.copyOf(sortedIdxCash);
    }

  }


  // TODO(zaqwes): перенести в мутатор.
  // TODO(zaqwes): Добавить потокозащиту.
  // K - path to file
  // V - Объект годны для сереализации через Gson - базовая структура - Map, List.
  public static void saveListObjects(Map<String, Object> data) throws IOException {
    // Само сохранение. Вряд ли удасться выделить в метод. И свернуть в цикл.
    Closer closer = Closer.create();
    try {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      for (final String key: data.keySet()) {
        closer.register(new BufferedWriter(new FileWriter(key))).write(gson.toJson(data.get(key)));
      }
    } catch (Throwable e) {
      closer.rethrow(e);
    } finally {
      closer.close();
    }
  }
}
