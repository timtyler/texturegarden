package com.texturegarden.xml.saxdriver;

import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface SAXContentHandlerExtension extends org.xml.sax.ContentHandler {
  Writer startDocument(final Writer writer) throws SAXException;
  Writer startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts, final Writer writer) throws SAXException;
}
