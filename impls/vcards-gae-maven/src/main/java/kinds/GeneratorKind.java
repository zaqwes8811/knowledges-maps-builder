package kinds;

import java.util.ArrayList;

import com.googlecode.objectify.LoadResult;
import instances.AppInstance;
import net.jcip.annotations.NotThreadSafe;
import pipeline.math.DistributionElement;
import pipeline.math.GeneratorAnyDistribution;

import com.google.common.base.Optional;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;

import static gae_store_space.OfyService.ofy;

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
public class GeneratorKind
{
  @Id public Long id;
  @Index private String name;
  @Serialize private ArrayList<DistributionElement> distribution;  // порядок элементов важен
  //@Serialize private ArrayList<Integer> equalizeMask_;  // same size as distribution

  @Ignore private Optional<GeneratorAnyDistribution> generator = Optional.absent();

  public Long getId()  { return id; }
  public String getName() { return name; }

  public ArrayList<DistributionElement> getCurrentDistribution() {
    return distribution;
  }
   
  public Integer getActiveCount() {
  	Integer r = 0;
    //r += e.getImportance();  // по объему, но пока по штукам
  	for (DistributionElement e: distribution)
  		if (e.isActive())	r++;

  	return r;
  }

  // Любой список с числами
  // @throws: GeneratorDistributionException
  public static GeneratorKind create(ArrayList<DistributionElement> distribution) {
    return new GeneratorKind(distribution, AppInstance.defaultGeneratorName);
  }

  public Integer getPosition() {
    return generator.get().getPosition();
  }

  public void resetDistribution(ArrayList<DistributionElement> d) {
  	distribution = d;
    recreateGenerator(distribution);
  }

  private void recreateGenerator(ArrayList<DistributionElement> d) {
    generator = Optional.of(GeneratorAnyDistribution.create(d));
  }

  private GeneratorKind(ArrayList<DistributionElement> distribution, String name) {
    this.distribution = distribution;
    this.name = name;
    
    resetDistribution(distribution);
  }
  
  private void checkUnknown(Integer idx) {
  	checkIndex(idx);
  	if (!distribution.get(idx).isUnknown())
  		throw new IllegalStateException();
  }

  // TODO: Проверка границ - это явно ошибка
  // TODO: Похоже нужна non-XG - транзакция. Кажется может возникнуть исключение.
  public void disablePoint(Integer idx) {
  	checkIndex(idx);
  	checkUnknown(idx);

  	distribution.get(idx).markKnown();
    resetDistribution(distribution);
  }

  private void checkIndex(Integer idx) {
    if (idx >= distribution.size() || idx < 0)
      throw new IndexOutOfBoundsException();  // сообщения безсмысленны, тип важнее
  }

  public Integer getMaxImportance() {
  	return distribution.get(0).getImportance();
  }

  // похоже при восстановлении вызывается он
  // TODO: момент похоже скользкий - а будет ли распределение инициализировано?
  // DANGER:
  //   http://www.quizful.net/post/java-fields-initialization
  // Обязательно вызывать после восстановления из хранилища! конструктором по умолчанию воспользоваться нельзя!
  private GeneratorKind() { }

  // похоже при восстановлении вызывается он
  // TODO: момент похоже скользкий - а будет ли распределение инициализировано?
  public static Optional<GeneratorKind> restoreById(Long id) {
    LoadResult<GeneratorKind> q = ofy().load().type(GeneratorKind.class).id(id);
    Optional<GeneratorKind> g = Optional.fromNullable(q.now());
    if (g.isPresent()) {
      if (!Optional.fromNullable(g.get().distribution).isPresent())
        throw new IllegalStateException();
      g.get().resetDistribution(g.get().distribution);
    }

    return g;
  }
}
