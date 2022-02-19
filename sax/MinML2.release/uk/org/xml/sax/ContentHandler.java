package uk.org.xml.sax;

import java.io.Writer;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

public interface ContentHandler extends org.xml.sax.ContentHandler {
  Writer startDocument(final Writer writer) throws SAXException;
  Writer startElement(final String namespaceURI,
                           final String localName,
                           final String qName,
                           final Attributes atts,
                           final Writer writer)
        throws SAXException;
}
