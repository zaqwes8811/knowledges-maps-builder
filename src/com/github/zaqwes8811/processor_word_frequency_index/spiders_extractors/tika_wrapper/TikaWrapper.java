package com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.tika_wrapper;

import java.io.*;
import java.net.URL;

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
  public void process(String filename) {
    try {
      ParseContext context = new ParseContext();
      Detector detector = new DefaultDetector();
      Parser parser = new AutoDetectParser(detector);
      context.set(Parser.class, parser);
      Metadata metadata = new Metadata();

      URL url;
      File file = new File(filename);
      String out_fname = "tika_tmp.txt";

      Closer closer = Closer.create();
      try {
        if (file.isFile()) {
          url = file.toURI().toURL();
        } else {
          url = new URL(filename);
        }

        //ProfilingWriter out = new ProfilingWriter();
        // Открываем потоки

        InputStream in = closer.register(TikaInputStream.get(url, metadata));
        OutputStream out = closer.register(new FileOutputStream(new File(out_fname)));
        ContentHandler handler = new BodyContentHandler(out);
        parser.parse(in, handler, metadata, context);

        //print(out.toString());
        // Неплохо бы здесь определить язык


      } catch (SAXException e) {
        e.printStackTrace();
      } catch (TikaException e) {
        e.printStackTrace();
      } catch (Throwable e) { // must catch Throwable
        throw closer.rethrow(e);
      } finally {
        closer.close();
      }

      Closer closerLangDetector = Closer.create();
      // Определяем язык - приходится читать файл еще раз, но по другому пока не ясно как
      //   язык определяется как литовский
      try {
        BufferedReader java_in = closerLangDetector.register(new BufferedReader(new FileReader(out_fname)));
        ProfilingWriter writer = new ProfilingWriter();
        while (true) {
          String s = java_in.readLine();
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