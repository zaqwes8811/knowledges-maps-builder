package common;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

final public class Util {
  private Util() {}
  static public void listToFile(List<String> list, String filename) throws IOException {
    Closer closer = Closer.create();
    try {
      closer.register(
        new BufferedWriter(
          new FileWriter(filename)))
        .write(Joiner.on("\n").join(list));
    } catch (Throwable e) {
      closer.rethrow(e);
    } finally {
      closer.close();
    }
  }

  static public Optional<String> fileToString(String filename) throws IOException {
    Closer closer = Closer.create();
    try {
      BufferedReader in = closer.register(new BufferedReader(new FileReader(filename)));
      String s;
      StringBuffer buffer = new StringBuffer();
      while ((s = in.readLine()) != null) buffer.append(s);
      return Optional.of(buffer.toString());
    } catch (Throwable e) {
      closer.rethrow(e);
    } finally {
      closer.close();
    }
    return Optional.absent();
  }

  // Файл по сути read-only т.е. это getter и поэтому результат будет константным.
  // Плохо то, что не ясно что именно произошло. Хотя толку от сообщения тоже.
  /*static public Optional<ImmutableList<String>> file2list(String filename) {
    List<String> result = new ArrayList<String>();
    try {
      Closer closer = Closer.create();
      try {
        // TODO(zaqwes): Может лучше считать разом, а потом разбить на части?
        BufferedReader in = closer.register(new BufferedReader(new FileReader(filename)));
        String s;
        while ((s = in.readLine()) != null) result.add(s);
        return Optional.of(ImmutableList.copyOf(result));
      } catch (Throwable e) {
        closer.rethrow(e);
      } finally {
        closer.close();
      }
    } catch (IOException e) {
      utils.print(e.getMessage());
      return Optional.absent();
    }
    return Optional.absent();
  }  */

  static public ImmutableList<String> fileToList(String filename) throws IOException {
    Closer closer = Closer.create();
    List<String> result = new ArrayList<String>();
    try {
      // TODO(zaqwes): Может лучше считать разом, а потом разбить на части?
      BufferedReader in = closer.register(new BufferedReader(new FileReader(filename)));
      String s;
      while ((s = in.readLine()) != null) result.add(s);
    } catch (Throwable e) {
      closer.rethrow(e);
    } finally {
      closer.close();
    }
    return ImmutableList.copyOf(result);
  }

  static public void print(Object msg) {
    if (System.console() == null) {
      System.out.println(msg);
    } else {
      System.console().writer().println(msg);
    }
  }

  static public void log(Object msg) {
    print(msg);
  }

  private final static  class DirFilter implements FilenameFilter {
    private Pattern pattern;
    public DirFilter(String regex) {
      pattern = Pattern.compile(regex);
    }
    public boolean accept(File dir, String name) {
      return  pattern.matcher(name).matches();
    }
  }
  public static List<String> getListNamesMetaFiles(String pathToNode, String regex) {
    File nodeContainer = new File(pathToNode);
    List<String> result = Arrays.asList(nodeContainer.list(new DirFilter(regex)));
    return result;
  }

  public static List<String> getListFilenamesByExtention(String path, String regex) {
    List<String> result = Arrays.asList(new File(path).list(new DirFilter(regex)));
    return result;
  }
}
