package business.mapreduce;


import common.InnerReuse;
import hided.jobs_processors.JobsFormer;
import hided.jobs_processors.ProcessorTargets;
import crosscuttings.AppConstants;
import com.google.common.base.Joiner;
import com.google.common.io.Closer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentencesApp {

  public static void main(String [] args) {
    // Получаем работы
    List<List<String>> jobs = JobsFormer.getJobs();

    // Map Stage
    List<List> result_map_stage = new ArrayList<List>();
    for (List<String> job : jobs) {
      result_map_stage.add(SentencesMapper.map(job));
      //break;  // DEVELOP
    }

    // Shuffle Stage - сейчас фактически нет - один узел - один файл
    List<List> result_shuffle_stage  = result_map_stage;

    // Reduce Stage  - так же нет, т.к. - один узел - один файл
    Map<String, Map<String, String>> result_reduce_stage =
        new HashMap<String, Map<String, String>>();
    for (List task: result_shuffle_stage) {
      Map<String, String> one = SentencesReduce.reduce_sentences_level(task);

      String node_name = (String)task.get(NewMapper.IDX_NODE_NAME);
      result_reduce_stage.put(node_name, one);
    }


    // Save result
    try {
      Closer closer = Closer.create();
      try {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String resultInJson = gson.toJson(result_reduce_stage);
        // Write
        String path_for_save = Joiner.on(AppConstants.PATH_SPLITTER)
          .join(
            ProcessorTargets.getPathToIndex(),
            AppConstants.STATIC_NOTES_FILENAME);

        BufferedWriter out = closer.register(new BufferedWriter(new FileWriter(path_for_save)));
        out.write(resultInJson);
        InnerReuse.print(resultInJson);

        InnerReuse.print("Notes write to file : " + path_for_save);

      } catch (Throwable e) {
        closer.rethrow(e);
      } finally {
        closer.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    InnerReuse.print("Done. Sentences level processing.");
  }
}
