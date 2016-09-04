import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.google.common.base.Splitter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.*;

public class SentenceTokenizerTest //extends AbstractBenchmark 
{
	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();
	
  @Test
  public void testDevelop() {
    String sent = "Прочие, связанные с аудиторской деятельностью услуги представляют собой:  "+
      "- постановка, восстановление и ведение бухгалтерского учета;  "+
      "- составление отчетности  "+
      "- консультирование (в том числе налоговое, управленческое и правовое   консультирование);  "+
      "- анализ финансово-хозяйственной деятельности;  "+
      "9) автоматизация бухгалтерского учета, внедрение информационных   технологий;  "+
      "- разработка и анализ инвестиционных проектов;  - оценка стоимости имущества;     4      "+
      "- проведение научно-исследовательских работ;  "+
      "- и другие услуги, связанные с аудиторской деятельностью.  ";
    Iterable<String> result = Splitter.onPattern("(-|[0-9])?[:;]")
      .trimResults()
      .omitEmptyStrings()
      .split(sent);

    for (String item: result) {
      assertFalse(item.isEmpty());
    }
  }
}
