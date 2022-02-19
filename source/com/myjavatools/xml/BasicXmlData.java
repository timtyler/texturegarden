/**
 *
 * <p>Title: MyJavaTools: XML Data</p>
 * <p>Description: Decorated hierarchical Map that stores Xml-like data.</p>
 * <p>Copyright: This is public domain;
 * The right of people to use, distribute, copy or improve the contents of the
 * following may not be restricted.</p>
 *
 * @author Vlad Patryshev
 * @see http://www.myjavatools.com/projects/PracticalXML/
 *
 * <p>BasicXmlData is a simple container of data usually retrieved from an XML file.
 * It implements XmlData interface (@see com.myjavatools.XmlData).
 * It has a type, a value, a map of attributes and a set of kids (nodes) grouped by their types.
 * Inside a type, kids are arranged as a collection, so that they do have some order.
 * Kids also implement XmlData.
 *
 * <p>Note that the hierarchy is unidirectional: kids have no knowledge of contaners they are members of.
 * This sounds natural when we talk about containers; files do not know about their current location,
 * web pages do not generally know their full url - but somehow in DOM it is all different (and this is what makes it hard to handle: parent references).
 */

package com.myjavatools.xml;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.xml.sax.AttributeList;

import com.myjavatools.lib.Objects;
import com.myjavatools.lib.Strings;

public class BasicXmlData implements XmlData
{
  private static boolean DEBUG = false;
  public static void debug(boolean doit) {
    DEBUG = doit;
  }

  private class Slot {
    ArrayList entries;
    Map index;
    Set indexedAttributes = new HashSet();

    Slot() {
      entries = new ArrayList();
      index = new HashMap();
    }

    void addItem(XmlData item) {
      entries.add(item);

      for (Iterator i = item.getAttributes().entrySet().iterator();
           i.hasNext();) {
        Map.Entry entry = (Map.Entry)i.next();
        String key = (String)entry.getKey();
        if (indexedAttributes.contains(key)) {
          Map thisIndex = (Map)index.get(key);
          if (thisIndex == null) {
            index.put(key, thisIndex = new HashMap());
          }
          thisIndex.put(entry.getValue(), item);
        }
      }
    }
  }

  public final static Class XmlDataClass = XmlData.class;//(new BasicXmlData()).getClass();
  String type = null;
  String value = null;
  Map attributes = new HashMap();
  Map byType = new HashMap();
  ArrayList allKids = new ArrayList();

  /**
   * Default constructor.
   *
   * <p>Creates an empty instance.
   */
  public BasicXmlData() {}

  /**
   * Creates an instance of BasicXmlData of specified type.
   *
   * @param type the type of data, an arbitrary identifier string.
   */
  public BasicXmlData(String type) {
    this();
    this.type = type;
  }

  /**
   * Creates an instance of specified type and with the specified value.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   *
   * <p><b>Example</b>:
   * <li><code>new BasicXmlData("example", "This is an example").save(<i>filename</i>)</code>
   * <br>will result in the following output:
   * <p><pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   *&lt;example&gt;This is an example&lt;/example&gt;
   * </pre></li>
   */
  public BasicXmlData(String type, String value) {
    this(type);
    this.value = value;
    trim();
  }

  /**
   * Creates an instance of specified type, with the specified value and
   * the kids that are listed in the provided collection.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   * @param kids a Collection of XmlData that attach themselves as kids to the instance being created
   *
   * <p><b>Example</b>:
   * <li><code>new BasicXmlData("example", "This is an example",
   *                       Arrays.asList(new BasicXmlData[] {new BasicXmlData("subexample", "This is a kid of example")}).save(<i>filename</i>)</code>
   * <br>will result in the following output:
   * <p><pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   *&lt;example&gt;
   *  &lt;subexample&gt;This is a kid of example&lt;/subexample&gt;This is an example&lt;/example&gt;
   * </pre></li>
   */
  public BasicXmlData(String type, String value, Collection kids) {
    this(type, value);
    addKids(kids);
  }

  /**
   * Creates an instance of specified type, with the specified value and
   * the kids that are listed in the provided array.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   * @param kids an array of XmlData that attach themselves as kids to the instance being created
   *
   * <p><b>Example</b>:
   * <li><code>new BasicXmlData("example", "This is an example",
   *                       new BasicXmlData[] {new BasicXmlData("subexample", "This is a kid of example")}).save(<i>filename</i>)</code>
   * <br>will result in the following output:
   * <p><pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   *&lt;example&gt;
   *  &lt;subexample&gt;This is a kid of example&lt;/subexample&gt;This is an example&lt;/example&gt;
   * </pre></li>
   */
  public BasicXmlData(String type, String value, BasicXmlData[] kids) {
    this(type, value);
    for (int i = 0; i < kids.length; i++) {
      addKid(kids[i]);
    }
    trim();
  }

  /**
   * Creates an instance of specified type, with the specified value and
   * the attributes that are listed in the provided array as name-value pairs.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   * @param attributes an String array of name-value pairs
   *
   * <p><b>Example</b>:
   * <li><code>new BasicXmlData("example", "This is an example", new String[] {"id", "0123dx", "name", "Whittman"}).save(<i>filename</i>)</code>
   * <br>will result in the following output:
   * <p><pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   *&lt;example id="0123dx" name="Whittman"&gt;This is an example&lt;/example&gt;
   * </pre></li>
   */
  public BasicXmlData(String type, String value, String[] attributes) {
    this(type, value);
    setAttributes(attributes);
  }

