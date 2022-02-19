// Copyright (c) 2001 The Wilson Partnership.
// All Rights Reserved.
// @(#)MinML2.java, 0.3, 25 November 2000, 2001
// Author: John Wilson - tug@wilson.co.uk

package uk.co.wilson.xml;

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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;

import uk.org.xml.sax.ContentHandler;
import uk.org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.Hashtable;
import java.util.Stack;
import java.util.EmptyStackException;
import java.util.Vector;

public class MinML2 implements XMLReader, ContentHandler, Locator, ErrorHandler {
  public static final int endStartName = 0;
  public static final int emitStartElement = 1;
  public static final int emitEndElement = 2;
  public static final int possiblyEmitCharacters = 3;
  public static final int emitCharacters = 4;
  public static final int emitCharactersSave = 5;
  public static final int saveAttributeName = 6;
  public static final int saveAttributeValue = 7;
  public static final int startComment = 8;
  public static final int endComment = 9;
  public static final int incLevel = 10;
  public static final int decLevel = 11;
  public static final int startCDATA = 12;
  public static final int endCDATA = 13;
  public static final int processCharRef = 14;
  public static final int writeCdata = 15;
  public static final int exitParser = 16;
  public static final int parseError = 17;
  public static final int discardAndChange = 18;
  public static final int discardSaveAndChange = 19;
  public static final int saveAndChange = 20;
  public static final int change = 21;

  public static final int inSkipping = 0;
  public static final int inSTag = 1;
  public static final int inPossiblyAttribute = 2;
  public static final int inNextAttribute = 3;
  public static final int inAttribute = 4;
  public static final int inAttribute1 = 5;
  public static final int inAttributeValue = 6;
  public static final int inAttributeQuoteValue = 7;
  public static final int inAttributeQuotesValue = 8;
  public static final int inETag = 9;
  public static final int inETag1 = 10;
  public static final int inMTTag = 11;
  public static final int inTag = 12;
  public static final int inTag1 = 13;
  public static final int inPI = 14;
  public static final int inPI1 = 15;
  public static final int inPossiblySkipping = 16;
  public static final int inCharData = 17;
  public static final int inCDATA = 18;
  public static final int inCDATA1 = 19;
  public static final int inComment =20;
  public static final int inDTD = 21;

  public static final int defaultInitialBufferSize = 256;
  public static final int defaultBufferIncrement = 256;

  public MinML2(final int initialBufferSize, final int bufferIncrement) {
    this.initialBufferSize = initialBufferSize;
    this.bufferIncrement = bufferIncrement;
  }

  public MinML2() {
    this(defaultInitialBufferSize, defaultBufferIncrement);
  }

