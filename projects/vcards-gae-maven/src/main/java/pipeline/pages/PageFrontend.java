package pipeline.pages;

import com.google.common.base.Optional;
import gae_data_source_layer.PageKind;
import pipeline.math.DistributionElement;
import service_layer.protocols.PathValue;
import service_layer.protocols.WordDataValue;

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