  /**
   * Creates an instance of specified type, with the specified value,
   * attributes that are listed in the provided array as name-value pairs, and
   * the kids that are listed in the provided collection.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   * @param attributes an String array of name-value pairs
   * @param kids a Collection of XmlData that attach themselves as kids to the instance being created
   *
   * <p><b>Example</b>:
   * <li><code>new BasicXmlData("example", "This is an example",
   *                       new String[] {"id", "007", "name", "Bond"},
   *                       Arrays.asList(new BasicXmlData[] {new BasicXmlData("subexample", "This is a kid of example")}).save(<i>filename</i>)</code>
   * <br>will result in the following output:
   * <p><pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   *&lt;example id="007" name="Bond"&gt;
   *  &lt;subexample&gt;This is a kid of example&lt;/subexample&gt;This is an example&lt;/example&gt;
   * </pre></li>
   */
  public BasicXmlData(String type, String value, String[] attributes, Collection kids) {
    this(type, value, kids);
    setAttributes(attributes);
  }

  /**
   * Creates an instance of BasicXmlData of specified type, with the specified value,
   * attributes that are listed in the provided array as name-value pairs, and
   * the kids that are listed in the provided array of XmlData.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   * @param attributes an String array of name-value pairs
   * @param kids an array of XmlData that attach themselves as kids to the instance being created
   *
   * <p><b>Example</b>:
   * <li><code>new BasicXmlData("example", "This is an example",
   *                       new String[] {"id", "007", "name", "Bond"},
   *                       new BasicXmlData[] {new BasicXmlData("subexample", "This is a kid of example")}).save(<i>filename</i>)</code>
   * <br>will result in the following output:
   * <p><pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   *&lt;example id="007" name="Bond"&gt;
   *  &lt;subexample&gt;This is a kid of example&lt;/subexample&gt;This is an example&lt;/example&gt;
   * </pre></li>
   */
  public BasicXmlData(String type, String value, String[] attributes, BasicXmlData []kids) {
    this(type, value, kids);
    setAttributes(attributes);
  }

  /**
   * Creates an instance of specified type, with the specified value,
   * attributes that are listed in the provided AttributeList.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   * @param attributes an AttributeList (@see org.xml.sax.AttributeList)
   */
  public BasicXmlData(String type, String value, AttributeList attributes) {
    this(type, value);
    setAttributes(attributes);
  }

  /**
   * Creates an instance of specified type, with the specified value,
   * attributes that are listed in the provided AttributeList, and
   * the kids that are listed in the provided collection.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   * @param attributes an AttributeList (@see org.xml.sax.AttributeList)
   * @param kids a Collection of XmlData that attach themselves as kids to the instance being created
   */
  public BasicXmlData(String type, String value, AttributeList attributes, Collection kids) {
    this(type, value, kids);
    setAttributes(attributes);
  }

  /**
   * Creates an instance of specified type, with the specified value,
   * attributes that are listed in the provided AttributeList, and
   * the kids that are listed in the provided array of XmlData.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   * @param attributes an AttributeList (@see org.xml.sax.AttributeList)
   * @param kids an array of XmlData that attach themselves as kids to the instance being created
   */
  public BasicXmlData(String type, String value, AttributeList attributes, BasicXmlData []kids) {
    this(type, value, kids);
    setAttributes(attributes);
  }

  /**
   * Creates an instance of specified type, with the specified value,
   * attributes that are listed in the provided map (name -> value), and
   * the kids that are listed in the provided collection.
   *
   * @param type the type of data, an arbitrary identifier string.
   * @param value the value of data, any string.
   * @param attributes a Map that maps attributes names to values
   * @param kids a Collection of XmlData that attach themselves as kids to the instance being created
   */
  public BasicXmlData(String type, String value, Map attributes, Collection kids) {
    this(type, value, kids);
    this.attributes = attributes;
  }

  /**
   * Creates an instance from another XmlData object (deep copy).
   *
   * @param org original XmlData
   */
  public BasicXmlData(XmlData org) {
    this(org.getType(),
         org.getValue(),
         new HashMap(org.getAttributes()), new ArrayList());
    for (Iterator i = org.getAllKids().iterator(); i.hasNext();) {
      addKid(new BasicXmlData((XmlData)i.next()));
    }
  }
  /**
   * Creates an instance from the data read from input stream.
   *
   * @param in input stream
   *
   * @throws java.io.IOException
   * @throws java.lang.InstantiationiException
   */
  public BasicXmlData(InputStream in)
    throws java.io.IOException,
           java.lang.InstantiationException {
    this((new XmlReader(in)).read());
  }

  /**
   * Creates an instance from the data read from a file.
   *
   * @param in file containing XML data
   *
   * @throws java.io.IOException
   * @throws java.lang.InstantiationiException
   */
  public BasicXmlData(File in)
    throws java.io.IOException,
           java.lang.InstantiationException {
    this((new XmlReader(in)).read());
  }

  protected static InputStream getInputStream(java.net.URL url)
    throws java.io.IOException,
           java.lang.InstantiationException {
    InputStream input = null;
    java.net.URLConnection conn = url.openConnection();
    conn.connect();
    input = url.openStream();

    if (input == null) {
      throw new java.lang.InstantiationException("Url " + url +
                                                 " does not provide data.");
    }

    return input;
  }

