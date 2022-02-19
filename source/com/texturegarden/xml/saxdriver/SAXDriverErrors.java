package com.texturegarden.xml.saxdriver;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SAXDriverErrors {
  private ErrorHandler error_handler;

  public SAXDriverErrors(final ErrorHandler error_handler) {
    this.error_handler = error_handler;
  }

  public void setErrorHandler(final ErrorHandler handler) {
    this.error_handler = handler;
  }

  public ErrorHandler getErrorHandler() {
    return this.error_handler;
  }

  public void warning(final SAXParseException e) throws SAXException {}

  public void error(final SAXParseException e) throws SAXException {}

  public void fatalError(final SAXParseException e) throws SAXException {
    throw e;
  }

  void fatalError(final String msg, final int line_number, final int column_number) throws SAXException {
    error_handler.fatalError(new SAXParseException(msg, null, null, line_number, column_number));
  }
}
