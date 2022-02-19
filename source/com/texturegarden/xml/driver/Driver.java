package com.texturegarden.xml.driver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

public class Driver implements XMLReaderExtension, ContentHandlerExtension,
    Locator, ErrorHandler, Constants {
  private static final String LNAM = "lnam";
  private ContentHandlerExtension content_handler_extension = this;

  private org.xml.sax.ContentHandler content_handler = this;

  private ErrorHandler error_handler = this;

  private final Stack stack = new Stack();

  final ParserState state = new ParserState();

  AttributesBasic attributes_basic;
  Attributes attributes;
  BufferForXML buffer;

  public void parse(final Reader in) throws SAXException, IOException {
    attributes_basic = new AttributesBasic();
    attributes = new AttributesExtended(attributes_basic);
    buffer = new BufferForXML(in, this);

    char c = 0;

    try {
      this.content_handler.startDocument();
      while (true) {
        c = buffer.getNextChar();
        if (c < 0) {
          return;
        }

        // start dealing with comments...
        try {
          if (state.in_element) {
            if (state.comment_stage != StateComment.OUTSIDE) {
              if (c == '-') {
                dealWithMinus();
              } else {
                dealWithNonMinus();
              }
            }
          }

          if (state.comment_stage == StateComment.OUTSIDE) {
            if (state.in_element) {
              if (state.element_stage == StateElement.WAITING_FOR_VALUE) {
                if (c == '"') {
                  dealWithQuotes2();
                } else {
                  state.name += (char) c;
                }
              } else {
                switch (c) {
                  case '!':
                    dealWithExclamation();
                    break;
                  case '<':
                    dealWithLessThan();
                    break;
                  case '>':
                    dealWithGreaterThan();
                    break;
                  case '/':
                    dealWithSlash();
                    break;
                  case '"':
                    dealWithQuotes();
                    break;
                  default:
                    if (StringUtilitiesForSAXDriver.isXMLElementPart((char) c)) {
                      dealWithXMLElementCharacter(c);
                    } else {
                      ParserStateUpdater.endOfIdentifier(state);
                    }
                    break;
                }
              }
            } else {
              if (c == '<') {
                flushCharacters(state);
                ParserStateReset.parserStateReset(state);
              } else {
                state.output_stream.add((char) c);
              }
            }
          }

          // end of comment state...
          if (state.in_element) {
            if (state.comment_stage != StateComment.OUTSIDE) {
              if (c == '>') {
                dealWithGreaterThan2();
              }
            }
          }
        } finally {
          state.position.x++;

          if (c == '\n') {
            dealWithNewLine();
          }
          state.offset++;
        }
      }
    } catch (final IOException e) {
      this.error_handler.fatalError(new SAXParseException(e.toString(), null,
          null, state.position.y, state.position.x, e));
    } finally {
      flushCharacters(state);
      this.content_handler.endDocument();
      this.content_handler = this;
      this.content_handler_extension = this;
      this.error_handler = this;
      this.stack.removeAllElements();
    }
  }

  private void dealWithNewLine() {
    state.position.y++;
    state.position.x = 0;
  }

  private void dealWithNonMinus() {
    if (state.comment_stage == StateComment.MINUS_1) {
        dealWithExclamation();
    } else if (state.comment_stage == StateComment.MINUS_3) {
      state.comment_stage = StateComment.MINUS_2;
    }
  }

  private void dealWithGreaterThan2() {
    if (state.comment_stage == StateComment.EXCLAMATION) {
      state.comment_stage = StateComment.OUTSIDE;
      state.in_element = false;
    }
  }

  private void dealWithXMLElementCharacter(int c) {
    if ((state.element_stage == StateElement.WAITING_FOR_ELEMENT) ||
     (state.element_stage == StateElement.WAITING_FOR_ATTRIBUTE)) {
      state.name += (char) c;
    }
  }

  private void dealWithQuotes2() {
    state.element_stage = StateElement.WAITING_FOR_ATTRIBUTE;
    attributes_basic.attribute_local_names.add(state.name_attribute);
    attributes_basic.attribute_values.add(state.name);
    state.name_attribute = "";
    state.name_value = "";
  }

  private void dealWithMinus() {
    moveOnToNextCommentStage();
  }

  private void dealWithLessThan() throws SAXException {
    if (state.in_element) {
      reportError(ConstantsErrorMessages.LT_INSIDE_LT);
    }
  }

  private void dealWithExclamation() {
    state.comment_stage = StateComment.EXCLAMATION;
  }

  private void dealWithSlash() {
    if ("".equals(state.name_element)) {
      state.closing_element = true;
    } else {
      state.element_singular = true;
    }
  }

  private void dealWithGreaterThan() throws SAXException {
    state.in_element = false;
    ParserStateUpdater.endOfIdentifier(state);
    if (state.closing_element) {
      this.content_handler.endElement("", LNAM, state.name_element);
      final String popped = (String) stack.pop();
      if (!popped.equals(state.name_element)) {
        String error = ConstantsErrorMessages.MISMATCHED + state.name_element;
        error += ConstantsErrorMessages.DOES_NOT_MATCH + popped;
        reportError(error);
      }
    } else {
      this.content_handler.startElement("", LNAM, state.name_element, attributes);
      if (!state.element_singular) {
        stack.push(state.name_element);
      }

      attributes_basic.removeAllElements();
    }
  }

  private void dealWithQuotes() throws SAXException {
    if (state.element_stage == StateElement.WAITING_FOR_VALUE_QUOTE) {
      state.element_stage = StateElement.WAITING_FOR_VALUE;
    } else {
      if (state.element_stage == StateElement.WAITING_FOR_VALUE) {
        dealWithQuotes2();
      } else {
        reportError(ConstantsErrorMessages.MISPLACED_QUOTE);
      }
    }
  }

  private void moveOnToNextCommentStage() {
    if (state.comment_stage == StateComment.EXCLAMATION) {
      state.comment_stage = StateComment.MINUS_1;
    } else if (state.comment_stage == StateComment.MINUS_1) {
      state.comment_stage = StateComment.MINUS_2;
    } else if (state.comment_stage == StateComment.MINUS_2) {
      state.comment_stage = StateComment.MINUS_3;
    } else if (state.comment_stage == StateComment.MINUS_3) {
      state.comment_stage = StateComment.EXCLAMATION;
    }
  }

  private void flushCharacters(ParserState state) throws SAXException {
    final CharArrayOutputStream output_stream = state.output_stream;
    if (output_stream.getSize() > 0) {
      final char[] ca = state.output_stream.getRawArray();
      // check for white space...
      int n = output_stream.getSize();
      if (!StringUtilitiesForSAXDriver.isWhiteSpace(ca, n)) {
        int i = 0;
        while (ca[n - 1] <= ' ') {
          n--;
        }
        while (ca[i] <= ' ') {
          i++;
          n--;
        }
        this.content_handler.characters(state.output_stream.getRawArray(), i, n);
      }
    }

    state.output_stream.setSize(0);
  }


  public void parse(final InputSource source) throws SAXException, IOException {
    if (source.getCharacterStream() != null) {
      parse(source.getCharacterStream());
    } else if (source.getByteStream() != null) {
      parse(new InputStreamReader(source.getByteStream()));
    } else {
      parse(new InputStreamReader(new URL(source.getSystemId()).openStream()));
    }
  }

  public void parse(final String system_id) throws SAXException, IOException {
    parse(new InputSource(system_id));
  }

  public void setContentHandler(final org.xml.sax.ContentHandler handler) {
    if (this.content_handler == null) {
      throw new NullPointerException();
    }
    this.content_handler = handler;
  }

  public void setContentHandler(final ContentHandlerExtension handler) {
    this.content_handler_extension = (handler == null) ? this : handler;
    content_handler = this.content_handler_extension;
    this.content_handler.setDocumentLocator(this);
  }

  public org.xml.sax.ContentHandler getContentHandler() {
    return this.content_handler;
  }

  public void setErrorHandler(final ErrorHandler handler) {
    this.error_handler = (handler == null) ? this : handler;
  }

  public ErrorHandler getErrorHandler() {
    return this.error_handler;
  }

  public void setDocumentLocator(final Locator locator) {
  }

  public void setDTDHandler(final DTDHandler handler) {
    // not implemented
  }

  public DTDHandler getDTDHandler() {
    return null;
  }

  public void setEntityResolver(final EntityResolver resolver) {
    // not implemented
  }

  public EntityResolver getEntityResolver() {
    return null;
  }

  public String getDefaultNamespace() {
    return null;
  }

  public String getNamespace(final String tag) {
    return null;
  }

  public void startDocument() throws SAXException {
  }

  public Writer startDocument(final Writer writer) throws SAXException {
    this.content_handler.startDocument();
    return writer;
  }

  public void endDocument() throws SAXException {
    //...
  }

  public void startPrefixMapping(final String prefix, final String uri)
      throws SAXException {
    //...
  }

  public void endPrefixMapping(final String prefix) throws SAXException {
    //...
  }

  public void startElement(final String namespace_uri, final String name_local,
      final String name_q, final Attributes atts) throws SAXException {
    //...
  }

  public Writer startElement(final String namespace_uri,
      final String name_local, final String name_q, final Attributes atts,
      final Writer writer) throws SAXException {
    this.content_handler.startElement(namespace_uri, name_local, name_q, atts);
    return writer;
  }

  public void endElement(final String namespace_uri, final String name_local,
      final String name_q) throws SAXException {
    //...
  }

  public void characters(final char[] ch, final int start, final int length)
      throws SAXException {
    //...
  }

  public void ignorableWhitespace(final char[] ch, final int start,
      final int length) throws SAXException {
    // not implemented
  }

  public void processingInstruction(final String target, final String data)
      throws SAXException {
    // not implemented
  }

  public void skippedEntity(final String name) throws SAXException {
    // not implemented
  }

  public boolean getFeature(final String name)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.equals(NAMESPACES)) {
      return false;
    }
    if (name.equals(NAMESPACE_PREFIXES)) {
      return false;
    }
    if (name.equals(VALIDATION)) {
      return false;
    }
    throw new SAXNotRecognizedException(name);
  }

  public void setFeature(final String name, final boolean value)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.equals(NAMESPACES) || name.equals(NAMESPACE_PREFIXES)
        || name.equals(VALIDATION)) {
      throw new SAXNotSupportedException(name);
    }
    throw new SAXNotRecognizedException(name);
  }

  public Object getProperty(final String name)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    throw new SAXNotRecognizedException(name);
  }

  public void setProperty(final String name, final Object value)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    throw new SAXNotRecognizedException(name);
  }

  public void warning(final SAXParseException e) throws SAXException {
  }

  public void error(final SAXParseException e) throws SAXException {
  }

  public void fatalError(final SAXParseException e) throws SAXException {
    throw e;
  }

  private void fatalError(final String msg, final int line_number,
      final int column_number) throws SAXException {
    this.error_handler.fatalError(new SAXParseException(msg, null, null,
        line_number, column_number));
  }

  void reportError(String msg) throws SAXException {
    fatalError(msg, state.position.y, state.position.x);
  }

  public String getPublicId() {
    return "";
  }

  public String getSystemId() {
    return "";
  }

  public int getLineNumber() {
    return state.position.y;
  }

  public int getColumnNumber() {
    return state.position.x;
  }
}
