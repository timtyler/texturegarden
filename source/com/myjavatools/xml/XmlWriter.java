/**
 *
 * <p>Title: MyJavaTools: XML Writer</p>
 * <p>Description: Writes XmlData.</p>
 * <p>Copyright: This is public domain;
 * The right of people to use, distribute, copy or improve the contents of the
 * following may not be restricted.</p>
 *
 * @author Vlad Patryshev
 * @see http://www.w3.org/TR/REC-xml#sec-intro
 */

package com.myjavatools.xml;

import java.util.*;
import java.io.*;
//import com.myjavatools.lib.Tools;

public class XmlWriter extends Writer
{
  protected final Writer writer;
  protected String prefix  = "";
  protected Stack Elements = new Stack();
  protected boolean isEmpty;

  XmlWriter() {
    super();
    writer = this;
  }

  XmlWriter(Object lock) {
    super(lock);
    writer = this;
  }

  /**
   * Creates an XmlWriter from a Writer, using specified encoding.
   *
   * @param out the original writer
   * @param encoding encoding to use
   * @throws java.io.IOException
   */
  public XmlWriter(Writer out, String encoding) throws java.io.IOException {
    super();
    writer = out;
    int utfthere = encoding.indexOf("UTF");
    if (utfthere >= 0 && encoding.indexOf("UTF-") < 0) {
      encoding = "UTF-" + encoding.substring(3);
    }
    write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
    isEmpty = true;
  }

  /**
   * Creates an XmlWriter from a Writer, using default UTF8 encoding.
   *
   * @param out the original Writer
   * @throws java.io.IOException
   */
  public XmlWriter(Writer out) throws java.io.IOException {
    this(out, "UTF8");
  }


    /**
     * Creates an XmlWriter from an OutputStreamWriter, using its encoding.
     *
     * @param out the original OutputStreamWriter
     * @throws java.io.IOException
     */
  public XmlWriter(OutputStreamWriter out) throws java.io.IOException {
    this(out, out.getEncoding());
  }

  /**
   * Creates an XmlWriter from an OutputStream, using apwxidiws encoding.
   *
   * @param out the OutputStream to use
   * @param encoding encoding to use
   * @throws java.io.IOException
   */
  public XmlWriter(OutputStream out, String encoding)
    throws java.io.UnsupportedEncodingException, java.io.IOException {
    this(new OutputStreamWriter(out, encoding));
  }

  /**
   * Creates an XmlWriter from an OutputStream, using default UTF8 encoding.
   *
   * @param out the OutputStream to use
   * @throws java.io.IOException
   */
  public XmlWriter(OutputStream out)
    throws java.io.UnsupportedEncodingException, java.io.IOException {
    this(out, "UTF8");
  }

  /**
   * escapeChars - characters that ought to be escaped in output Xml file
   */
  static public final String escapedChars = "<>\'\"&]";

  /**
   * okChars - characters that are okay to be kept intact in output Xml file
   */
  static public final String okChars      = "\r\n\t";

  static protected boolean needsEscape(char c) {
    return ((c < '\u0020' && okChars.indexOf(c) < 0) ||
            escapedChars.indexOf(c) >= 0);
  }

  /**
   * Converts a string to the form that is acceptable inside Xml files,
   * escaping special characters.
   *
   * @param s the string to convert
   * @return string with certain characters replaiced with their SGML entity representations
   */
  static public String xmlEscape(String s)
  {
    if (isAlmostEmpty(s)) return "";

    boolean bNeedEscape = false;

    for (int i = 0; i < s.length() && !bNeedEscape; i++) {
      bNeedEscape = needsEscape(s.charAt(i));
    }

    if (!bNeedEscape) return s;

    StringBuffer result = new StringBuffer(s.length() * 6 / 5);

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      String esc = sgmlEntity(c);
      if (esc == null) result.append(c);
      else             result.append(esc);
    }

