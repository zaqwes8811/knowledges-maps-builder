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
import java.util.*;

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
  public final static String FILENAME_SENTENCES_IDX = "ptrs-to-sentences.txt";
  public final static String FILENAME_DESCRIPTIONS_IDX = "descriptions.txt";
  public final static String FILENAME_FREQ_IDX = "frequences.txt";
  public final static String CONTENT_FILENAME = "content.txt";

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
      //   Иначе будет хранится not-immutable ссылка!

    // А вообще словарь лучше использовать как фильтр? А все данные генерировать
    //   из индекса? Кажется так правильнее.

    private final ImmutableMap<String, List<Integer>> CASH_DESCRIPTIONS_IDX;
    private final ImmutableMap<String, Integer> CASH_FREQUENCY_IDX;

    // ! Внутри просто List! можно оставить так, но доступа на запись быть не должно!
    // Доступ копирует список в ImmutableList и его возвращает.
    private final ImmutableMap<String, List<Integer>> CASH_SENTENCES_KEYS_IDX;

    // Сам контент! Здесь жестко задан, но это неправильно. На этапе тестирование идеи сойдет.
    private final ImmutableList<String> CASH_CONTENT;

    // TODO(zaqwes): TOTH: Путь проверять на существование в конструкторе, или потом.
    // Кажется вышел довольно большой
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
      String jsonTmp = Util.fileToString(
          Joiner.on(AppConstants.PATH_SPLITTER).join(PATH_TO_NODE, FILENAME_FREQ_IDX)).get();
      Map<String, Integer> freqIdx = (new Gson().fromJson(jsonTmp,
          new TypeToken<HashMap<String, Integer>>() {}.getType()));
      CASH_FREQUENCY_IDX = ImmutableMap.copyOf(freqIdx);


      // А вообще он должен хранится отсортированными? Может хранить просто список слов
      //   и если нужно отсортировать потом?
      // Создаем сортированный список
      List<Map.Entry<String, Integer>> list = new LinkedList(CASH_FREQUENCY_IDX.entrySet());

      // sort list based on comparator
      Collections.sort(list, new Comparator() {
        public int compare(Object o1, Object o2) {
          return ((Comparable) ((Map.Entry) (o2)).getValue())
            .compareTo(((Map.Entry) (o1)).getValue());
        }
      });

      // Сортированный список
      List<String> sortedIdxCash = new ArrayList<String>();
      for (Map.Entry<String, Integer> entry : list) {
        sortedIdxCash.add(entry.getKey());
      }

      CASH_SORTED_NODE_IDX = ImmutableList.copyOf(sortedIdxCash);

      //
      jsonTmp = Util.fileToString(
        Joiner.on(AppConstants.PATH_SPLITTER).join(PATH_TO_NODE, FILENAME_SENTENCES_IDX)).get();
      Map<String, List<Integer>> tmp = (new Gson().fromJson(jsonTmp,
        new TypeToken<HashMap<String, List<Integer>>>() {}.getType()));
      CASH_SENTENCES_KEYS_IDX = ImmutableMap.copyOf(tmp);

      jsonTmp = Util.fileToString(
        Joiner.on(AppConstants.PATH_SPLITTER).join(PATH_TO_NODE, FILENAME_DESCRIPTIONS_IDX)).get();
      tmp = (new Gson().fromJson(jsonTmp,
        new TypeToken<HashMap<String, List<Integer>>>() {}.getType()));
      CASH_DESCRIPTIONS_IDX = ImmutableMap.copyOf(tmp);

      // Получить список единиц контанта узла
      CASH_CONTENT = ImmutableList.copyOf(Util.fileToList(
          Joiner.on(AppConstants.PATH_SPLITTER).join(PATH_TO_NODE, CONTENT_FILENAME)));


      // Проверяем чтобы размеры подидексов были равны размеру сортированного
      //   А нужно ли? Просто проверять вхождение перед вызовом, если нет, возвращать пустоту.
    }

    // В принципе генерировать исключений не должно.
    @Override
    public ImmutableList<Integer> getDistribution() {
      //List<Integer> tmp = new ArrayList<Integer>();
      //for (final String: CASH_SORTED_NODE_IDX) {

      //}
      return null;
    }
  }



  /*

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
  } */
}
