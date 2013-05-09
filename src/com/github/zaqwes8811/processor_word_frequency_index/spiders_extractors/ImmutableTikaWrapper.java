package com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.github.zaqwes8811.processor_word_frequency_index.AppConstants;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ImmutableAppUtils;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ImmutableProcessorTargets;
import com.google.common.io.Closer;
import com.google.gson.Gson;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.ProfilingWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

final public class ImmutableTikaWrapper {
  // Fields
  public static final String LANG_META = "lang";
  public static final String SOURCE_URL = "src_url";

  public static void extractAndSaveText(String inFileName, String pathToSrcFile, String nodeName) {
    try {
      // Настраиваем пути
      String fullOutFilenameNoExt =
          ImmutableProcessorTargets.getPathToIndex()+"/"+
          AppConstants.TMP_FOLDER+"/"+nodeName+'/'+inFileName;

      // имя файла старое! для сохр. нужно добавть *.ptxt or *.meta
      String outFileNameRaw = fullOutFilenameNoExt+".ptxt";
      String fullNameSrcFile = pathToSrcFile+'/'+inFileName;

      // TODO(zaqwes): Как установить читабельными русски буквы?
      ImmutableAppUtils.print("Process file: " + fullNameSrcFile);

      // Извлекаем содержимое файла
      Closer closer = Closer.create();
      try {
        File file = new File(fullNameSrcFile);
        URL url;
        if (file.isFile()) {
          url = file.toURI().toURL();
        } else {
          url = new URL("file:///"+fullNameSrcFile);
        }
        ParseContext context = new ParseContext();
        Detector detector = new DefaultDetector();
        Parser parser = new AutoDetectParser(detector);
        context.set(Parser.class, parser);
        Metadata metadata = new Metadata();
        InputStream in = closer.register(TikaInputStream.get(url, metadata));
        OutputStream out = closer.register(new FileOutputStream(new File(outFileNameRaw)));
        ContentHandler handler = new BodyContentHandler(out);
        parser.parse(in, handler, metadata, context);

      // Обработка ошибок
      } catch (SAXException e) {
        e.printStackTrace();
      } catch (TikaException e) {
        e.printStackTrace();
      } catch (Throwable e) { // must catch Throwable
        throw closer.rethrow(e);
      } finally {
        closer.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // {url: path_to_file, lang: ru}
  public static String extractAndSaveMetadata(String inFileName, String pathToSrcFile, String nodeName) {
    try {
      // Настраиваем пути
      // Настраиваем пути
      String fullOutFilenameNoExt =
        ImmutableProcessorTargets.getPathToIndex()+"/"+
        AppConstants.TMP_FOLDER+"/"+nodeName+'/'+inFileName;

      // имя файла старое! для сохр. нужно добавть *.ptxt or *.meta
      String outFileNameRaw = fullOutFilenameNoExt+".ptxt";
      String fullNameSrcFile = pathToSrcFile+'/'+inFileName;

      // Определяем язык - приходится читать файл еще раз, но по другому пока не ясно как
      //   язык определяется как литовский
      Closer postprocessCloser = Closer.create();
      try {
        BufferedReader inExtractedFile = postprocessCloser.register(
          new BufferedReader(new FileReader(outFileNameRaw)));
        ProfilingWriter writer = new ProfilingWriter();
        while (true) {
          String s = inExtractedFile.readLine();
          if (s == null)  break;
          writer.append(s);
        }
        LanguageIdentifier identifier = writer.getLanguage();
        String lang = identifier.getLanguage();

        // Заполняем метадынные файла
        String metaFileName = fullOutFilenameNoExt+".meta";
        Map<String, String> meta = new HashMap<String, String>();
        meta.put(LANG_META, lang);
        meta.put(SOURCE_URL, fullNameSrcFile);

        PrintWriter metaOut = postprocessCloser.register(
          new PrintWriter(
            new BufferedWriter(
              new FileWriter(metaFileName))));

        Gson gson = new Gson();
        metaOut.println(gson.toJson(meta));
        return lang;

      } catch (Throwable e) { // must catch Throwable
        throw postprocessCloser.rethrow(e);
      } finally {
        postprocessCloser.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
      return "error occure";
    }
  }
}