    return result.toString();
  }

  /**
   * Writes characters to output stream.
   *
   * @param cc char array to output
   * @param off offset inside the arrray
   * @param len number of characters to write
   * @throws java.io.IOException
   */
  public void write(char[]cc, int off, int len)
    throws java.io.IOException  {
    writer.write(cc, off, len);
  }

  /**
   * Writes a string to output.
   *
   * @param s the strign to write
   * @throws java.io.IOException if something went wrong
   */
  public void write(String s) throws java.io.IOException
  {
    writer.write(s);
    if (isEmpty && !isEmpty(s)) {
      isEmpty = false;
    }
  }

  /**
   * Flushes the output.
   *
   * @throws java.io.IOException if something went wrong
   */
  public void flush() throws java.io.IOException
  { writer.flush(); }

  protected void eol() throws java.io.IOException {
    write('\n');
  }

  /**
   * Writes an attribute name-value pair to the output.
   *
   * @param name attribute name
   * @param value attribute value
   * @throws java.io.IOException if something went wrong
   */
  public void writeAttribute(String name, String value) throws java.io.IOException
  {
    write(attributeToString(name, value));
  }

  /**
   * Converts a name-value pair to a string of form name="value".
   *
   * @param name the name of the attribute
   * @param value the value of the attribute
   * @return a string of form &lt;<i>name</i>&gt;="&lt;<i>value</i>&gt;". If value
   * is null or an empty string, result is an empty string.
   */
  public String attributeToString(String name, String value) {
    return isEmpty(value) ? "" :
           (" " + name + "=\"" + xmlEscape(value) + "\"");

  }

  /**
   * Writes attributes, name-value pairs, to the output.
   *
   * @param attrs String array of name-value pairs
   * @throws java.io.IOException if somethings went wrong
   */
  public void writeAttributes(String[]attrs) throws java.io.IOException
  {
    if (attrs != null) {
      for (int i = 0; i < attrs.length; i += 2) {
        writeAttribute(attrs[i], attrs[i+1]);
      }
    }
  }

  /**
   * Write attributes, name-value pairs, to the output.
   *
   * @param attrs maps attribute names to values
   * @throws java.io.IOException if somethings went wrong
   */
  public void writeAttributes(Map attrs) throws java.io.IOException
  {
    if (attrs != null) {
      Iterator keys = attrs.keySet().iterator();
      while (keys.hasNext()) {
        String key = (String)keys.next();
        writeAttribute(key, (String)attrs.get(key));
      }
    }
  }

  /**
   * Return the number of attrubutes in the attribute list which can be a string.
   * array or a map.
   *
   * @param attrs a string array or a map of attributes
   * @return the number of attributes (size of array / 2, number of keys in a map,
   * 0 otherwise).
   */
  public int attrSize(Object attrs) {
    return (attrs == null) ? 0 :
           (attrs instanceof String[]) ? ((String[])attrs).length :
           (attrs instanceof Map)  ? ((Map)attrs).size() :
           0;
  }

  /**
   * Gets a value of an attribute from an attribute list hat can be a map or a name-value pair string array.
   *
   * @param attrs attribute list
   * @param name the name of the attribute
   * @return the value of the attribute, or null if not found
   */
  public String getAttribute(Object attrs, String name) {
    if (attrs == null) {
     return null;
    } else if (attrs instanceof String[]) {
      String[] array = (String[])attrs;
      for (int i = 0; i < array.length; i+=2) {
        if (array[i].equals(name)) {
          return array[i+1];
        }
      }
    } else if (attrs instanceof Map) {
      return (String)((Map)attrs).get(name);
    }

    return null;
  }

  /**
   * Write attributes, name-value pairs, to the output.
   *
   * @param attrs it is either a name-value pair string array, or a map that maps attribute names to values
   * @throws java.io.IOException if somethings went wrong
   */
  public void writeAttributes(Object attrs) throws java.io.IOException
  {
    if (attrs == null) return;

    if (attrs instanceof String[]) {
      writeAttributes((String[])attrs);
    } else if (attrs instanceof HashMap) {
      writeAttributes((HashMap)attrs);
    }
  }

  /**
   * Converts a list of attibutes to a string.
   *
   * @param attrs a name-value pair string array
   * @return its representaion as string, by the rules of Xml.
   * @throws java.io.IOException
   */
  public String attributesToString(String[]attrs) throws java.io.IOException
  {
    String buf = "";
    if (attrs != null) {
      for (int i = 0; i < attrs.length; i += 2) {
        buf += attributeToString(attrs[i], attrs[i+1]);
      }
    }
    return buf;
  }

  /**
   * Starts outputting an element of Xml.
   * <p> as a result of this, "&lt;"<i>type</i> followed by attributes
   * followed by "&gt;" is sent to the output
   *
   * @param type element type
   * @param attrs attributes (can be a string array or a map)
   * @throws java.io.IOException
   */
  public void openElement(String type, Object attrs) throws java.io.IOException
  {
    Elements.push(type);
    write(prefix + "<" + type);
    if (attrs != null) writeAttributes(attrs);
    write(">");
    prefix += "  ";
  }

