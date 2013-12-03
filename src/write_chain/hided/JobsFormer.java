package write_chain.hided;

import com.google.common.base.Joiner;
import net.jcip.annotations.Immutable;
import through_functional.CrosscuttingsException;
import info_core_accessors.hided.ImmutableBaseCoursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Immutable
public class JobsFormer {
  public static final int IDX_NODE_NAME = 0;
  public static final int IDX_FILENAME = 1;

  public static List<List<String>> getJobs() throws CrosscuttingsException {

    List<List<String>> jobs = new ArrayList<List<String>>();
    //
    List<String> nodes = ImmutableBaseCoursor.getListNodes().get();
    for (String node: nodes) {
      String contentFilename = Joiner.on(GlobalConstants.PATH_SPLITTER)
        .join(
          ProcessorTargets.getPathToIndex(),
          GlobalConstants.CONTENT_FOLDER,
          node,
          GlobalConstants.CONTENT_FILENAME);
      jobs.add(Arrays.asList(node, contentFilename));
    }
    return jobs;
  }
}
