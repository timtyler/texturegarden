package com.texturegarden.xml.saxdriver;

import org.xml.sax.Attributes;

public class AttributesExtended implements Attributes, SAXDriverConstants {
  private AttributesWrapper aw;

  AttributesExtended(AttributesWrapper aw) {
    this.aw = aw;
  }

  public int getLength() {
    return aw.attribute_values.size();
  }

  public String getURI(final int i) {
    return (String) aw.attribute_uris.elementAt(i);
  }

  public String getLocalName(final int i) {
    return (String) aw.attribute_local_names.elementAt(i);
  }

  public String getQName(final int i) {
    return (String) aw.attribute_qnames.elementAt(i);
  }

  public String getType(final int i) {
    return CDATA;
  }

  public String getValue(final int i) {
    return (String) aw.attribute_values.elementAt(i);
  }

  public int getIndex(final String uri, final String local_part) {
    int i = -1;

    while (true) {
      i = aw.attribute_local_names.indexOf(local_part, i + 1);

      if (i == -1 || uri.equals(aw.attribute_uris.elementAt(i))) {
        return i;
      }
    }
  }

  public int getIndex(final String q_name) {
    return aw.attribute_qnames.indexOf(q_name);
  }

  public String getType(final String uri, final String local_name) {
    return CDATA;
  }

  public String getType(final String q_name) {
    return CDATA;
  }

  public String getValue(final String uri, final String local_name) {
    final int index = this.getIndex(uri, local_name);

    return (index == -1) ? null : (String) aw.attribute_values.elementAt(index);
  }

  public String getValue(final String q_name) {
    final int index = aw.attribute_qnames.indexOf(q_name);

    return (index == -1) ? null : (String) aw.attribute_values.elementAt(index);
  }

  public void setAw(AttributesWrapper aw) {
    this.aw = aw;
  }

  public AttributesWrapper getAw() {
    return aw;
  }

  //public String getURI(int index) {
   // return "getURI";
  //}
}