/**
 * Closes an element in output.
 *
 * If the last element has a type <i>t</i>, the characters "&lt;/<i>t</i>&gt;"
 * are sent to the output
 *
 * @throws java.io.IOException
 */
  public void closeElement() throws java.io.IOException
  {
    try {
      prefix = prefix.substring(0, Math.max(0,prefix.length() - 2));
      write(prefix + "</" + Elements.pop() + ">");
    } catch (Exception e) {};
  }

  /**
   * Closes the output of Xml, dumping end tags of all elements stored in the stack.
   *
   * @throws java.io.IOException
   */
  public void close() throws java.io.IOException
  {
    while (!Elements.empty()) {
      closeElement();
      eol();
    }
    writer.close();
  }

  /**
   * Writes an element that does not have any attributes or kids.
   *
   * @param type element type
   * @param value element value
   * @throws java.io.IOException
   */
  public void writeElement(String type, String value) throws java.io.IOException
  {
    if (value != null && value.length() > 0)
      write(prefix + "<" + type + ">" + xmlEscape(value) + "</" + type + ">");
  }

  /**
   * Writes an element that does not have any kids.
   *
   * @param type element type
   * @param value element value
   * @param attrs element attributes
   * @throws java.io.IOException
   */
  public void writeElement(String type, String value, Object attrs)
  throws java.io.IOException
  {
    String id = getAttribute(attrs, "id");
    boolean isTopLevel = isEmpty;
    if (isTopLevel ||
        attrSize(attrs) > 1 ||
        (id == null && attrSize(attrs) == 1)) {
      write(prefix + "<" + type);
      writeAttributes(attrs);
      write(isAlmostEmpty(value) && !isTopLevel ? "/>\n" :
                      ">" + xmlEscape(value) + "</" + type + ">");
    } else if (!isAlmostEmpty(value)) {
      writeElement(type, value);
    } else {
      // just ignore this element
    }
  }

  protected void writeXmlData(XmlData data)
    throws IOException {

    Map attributes = data.getAttributes();
    Collection kids = data.getAllKids();

    if ((kids != null) && (kids.size() > 0)) {
      openElement(data.getType(), attributes);

      for (Iterator i = kids.iterator(); i.hasNext();) {
        eol();
        Object next = i.next();
        writeXmlData((XmlData)next);
      }
      if (!isAlmostEmpty(data.getValue())) {
        write(xmlEscape(data.getValue()));
      } else {
        eol();
      }
      closeElement();
    } else {
      writeElement(data.getType(), data.getValue(), attributes);
    }
  }

