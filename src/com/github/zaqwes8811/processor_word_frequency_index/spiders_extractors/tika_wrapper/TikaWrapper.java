package com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.tika_wrapper;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.ContentHandler;

public class TikaWrapper {
  public void process(String filename) {
    ParseContext context = new ParseContext();
    Detector detector = new DefaultDetector();
    Parser parser = new AutoDetectParser(detector);
    context.set(Parser.class, parser);
    OutputStream outputstream = new ByteArrayOutputStream();
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
    input.close();

    //Get the text into a String object
    String extractedText = outputstream.toString();
    //Do whatever you want with this String object.
    System.out.println(extractedText);
  }
}