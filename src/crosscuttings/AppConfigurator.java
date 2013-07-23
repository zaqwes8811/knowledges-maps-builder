package crosscuttings;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closer;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.*;
import java.util.Map;

@Immutable
@NotThreadSafe
public class AppConfigurator {
  private static final String ROOT_NAME = "App";
  private static final String APP_CFG_FULL_FILENAME = "conf/app.yaml";

  // Папка не обязательно в рабочей директории программы
  public static Optional<String> getPathToAppFolder()
      throws NoFoundConfFile, ConfFileIsCorrupted, RecordNoFound {

    String requestedPath = "App/Scriber/app_folder";
    Iterable<String> pieces = Splitter.on("/").split(requestedPath);

    Yaml yaml = new Yaml();
    try {
      Closer closer = Closer.create();
      try {
        InputStream input = closer.register(new FileInputStream(new File(APP_CFG_FULL_FILENAME)));

        // Обрабатываем путь
        Map topCfg = (Map)(Map<String, Object>)yaml.load(input);
        Map step = (Map)topCfg.get(ROOT_NAME);
        Map conf = (Map)step.get("Scriber");

        // Конечную точки заворачиваем в Optional
        String path = (String)conf.get("app_folder");
        return Optional.of(path);
      } catch (Throwable e) {
        throw closer.rethrow(e);
      } finally {
        closer.close();
      }
    } catch (FileNotFoundException e) {
      NoFoundConfFile c = new NoFoundConfFile(e);
      c.setFileName(APP_CFG_FULL_FILENAME);
      throw c;
    } catch (ScannerException e) {
      throw new ConfFileIsCorrupted(e);
    } catch (NullPointerException e) {
      throw new RecordNoFound(e, requestedPath);
    } catch (IOException e) {
      throw new CrosscuttingsException(e);
    }
  }

  // Получить список узлов
}
