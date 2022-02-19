/**
 *
 * <p>Title: MyJavaTools: XML Interface</p>
 * <p>Description: Defines a contract for storing Xml data.</p>
 * <p>Copyright: This is public domain;
 * The right of people to use, distribute, copy or improve the contents of the
 * following may not be restricted.</p>
 *
 * @author Vlad Patryshev
 * @see http://www.myjavatools.com/projects/PracticalXML/
 *
 * <p>Xml defines methods that it is natural to have for a structure that is retrieved from or stored to XML files.
 * It has a type, a value, a map of attributes and a set of kids (nodes) grouped by their types.
 * Inside a type, kids are arranged as a collection, so that they do have some order.
 * Kids are also Xml.
 *
 * <p>Note that the hierarchy is unidirectional: kids have no knowledge of contaners they are members of.
 * This sounds natural when we talk about containers; files do not know about their current location,
 * web pages do not generally know their full url - but somehow in DOM it is all different (and this is what makes it hard to handle: parent references).
 */

package com.myjavatools.xml;

import java.util.Collection;
import java.util.Map;

import org.xml.sax.AttributeList;

public interface XmlData
{

  /**
   * Compares contents with other Object
   *
   * @param other Object
   * @return boolean
   */
  public boolean equals(Object other);

  /**
   * Sets the contents from another XmlData.
   *
   * @param org original XmlData
   */

  public void setXmlContent(XmlData org);

  /**
   * Gets the contents of this XmlData (probably, itself).
   *
   * @return itself
   */
  public XmlData getXmlContent();

  /**
   * Clones XmlData, same thing as deepCopy.
   *
   * @return deep copy of itself
   */
  public Object clone();

 /**
   * cleans attributes map
   *
   * Just removes all the attributes
   */

  public void cleanAttributes();

  /**
   * Sets an attribute value.
   *
   * @param name attribute name
   * @param value attribute value
   */
  public void setAttribute(String name, String value);

  /**
   * Sets attributes from an array of name-value pairs.
   * The preexisting attributes are not removed by this operation.
   *
   * @param attributes a String array consisting of name-value pairs.
   */
  public void setAttributes(String[] attributes);

  /**
   * Sets attributes from an AttributeList.
   *
   * @param attributes AttributeList
   */
  public void setAttributes(AttributeList attributes);

  /**
   * Sets attributes from a Map, name -> value.
   * The preexisting attributes are not removed by this operation.
   *
   * @param attributes a Map, attributeName -> attributeValue.
   */
  public void setAttributes(Map attributes);

  /**
   * Gets a Map of attributes.
   * The preexisting attributes are not removed by this operation.
   *
   * @return a Map that maps attribute names to attribute values, all Strings
   */
  public Map getAttributes();

  /**
   * Gets XmlData type, which is just a String.
   *
   * @return the type string
   */
  public String getType();

  /**
   * Gets a value of XmlData, which is just a String.
   *
   * @return the value
   */
  public String getValue();

  /**
   * Sets the value of XmlData.
   *
   * @param v new value for XmlData
   *
   * @return itself
   */
  public XmlData setValue(String v);

  /**
   * Gets the value of a specified attribute.
   *
   * @param name attribute name
   * @return attribute value
   */
  public String getAttribute(String name);

  /**
   * Gets the value of a specified attribute; if there is none, returns default value.
   *
   * @param name attribute name
   * @param defaultValue
   * @return attribute value or defaultValue
   */
  public String getAttribute(String name, String defaultValue);

  /**
   * Gets the name of XmlData, which is the value of attribute "name".
   *
   * @return the name string
   */
  public String getName();

  /**
   * Gets the id of XmlData, which is the value of attribute "id".
   *
   * @return the id string
   */
  public String getId();

  /**
   * Gets a collection of all kids of XmlData. Normally you would not need it;
   * retrieving kids by type is what one normally does, but you never know...
   *
   * @return a Collection of XmlData that contains all kids
   */
  public Collection getAllKids();

  /**
   * Gets a Collection of kids of specified type.
   *
   * @param type type of the kids to choose
   * @return all kids that have specified type
   */
  public Collection getKids(String type);

  /**
   * Gets the number of kids of specified type.
   *
   * @param type
   * @return the number of such kids
   */
  public int getKidCount(String type);

  /**
   * Gets a kid at specified position.
   *
   * @param position position of the kid to retrieve, starting with 0;
   * @return any kid that has this type or null if there is no such kid
   */
  public XmlData getKid(int position);

  /**
   * Gets a kid of specified type, if any.
   *
   * @param type type of the kid to chose
   * @return any kid that has this type or null if there is no such kid
   */
  public XmlData getKid(String type);

  /**
   * Gets the value of a kid of specified type, if any.
   *
   * @param type
   * @return the value of a kid of specified type or null if there's none
   */
  public String getKidValue(String type);

  /**
   * Sets the value of a kid of specified type, creating it if necessary.
   *
   * @param type
   * @param value
   */
  public void setKidValue(String type, String value);

  /**
   * Gets the value of specified attribute of a kid of specified type, if any
   *
   * @param type String
   * @param attribute String
   * @return String the value of the attribute of the kid of specified type, or null if ther's none
   */
  public String getKidAttribute(String type, String attribute);

  /**
   * Gets a kid of specified type that has an attribute with a specified value.
   *
   * @param type type of the kid to choose
   * @param attribute attribute name
   * @param value attribute value
   * @return such a kid, if found, or null otherwise
   */
  public XmlData getKid(String type, String attribute, String value);

  /**
   * Gets a kid having specified type and specified id.
   *
   * @param type kid type
   * @param id kid id
   * @return a kid of specified type that has the value named "id" equal to id
   */
  public XmlData getKid(String type, String id);

  /**
   * Adds a kid to the set of kids.
   *
   * @param kid kid to add
   * @return the kid
   */
  public XmlData addKid(XmlData kid);

  /**
   * Removes a kid from the set of kids.
   *
   * @param kid kid to remove
   */
  public void removeKid(XmlData kid);

  /**
   * Replaces a kid with another kid.
   *
   * @param oldKid kid to remove
   * @param newKid kid to insert in its place
   */
  public void replaceKid(XmlData oldKid, XmlData newKid);

  /**
   * Adds all XmlData elements from given Collection to the set of kids,
   * skipping elements that are not XmlData.
   *
   * @param kids
   */
  public void addKids(Collection kids);
  /**
   * Removes all kids of given type.
   *
   * @param type type of kids to remove
   * @return Collection of removed kids
   */
  public Collection removeKids(String type);

  /**
   * <p>XmlData.Condition has just one method that checks whether XmlData satisfies
   * a condition. Best used in search or selection in XmlData.</p>
   */
  public interface Condition {

    /**
     * Checks whether XmlData satisfies a condition.
     *
     * @param data the data to check
     * @return true if yes, it satisfies
     */
    public boolean satisfies(XmlData data);
  }

  public XmlData selectTree(Condition condition);


  /**
   * Casts kids of specified type to a specified class (actually replacing them with the new instances).
   *
   * @param type type of the kids to cast
   * @param clazz class to cast to
   * @return true in case of success, false if casting failed somehow
   *
   * To cast, the class has to have a constructor that takes XmlData as the only argument
   */
  public boolean castKids(String type, Class clazz);
}
