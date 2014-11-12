package gae_store_space;

import gae_store_space.queries.GAEStoreAccessManager;

import java.util.ArrayList;

import net.jcip.annotations.NotThreadSafe;
import pipeline.TextPipeline;
import pipeline.math.DistributionElement;
import pipeline.math.GeneratorAnyDistribution;

import com.google.common.base.Optional;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;

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
  @Id
  public Long id;
  
  public Long getId()  { return id; }
  
  @Index
  private String name;
  
  public String getName() { return name; }
  
  // Индексируется as embedded- это состояние генератора
  // FIXME: какая лажа с порядком загрузки
  @Serialize ArrayList<DistributionElement> d_;  // порядок важен
  @Serialize ArrayList<Integer> equalizeMask_;  // same size as distr.
  
  // Можно и не индексировать - пока алгоритм одни
  // придется хранить отдельно
  // все-таки на объект накладываются ограничения!!
  // FIXME: вообще нужно быть внимательным, порядок иниц. может все сломать
  // Наверное можно было бы сереализовать его, но из-за эквалайзинга,
  //   сохраняю исходные распределения отдельно
  @Ignore 
  Optional<GeneratorAnyDistribution> gen = Optional.absent();
  
  @Ignore
  GAEStoreAccessManager gae = new GAEStoreAccessManager();

  public ArrayList<DistributionElement> getCurrentDistribution() {
    return d_;
  }
   
  public Integer getActiveCount() {
  	Integer r = 0;
  	for (DistributionElement e: d_)
  		if (e.isActive()) {
  			//r += e.getImportance();  // по объему, но пока по штукам
  			r++;
  		}

  	return r;
  }

  // Любой список с числами
  // @throws: GeneratorDistributionException
  public static GeneratorKind create(ArrayList<DistributionElement> distribution) {
    return new GeneratorKind(distribution, TextPipeline.defaultGenName);
  }

  public Integer getPosition() {
    return gen.get().getPosition();
  }

  public void reloadGenerator(ArrayList<DistributionElement> d) {
  	d_ = d;
    gen = Optional.of(GeneratorAnyDistribution.create(d_));
  }

  private GeneratorKind(ArrayList<DistributionElement> distribution, String name) {
    this.d_ = distribution;
    this.name = name;
    
    reloadGenerator(distribution);
  }
  
  private void checkUnknown(Integer idx) {
  	checkIndex(idx);
  	if (!d_.get(idx).isUnknown())
  		throw new IllegalStateException();
  }

  public void disablePoint(Integer idx) {
  	checkIndex(idx);
  	checkUnknown(idx);
  	
    // TODO: Проверка границ - это явно ошибка
    // TODO: Похоже нужна non-XG - транзакция. Кажется может возникнуть исключение.
  	d_.get(idx).markKnown();
    reloadGenerator(d_);
  }

  private void checkIndex(Integer idx) {
    if (idx >= d_.size() || idx < 0)
      throw new IndexOutOfBoundsException("On get element");  // сообщения безсмысленны, тип важнее
  }

  public Integer getMaxImportance() {
  	return d_.get(0).getImportance();
  }
  
  private GeneratorKind() { }
  
  // похоже при восстановлении вызывается он
  // TODO: момент похоже скользкий - а будет ли распределение инициализировано?
  // DANGER:
  //   http://www.quizful.net/post/java-fields-initialization
  // Обязательно вызывать после восстановления из хранилища! конструктором по умолчанию воспользоваться нельзя!
  public void reload() {
  	if (d_ == null)
  		throw new IllegalStateException();
  		
    // похоже при восстановлении вызывается он
    // TODO: момент похоже скользкий - а будет ли распределение инициализировано?
    reloadGenerator(d_);
  }
}
