/**
 * <p>Title: MyJavaTools: Formatted Output to Files</p>
 * <p>Description: A typical formatted file consists of head, body and tail.
 * Body typicaly consists of repeating entries. This class does the output of this kind of files.
 * <p>Good for Java 1.3.1 and up.</p>
 * <p>Copyright: This is public domain;
 * The right of people to use, distribute, copy or improve the contents of the
 * following may not be restricted.</p>
 *
 * @author Vlad Patryshev
 */

package com.myjavatools.lib;

import java.util.* ;
import java.io.*;
import java.text.MessageFormat;

public class FileFormat{
  MessageFormat head = null;
  MessageFormat body = null;
  MessageFormat tail = null;
  Writer writer = null;

  /**
   * Constructor
   * @see FileFormat(Writer,String,String,String)
   *
   * @param writer the writer that outputs the formatted contents
   * @param format head format string
   */
  public FileFormat(Writer writer, String format) {
    this.writer = writer;
    head = format == null ? null : new MessageFormat(format);
  }

  /**
   * Constructor
   *
   * @param writer the writer that outputs the formatted contents
   * @param head format string for the file head
   * @param body format string for (repeating) entries of the file body
   * @param tail format strings for the file tail
   *
   * <p><b>Example</b><br>
   * <code>
   * String head = "/*\r\n Resource Data for package {0} in project {1}\r\n&#47;*\r\n" +<br>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"class ResourceData {\r\n";<br>
   * String body = "  public String {0} = \"{1}\";\r\n";<br>
   * String tail = "}\r\n";<br>
   *<br>
   * FileFormat ff = new FileFormat(new FileWriter("resource.java", head, body, tail);<br>
   * ff.open(packageName, "My Cool Project");<br>
   * Properties p = ...;<br>
   * for (Enumeration i = p.keys(); i.hasMoreElements();) {<br>
   * &nbsp;&nbsp;String key = i.nextElement().toString();<br>
   * &nbsp;&nbsp;ff.write(new String[] {key, p.getProperty(key, "undef"});<br>
   * }<br>
   * ff.close();</code>
   * <p>This code will produce something like this:<br><code>
   * /*<br>
   *  Resource Data for package com.my.package in project My Cool Project<br>
   * *&#47;<br>
   * class ResourceData {<br>
   * &nbsp;&nbsp;String dialogTitle = "Romeo vs Juliet";<br>
   * &nbsp;&nbsp;String label1      = "One of unmentionable major copyrighted labels";<br>
   * &nbsp;&nbsp;String size        = "100x200";<br>
   * }<br></code>
   */
  public FileFormat(Writer writer, String head, String body, String tail) {
    this(writer, head);
    this.body = body == null ? null : new MessageFormat(body);
    this.tail = tail == null ? null : new MessageFormat(tail);
  }

/**
 * Constructor
 * @see FileFormat(Writer,String)
 *
 * @param os output stream that outputs the formatted contents
 * @param format head format string
 */
  public FileFormat(OutputStream os, String format) {
    this(new OutputStreamWriter(os), format);
  }

  /**
   * @see FileFormat(Writer,String,String,String)
   *
   * @param os output stream that outputs the formatted contents
   * @param head format string for the file head
   * @param body format string for (repeating) entries of the file body
   * @param tail format strings for the file tail
   */
  public FileFormat(OutputStream os, String head, String body, String tail) {
    this(new OutputStreamWriter(os), head, body, tail);
  }

  /**
   * Gets head format string
   * @return head format string
   */
  public MessageFormat getHead() { return head; }

  /**
   * Gets body format string
   * @return body format string
   */
  public MessageFormat getBody() { return body; }

  /**
   * gets tail format string
   * @return tail format string
   */
  public MessageFormat getTail() { return tail; }

  /**
   * Opens formatted output
   * @param args format arguments for the head format
   * @throws IOException
   */
  public void open(Object[]args)
    throws IOException {
    writer.write(head.format(args));
  }

  /**
   * Opens formatted output
   * @param arg the only argiment for the head format
   * @throws IOException
   */
  public void open(Object arg)
    throws IOException {
    writer.write(head.format(new Object[] {arg}));
  }

  /**
   * Opens formatted output, no format arguments for head
   * @throws IOException
   */
  public void open()
    throws IOException {
    writer.write(head.format(null));
  }

  /**
   * Writes a body string to formatted output
   *
   * @param args arguments for body format
   * @throws IOException
   */
  public void write(Object[]args)
    throws IOException {
    if (body != null) {
      writer.write(body.format(args));
    } else {
      open(args);
      close();
    }
  }

  /**
   * Writes an arbitrary string to (generally) formatted output
   *
   * @param s the string to output
   * @throws IOException
   */
  public void write(String s)
    throws IOException {
    writer.write(s);
  }

  /**
   * Writes the tail to formatted output and closes it
   *
   * @param args arguments for tail format
   * @throws IOException
   */
  public void close(Object[]args)
    throws IOException {
    if (tail != null) {
      writer.write(tail.format(args));
    }
    writer.close();
  }

  /**
   * Writes the tail to formatted output and closes it
   *
   * @throws IOException
   */
  public void close()
    throws IOException {
    close(null);
  }
}