/*
  private void writeMap(Map m)
  throws IOException
  {
    String[] a = (String[])m.get("attr");
    String type = (String)m.get("type");
    ArrayList kids = (ArrayList)m.get("kids");

    if ((kids != null) && (kids.size() > 0)) {
      openElement(type, a);

      for (int i = 0; i < kids.size(); i++) {
        write(kids.get(i));
      }
      closeElement();
    } else {
      writeElement(type, (String)m.get("value"), a);
    }
    eol();
  }
*/

  /**
   * Writes an object as Xml content to the output.
   *
   * @param o the data to be sent to output. If it is not XmlData, it is stringified first.
   * @throws IOException
   */
  public void write(Object o)
  throws IOException
  {
    if(o instanceof XmlData) {
      writeXmlData((XmlData)o);
      eol();
//    } else if(o instanceof Map) {
//      writeMap((Map)o);
    } else {
      write(o.toString());
    }
    flush();
  }

  /**
   * Writes an object as Xml content to the output.
   *
   * @param out the stream to output the data to
   * @param o the data to output
   * @throws IOException
   */
  public static void write(OutputStream out, Object o)
    throws IOException {
    (new XmlWriter(out)).write(o);
  }

  /**
   * Write the contents of object as an Xml file
   *
   * @param f the file to write to
   * @param o the object to write
   * @throws IOException
   */
  public static void write(File f, Object o)
    throws IOException {
    File location = f.getParentFile();
    if (location != null && !location.exists()) {
      location.mkdirs();
    }
    if (location != null && !location.exists()) {
      throw new IOException("failed to create directory " + location);
    }
    write(new FileOutputStream(f), o);
  }

  /**
   * Write the contents of object as an Xml file.
   *
   * @param filename the name of the file to write to
   * @param o the object to write
   * @throws IOException
   */
  public static void write(String filename, Object o)
    throws IOException {
    write(new File(filename), o);
  }

  protected static final boolean isEmpty(String string) {
    if (string == null) return true;
    return string.length() == 0;
  }

  protected static final boolean isAlmostEmpty(String string) {
    if (isEmpty(string)) return true;

    for (int i = 0; i < string.length(); i++) {
      if (string.charAt(i) > ' ') return false;
    }
    return true;
  }

  /**
   * Converts a character to its SGML numeric encoding
   *
   * @param c the character
   * @return a string with the representation of c as
   * "Numeric Character Reference" in SGMLese
   *
   * <br><br><b>Example</b>:
   * <li><code>toSgmlEncoding('\n')</code>
   * returns "&amp;#10;".</li>
   */
  public static String toSgmlEncoding(char c) {
    return c > 0x20 || c == 0x9 || c == 0xa || c == 0xd ? "&#" + (int)c + ";" : "?";
  }

  /**
   * Encodes a character by SGML rules
   * It can be a hex representation
   * @param c the character
   * @return the string with either Predefined Entity, Numeric Character Reference,
   * or null if no entity could be found
   *
   * <br><br><b>Examples</b>:
   * <li><code>sgmlEntity('\u005c60ab')</code> returns "&amp;#24747;" (that is, Numeric Character Reference);</li>
   * <li><code>sgmlEntity('<')</code> returns "&amp;lt;" (that is, Predefined Entity);</li>
   * <li><code>sgmlEntity('&')</code> returns "&amp;lt;" (that is, Predefined Entity);</li>
   * <li><code>sgmlEntity('X')</code> returns <b>null</b>";</li>
   * <li><code>sgmlEntity('\n')</code> returns <b>null</b>".</li>
   */
  public static String sgmlEntity(char c) {
    return (c == '<') ? "&lt;" :
           (c == '>') ? "&gt;" :
           (c == '\'') ? "&apos;" :
           (c == '\"') ? "&quot;" :
           (c == '&') ? "&amp;" :
           (c == ']') ? "&#93;" :
           (c < '\u0020' && c != '\n' && c != '\r' && c != '\t') || c > '\u0080' ?
              toSgmlEncoding(c) :
           null;
  }
}
