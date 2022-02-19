package com.texturegarden.xml.saxdriveroriginal;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.xml.sax.SAXException;

public class SAXDriverOriginalBuffer extends Writer {
  private SAXDriverOriginal sd;
  private int next_in = 0;
  private int last_in = 0;
  char[] chars;
  private final Reader in;
  private int count = 0;
  private Writer writer = this;
  private boolean flushed = false;
  private boolean written = false;

  public SAXDriverOriginalBuffer(final Reader in, SAXDriverOriginal sd) {
    this.in = in;
    this.sd = sd;

    chars = new char[sd.getInitialBufferSize()];
  }

  public void close() throws IOException {
    flush();
  }

  public void flush() throws IOException {
    try {
      flushBuffer();
      if (writer != this) {
        writer.flush();
      }
    } finally {
      flushed = true;
    }
  }

  public void write(final int c) throws IOException {
    written = true;
    chars[count++] = (char) c;
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
    sd.getStack().push(this.writer);

    this.writer = (writer == null) ? this : writer;

    written = false;
    flushed = false;
  }

  public Writer getWriter() {
    return writer;
  }

  public void popWriter() throws IOException {
    try {
      if (!flushed && writer != this) {
        writer.flush();
      }
    } finally {
      writer = (Writer) sd.getStack().pop();
      written = false;
      flushed = false;
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
    if (next_in == last_in) {
      if (count != 0) {
        if (written) {
          flushBuffer();
        } else if (count >= (chars.length - sd.getBufferIncrement())) {
          final char[] new_chars = new char[chars.length + sd.getBufferIncrement()];

          System.arraycopy(chars, 0, new_chars, 0, count);
          chars = new_chars;
        }
      }

      final int number_read = in.read(chars, count, chars.length - count);

      if (number_read == -1) {
        return -1;
      }

      next_in = count;
      last_in = count + number_read;
    }

    return chars[next_in++];
  }

  private void flushBuffer() throws IOException {
    if (count != 0) {
      try {
        if (writer == this) {
          try {
            sd.getContentHandler().characters(chars, 0, count);
          } catch (final SAXException e) {
            throw new IOException(e.toString());
          }
        } else {
          writer.write(chars, 0, count);
        }
      } finally {
        count = 0;
      }
    }
  }

  public void setChars(char[] chars) {
    this.chars = chars;
  }

  public char[] getChars() {
    return chars;
  }

  public void setLastIn(int last_in) {
    this.last_in = last_in;
  }

  public int getLastIn() {
    return last_in;
  }

  public void setNextIn(int next_in) {
    this.next_in = next_in;
  }

  public int getNextIn() {
    return next_in;
  }

  public void setSd(SAXDriverOriginal sd) {
    this.sd = sd;
  }

  public SAXDriverOriginal getSd() {
    return sd;
  }
}
