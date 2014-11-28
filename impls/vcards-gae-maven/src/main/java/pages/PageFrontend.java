package pages;

import com.google.common.base.Optional;
import kinds.PageKind;
import pipeline.math.DistributionElement;
import web_relays.protocols.PathValue;
import web_relays.protocols.WordDataValue;

import java.util.ArrayList;

/**
 * Created by zaqwes on 11/27/2014.
 */
public interface PageFrontend {
  public PageKind getRawPage();

  public ArrayList<Integer> getLengthsSentences();

  public ArrayList<DistributionElement> getImportanceDistribution();

  public ArrayList<DistributionElement> buildImportanceDistribution();

  public void disablePoint(PathValue p);

  public void atomicDeleteRawPage();

  public Optional<WordDataValue> getWordData();
}
