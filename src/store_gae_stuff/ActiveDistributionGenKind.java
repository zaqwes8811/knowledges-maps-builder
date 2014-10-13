package store_gae_stuff;

import java.util.ArrayList;

import net.jcip.annotations.NotThreadSafe;


//import com.google.appengine.repackaged.org.apache.http.annotation.NotThreadSafe;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

import core.math.DistributionElement;
import core.math.GeneratorAnyDistribution;
import frozen.dal.accessors_text_file_storage.OutOfRangeOnAccess;

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
//
// Есть проблемы с сохранением
// http://code.google.com/p/objectify-appengine/wiki/IntroductionToObjectify#Embedded_Collections_and_Arrays
//
@NotThreadSafe
@Entity
public class ActiveDistributionGenKind //implements DistributionGen  // no way
{
  @Id
  public Long id;
  
  @Index
  String name;
  
  // Индексируется as embedded- это состояние генератора
  //@Embedded  // кажеться и так понимает
  // FIXME: какая лажа с порадком загрузки
  //@Load
  ArrayList<DistributionElement> distribution;  // порядок важен
  ArrayList<Integer> equalizeMask;  // same size as distr.
  
  // Можно и не индексировать - пока алгоритм одни
  // придется хранить отдельно
  // все-таки на объект накладываются ограничения!!
  // FIXME: вообще нужно быть внимательным, порядок иниц. может все сломать
  @Unindex  
  GeneratorAnyDistribution gen;

  @Unindex
  Integer codeAction;  // возможность подкл. алгоритма при создании

  public ImmutableList<DistributionElement> getDistribution() {
    return ImmutableList.copyOf(distribution);
  }

  // Любой список с числами
  // @throws: GeneratorDistributionException
  public static ActiveDistributionGenKind create(ArrayList<DistributionElement> distribution) {
    return new ActiveDistributionGenKind(distribution);
  }

  public Integer getPosition() {
    return Optional.of(gen).get().getPosition();
  }

  private void reloadGenerator(ArrayList<DistributionElement> distribution) {
    gen = GeneratorAnyDistribution.create(distribution);
  }

  private ActiveDistributionGenKind(ArrayList<DistributionElement> distribution) {
    this.distribution = distribution;
    reloadGenerator(distribution);
  }

  public void disablePoint(Integer idx) {
    // TODO: Проверка границ - это явно ошибка
    // TODO: Похоже нужна non-XG - транзакция. Кажется может возникнуть исключение.
    getElem(idx).enabled = false;
    reloadGenerator(distribution);
  }

  private DistributionElement getElem(Integer idx) {
    if (idx >= distribution.size() || idx < 0)
      throw new OutOfRangeOnAccess("On get element");  // сообщения безсмысленны, тип важнее

    // хотя наверное и так бросит
    return distribution.get(idx);
  }

  // DANGER:
  //   http://www.quizful.net/post/java-fields-initialization
  private ActiveDistributionGenKind() {
  	if (this.distribution == null)
  		throw new IllegalStateException();
  		
    // похоже при восстановлении вызывается он
    // TODO: момент похоже скользкий - а будет ли распределение инициализировано?
    reloadGenerator(distribution);
  }

  public void enablePoint(Integer idx) {
    getElem(idx).enabled = true;
    reloadGenerator(distribution);
  }

  ActiveDistributionGenKind cloneGenerator() {
    // Возможно еще придется что-то добавить
    return create(this.distribution);
  }
}
