// Copyright (c) 2001 The Wilson Partnership.
// All Rights Reserved.
// @(#)MinML2.java, 0.3, 25 November 2000, 2001
// Author: John Wilson - tug@wilson.co.uk

package com.texturegarden.xml.saxdriveroriginal;

/*
Copyright (c) 2000, 2001 John Wilson (tug@wilson.co.uk).
All rights reserved.
Redistribution and use in source and binary forms,
with or without modification, are permitted provided
that the following conditions are met:

Redistributions of source code must retain the above
copyright notice, this list of conditions and the
following disclaimer.

Redistributions in binary form must reproduce the
above copyright notice, this list of conditions and
the following disclaimer in the documentation and/or
other materials provided with the distribution.

All advertising materials mentioning features or use
of this software must display the following acknowledgement:

This product includes software developed by John Wilson.
The name of John Wilson may not be used to endorse or promote
products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY JOHN WILSON ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JOHN WILSON
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE
*/

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Hashtable;
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

public class SAXDriverOriginal implements XMLReaderExtension,
    ContentHandlerExtension, Locator, ErrorHandler, SAXDriverOriginalTransitions,
    Constants {
  private ContentHandlerExtension content_handler_extension = this;

  private org.xml.sax.ContentHandler content_handler = this;

  private ErrorHandler error_handler = this;

  private final Stack stack = new Stack();

  private int line_number = 1;

  private int column_number = 0;

  private final int initial_buffer_size;

  private final int buffer_increment;

  private final Hashtable namespaces = new Hashtable();

  private String namespace_default = "";

  public SAXDriverOriginal(final int initial_buffer_size,
      final int buffer_increment) {
    this.initial_buffer_size = initial_buffer_size;
    this.buffer_increment = buffer_increment;
  }

  public SAXDriverOriginal() {
    this(SAXDriverOriginalConstantsMisc.defaultInitialBufferSize, SAXDriverOriginalConstantsMisc.defaultBufferIncrement);
  }

  public void parse(final Reader in) throws SAXException, IOException {
    AttributesBasic attributes_wrapper = new AttributesBasic();

    final Attributes attrs = new AttributesExtended(attributes_wrapper);

    this.namespaces.put("xml", NAMESPACE);

    final SAXDriverOriginalBuffer buffer = new SAXDriverOriginalBuffer(in, this);

    boolean first_element = true;
    int current_char = 0;
    int count = 0;
    int level = 0;
    int mixed_content_level = -1;
    String element_name = null;
    String state = TRANSITIONS[SAXDriverOriginalConstantsLocation.inSkipping];
    String attribute_name = "";

    this.line_number = 1;
    this.column_number = 0;

    try {
      while (true) {
        count++;
        current_char = getNextChar(buffer);

        final int transition;

        if (current_char > ']') {
          transition = state.charAt(14);
        } else {
          final int char_class = SAXDriverOriginalConstantsClasses.CHAR_CLASSES[current_char + 1];

          if (char_class == -1) {
            reportError("Document contains illegal control character with value "
                + current_char);
          }

          if (char_class == 12) {
            if (current_char == '\r') {
              current_char = '\n';
              count = -1;
            }

            if (current_char == '\n') {
              if (count == 0) {
                continue; // preceeded by '\r' so ignore
              }

              if (count != -1) {
                count = 0;
              }

              this.line_number++;
              this.column_number = 0;
            }
          }

          transition = state.charAt(char_class);
        }

        this.column_number++;

        final String operand = TRANSITIONS[transition >>> 8];

        switch (transition & 0XFF) {
          case ConstantsAction.END_START_NAME:
            // end of start element name
            element_name = buffer.getString();

            this.stack.push(null);

            if (current_char != '>' && current_char != '/') {
              break; // change state to operand
            }
          // drop through to emit start element (we have no attributes)

          case ConstantsAction.EMIT_START_ELEMENT:
            // emit start element

            for (int i = 0; i != attributes_wrapper.attribute_uris.size(); i++) {
              final Object attribute_namespace_name = attributes_wrapper.attribute_uris
                  .elementAt(i);
              final Object uri;

              if (attribute_namespace_name != null) {
                uri = this.namespaces.get(attribute_namespace_name);

                if (uri == null) {
                  throw new SAXException("Namespace \""
                      + attribute_namespace_name + "\" is not defined");
                }
              } else {
                uri = "";
              }

              attributes_wrapper.attribute_uris.setElementAt(uri, i);
            }

            final String element_name_local;
            final String uri;
            final int colon_index = element_name.indexOf(':');

            if (colon_index != -1) {
              final String namespace_name = element_name.substring(0,
                  colon_index);

              uri = (String) this.namespaces.get(namespace_name);

              if (uri == null) {
                throw new SAXException("Namespace \"" + namespace_name
                    + "\" is not defined");
              }
              element_name_local = element_name.substring(colon_index + 1);

            } else {
              element_name_local = element_name;
              uri = this.namespace_default;
            }

            this.stack.push(element_name_local);
            this.stack.push(uri);
            this.stack.push(element_name);

            buffer.pushWriter(this.content_handler_extension.startElement(uri,
                element_name_local, element_name, (Attributes) attrs,
                (first_element) ? this.content_handler_extension
                    .startDocument(buffer) : buffer.getWriter()));

            first_element = false;
            attributes_wrapper.attribute_uris.removeAllElements();
            attributes_wrapper.attribute_local_names.removeAllElements();
            attributes_wrapper.attribute_qnames.removeAllElements();
            attributes_wrapper.attribute_values.removeAllElements();

            if (mixed_content_level != -1) {
              mixed_content_level++;
            }

            if (current_char != '/') {
              break; // change state to operand
            }
          // <element/> fall through

          case ConstantsAction.EMIT_END_ELEMENT:
            // emit end element

            try {
              buffer.popWriter();
              element_name = buffer.getString();

              final String begin = (String) this.stack.pop();

              if (current_char != '/' && !element_name.equals(begin)) {
                reportError("end tag </" + element_name
                    + "> does not match begin tag <" + begin + ">");
              } else {
                this.content_handler.endElement((String) this.stack.pop(),
                    (String) this.stack.pop(), begin);

                String namespace_name;

                while ((namespace_name = (String) this.stack.pop()) != null) {
                  final String element_uri;

                  if ((element_uri = (String) this.stack.pop()) == null) {
                    this.namespaces.remove(namespace_name);
                  } else {
                    if (namespace_name.length() == 0) {
                      this.namespace_default = element_uri;
                    } else {
                      this.namespaces.put(namespace_name, element_uri);
                    }
                  }

                  this.content_handler.endPrefixMapping(namespace_name);
                }

                if (this.stack.empty()) {
                  this.content_handler.endDocument();
                  return;
                }
              }
            } catch (final EmptyStackException e) {
              reportError("end tag at begining of document");
            }

            if (mixed_content_level != -1) {
              --mixed_content_level;
            }

            break; // change state to operand

          case ConstantsAction.EMIT_CHARACTERS:
            // emit characters

            buffer.flush();
            break; // change state to operand

          case ConstantsAction.EMIT_CHARACTERS_SAVE:
            // emit characters and save current character

            if (mixed_content_level == -1) {
              mixed_content_level = 0;
            }

            buffer.flush();

            buffer.saveChar((char) current_char);

            break; // change state to operand

          case ConstantsAction.POSSIBLY_EMIT_CHARACTERS:
            // write any skipped whitespace if in mixed content

            if (mixed_content_level != -1) {
              buffer.flush();
            }
            break; // change state to operand

          case ConstantsAction.SAVE_ATTRIBUTE_NAME:
            // save attribute name

            attribute_name = buffer.getString();
            break; // change state to operand

          case ConstantsAction.SAVE_ATTRIBUTE_VALUE:
            final String value = buffer.getString();

            if (attribute_name.equals("xmlns")) {
              this.stack.push(this.namespace_default);
              this.stack.push("");

              this.namespace_default = value;

              this.content_handler.startPrefixMapping("", value);

            } else if (attribute_name.startsWith("xmlns:")) {
              final String namespace_name = attribute_name.substring(6);

              this.stack.push(this.namespaces.put(namespace_name, value));
              this.stack.push(namespace_name);

              this.content_handler.startPrefixMapping(namespace_name, value);
            } else {
              final int colon_index2 = attribute_name.indexOf(':');

              if (colon_index2 != -1) {
                attributes_wrapper.attribute_uris.addElement(attribute_name
                    .substring(0, colon_index2));
                attributes_wrapper.attribute_local_names
                    .addElement(attribute_name.substring(colon_index2 + 1));
              } else {
                attributes_wrapper.attribute_uris.addElement(null);
                attributes_wrapper.attribute_local_names
                    .addElement(attribute_name);
              }

              attributes_wrapper.attribute_qnames.addElement(attribute_name);
              attributes_wrapper.attribute_values.addElement(value);
            }

            break; // change state to operand

          case ConstantsAction.START_COMMENT:
            // change state if we have found "<!--"

            if (buffer.read() != '-') {
              continue; // not "<!--"
            }

            break; // change state to operand

          case ConstantsAction.END_COMMENT:
            // change state if we find "-->"

            if ((current_char = buffer.read()) == '-') {
              // deal with the case where we might have "------->"
              while ((current_char = buffer.read()) == '-') {
                //...
                current_char++;
              }

              if (current_char == '>') {
                break; // end of comment, change state to operand
              }
            }

            continue; // not end of comment, don't change state

          case ConstantsAction.INC_LEVEL:

            level++;

            break;

          case ConstantsAction.DEC_LEVEL:

            if (level == 0) {
              break; // outer level <> change state
            }
            level--;

            continue; // in nested <>, don't change state

          case ConstantsAction.START_CDATA:
            // change state if we have found "<![CDATA["

            if (buffer.read() != 'C') {
              continue; // don't change state
            }
            if (buffer.read() != 'D') {
              continue; // don't change state
            }
            if (buffer.read() != 'A') {
              continue; // don't change state
            }
            if (buffer.read() != 'T') {
              continue; // don't change state
            }
            if (buffer.read() != 'A') {
              continue; // don't change state
            }
            if (buffer.read() != '[') {
              continue; // don't change state
            }
            break; // change state to operand

          case ConstantsAction.END_CDATA:
            // change state if we find "]]>"

            if ((current_char = buffer.read()) == ']') {
              // deal with the case where we might have "]]]]]]]>"
              while ((current_char = buffer.read()) == ']') {
                buffer.write(']');
              }

              if (current_char == '>') {
                break; // end of CDATA section, change state to operand
              }

              buffer.write(']');
            }

            buffer.write(']');
            buffer.write(current_char);
            continue; // not end of CDATA section, don't change state

          case ConstantsAction.PROCESS_CHAR_REF:
            // process character entity

            int c_ref_state = 0;

            current_char = buffer.read();

            while (true) {
              if (SAXDriverOriginalConstantsMisc.ENTITY_STRING.charAt(c_ref_state) == current_char) {
                c_ref_state++;

                if (current_char == ';') {
                  buffer.write(SAXDriverOriginalConstantsMisc.ENTITY_STRING.charAt(c_ref_state));
                  break;
                } else if (current_char == '#') {
                  final int radix;

                  current_char = buffer.read();

                  if (current_char == 'x') {
                    radix = 16;
                    current_char = buffer.read();
                  } else {
                    radix = 10;
                  }

                  int char_ref = Character.digit((char) current_char, radix);

                  while (true) {
                    current_char = buffer.read();

                    final int digit = Character.digit((char) current_char,
                        radix);

                    if (digit == -1) {
                      break;
                    }

                    char_ref = (char) ((char_ref * radix) + digit);
                  }

                  if (current_char == ';' && char_ref != -1) {
                    buffer.write(char_ref);
                    break;
                  }

                  reportError("invalid Character Entitiy");
                } else {
                  current_char = buffer.read();
                }
              } else {
                c_ref_state = SAXDriverOriginalConstantsEntities.ENTITIES.charAt(c_ref_state);

                if (c_ref_state == 255) {
                  reportError("invalid Character Entitiy");
                }
              }
            }

            break;

          case ConstantsAction.PARSE_ERROR:
            // report fatal error

            reportError(operand);
          // drop through to exit parser

          case ConstantsAction.EXIT_PARSER:
            // exit parser

            return;

          case ConstantsAction.WRITE_CDATA:
            // write character data
            // this will also write any skipped whitespace

            buffer.write(current_char);
            break; // change state to operand

          case ConstantsAction.DISCARD_AND_CHANGE:
            // throw saved characters away and change state

            buffer.reset();
            break; // change state to operand

          case ConstantsAction.DISCARD_SAVE_AND_CHANGE:
            // throw saved characters away, save character and change state

            buffer.reset();
          // drop through to save character and change state

          case ConstantsAction.SAVE_AND_CHANGE:
            // save character and change state

            buffer.saveChar((char) current_char);
            break; // change state to operand

          case ConstantsAction.CHANGE:
            // change state to operand

            break; // change state to operand
        }

        state = operand;
      }
    } catch (final IOException e) {
      this.error_handler.fatalError(new SAXParseException(e.toString(), null,
          null, this.line_number, this.column_number, e));
    } finally {
      this.content_handler = this;
      this.content_handler_extension = this;
      this.error_handler = this;
      this.stack.removeAllElements();
      this.namespaces.clear();
      this.namespace_default = "";
    }
  }

  private int getNextChar(final SAXDriverOriginalBuffer buffer)
      throws IOException {
    if (buffer.getNextIn() == buffer.getLastIn()) {
      return buffer.read();
    } else {
      buffer.setNextIn(buffer.getNextIn() + 1);
      return buffer.chars[buffer.getNextIn() - 1];
    }
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
    return this.namespace_default;
  }

  public String getNamespace(final String tag) {
    return (String) this.namespaces.get(tag);
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
      return true;
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
    fatalError(msg, this.line_number, this.column_number);
  }

  public String getPublicId() {
    return "";
  }

  public String getSystemId() {
    return "";
  }

  public int getLineNumber() {
    return this.line_number;
  }

  public int getColumnNumber() {
    return this.column_number;
  }

  public Stack getStack() {
    return stack;
  }

  public int getBufferIncrement() {
    return buffer_increment;
  }

  public int getInitialBufferSize() {
    return initial_buffer_size;
  }
}
