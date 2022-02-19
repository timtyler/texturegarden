package com.texturegarden.xml.saxdriver;

public interface SAXXMLReaderExtension extends org.xml.sax.XMLReader {
  //String getNamespace(String tag);
  //String getDefaultNamespace();
  void setContentHandler(SAXContentHandlerExtension handler);
}
