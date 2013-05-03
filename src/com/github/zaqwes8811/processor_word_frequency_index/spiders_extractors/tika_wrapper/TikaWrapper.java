package com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.tika_wrapper;

import java.io.*;
import java.net.URL;

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

public class TikaWrapper {
  public static void print(Object msg) {
    System.out.println(msg);
  }



  public void process(String inFileName, String nodeName, String pathToAppFolder) {
    try {
      // Настраиваем пути
      String pathToNode = pathToAppFolder+"/"+nodeName;
      print(pathToNode);
      // outFileNameRaw = getTmpFileName(
      //    inFileName, pathToNode);  // имя файла старое! для сохр. нужно добавть *.ptxt or *.meta
      //return;
      String outFileNameRaw = "tmp.txt";
      // Извлекаем содержимое файла
      URL url;
      File file = new File(inFileName);
      Closer closer = Closer.create();
      try {
        if (file.isFile()) {
          url = file.toURI().toURL();
        } else {
          url = new URL(inFileName);
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