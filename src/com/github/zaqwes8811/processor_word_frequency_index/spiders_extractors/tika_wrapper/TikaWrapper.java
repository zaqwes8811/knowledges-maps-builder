package com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.tika_wrapper;

import java.io.*;
import java.net.URL;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaWrapper {
  public void process(String filename) {
    try {
      ParseContext context = new ParseContext();
      Detector detector = new DefaultDetector();
      Parser parser = new AutoDetectParser(detector);
      context.set(Parser.class, parser);
      OutputStream outputstream = new ByteArrayOutputStream(); // TODO(zaqwes): bad!!! No closed!!
      Metadata metadata = new Metadata();

      URL url;
      File file = new File(filename);
      if (file.isFile()) {
        url = file.toURI().toURL();
      } else {
        url = new URL(filename);
      }
      InputStream input = TikaInputStream.get(url, metadata);
      ContentHandler handler = new BodyContentHandler(outputstream);
      parser.parse(input, handler, metadata, context);

      // TODO(zaqwes): bad!!!
      input.close();

      // Get the text into a String object
      String extractedText = outputstream.toString();

      // Пишем в файл. Формать должен быть UTF-8
      System.out.println(extractedText);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (TikaException e) {
      e.printStackTrace();
    }
  }
}