  public void parse(final Reader in) throws SAXException, IOException {
  final Vector attributeURIs = new Vector();
  final Vector attributeLocalNames = new Vector();
  final Vector attributeQNames = new Vector();
  final Vector attributeValues = new Vector();

  final Attributes attrs = new Attributes() {
                                  public int getLength() {
                                    return attributeValues.size();
                                  }

                                  public String getURI(final int i) {
                                    return (String)attributeURIs.elementAt(i);
                                  }

                                  public String getLocalName(final int i) {
                                    return (String)attributeLocalNames.elementAt(i);
                                  }

                                  public String getQName(final int i) {
                                    return (String)attributeQNames.elementAt(i);
                                  }

                                  public String getType(final int i) {
                                    return "CDATA";
                                  }

                                  public String getValue(final int i) {
                                    return (String)attributeValues.elementAt(i);
                                  }

                                  public int getIndex(final String uri, final String localPart) {
                                  int i = -1;

                                    while (true) {
                                      i = attributeLocalNames.indexOf(localPart, i + 1);

                                      if (i == -1 || uri.equals(attributeURIs.elementAt(i))) return i;
                                    }
                                  }

                                  public int getIndex(final String qName) {
                                    return attributeQNames.indexOf(qName);
                                  }

                                  public String getType(final String uri, final String localName) {
                                    return "CDATA";
                                  }

                                  public String getType(final String qName) {
                                    return "CDATA";
                                  }

                                  public String getValue(final String uri, final String localName) {
                                  final int index = this.getIndex(uri, localName);

                                    return (index == -1) ? null : (String)attributeValues.elementAt(index);
                                  }

                                  public String getValue(final String qName) {
                                  final int index = attributeQNames.indexOf(qName);

                                    return (index == -1) ? null : (String)attributeValues.elementAt(index);
                                  }
                      };

    this.namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");

    final MinMLBuffer buffer = new MinMLBuffer(in);
    boolean firstElement = true;
    int currentChar = 0, charCount = 0;
    int level = 0;
    int mixedContentLevel = -1;
    String elementName = null;
    String state = operands[inSkipping];
    String attributeName = "";

    this.lineNumber = 1;
    this.columnNumber = 0;

    try {
      while(true) {
        charCount++;

        //
        // this is to try and make the loop a bit faster
        // currentChar = buffer.read(); is simpler but is a bit slower.
        //
        currentChar = (buffer.nextIn == buffer.lastIn) ? buffer.read() : buffer.chars[buffer.nextIn++];

        final int transition;

        if (currentChar > ']') {
          transition = state.charAt(14);
        } else {
        final int charClass = charClasses[currentChar + 1];

          if (charClass == -1) fatalError("Document contains illegal control character with value " + currentChar, this.lineNumber, this.columnNumber);

          if (charClass == 12) {
            if (currentChar == '\r') {
              currentChar = '\n';
              charCount = -1;
            }

            if (currentChar == '\n') {
              if (charCount == 0) continue;  // preceeded by '\r' so ignore

              if (charCount != -1) charCount = 0;

              this.lineNumber++;
              this.columnNumber = 0;
            }
          }

          transition = state.charAt(charClass);
       }

        this.columnNumber++;

        final String operand = operands[transition >>> 8];

        switch (transition & 0XFF) {
          case endStartName:
          // end of start element name
            elementName = buffer.getString();

            this.stack.push(null);

            if (currentChar != '>' && currentChar != '/') break;  // change state to operand
            // drop through to emit start element (we have no attributes)

          case emitStartElement: {
          // emit start element

            for (int i = 0; i != attributeURIs.size(); i++) {
            final Object attributeNamespaceName = attributeURIs.elementAt(i);
            final Object uri;

              if (attributeNamespaceName != null) {
                uri = this.namespaces.get(attributeNamespaceName);

                if (uri == null) throw new SAXException("Namespace \"" + attributeNamespaceName + "\" is not defined");
              } else {
                uri = "";
              }

              attributeURIs.setElementAt(uri, i);
            }

            final String elementLocalName;
            final String uri;
            final int colonIndex = elementName.indexOf(':');

            if (colonIndex != -1) {
            final String namespaceName = elementName.substring(0, colonIndex);

              uri = (String)this.namespaces.get(namespaceName);

              if (uri == null) throw new SAXException("Namespace \"" + namespaceName + "\" is not defined");

              elementLocalName = elementName.substring(colonIndex + 1);

            } else {
              elementLocalName = elementName;
              uri = this.defaultNamespace;
            }

            this.stack.push(elementLocalName);
            this.stack.push(uri);
            this.stack.push(elementName);

            buffer.pushWriter(this.extContentHandler.startElement(uri,
                                                                  elementLocalName,
                                                                  elementName,
                                                                  attrs,
                                                                  (firstElement) ?
                                                                    this.extContentHandler.startDocument(buffer)
                                                                  :
                                                                    buffer.getWriter()));

            firstElement = false;
            attributeURIs.removeAllElements();
            attributeLocalNames.removeAllElements();
            attributeQNames.removeAllElements();
            attributeValues.removeAllElements();

            if (mixedContentLevel != -1) mixedContentLevel++;

            if (currentChar != '/') break;  // change state to operand
          }
            // <element/> drop through

          case emitEndElement:
          // emit end element

            try {
              buffer.popWriter();
              elementName = buffer.getString();

              final String begin = (String)this.stack.pop();

              if (currentChar != '/' && !elementName.equals(begin)) {
                fatalError("end tag </" + elementName + "> does not match begin tag <" + begin + ">",
                           this.lineNumber,
                           this.columnNumber);
              } else {
                this.contentHandler.endElement((String)this.stack.pop(), (String)this.stack.pop(), begin);

                String namespaceName;

                while ((namespaceName = (String)this.stack.pop()) != null) {
                final String elementURI;

                  if((elementURI = (String)this.stack.pop()) == null) {
                    this.namespaces.remove(namespaceName);
                  } else {
                    if (namespaceName.length() == 0)
                      this.defaultNamespace = elementURI;
                    else
                      this.namespaces.put(namespaceName, elementURI);
                  }

                  this.contentHandler.endPrefixMapping(namespaceName);
                }

                if (this.stack.empty()) {
                  this.contentHandler.endDocument();
                  return;
                }
              }
            }
            catch (final EmptyStackException e) {
              fatalError("end tag at begining of document", this.lineNumber, this.columnNumber);
            }

            if (mixedContentLevel != -1) --mixedContentLevel;

            break;  // change state to operand

          case emitCharacters:
          // emit characters

            buffer.flush();
            break;  // change state to operand

          case emitCharactersSave:
          // emit characters and save current character

            if (mixedContentLevel == -1) mixedContentLevel = 0;

            buffer.flush();

            buffer.saveChar((char)currentChar);

            break;  // change state to operand

          case possiblyEmitCharacters:
          // write any skipped whitespace if in mixed content

            if (mixedContentLevel != -1) buffer.flush();
            break;  // change state to operand

          case saveAttributeName:
          // save attribute name

            attributeName = buffer.getString();
            break;  // change state to operand

          case saveAttributeValue: {
          final String value = buffer.getString();

            if (attributeName.equals("xmlns")) {
              this.stack.push(this.defaultNamespace);
              this.stack.push("");

              this.defaultNamespace = value;

              this.contentHandler.startPrefixMapping("", value);

            } else if (attributeName.startsWith("xmlns:")) {
            final String namespaceName = attributeName.substring(6);

              this.stack.push(this.namespaces.put(namespaceName, value));
              this.stack.push(namespaceName);

              this.contentHandler.startPrefixMapping(namespaceName, value);
            } else {
            final int colonIndex = attributeName.indexOf(':');

              if (colonIndex != -1) {
                attributeURIs.addElement(attributeName.substring(0, colonIndex));
                attributeLocalNames.addElement(attributeName.substring(colonIndex + 1));
               } else {
                attributeURIs.addElement(null);
                attributeLocalNames.addElement(attributeName);
              }

              attributeQNames.addElement(attributeName);
              attributeValues.addElement(value);
            }

            break;  // change state to operand
          }

          case startComment:
          // change state if we have found "<!--"

            if (buffer.read() != '-') continue; // not "<!--"

            break;  // change state to operand

          case endComment:
          // change state if we find "-->"

            if ((currentChar = buffer.read()) == '-') {
              // deal with the case where we might have "------->"
              while ((currentChar = buffer.read()) == '-');

              if (currentChar == '>') break;  // end of comment, change state to operand
            }

            continue;   // not end of comment, don't change state

          case incLevel:

            level++;

            break;

          case decLevel:

            if (level == 0) break; // outer level <> change state

            level--;

            continue; // in nested <>, don't change state

          case startCDATA:
          // change state if we have found "<![CDATA["

            if (buffer.read() != 'C') continue;   // don't change state
            if (buffer.read() != 'D') continue;   // don't change state
            if (buffer.read() != 'A') continue;   // don't change state
            if (buffer.read() != 'T') continue;   // don't change state
            if (buffer.read() != 'A') continue;   // don't change state
            if (buffer.read() != '[') continue;   // don't change state
            break;  // change state to operand

          case endCDATA:
          // change state if we find "]]>"

            if ((currentChar = buffer.read()) == ']') {
              // deal with the case where we might have "]]]]]]]>"
              while ((currentChar = buffer.read()) == ']') buffer.write(']');

              if (currentChar == '>') break;  // end of CDATA section, change state to operand

              buffer.write(']');
            }

            buffer.write(']');
            buffer.write(currentChar);
            continue;   // not end of CDATA section, don't change state

          case processCharRef:
          // process character entity

            int crefState = 0;

            currentChar = buffer.read();

            while (true) {
              if ("#amp;&pos;'quot;\"gt;>lt;<".charAt(crefState) == currentChar) {
                crefState++;

                if (currentChar == ';') {
                  buffer.write("#amp;&pos;'quot;\"gt;>lt;<".charAt(crefState));
                  break;

                } else if (currentChar == '#') {
                final int radix;

                  currentChar = buffer.read();

                  if (currentChar == 'x') {
                    radix = 16;
                    currentChar = buffer.read();
                  } else {
                    radix = 10;
                  }

                  int charRef = Character.digit((char)currentChar, radix);

                  while (true) {
                    currentChar = buffer.read();

                    final int digit = Character.digit((char)currentChar, radix);

                    if (digit == -1) break;

                    charRef = (char)((charRef * radix) + digit);
                  }

                  if (currentChar == ';' && charRef != -1) {
                    buffer.write(charRef);
                    break;
                  }

                  fatalError("invalid Character Entitiy", this.lineNumber, this.columnNumber);
                } else {
                  currentChar = buffer.read();
                }
              } else {
                crefState = ("\u0001\u000b\u0006\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff" +
//                               #     a     m     p     ;     &     p     o     s     ;     '
//                               0     1     2     3     4     5     6     7     8     9     a
                             "\u0011\u00ff\u00ff\u00ff\u00ff\u00ff\u0015\u00ff\u00ff\u00ff" +
//                               q     u     o     t     ;     "     g     t     ;     >
//                               b     b     d     e     f     10    11    12    13    14
                             "\u00ff\u00ff\u00ff").charAt(crefState);
//                               l     t     ;
//                               15    16    17

                if (crefState == 255) fatalError("invalid Character Entitiy", this.lineNumber, this.columnNumber);
              }
            }

            break;

          case parseError:
          // report fatal error

            fatalError(operand, this.lineNumber, this.columnNumber);
            // drop through to exit parser

          case exitParser:
          // exit parser

            return;

          case writeCdata:
          // write character data
          // this will also write any skipped whitespace

            buffer.write(currentChar);
            break;  // change state to operand

          case discardAndChange:
          // throw saved characters away and change state

            buffer.reset();
            break;  // change state to operand

          case discardSaveAndChange:
          // throw saved characters away, save character and change state

            buffer.reset();
            // drop through to save character and change state

          case saveAndChange:
          // save character and change state

            buffer.saveChar((char)currentChar);
            break;  // change state to operand

          case change:
          // change state to operand

            break;  // change state to operand
        }

        state = operand;
      }
    }
    catch (final IOException e) {
      this.errorHandler.fatalError(new SAXParseException(e.toString(), null, null, this.lineNumber, this.columnNumber, e));
    }
    finally {
      this.contentHandler = this.extContentHandler = this;
      this.errorHandler = this;
      this.stack.removeAllElements();
      this.namespaces.clear();
      this.defaultNamespace = "";
    }
  }

