package crosscuttings;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closer;
import common.Util;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.*;
import java.util.List;
import java.util.Map;

@Immutable
@NotThreadSafe
public class AppConfigurator<V> {
  private static final String ROOT_NAME = "App";
  private static final String APP_CFG_FULL_FILENAME = "conf/app.yaml";

  public static abstract class Reader<V> {
    // Конечную точки заворачиваем в Optional
    public abstract Optional<V> read(String key) throws IOException;
  }

  public static class ReaderDecorator<V> {
    public Optional<V> read(Reader<V> reader, String key)
      throws NoFoundConfFile, ConfFileIsCorrupted, RecordNoFound {
    try {
      return reader.read(key);
    } catch (FileNotFoundException e) {
      NoFoundConfFile c = new NoFoundConfFile(e);
      c.setFileName(APP_CFG_FULL_FILENAME);
      throw c;
    } catch (ScannerException e) {
      throw new ConfFileIsCorrupted(e);
    } catch (NullPointerException e) {
      throw new RecordNoFound(e, key);
    } catch (IOException e) {
      throw new CrosscuttingsException(e);
    }
  }


  // Папка не обязательно в рабочей директории программы
  public static Optional<String> getPathToAppFolder()
      throws NoFoundConfFile, ConfFileIsCorrupted, RecordNoFound {

    String requestedPath = "App/Scriber/app_folder";
    return new ReaderDecorator<String>().read(new Reader<String>() {
        @Override
        public Optional<String> read(String key) throws IOException {
          Closer closer = Closer.create();
          String path;
          try {
            InputStream input = closer.register(new FileInputStream(new File(APP_CFG_FULL_FILENAME)));

            // Обрабатываем путь
            Map topCfg = (Map)(Map<String, Object>) (new Yaml()).load(input);

            ImmutableList<String> pieces = ImmutableList.copyOf(Splitter.on("/").split(key));
            Map step = (Map)topCfg.get(pieces.get(0));
            step = (Map)step.get(pieces.get(1));

            // Конечную точки заворачиваем в Optional
            path = (String)step.get(pieces.get(2));
          } catch (Throwable e) {
            throw closer.rethrow(e);
          } finally {
            closer.close();
          }
          return Optional.of(path);
          //
        }
      }, requestedPath);
  }

  // Получить список узлов в виде потей к ним.
  //
  // Пути уникальные.
  public static Optional<ImmutableSet<String>> getRegisteredNodes()
    throws NoFoundConfFile, ConfFileIsCorrupted, RecordNoFound {
    String requestedPath = "App/registered nodes";
    ImmutableList<String> pieces = ImmutableList.copyOf(Splitter.on("/").split(requestedPath));
    Integer size = pieces.size();

    Yaml yaml = new Yaml();
    try {
      Closer closer = Closer.create();
      try {
        InputStream input = closer.register(new FileInputStream(new File(APP_CFG_FULL_FILENAME)));

        // Обрабатываем путь
        Map topCfg = (Map)(Map<String, Object>)yaml.load(input);

        Map step = (Map)topCfg.get(pieces.get(0));
        List<String> nodes = (List)step.get(pieces.get(1));
        return Optional.of(ImmutableSet.copyOf(nodes));
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
}
