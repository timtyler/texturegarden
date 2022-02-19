import java.io.IOException;

import org.xml.sax.*;

import uk.co.wilson.xml.MinML2;
//import uk.co.wilson.xml.SysBuild;

import java.util.Vector;
import java.util.Date;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MinML2Test extends MinML2 {
  public MinML2Test() {
//    new SysBuild();
///*
    try {
      parse(new InputStreamReader(new BufferedInputStream(new java.io.FileInputStream("example.xml"), 1024)));
      //parse(new InputStreamReader(new BufferedInputStream(System.in, 1024)));
    }
    catch (final IOException e) {
      System.out.println("IOException: " + e);
      e.printStackTrace();
    }
    catch (final SAXException e) {
      System.out.println("SAXException: " + e);
      e.printStackTrace();
    }
    catch (final Throwable e) {
      System.out.println("Other Exception: " + e);
      e.printStackTrace();
    }
//*/
  }

  public void startDocument() {
    System.out.println("Start of Document");
  }

  public void endDocument() {
    System.out.println("End of Document");
  }

  public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    System.out.println("Start prefix mapping: prefix = \"" + prefix + "\" uri = \"" + uri + "\"");
  }

  public void endPrefixMapping(final String prefix) throws SAXException {
    System.out.println("End prefix mapping: prefix = \"" + prefix + "\"");
  }

  public void startElement(final String namespaceURI,
                           final String localName,
                           final String qName,
                           final Attributes atts)
    throws SAXException
  {
    System.out.println("Start of Element: qName = \"" + qName + "\"");
    System.out.println("Start of Element: localName = \"" + localName + "\"");
    System.out.println("Start of Element: namespaceURI = \"" + namespaceURI + "\"");
    System.out.println("Start of Attributes");

    for (int i = 0; i < atts.getLength(); i++)
      System.out.println("qName: \"" + atts.getQName(i)
                         + "\" localName: \"" + atts.getLocalName(i)
                         + "\" uri: \"" + atts.getURI(i)
                         + "\" Type: " + atts.getType(i)
                         + " Value: \"" + atts.getValue(i) + "\"");

    System.out.println("End of Attributes");
  }

  public void endElement(final String namespaceURI,
                         final String localName,
                         final String qName)
    throws SAXException
  {
    System.out.println("End of Element: qName = \"" + qName + "\"");
    System.out.println("End of Element: localName = \"" + localName + "\"");
    System.out.println("End of Element: namespaceURI = \"" + namespaceURI + "\"");
  }

  public void characters (char ch[], int start, int length) {
    System.out.println("Characters: \"" + new String(ch, start, length) + "\"");
  }

  public void fatalError (SAXParseException e) throws SAXException {
    System.out.println("Error: " + e);
    throw e;
  }

  public static void main (final String[] args) {
    new MinML2Test();
  }
}