  public void parse(final InputSource source) throws SAXException, IOException {
    if (source.getCharacterStream() != null)
      parse(source.getCharacterStream());
    else if (source.getByteStream() != null)
      parse(new InputStreamReader(source.getByteStream()));
    else
     parse(new InputStreamReader(new URL(source.getSystemId()).openStream()));
  }

  public void parse(final String systemId) throws SAXException, IOException {
    parse(new InputSource(systemId));
  }

  public void setContentHandler(final org.xml.sax.ContentHandler handler) {
    if (this.contentHandler == null) throw new NullPointerException();

    this.contentHandler = handler;
  }

  public void setContentHandler(final ContentHandler handler) {
   contentHandler = this.extContentHandler = (handler == null) ? this : handler;
   this.contentHandler.setDocumentLocator(this);
  }

  public org.xml.sax.ContentHandler getContentHandler() {
    return this.contentHandler;
  }

  public void setErrorHandler(final ErrorHandler handler) {
   this.errorHandler = (handler == null) ? this : handler;
  }

  public ErrorHandler getErrorHandler() {
    return this.errorHandler;
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
    return this.defaultNamespace;
  }

  public String getNamespace(final String tag) {
    return (String)this.namespaces.get(tag);
  }

  public void startDocument() throws SAXException {
  }

