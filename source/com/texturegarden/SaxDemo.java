package com.texturegarden;

import java.io.FileReader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.texturegarden.utilities.Log;

public class SaxDemo extends DefaultHandler {
  public static void main(String arg[]) throws Exception {

    String[] args = { "demo.xml" };

    //XMLReader xr = new org.apache.crimson.parser.XMLReaderImpl();
    //XMLReader xr = new sd.Sax2Driver();
    //XMLReader xr = new com.texturegarden.xml.saxdriver.SAXDriver();
    //XMLReader xr = new com.texturegarden.xml.driver.SAXDriverOriginal();
    XMLReader xr = new com.texturegarden.xml.driver.Driver();

    SaxDemo handler = new SaxDemo();
    xr.setContentHandler(handler);
    xr.setErrorHandler(handler);

    // Parse each file provided on the
    // command line.
    for (int i = 0; i < args.length; i++) {
      FileReader r = new FileReader(args[i]);
      //Log.log("Start P" + i);
      xr.parse(new InputSource(r));
      //Log.log("End P");
    }
  }

  public SaxDemo() {
    super();
  }

  ////////////////////////////////////////////////////////////////////
  // Event handlers.
  ////////////////////////////////////////////////////////////////////

  public void startDocument() {
    Log.log("Start document");
  }

  public void endDocument() {
    Log.log("End document");
  }

  public void startElement(String uri, String name, String qName, Attributes atts) {
    if ("".equals(uri)) {
      Log.log("Start element: " + qName);
    } else {
      Log.log("Start element: {" + uri + "}" + name);
    }

    int n = atts.getLength();
    if (n > 0) {
      for (int i = 0; i < n; i++) {
        Log.log("Attribute-name: " + atts.getLocalName(i));
        Log.log("Attribute-value: " + atts.getValue(i));
      }
    }
  }

  public void endElement(String uri, String name, String qName) {
    if ("".equals(uri)) {
      Log.log("End element: " + qName);
    } else {
      Log.log("End element:   {" + uri + "}" + name);
    }
  }

  public void ignorableWhitespace(char[] ch, int start, int length) {
    Log.put("ignorableWhitespace:");
    characters(ch, start, length);
  }

  public void skippedEntity(String name) {
    Log.put("skippedEntity:" + name);
  }

  public void characters(char ch[], int start, int length) {
    Log.put("Characters:    \"");
    for (int i = start; i < start + length; i++) {
      switch (ch[i]) {
        case '\\' :
          Log.put("\\\\");
          break;
        case '"' :
          Log.put("\\\"");
          break;
        case '\n' :
          Log.put("\\n");
          break;
        case '\r' :
          Log.put("\\r");
          break;
        case '\t' :
          Log.put("\\t");
          break;
        default :
          Log.put("" + ch[i]);
          break;
      }
    }
    Log.put("\"\n");
  }
}