  /**
   * Creates an instance from the data read from URL.
   *
   * @param url url pointing to XML data
   *
   * @throws java.io.IOException
   * @throws java.lang.InstantiationiException
   */
  public BasicXmlData(java.net.URL url)
    throws java.io.IOException,
           java.lang.InstantiationException {
    this(getInputStream(url));
  }

  /**
   * Sets the contents from another XmlData.
   *
   * @param org original XmlData
   */
  public void setXmlContent(XmlData org) {
    type = org.getType();
    value = org.getValue();
    attributes = org.getAttributes();
    byType = new HashMap();
    allKids = new ArrayList();
    this.addKids(org.getAllKids());
  }

  /**
   * Gets the contents of this XmlData (that is, itself).
   *
   * @return itself
   */
  public XmlData getXmlContent() {
    return this;
  }

  /**
   * Deep copy of XmlData.
   *
   * @return deep copy of itself
   */
  public XmlData deepCopy() {
    BasicXmlData result = new BasicXmlData(type,
                                 value);
    for (Iterator i = getAllKids().iterator(); i.hasNext();) {
      result.addKid(((BasicXmlData)i.next()).deepCopy());
    }

    result.setAttributes(attributes);
    return result;
  }

  /**
   * Clones of XmlData, same thing as deepCopy.
   *
   * @return deep copy of itself
   */
  public Object clone() {
    return deepCopy();
  }

  static boolean equal(ArrayList x, ArrayList y) {
    if (y == x)
      return true;

    if (x.size() != y.size()) {
      if (DEBUG) System.out.println("*DBG*" + x + ".size()=" + x.size() +
                                    "!= " + y + ".size()=" + y.size());
      return false;
    }

    ListIterator e1 = x.listIterator();
    ListIterator e2 = y.listIterator();
    while(e1.hasNext() && e2.hasNext()) {
        Object o1 = e1.next();
        Object o2 = e2.next();
        if (!(o1==null ? o2==null : o1.equals(o2))) {
          if (DEBUG) System.out.println("*DBG*" + x + "->" + o1 +
                                        "!= " + y + "->" + o2);
          return false;
        }
    }
    boolean result = !(e1.hasNext() || e2.hasNext());
    if (!result) {
      int i = 5;
    }
    return result;
  }

  static boolean equal(Map x, Map y) {
    if (y == x)
        return true;

    if (x.size() != y.size()) {
      if (DEBUG) System.out.println("*DBG*" + x + ".size()=" + x.size() +
                                    "!= " + y + ".size()=" + y.size());
      return false;
    }

    for (Iterator i = x.keySet().iterator(); i.hasNext();) {
      Object key = i.next();
      if (!y.containsKey(key)) {
        if (DEBUG) System.out.println("*DBG*" + y + " does not contain " + key + " from " + x);
        return false;
      }
      if (!x.get(key).equals(y.get(key))) {
        if (DEBUG) System.out.println("*DBG*" + x + "{" + key + "}->" + x.get(key) +
                                      "!= " + y + "{" + key + "}->" + y.get(key));
        return false;
      }
    }
    return true;
  }

  protected static boolean equal(Object o1, Object o2) {
    if (o1 == null && o2 == null) return true;
    if (o1 == null) return false;
    if (o1 instanceof ArrayList) {
      return equal((ArrayList)o1, (ArrayList)o2);
    } if (o1 instanceof Map) {
      return equal((Map)o1, (Map)o2);
    }
    return o1.equals(o2);
  }

  /**
   * Compares the specified object with this XmlData for equality.
   * Returns true if the given object is also a XmlData, and the two XmlData
   * represent the same data. More formally, two XmlDatas t1 and t2 represent
   * the same data if t1.type.equals(t2.type) and t1.value.equals(t2.value)
   * and t1.getAttributes.equals(t2.attributes()) and t1.getAllKids().equals(t2.getAllKids()).
   *
   * @param o object to be compared for equality with this XmlData.
   * @return true if the specified object is equal to this XmlData.
   *
   * <p><b>Example</b>:
   * <li>
   * <pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   * &lt;example a1="v1" a2="v2"&gt;
   *   &lt;sub1 ax="vx" ay="vy"/&gt;
   *   &lt;sub2 aa="va" ab="vb"/&gt;
   * &lt;/example&gt;
   * </pre>
   * and
   * <pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   * &lt;example a2="v2" a1="v1"&gt;
   *   &lt;sub2 ab="vb" aa="va"/&gt;
   *   &lt;sub1 ay="vy" ax="vx"/&gt;
   * &lt;/example&gt;
   * </pre>
   * are equal.
   * </li>
   */
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }

    if (!(o instanceof BasicXmlData)) {
      return false;
    }

    BasicXmlData other = (BasicXmlData)o;

    if (!equal(other.type, type))  {
      return false;
    }

    if (!(Strings.isAlmostEmpty(value) && Strings.isAlmostEmpty(other.value)) &&
        !equal(other.value, value)) {
      return false;
    }

    if (!equal(other.attributes, attributes)) {
      return false;
    }

