package uk.org.xml.sax;

public interface XMLReader extends org.xml.sax.XMLReader {
  String getNamespace(String tag);
  String getDefaultNamespace();
  void setContentHandler(ContentHandler handler);
}