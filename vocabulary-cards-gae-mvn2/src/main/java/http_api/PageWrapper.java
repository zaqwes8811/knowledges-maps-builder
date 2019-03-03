package http_api;

import backend.KindPage;
import com.google.common.base.Optional;
import backend.math.DistributionElement;

import java.util.ArrayList;

/**
 * Created by zaqwes on 11/27/2014.
 */
public interface PageWrapper
{
  public KindPage getRawPage();

  public ArrayList<Integer> getLengthsSentences();

  public ArrayList<DistributionElement> getImportanceDistribution();

  public ArrayList<DistributionElement> buildImportanceDistribution();

  public void disablePoint(ValuePath p);

  public void atomicDeleteRawPage();

  public Optional<ValueWordData> getWordData();
}