//    if (!equal(other.allKids, allKids)) {
//      return false;
//    }

    if (!equal(other.byType, byType)) {
      return false;
    }

    return true;
  }

  /**
   * Saves XmlData to a file.
   *
   * @param filename name of the file to write to
   * @throws java.io.IOException
   */
  public void save(String filename)
    throws java.io.IOException {
    XmlWriter.write(filename, this);
  }

  /**
   * Saves XmlData to a file.
   *
   * @param file file to write to
   * @throws java.io.IOException
   */
  public void save(File file)
    throws java.io.IOException {
    XmlWriter.write(file, this);
  }

  /**
   * Saves XmlData to an output stream.
   *
   * @param os output stream to write to
   * @throws java.io.IOException
   */
  public void save(OutputStream os)
    throws java.io.IOException {
    XmlWriter.write(os, this);
  }

  /**
   * cleans attributes map
   *
   * Just removes all the attributes except xmlns (because)
   */
  public void cleanAttributes() {
    Collection keys = attributes.keySet();
    for (Iterator i = keys.iterator(); i.hasNext();) {
      String key = (String)i.next();
      if (!key.equals("xmlns") &&
          !key.startsWith("xmlns:")) {
        attributes.remove(key);
      }
    }
  }

  /**
   * Sets an attribute value.
   *
   * @param name attribute name
   * @param value attribute value
   */
  public void setAttribute(String name, String value) {
    attributes.put(name, value);
  }

  /**
   * Sets attributes from an array of name-value pairs.
   *
   * @param attributes a String array consisting of name-value pairs.
   */
  public void setAttributes(String[] attributes) {
    for (int i = 0; i < attributes.length; i+=2) {
      setAttribute(attributes[i], attributes[i+1]);
    }
  }

  /**
   * Sets attributes from a Map.
   *
   * @param attributes a Map, name -> value.
   */
  public void setAttributes(Map attributes) {
    for (Iterator i = attributes.entrySet().iterator(); i.hasNext();) {
      Map.Entry entry = (Map.Entry)i.next();
      this.attributes.put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Sets attributes from an AttributeList.
   *
   * @param attributes AttributeList
   */
  public void setAttributes(AttributeList attributes) {
    for (int i = 0; i < attributes.getLength(); i++) {
      setAttribute(attributes.getName(i), attributes.getValue(i));
    }
    Objects.reallocate(this.attributes);
  }

  /**
   * Gets a Map of attributes.
   *
   * @return a Map that maps attribute names to attribute values, all Strings
   */
  public Map getAttributes() {
    return attributes;
  }

  /**
   * Gets XmlData type, which is just a String.
   *
   * @return the type string
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /**
   * Gets a value of XmlData, which is just a String.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of XmlData.
   *
   * @param v new value for XmlData
   *
   * @return itself
   */
  public XmlData setValue(String v) {
    value = v == null ? v : new String(v);
    return this;
  }

  /**
   * Gets the value of a specified attribute.
   *
   * @param name attribute name
   * @return attribute value
   */
  public String getAttribute(String name) {
    return (String)attributes.get(name);
  }

  /**
   * Gets the value of a specified attribute; if there is none, returns default value.
   *
   * @param name attribute name
   * @param defaultValue
   * @return attribute value or defaultValue
   */
  public String getAttribute(String name, String defaultValue) {
    String result = (String)attributes.get(name);
    return (result == null) ? defaultValue : result;
  }

  /**
   * Gets the name of XmlData, which is the value of attribute "name".
   *
   * <p> Same thing as <code>getAttribute("name")</code>.
   *
   * @return the name string
   */
  public String getName() {
    return getAttribute("name");
  }

  /**
   * Gets the id of XmlData, which is the value of attribute "id".
   *
   * <p> Same thing as <code>getAttribute("id")</code>.
   *
   * @return the id string
   */
  public String getId() {
    return getAttribute("id");
  }

  /**
   * Gets a collection of all kids of XmlData. Normally you would not need it;
   * retrieving kids by type is what one normally does, but you never know...
   *
   * @return a Collection of XmlData that contains all kids
   */
  public Collection getAllKids() {
    return allKids;
  }

  /**
   * Gets a Collection of kids of specified type.
   *
   * @param type type of the kids to choose
   * @return all kids that have specified type
   */
  public Collection getKids(String type) {
    return (Collection)byType.get(type);
  }

  protected void setKids(String type, Collection kids) {
    byType.put(type, kids);
    for (int i = allKids.size() - 1; i >= 0; i--) {
      XmlData kid = (XmlData)(allKids.get(i));
      if (kid.getType().equals(type)) {
        allKids.remove(i);
      }
    }
    allKids.addAll(kids);
  }

  /**
   * Gets the number of kids of specified type.
   *
   * @param type
   * @return the number of such kids
   */
  public int getKidCount(String type) {
    Collection kids = getKids(type);
    return (kids == null) ? 0 : kids.size();
  }

  /**
   * Gets a kid of specified type, if any.
   *
   * @param type type of the kid to chose
   * @return any kid that has this type or null if there is no such kid
   */
  public XmlData getKid(String type) {
    Collection kids = getKids(type);

    if (kids == null) return null;

    return (kids.size() > 0) ? ((BasicXmlData)kids.iterator().next()) : null;
  }

  /**
   * Gets a kid at specified position.
   *
   * @param position position of the kid to retrieve, starting with 0;
   * @return any kid that has this type or null if there is no such kid
   */
  public XmlData getKid(int position) {
    return (XmlData)allKids.get(position);
  }

  /**
   * Gets the value of a kid of specified type, if any.
   *
   * @param type
   * @return the value of a kid of specified type or null if there's none
   */
  public String getKidValue(String type) {
    XmlData kid = getKid(type);
    return (kid == null) ? null : kid.getValue();
  }

  /**
   * Sets the value of a kid of specified type, creating it if necessary
   *
   * @param type
   * @param value
   */
  public void setKidValue(String type, String value) {
    XmlData kid = getKid(type);

// Fixed by Asher Szmulewicz
    if (kid == null) {
      if (type != null && value != null)
             addKid(new BasicXmlData(type, value.trim()));
    } else {
      kid.setValue(value);
    }
  }

  /**
   * Gets the value of specified attribute of a kid of specified type, if any
   *
   * @param type String
   * @param attribute String
   * @return String the value of the attribute of the kid of specified type, or null if ther's none
   */
  public String getKidAttribute(String type, String attribute) {
    XmlData kid = getKid(type);
    return kid == null ? null : kid.getAttribute(attribute);
  }

  /**
   * Gets a kid of specified type that has an attribute with a specified value.
   *
   * @param type type of the kid to choose
   * @param attribute attribute name
   * @param value attribute value
   * @return such a kid, if found, or null otherwise
   */
  public XmlData getKid(String type, String attribute, String value) {
    Collection kids = getKids(type);

    if (kids == null) return null;

    for (Iterator i = kids.iterator(); i.hasNext();) {
      BasicXmlData kid = (BasicXmlData)i.next();
      String attr = kid.getAttribute(attribute);
      if (attr != null && attr.equals(value)) return kid;
    }
    return null;
  }

  /**
   * Gets a kid having specified type and specified id.
   *
   * @param type kid type
   * @param id kid id
   * @return a kid of specified type that has the value named "id" equal to id
   */
  public XmlData getKid(String type, String id) {
    return getKid(type, "id", id);
  }

  /**
   * insert a kid into its type slot
   *
   * @param kid kid to insert
   */
  private void insertKidByType(XmlData kid) {
    if (kid == null) return;

    String type = kid.getType();
    Collection slot = getKids(type);

    if (slot == null)
      byType.put(type, (slot = new ArrayList()));

    slot.add(kid);
  }

  /**
   * Adds a kid to the set of kids.
   *
   * @param kid kid to add
   * @return the kid
   */
  public XmlData addKid(XmlData kid) {
    if (kid == null) return kid;

    allKids.add(kid);
    insertKidByType(kid);
    return kid;
  }

  /**
   * returns the position of kid in the list of kids
   * @param kid XmlData
   * @return int kid position, starting at 0; -1 if not found
   */
  public int indexOf(XmlData kid) {
    for (int i = 0; i < allKids.size(); i++) {
      if (allKids.get(i) == kid) return i;
    }
    return -1;
  }

  /**
   * Insertes a kid into the list of kids.
   *
   * @param position int the position where to insert the kid
   * @param kid kid to add
   * @return the kid
   */
  public XmlData insertKid(int position, XmlData kid) {
    if (kid == null) return kid;

    allKids.add(position, kid);
    String type = kid.getType();
    Collection slot = getKids(type);

    if (slot == null)
      byType.put(type, (slot = new ArrayList()));

    slot.add(kid);
    return kid;
  }

  /**
   * Insertes a kid before a specified kid.
   *
   * @param before the kid before which this one should be inserted
   * @param kid kid to add
   * @return the inserted kid, or null if not inserted
   *
   * Does not do anything if the kid before does not exist
   */
  public XmlData insertKid(XmlData before, XmlData kid) {
    if (kid == null) return kid;
    int position = indexOf(before);
    if (position < 0) {
      return null;
    }
    return insertKid(position, kid);
  }

  /**
   * Delete a kid from its type slot
   *
   * @param kid kid to delete
   */
  private void deleteKidByType(XmlData kid) {
    if (kid == null) return;

    String type = kid.getType();
    Collection slot = getKids(type);
    if (slot != null) {
      slot.remove(kid);
    }
    if (slot.size() == 0) {
      byType.remove(type);
    }
  }

  /**
   * Removes a kid from the set of kids.
   *
   * @param kid kid to remove
   */
  public void removeKid(XmlData kid) {
    if (kid == null) return;
    allKids.remove(kid);
    String type = kid.getType();
    Collection slot = getKids(type);
    if (slot != null) {
      slot.remove(kid);
    }
    if (slot.size() == 0) {
      byType.remove(type);
    }
  }

  /**
   * Replaces a kid with another.
   *
   * @param oldKid kid to remove
   * @param newKid kid to insert in its place
   */
  public void replaceKid(XmlData oldKid, XmlData newKid) {
    if (oldKid == null || newKid == null) return;
    allKids.set(allKids.indexOf(oldKid), newKid);
    deleteKidByType(oldKid);
    insertKidByType(newKid);
  }

  /**
   * Adds all XmlData elements from given Iterator to the collection of kids,
   * skipping elements that are not BasicXmlData.
   *
   * @param kids kids to add; can be null
   */
  public void addKids(Iterator kids) {
    if (kids == null) {
      return;
    }
    for (Iterator i = kids; i.hasNext();) {
      Object next = i.next();
      if (next instanceof BasicXmlData) {
        addKid( (BasicXmlData) next);
      }
    }
    trim();
  }

  /**
   * Adds all XmlData elements from given Collection to the set of kids,
   * skipping elements that are not XmlData.
   *
   * @param kids kids to add; can be null
   */
  public void addKids(Collection kids) {
    if (kids != null) {
      addKids(kids.iterator());
    }
  }

  /**
   * Removes all kids of given type.
   *
   * @param type type of kids to remove
   * @return Collection of removed kids
   */
  public Collection removeKids(String type) {
    Collection result = (Collection)byType.remove(type);
    if (result != null) allKids.removeAll(result);

    return result;
  }

  /**
   *
   * <p>Class Policy defines three different casting policies.
   */

  public static class Policy {
    String id;
    String description;

    protected Policy(String newId, String newDescription) {
      id = newId;
      description = newDescription;
    };

    protected Class resolve(String className) throws ClassNotFoundException {
      return Class.forName(className);
    }

    protected XmlData cast(XmlData source, Class targetClass)throws
      SecurityException, NoSuchMethodException, InvocationTargetException,
      IllegalArgumentException, IllegalAccessException, InstantiationException
    {
      Constructor constructor = targetClass.getConstructor(new Class[] {XmlDataClass});
      return (XmlData) constructor.newInstance(new Object[] {source});
    }

    public final static Policy THROW_ON_ERROR =
        new Policy("THROW_ON_ERROR",
                   "Causes casting to throw any exception that happens while casting a node");

    public final static Policy SKIP_ON_ERROR =
        new Policy("SKIP_ON_ERROR",
                   "Causes casting to ignore nodes that failed to cast"){
          protected XmlData cast(XmlData source, Class targetClass) {
            try {
              return super.cast(source, targetClass);
            }
            catch (Exception ex) {
              return null;
            }
          }
          protected Class resolve(String className) {
            try {
              return Class.forName(className);
            }
            catch (ClassNotFoundException ex) {
              return null;
            }
          }
        };

    public final static Policy KEEP_ON_ERROR =
        new Policy("KEEP_ON_ERROR",
                   "Causes casting to keep intact nodes that failed to cast"){
      protected XmlData cast(XmlData source, Class targetClass) {
        try {
          return super.cast(source, targetClass);
        }
        catch (Exception ex) {
          return source;
        }
      }
      protected Class resolve(String className) {
        try {
          return Class.forName(className);
        }
        catch (ClassNotFoundException ex) {
          return null;
        }
      }
    };
  }

  private static XmlData cast(XmlData object, Class targetClass, Policy policy)throws
      SecurityException, NoSuchMethodException, InvocationTargetException,
      IllegalArgumentException, IllegalAccessException, InstantiationException,
      ClassNotFoundException
  {
    return (policy == null ? Policy.THROW_ON_ERROR : policy).cast(object, targetClass);
  }

  /**
   * Casts kids of specified type to a specified class (actually replacing them with the new instances).
   *
   * @param type type of the kids to cast
   * @param clazz class to cast to
   * @return true in case of success, false if casting failed somehow
   *
   * To cast, the class has to have a constructor that takes XmlData as the only argument
   */
  public boolean castKids(String type, Class clazz) {
    try {
//      Constructor[] cl = clazz.getConstructors();
      Constructor constructor = clazz.getConstructor(new Class[] {XmlDataClass});

      Collection oldKids = getKids(type);
      if (oldKids == null) return true;

      for (Iterator i = new ArrayList(oldKids).iterator(); i.hasNext();) {
        BasicXmlData oldKid = (BasicXmlData)i.next();
        BasicXmlData newKid = (BasicXmlData)constructor.newInstance(new Object[] {oldKid} );
        replaceKid(oldKid, newKid);
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Casts XmlData and its kids, recursively, to specified classes, according to the typemap.
   *
   * @param object the objec to cast
   * @param typemap maps types to classes. Type is what XmlData considers a type:
   * a string value of type property.
   * @param policy one of the following: Policy.THROW_ON_ERROR (causes an exception
   * in case of any error), Policy.KEEP_ON_ERROR (keeps nodes that cannot be cast),
   * Policy.SKIP_ON_ERROR (ignores nodes that cannot be cast).
   *
   * @return a new instance of the class corresponding, via typemap, to the type of data.

   * @throws InstantiationException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * Exception are thrown only if policy is Policy.THROW_ON_ERROR
   */

  public static XmlData cast(XmlData object, Map typemap, Policy policy)
      throws InstantiationException, NoSuchMethodException, InvocationTargetException,
      IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

    for (Iterator i = object.getAllKids().iterator(); i.hasNext(); ) {
      XmlData oldKid = (XmlData) i.next();
      XmlData newKid = cast(oldKid, typemap, policy);
      if (newKid != null) {
        object.replaceKid(oldKid, newKid);
      }
    }

    return cast(object, (Class)typemap.get(object.getType()), policy);
  }

  private static Class resolve(String name, Policy policy)
      throws ClassNotFoundException {
    Policy toCall = (policy == null ? Policy.THROW_ON_ERROR : policy);
    return toCall.resolve(name);
  }

  /**
   * Casts XmlData and its kids, recursively, to classes in specified package.
   *
   * @param object the objec to cast
   * @param packageName the name of the package for casting. XmlData types
   * are mapped to classes with the same name inside specified package.
   * packageName can be null: then it is ignored.
   * @param policy one of the following: Policy.THROW_ON_ERROR (causes an exception
   * in case of any error), Policy.KEEP_ON_ERROR (keeps nodes that cannot be cast),
   * Policy.SKIP_ON_ERROR (ignores nodes that cannot be cast).
   *
   * @return a new instance of the class corresponding, via typemap, to the type of data.

   * @throws InstantiationException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * Exception are thrown only if policy is Policy.THROW_ON_ERROR
   */

  public static XmlData castToPackage(XmlData object, String packageName, Policy policy)
      throws InstantiationException, NoSuchMethodException, InvocationTargetException,
      IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

    for (Iterator i = object.getAllKids().iterator(); i.hasNext();) {
      XmlData oldKid = (XmlData)i.next();
      XmlData newKid = castToPackage(oldKid, packageName, policy);
      if (newKid != null) {
        object.replaceKid(oldKid, newKid);
      }
    }
    String className = (packageName == null ? "" : (packageName + ".")) +
                                  object.getType();

    return cast(object, resolve(className, policy), policy);
  }

  /**
   * Casts XmlData and its kids, recursively, to specified classes, according to the typemap.
   *
   * @param typemap maps types to classes. Type is what XmlData considers a type:
   * a string value of type property.
   * @param policy one of the following: Policy.THROW_ON_ERROR (causes an exception
   * in case of any error), Policy.KEEP_ON_ERROR (keeps nodes that cannot be cast),
   * Policy.SKIP_ON_ERROR (ignores nodes that cannot be cast).
   *
   * @return a new instance of the class corresponding, via typemap, to the type of data.

   * @throws InstantiationException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * Exception are thrown only if policy is Policy.THROW_ON_ERROR
   */

  public XmlData cast(Map typemap, Policy policy)
      throws InstantiationException, NoSuchMethodException, InvocationTargetException,
      IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
    return cast(this, typemap, policy);
  }

  /**
   * Casts XmlData and its kids, recursively, to classes in specified package.
   *
   * @param packageName the name of the package for casting. XmlData types
   * are mapped to classes with the same name inside specified package.
   * packageName can be null: then it is ignored.
   * @param policy one of the following: Policy.THROW_ON_ERROR (causes an exception
   * in case of any error), Policy.KEEP_ON_ERROR (keeps nodes that cannot be cast),
   * Policy.SKIP_ON_ERROR (ignores nodes that cannot be cast).
   *
   * @return a new instance of the class which name is the type of the data
   * @throws InstantiationException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * Exception are thrown only if policy is Policy.THROW_ON_ERROR
   */
  public XmlData castToPackage(String packageName, Policy policy)
      throws InstantiationException, NoSuchMethodException, InvocationTargetException,
      IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
    return castToPackage(this, packageName, policy);
  }

  /**
   * Tries to trim the set of kids, so that there are no spare slots left.
   * <p>This operation does not go deep, since it is assumed that you apply it in the process
   * of building XmlData, and the kids are already trimmed.
   */
  public void trim() {
    for (Iterator list = byType.entrySet().iterator();
                  list.hasNext();) {
      Object candidate = ((Map.Entry)list.next()).getValue();
      if (candidate instanceof ArrayList) {
        ((ArrayList)candidate).trimToSize();
      }
    }
    byType = Objects.reallocate(byType);
    attributes = Objects.reallocate(attributes);
  }

  /**
   *
   * <p>An abstract class that stores an expression used in XmlData search/filtering.</p>
   */

    public static abstract class Expression implements Condition {

      /**
       * Parses a string containing search/selection condition.
       * <p>A condition consist of disjunctions of conjunctions of comparisons.
       * Comparisons compare a node's type, value or attribute value to a string constant.
       *
       * @param source the expression to be parsed
       * @return the internal (tree) representation of expression
       */
    public static Expression parse(String source) {
      return source == null ? null :
             Disjunction.parseDisjunction(source);
    };

    private static class Comparison extends Expression {
      static final int OP_NOP = 0;
      static final int OP_EQ  = 1;
      static final int OP_NE  = 2;
      String name;
      String value;
      int op;

      Comparison(String name, String op, String value) {
        this.name  = name;
        this.op    = op.equals("==") ? OP_EQ :
                     op.equals("=")  ? OP_EQ :
                     op.equals("!=") ? OP_NE :
                                       OP_NOP;
        this.value = value;
      }

      static Comparison parseComparison(String expression) {
        int n0 = 0;
        while (n0 < expression.length() && expression.charAt(n0) == ' ') n0++;
        if (n0 >= expression.length()) return null;
        int n1 = n0+1;
        while (n1 < expression.length() && expression.charAt(n1) != ' ') n1++;
        if (n1 >= expression.length()) return null;

        String name = (expression.charAt(n0) == '"')     ?
                        expression.substring(n0+1, n1-1) :
                        expression.substring(n0,   n1);
        int o0 = n1+1;
        while (o0 < expression.length() && expression.charAt(o0) == ' ') o0++;
        if (o0 >= expression.length()) return null;
        int o1 = o0+1;
        while (o1 < expression.length() && expression.charAt(o1) != ' ') o1++;
        if (o1 >= expression.length()) return null;
        String op = expression.substring(o0, o1);
        int v0 = o1+1;
        while (v0 < expression.length() && expression.charAt(v0) != '"') v0++;
        if (v0 >= expression.length()) return null;
        int v1 = v0+1;
        while (v1 < expression.length() && expression.charAt(v1) != '"') v1++;
        if (v1 >= expression.length()) return null;
        String value = expression.substring(v0+1, v1);
        return new Comparison(name, op, value);
      }

      public boolean satisfies(XmlData entity) {
        String toCompare = name.equals(".") ? entity.getValue() :
                           name.equals(":") ? entity.getType()  :
                           entity.getAttribute(name);
        return (op == OP_EQ) ?  value.equals(toCompare) :
               (op == OP_NE) ? !value.equals(toCompare) : false;
      }
    }

    private static class Conjunction extends Expression {
      Comparison[] comparisons;

      Conjunction(ArrayList comparisonList) {

        comparisons = (Comparison[])comparisonList.toArray(
                        new Comparison[comparisonList.size()]);
      }

      static Conjunction parseConjunction(String expression) {
        ArrayList comparisonList = new ArrayList();
        boolean inQuote = false;
        int last = expression.length();
        for (int i = 0; i < expression.length(); i = last+1) {
          for (last = i; last < expression.length(); last++) {
            char c = expression.charAt(last);
            if (c == '"') {
              inQuote = !inQuote;
            } else if (!inQuote && c == '&') {
              break;
            }
          }
          Comparison comparison = Comparison.parseComparison(expression.substring(i, last));
          if (comparison != null)
            comparisonList.add(comparison);
        }
        return new Conjunction(comparisonList);
      }

      public boolean satisfies(XmlData entity) {
        for (int i = 0; i < comparisons.length; i++) {
          if (!comparisons[i].satisfies(entity)) {
            return false;
          }
        }
        return true;
      }
    }

    private static class Disjunction extends Expression {
      Conjunction[] conjunctions;

      Disjunction(java.util.ArrayList conjunctionList) {
        conjunctions = (Conjunction[])conjunctionList.toArray(
                        new Conjunction[conjunctionList.size()]);
      }

      static Disjunction parseDisjunction(String expression) {
        ArrayList conjunctionList = new ArrayList();
        boolean inQuote = false;
        int last = expression.length();
        for (int i = 0; i < expression.length(); i = last+1) {
          for (last = i; last < expression.length(); last++) {
            char c = expression.charAt(last);
            if (c == '"') {
              inQuote = !inQuote;
            } else if (!inQuote && c == '|') {
              break;
            }
          }
          Conjunction conjunction = Conjunction.parseConjunction(expression.substring(i, last));
          if (conjunction != null)
            conjunctionList.add(conjunction);
        }
        return new Disjunction(conjunctionList);
      }

      public boolean satisfies(XmlData entity) {
        for (int i = 0; i < conjunctions.length; i++) {
          if (conjunctions[i].satisfies(entity)) {
            return true;
          }
        }
        return false;
      }
    }

    public abstract boolean satisfies(XmlData entity);
  }

  /**
   * Checks whether this XmlData node satisfies given condition.
   *
   * @param condition
   * @return true if it does satisfy the condition, false otherwise. Any node satisfies an empty condition.
   * This can look arguable, but you have to believe it, it comes from logic.
   */
  public boolean satisfies(Condition condition) {
    return condition == null ? true : condition.satisfies(this);
  }

  /**
   * Checks whether this XmlData node satisfies given expression (presented as string).
   * <p> the string is parsed first, and then the node is checked against the parsed expression.
   *
   * @param expression
   * @return true if it does satisfy the expression, false otherwise. Note that if
   * the expression is void or invalid, any node satisfies this expression.
   */
  public boolean satisfies(String expression) {
    return satisfies(Expression.parse(expression));
  }

  /**
   * Selects a subtree from XmlData, that is, the tree of those nodes that satisfy a condition.
   *
   * <p> The node is included into the resulting tree iff it satisfies the condition;
   * this filtering operation is applied recursively to its kids.
   *
   * @param condition
   * @return XmlData containing only nodes that satisfy the filter
   */
  public XmlData selectTree(Condition condition) {
    if (condition == null) return this;
    if (!condition.satisfies(this)) return null;

    ArrayList goodKids = new ArrayList();
    for (Iterator i = getAllKids().iterator(); i.hasNext();) {
      XmlData candidate = ((XmlData)i.next()).selectTree(condition);
      if (candidate != null) goodKids.add(candidate);
    }

    return new BasicXmlData(type, value, attributes, goodKids);
  }

  /**
   * Selects a subtree from XmlData, that is, the tree of those nodes that satisfy a condition.
   *
   * <p> The node is included into the resulting tree iff it satisfies the condition;
   * this filtering operation is applied recursively to its kids.
   *
   * @param expression the string that contains a filtering expression
   * @return XmlData containing only nodes that satisfy the expression
   *
   * <p><b>Example</b>:
   * <li>for the following data:
   * <br><pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   * &lt;example a1="v1" a2="v2"&gt;
   *   &lt;sub1 ax="vx" ay="vy"/&gt;
   *   &lt;sub2 aa="va" ab="vb"/&gt;
   * &lt;/example&gt;
   * </pre>
   * <code>selectTree("ax != \"vx\"")</code> and
   * <br><code>selectTree("a1 == \"v1\" || ab == \"vb\"")</code>
   * <br> will select the same subtree,
   * <br><pre>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
   * &lt;example a1="v1" a2="v2"&gt;
   *   &lt;sub2 aa="va" ab="vb"/&gt;
   * &lt;/example&gt;
   * </pre>
   *
   * </li>
   */

  public XmlData selectTree(String expression) {
    return selectTree(Expression.parse(expression));
  }

  /**
   * Gets a collection of kid types
   * @return Collection all present kid types (as strings)
   */
  public Collection getKidTypes() {
  return byType.keySet();
  }
}
