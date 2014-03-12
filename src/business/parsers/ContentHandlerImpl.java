package business.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 http://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html
 */
public class ContentHandlerImpl extends DefaultHandler {
  private List<String> sink_;  // Injected

  public ContentHandlerImpl(List<String> sink) {
    sink_ = sink;
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void startDocument() throws SAXException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void endDocument() throws SAXException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    sink_.add(new String(ch));
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void skippedEntity(String name) throws SAXException {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