  public Writer startDocument(final Writer writer) throws SAXException {
    this.contentHandler.startDocument();
    return writer;
  }

  public void endDocument() throws SAXException {
  }

  public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
  }

  public void endPrefixMapping(final String prefix) throws SAXException {
  }

  public void startElement(final String namespaceURI,
                           final String localName,
                           final String qName,
                           final Attributes atts)
    throws SAXException
  {
  }

  public Writer startElement(final String namespaceURI,
                             final String localName,
                             final String qName,
                             final Attributes atts,
                             final Writer writer)
    throws SAXException
  {
    this.contentHandler.startElement(namespaceURI, localName, qName, atts);
    return writer;
  }

  public void endElement(final String namespaceURI,
                         final String localName,
                         final String qName)
    throws SAXException
  {
  }

  public void characters(final char ch[], final int start, final int length) throws SAXException {
  }

  public void ignorableWhitespace(final char ch[], final int start, final int length) throws SAXException {
    // not implemented
  }

  public void processingInstruction(final String target, final String data)
    throws SAXException
  {
    // not implemented
  }

  public void skippedEntity(final String name) throws SAXException {
    // not implemented
  }

  public boolean getFeature(final String name)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (name.equals("http://xml.org/sax/features/namespaces")) return true;

    if (name.equals("http://xml.org/sax/features/namespace-prefixes")) return false;

    if (name.equals("http://xml.org/sax/features/validation")) return false;

    throw new SAXNotRecognizedException(name);
  }

  public void setFeature(final String name, final boolean value)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (name.equals("http://xml.org/sax/features/namespaces") ||
        name.equals("http://xml.org/sax/features/namespace-prefixes") ||
        name.equals("http://xml.org/sax/features/validation"))
          throw new SAXNotSupportedException(name);

    throw new SAXNotRecognizedException(name);
  }

  public Object getProperty(final String name)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    throw new SAXNotRecognizedException(name);
  }

  public void setProperty(final String name, final Object value)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    throw new SAXNotRecognizedException(name);
  }

  public void warning(final SAXParseException e) throws SAXException {
  }

  public void error(final SAXParseException e) throws SAXException {
  }

  public void fatalError(final SAXParseException e) throws SAXException {
    throw e;
  }

  public String getPublicId() {
    return "";
  }

  public String getSystemId() {
    return "";
  }

  public int getLineNumber () {
    return this.lineNumber;
  }

  public int getColumnNumber () {
    return this.columnNumber;
  }

  private void fatalError(final String msg, final int lineNumber, final int columnNumber) throws SAXException {
    this.errorHandler.fatalError(new SAXParseException(msg, null, null, lineNumber, columnNumber));
  }

  private class MinMLBuffer extends Writer {
    public MinMLBuffer(final Reader in) {
      this.in = in;
    }

    public void close() throws IOException {
      flush();
    }

    public void flush() throws IOException {
      try {
        _flush();
        if (writer != this) writer.flush();
      }
      finally {
        flushed = true;
      }
    }

    public void write(final int c) throws IOException {
      written = true;
      chars[count++] = (char)c;
    }

    public void write(final char[] cbuf, final int off, final int len) throws IOException {
      written = true;
      System.arraycopy(cbuf, off, chars, count, len);
      count += len;
    }

    public void saveChar(final char c) {
      written = false;
      chars[count++] = c;
    }

    public void pushWriter(final Writer writer) {
      MinML2.this.stack.push(this.writer);

      this.writer = (writer == null) ? this : writer;

      flushed = written = false;
    }

    public Writer getWriter() {
      return writer;
    }

    public void popWriter() throws IOException {
      try {
        if (!flushed && writer != this) writer.flush();
      }
      finally {
        writer = (Writer)MinML2.this.stack.pop();
        flushed = written = false;
      }
    }

    public String getString() {
    final String result = new String(chars, 0, count);

      count = 0;
      return result;
    }

    public void reset() {
      count = 0;
    }

    public int read() throws IOException {
      if (nextIn == lastIn) {
        if (count != 0) {
          if (written) {
            _flush();
          } else if (count >= (chars.length - MinML2.this.bufferIncrement)) {
          final char[] newChars = new char[chars.length + MinML2.this.bufferIncrement];

            System.arraycopy(chars, 0, newChars, 0, count);
            chars = newChars;
          }
        }

        final int numRead = in.read(chars, count, chars.length - count);

        if (numRead == -1) return -1;

        nextIn = count;
        lastIn = count + numRead;
      }

      return chars[nextIn++];
    }

    private void _flush() throws IOException {
      if (count != 0) {
        try {
          if (writer == this) {
            try {
              MinML2.this.contentHandler.characters(chars, 0, count);
            }
            catch (final SAXException e) {
              throw new IOException(e.toString());
            }
          } else {
            writer.write(chars, 0, count);
          }
        }
        finally {
          count = 0;
        }
      }
    }

    private int nextIn = 0, lastIn = 0;
    private char[] chars = new char[MinML2.this.initialBufferSize];
    private final Reader in;
    private int count = 0;
    private Writer writer = this;
    private boolean flushed = false;
    private boolean written = false;
  }

  private ContentHandler extContentHandler = this;
  private org.xml.sax.ContentHandler contentHandler = this;
  private ErrorHandler errorHandler = this;
  private final Stack stack = new Stack();
  private int lineNumber = 1;
  private int columnNumber = 0;
  private final int initialBufferSize;
  private final int bufferIncrement;
  private final Hashtable namespaces = new Hashtable();
  private String defaultNamespace = "";

  private static final byte[] charClasses = {
  //  EOF
      13,
  //                                      \t  \n          \r
      -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, -1, -1, 12, -1, -1,
  //
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
  //  SP   !   "   #   $   %   &   '   (   )   *   +   ,   -   .   /
      12,  8,  7, 14, 14, 14,  3,  6, 14, 14, 14, 14, 14, 11, 14,  2,
  //   0   1   2   3   4   5   6   7   8   9   :   ;   <   =   >   ?
      14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,  0,  5,  1,  4,
  //
      14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
  //                                               [   \   ]
      14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,  9, 14, 10
  };

  private static final String[] operands = {
    "\u0d15\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u1611\u0015\u0010\u1611",
    "\u1711\u1000\u0b00\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u0114\u0200\u1811\u0114",
    "\u1711\u1001\u0b01\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u0215\u1811\u0414",
    "\u1711\u1001\u0b01\u1711\u1911\u1911\u1911\u1911\u1911\u1911\u1911\u1911\u0315\u1811\u0414",
    "\u1911\u1911\u1911\u1911\u1911\u0606\u1911\u1911\u1911\u1911\u1911\u0414\u0515\u1811\u0414",
    "\u1911\u1911\u1911\u1911\u1911\u0606\u1911\u1911\u1911\u1911\u1911\u1911\u0515\u1811\u1911",
    "\u1a11\u1a11\u1a11\u1a11\u1a11\u1a11\u0715\u0815\u1a11\u1a11\u1a11\u1a11\u0615\u1811\u1a11",
    "\u0714\u0714\u0714\u070e\u0714\u0714\u0307\u0714\u0714\u0714\u0714\u0714\u0714\u1811\u0714",
    "\u0814\u0814\u0814\u080e\u0814\u0814\u0814\u0307\u0814\u0814\u0814\u0814\u0814\u1811\u0814",
    "\u1711\u1002\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u0914\u0915\u1811\u0914",
    "\u1b11\u1b11\u0904\u1b11\u1b11\u1b11\u1b11\u1b11\u1215\u1b11\u1b11\u1b11\u1b11\u1811\u0105",
    "\u1711\u1012\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1711\u1811\u1711",
    "\u1711\u1c11\u0912\u1711\u0e12\u1711\u1711\u1711\u1212\u1711\u1711\u1711\u1711\u1811\u0113",
    "\u1711\u1c11\u0912\u1711\u0e12\u1711\u1711\u1711\u1212\u1711\u1711\u1711\u1711\u1811\u0113",
    "\u0e15\u0e15\u0e15\u0e15\u0f15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u1811\u0e15",
    "\u0e15\u0015\u0e15\u0e15\u0f15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u0e15\u1811\u0e15",
    "\u0c03\u110f\u110f\u110e\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u1014\u1811\u110f",
    "\u0a15\u110f\u110f\u110e\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u110f\u1811\u110f",
    "\u1d11\u1d11\u1d11\u1d11\u1d11\u1d11\u1d11\u1d11\u1d11\u130c\u1d11\u1408\u1d11\u1811\u1515",
    "\u130f\u130f\u130f\u130f\u130f\u130f\u130f\u130f\u130f\u130f\u110d\u130f\u130f\u1811\u130f",
    "\u1415\u1415\u1415\u1415\u1415\u1415\u1415\u1415\u1415\u1415\u1415\u0009\u1415\u1811\u1415",
    "\u150a\u000b\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1515\u1811\u1515",
    "expected Element",
    "unexpected character in tag",
    "unexpected end of file found",
    "attribute name not followed by '='",
    "invalid attribute value",
    "expecting end tag",
    "empty tag",
    "unexpected character after <!"
  };
}
