package com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.tika_wrapper;

import java.io.*;
import java.net.URL;

import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.AppConfigurer;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ProcessorTargets;
import com.google.common.base.Splitter;
import com.google.common.io.Closer;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.ProfilingHandler;
import org.apache.tika.language.ProfilingWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

final public class ImmutableTikaWrapper {
  public static void print(Object msg) {
    System.out.println(msg);
  }

  // Fields
  final private String pathToAppFolder_;
  final private String indexName_;

  public ImmutableTikaWrapper(
      String pathToAppFolder,
      String indexName)
    {
    pathToAppFolder_ = pathToAppFolder;
    indexName_ = indexName;
  }

  public final void process(
      String inFileName,
      String pathToSrcFile,
      String nodeName) {
    try {

      // Настраиваем пути
      String pathToNode = pathToAppFolder_+"/"+nodeName;
      // имя файла старое! для сохр. нужно добавть *.ptxt or *.meta
      String outFileNameRaw = pathToNode+'/'+inFileName+".ptxt";
      print(outFileNameRaw);

      // Извлекаем содержимое файла
      Closer closer = Closer.create();
      try {
        String fullNameSrcFile = pathToSrcFile+'/'+inFileName;
        File file = new File(fullNameSrcFile);
        URL url;
        if (file.isFile()) {
          url = file.toURI().toURL();
        } else {
          url = new URL(fullNameSrcFile);
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

      // Определяем язык - приходится читать файл еще раз, но по другому пока не ясно как
      //   язык определяется как литовский
      Closer closerLangDetector = Closer.create();
      try {
        BufferedReader inExtractedFile = closerLangDetector.register(
            new BufferedReader(new FileReader(outFileNameRaw)));
        ProfilingWriter writer = new ProfilingWriter();
        while (true) {
          String s = inExtractedFile.readLine();
          if (s == null)  break;
          writer.append(s);
        }
        LanguageIdentifier identifier = writer.getLanguage();
        String lang = identifier.getLanguage();
        print(lang);

        // Заполняем метадынные файла
        String metaFileName = inFileName+".meta";

      } catch (Throwable e) { // must catch Throwable
        throw closerLangDetector.rethrow(e);
      } finally {
        closerLangDetector.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}