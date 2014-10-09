package store_gae_stuff;

import business.math.DistributionElement;
import business.math.GeneratorAnyDistribution;
import com.google.appengine.repackaged.org.apache.http.annotation.NotThreadSafe;
import com.google.common.base.Optional;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

import java.util.ArrayList;

// About:
//   Класс способен генерировать последовательности любого дискретного распределения
//   Возвращает индекс массива исходного распределения.
//
// TODO: Как быть с распределением? Оно будет динамическим!
// Вариант - читать через кеш, он все равно будет - медленно, очень, особенно при горячем старте - кеш пуст.
//   лучше так кеш не использовать, а использовать для чтения.
// Вариант 2 - сохранять распределение в хранилище. Дублирование причем в 3 местах! Хуже всего что в генераторе,
//   но лучше генератор сделать внешним, хотяяяя... нет.
//
// TODO: Кто управляет временем жизни в storage?
// TODO: Читать однажды, а так сохранять в хранилище. Проблема в ширине кеша. Так же он заполнятся поштучно!
// TODO: Для чтения пойдет, а так не хотелось бы. Хотя на этапе может ширина известна на этапе формирования?
// TODO: Как изначально инициализировать. При формировании таблицы, например.
// TODO: Для горячего старта.
// TODO: Если мы меняем поля, то нужно сохранятся страницу в базу, сейчас персистентность управляется извне!
//   думаю она и должна оставаться управляемой извне.
@NotThreadSafe
@Entity
public class DistributionGenImpl implements DistributionGen {
  @Id
  Long id;

  // Можно и не индексировать - пока алгоритм одни
  @Unindex Optional<GeneratorAnyDistribution> gen = Optional.absent();

  // Индексируется!!
  ArrayList<DistributionElement> distribution;

  // Любой список с числами
  // @throws: GeneratorDistributionException
  public static DistributionGenImpl create(ArrayList<DistributionElement> distribution) {
    return new DistributionGenImpl(distribution);
  }

  @Override
  public Integer getPosition() {
    return gen.get().getPosition();
  }

  @Override
  public void reloadGenerator(ArrayList<DistributionElement> distribution) {
    gen = Optional.of(GeneratorAnyDistribution.create(distribution));
  }

  private DistributionGenImpl(ArrayList<DistributionElement> distribution) {
    gen = Optional.of(GeneratorAnyDistribution.create(distribution));
  }

  @Override
  public void disablePoint(Integer idx) {
    // TODO: Проверка границ - это явно ошибка

    // TODO: Похоже нужна non-XG - транзакция. Кажется может возникнуть исключение.
  }

  @Override
  public void enablePoint(Integer idx) {

  }
}
