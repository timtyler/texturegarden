package com.myjavatools.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.net.URL;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * <p>Title: MyJavaTools: Rss Class</p>
 * <p>Description: Defines a container for RSS, Real Simple Syndication data format.</p>
 * <p>Copyright: This is public domain;
 * The right of people to use, distribute, copy or improve the contents of the
 * following may not be restricted.</p>
 *
 * @author Vlad Patryshev
 * @see <a href="http://www.webreference.com/authoring/languages/xml/rss/intro/">Introduction to RSS</a>
 * @see <a href="http://www.xml.com/pub/a/2002/12/18/dive-into-xml.html?page=1">Another introduction</a>
 * @see <a href="http://web.resource.org/rss/1.0/spec">RSS 1.0 Spec</a>
 * @see <a href="http://blogs.law.harvard.edu/tech/rss">RSS 2.0 Spec</a>
 *
 * <p>The following RSS formats are suppored for reading: 0.91, 0.92, 1.0, 2.0.
 * Output is always 2.0. The version of original document can be retrieved using
 * getOriginalVersion().
 */
public class Rss
    extends BasicXmlData {
  /**
   * Current RSS version
   *
   * {@value}
   */
  public static final String LATEST_VERSION = "2.0";

  private final static Rss getDefaultRss() {
    try {
      return new Rss(new BasicXmlData("rss", null,
                                      new String[] {"version", LATEST_VERSION},
                                      new BasicXmlData[] {new BasicXmlData("channel")}));
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Default empty RSS structure
   */
  static final Rss DEFAULT_RSS = getDefaultRss();

  private XmlData channel = null;

  /**
   * maps titles to items
   */
  private Map itemIndex = new HashMap();

  private String originalVersion;

  /**
   * incorporates general purpose RSS member element functionality
   */
  private static class Member
      extends BasicXmlData {
    /**
     * creates a member based on XmlData
     * @param data XmlData source
     */
    protected Member(XmlData data) {
      super(data);
      setValue(null);
      cleanAttributes();
      setup();
    }

    /**
     * creates XmlData element from name and value
     * @param name String element name
     * @param value String element value
     * @return BasicXmlData
     */
    private static BasicXmlData StringToElement(String name, String value) {
      return new BasicXmlData(name, value);
    }

    /**
     * creates an array of XmlData elements from an array of name-value pairs
     * @param pairs String[] name-value pairs
     * @return BasicXmlData[]
     */
    private static BasicXmlData[] StringsToElements(String[] pairs) {
      BasicXmlData[] result = new BasicXmlData[pairs.length / 2];
      for (int i = 0; i < pairs.length; i += 2) {
        result[i / 2] = StringToElement(pairs[i], pairs[i + 1]);
      }
      return result;
    }

    /**
     * creates a typical member element
     * @param type String member type
     * @param elements BasicXmlData[] member data
     */
    protected Member(String type, BasicXmlData[] elements) {
      this(new BasicXmlData(type, null, elements));
    }

    /**
     * creates a typical member element
     * @param type String member type
     * @param pairs String[] name-value pairs of member data
     */
    protected Member(String type, String[] pairs) {
      this(type, StringsToElements(pairs));
    }

    /**
     * adds a data element to member
     * @param name String element name (a.k.a. type)
     * @param value String element value
     */
    protected void addElement(String name, String value) {
      if (value != null) {
        addKid(StringToElement(name, value));
      }
    }

    /**
     * sets a data element of a member
     * @param name String element name (a.k.a. type)
     * @param value String element value
     *
     * if value == null, the element is removed.
     * if element already exists, its value is changed,
     * otherwise a new element is added
     */
    protected void setElement(String name, String value) {
      if (value == null) {
        removeKids(name);
      }
      else if (getKid(name) == null) {
        addElement(name, value);
      }
      else {
        getKid(name).setValue(value);
      }
    }

    /**
     * gets member description
     * @return String
     */
    public String getDescription() {
      return getKidValue("description");
    }

    /**
     * sets member description
     * @param description String
     */
    public void setDescription(String description) {
      setElement("description", description == null ? null : description.trim());
    }

    /**
     * gets member name
     * @return String name
     */
    public String getName() {
      return getKidValue("name");
    }

    /**
     * gets member title
     * @return String title
     */
    public String getTitle() {
      return getKidValue("title");
    }

    /**
     * gets member url
     * @return String url
     */
    public String getUrl() {
      return getKidValue("url");
    }

    /**
     * sets member url
     * @param String url
     */
    public void setUrl(String url) {
      setKidValue("url", url);
    }

    /**
     * gets member link
     * @return String link
     */
    public String getLink() {
      return getKidValue("link");
    }

    protected void setup() {
      setDescription(getDescription());
    }
  }

  /**
   * <p>Image, Rss member class</p>
   * <p>Contains image data</p>
   */
  public static class Image
      extends Member {

    /**
     * creates an instance of Image data class
     * @param title String image title
     * @param url String image url
     * @param link String image link
     */
    public Image(String title,
                 String url,
                 String link) {
      super("image", new String[] {
            "title", title,
            "url", url,
            "link", link
      });
    }

    /**
     * creates an instance of Image data class
     * @param title String image title
     * @param description String image description
     * @param url String image url
     * @param link String image link
     * @param height String image height
     * @param width String image width
     */
    public Image(String title,
                 String description,
                 String url,
                 String link,
                 String height,
                 String width) {
      super("image", new String[] {
            "title", title,
            "url", url,
            "link", link,
            "height", height,
            "width", width,
            "description", description
      });
    }

    /**
     * creates an instance of Image Rss data from source XmlData
     * @param imageData XmlData containing
     */
    public Image(XmlData imageData) {
      super(imageData);

    }

    protected void setup() {
      super.setup();
      if (getAllKids().size() > 0) {
        if (getHeight() == null) {
          addElement("height", "31");
        }
        if (getWidth() == null) {
          addElement("width", "88");
        }
      }
    }

    /**
     * gets image height
     * @return String height
     */
    public String getHeight() {
      return getKidValue("height");
    }

    /**
     * gets image width
     * @return String width
     */
    public String getWidth() {
      return getKidValue("width");
    }

    public String toString() {
      return "Image {"  +
             "title=\"" + /*getTitle() +*/ "\", " +
             "url=\""   + getUrl() + "\", " +
             "link=\""  + /*getLink() +*/ "\", " +
             "description=\"" + /*getDescription() +*/ "\", " +
             "height=\"" + getHeight() + "\", " +
             "width=\""  + getWidth() + "\"}";
    }
  };

  /**
   * <p>TextInput, Rss member class</p>
   * <p>Contains textinput data</p>
   */
  public static class TextInput
      extends Member {

    /**
     * creates an instance of TextInput
     *
     * @param title String textinput title
     * @param description String textinput description
     * @param name String textinput name
     * @param link String textinput link
     */
    public TextInput(String title,
                     String description,
                     String name,
                     String link) {
      super("textInput", new String[] {
            "title", title,
            "name", name,
            "link", link,
            "description", description});
    }

    /**
     * creates an instance of TextInput based on XmlData
     * @param data XmlData data source
     */
    public TextInput(XmlData data) {
      super(data);
    }

    
    public String toString() {
      return "textInput {"  +
             "title=\"" + /*getTitle()*/ "\", " +
             "name=\""  + getName() + "\", " +
             "link=\""  + /*getLink()*/ "\", " +
             "description=\"" + /*getDescription()*/ "\"}";
    }
  }

  /**
   * <p>Item, Rss member class</p>
   * <p>Contains item data</p>
   */
  public static class Item
      extends Member {

    /**
     * creates an instance of Item containing description only
     * @param description String
     */
    public Item(String description) {
      super("item", new String[] {"description", description});
    }

    /**
     * creates an instance of Item without description
     * @param title String item title
     * @param link String item link
     */
    public Item(String title, String link) {
      super("item", new String[] {
            "title", title,
            "link", link});
    }

    /**
     * creates an instance of Item
     * @param title String item title
     * @param link String item link
     * @param description String item description
     */
    public Item(String title, String link, String description) {
      super("item", new String[] {
            "title", title,
            "link", link,
            "description", description});
    }

    /**
     * creates an instance of Item
     *
     * @param title String item title
     * @param link String item link
     * @param description String item description
     * @param author String item author
     * @param pubDate String item publication date
     */
    public Item(String title,
                String link,
                String description,
                String author,
                String pubDate) {
      this(title, link, description);
      addElement("author", author);
      addElement("pubDate", pubDate);
    }

    /**
     * creates an instance of Item
     *
     * @param title String item title
     * @param link String item link
     * @param description String item description
     * @param author String item author
     * @param pubDate String item publication date
     * @param category String item category
     * @param comments String item comments
     */
    public Item(String title,
                String link,
                String description,
                String author,
                String pubDate,
                String category,
                String comments) {
      this(title, link, description, author, pubDate);
      addElement("category", category);
      addElement("comments", comments);
    }

    /**
     * creates an instance of Item
     *
     * Converts all pre-2.0 formats to 2.0
     *
     * @param data XmlData data source
     */
    public Item(XmlData data) {
      super(data);
      XmlData creator = getKid("dc:creator");
      if (creator != null) {
        removeKid(creator);
        addElement("author", creator.getValue());
      }
      XmlData pubDate = getKid("dc:date");
      if (pubDate != null) {
        removeKid(pubDate);
        addElement("pubDate", pubDate.getValue());
      }
      XmlData enclosure = getKid("enclosure");
      if (enclosure != null) {
        enclosure.setValue(null);
      }
    }

    /**
     * gets item guid
     * @return String item guid
     */
    public String getGuid() {
      return getKidValue("guid");
    }

    /**
     * gets item author
     * @return String item author
     */
    public String getAuthor() {
      return getKidValue("author");
    }

    /**
     * gets item publication date
     * @return String item publication date
     */
    public String getPubDate() {
      return getKidValue("pubDate");
    }

    /**
     * gets item category
     * @return String item category
     */
    public String getCategory() {
      return getKidValue("category");
    }

    /**
     * gets item comments
     * @return String item comments
     */
    public String getComments() {
      return getKidValue("comments");
    }

    /**
     * gets item source name
     * @return String item source name
     */
    public String getSourceName() {
      return getKidValue("source");
    }

    /**
     * gets item source url
     * @return String item source url
     */
    public String getSourceUrl() {
      return getKidAttribute("source", "url");
    }

    /**
     * gets item enclosure url
     * @return String item enclosure url
     */
    public String getEnclosureUrl() {
      return getKidAttribute("enclosure", "url");
    }

    /**
     * gets item enclosure length
     * @return String item enclosure length
     */
    public String getEnclosureLength() {
      return getKidAttribute("enclosure", "length");
    }

    /**
     * gets item item enclosure type
     * @return String item enclosure type
     */
    public String getEnclosureType() {
      return getKidAttribute("enclosure", "type");
    }

    /**
     * sets item source
     *
     * @param url String source url
     * @param name String source name
     */
    public void setSource(String url, String name) {
      removeKids("source");
      addKid(new BasicXmlData("source", name, new String[] {"url", url}));
    }

    /**
     * sets item category
     *
     * @param category String source category
     */
    public void setCategory(String category) {
      setKidValue("category", category);
    }

    /**
     * sets item enclosure
     *
     * @param url String enclosure url
     * @param length String enclosure length
     * @param type String enclosure type
     */
    public void setEnclosure(String url, String length, String type) {
      removeKids("enclosure");
      addKid(new BasicXmlData("enclosure", null,
                              new String[] {"url", url,
                              "length", length,
                              "type", type}));
    }

    public void setExpirationDate(String date) {
      setElement("expirationDate", date);
    }

    public String getExpirationDate() {
      return getKidValue("expirationDate");
    }

    public void setup() {
      super.setup();
      String candidate = getKidValue("dc:subject");
      if (candidate != null) {
        removeKids("dc:subject");
        setCategory(candidate);
      }
    }

    public String toString() {
      return "Item {"  +
             "title=\"" + /*/getTitle()*/ "\", " +
             "link=\""  + /*/getLink()*/ "\", " +
             "description=\"" + /*/getDescription()*/ "\", " +
             "author=\"" + getAuthor() + "\", " +
             "category=\""  + getCategory() + "\", " +
             "pubDate=\"" + getPubDate() + "\", " +
             "comments=\""  + getComments() + "\"}";
    }
  }

  /**
   * creates empty Rss instance
   * @throws InstantiationException it should not
   */
  public Rss() throws InstantiationException {
    super(DEFAULT_RSS);
    setup();
  }

  /**
   * reads Rss contents from a file
   *
   * @param file File data source
   * @throws FileNotFoundException if file not found
   * @throws IOException if could not read
   * @throws InstantiationException if could not parse the contents
   */
  public Rss(File file) throws FileNotFoundException, IOException,
      InstantiationException {
    this(new FileInputStream(file));
  }

  /**
   * reads Rss from input stream
   * @param is InputStream data source
   * @throws IOException if could not read
   * @throws InstantiationException if could not parse the data
   */
  public Rss(InputStream is) throws IOException, InstantiationException {
    super(is);
    setup();
  }

  /**
   * creates an instance of Rss based on data from XmlData
   *
   * @param source XmlData data source
   * @throws InstantiationException if could not extract data
   */
  public Rss(XmlData source) throws InstantiationException {
    super(source);
    setup();
  }

  /**
   * reads Rss from a URL
   *
   * @param url URL data source
   * @throws IOException if could not read data
   * @throws InstantiationException if could not parse data
   */
  public Rss(URL url) throws InstantiationException, IOException {
    super(url);
    setup();
  }

  /**
   * reshuffles the data to conform to ver. 2.0
   *
   * @throws InstantiationException if could not actually parse the data
   */
  private void setup() throws InstantiationException {
    setValue(null);
    channel = getKid("channel");
    channel.setValue(null);
    if (channel == null) {
      throw new InstantiationException("Bad rss: channel missing");
    }

    if ("rdf:RDF".equals(getType())) { // 0.90 or 1.0
      parseRdfFormat();
    }
    else if ("rss".equals(getType())) {
      originalVersion = getVersion();
    }
    else {
      throw new InstantiationException("Unknown RSS format");
    }

    channel.castKids("image", Image.class);
    Collection images = channel.getKids("image");

    // remove empty images
    if (images != null) {
      for (Iterator i = images.iterator(); i.hasNext();) {
        Image image = (Image)i.next();
        if (image.getAllKids().size() == 0) {
          channel.removeKid(image);
        }
      }
    }
    channel.castKids("item", Item.class);
    setDescription(getDescription());
    indexItems();
    setAttribute("version", "2.0");
  }

  private void indexItems() {
    Collection items = channel.getKids("item");
    if (items != null) {
      for (Iterator i = items.iterator(); i.hasNext(); ) {
        Item item = new Item( (XmlData) i.next());
        itemIndex.put(item.getTitle(), item);
      }
    }
  }

  private void parseRdfFormat() {
    XmlData container;
    setType("rss");
    cleanAttributes();
    Collection badKids = new HashSet();
    for (Iterator i = byType.keySet().iterator(); i.hasNext(); ) {
      String key = i.next().toString();
      if (key.startsWith("rdf:")) {
        badKids.add(key);
      }
    }

    for (Iterator i = badKids.iterator(); i.hasNext(); ) {
      byType.remove(i.next());
    }
    channel.cleanAttributes();
    channel.removeKids("items");

    originalVersion = "0.90";
    container = this;

    for (Iterator i = getKids("item").iterator(); i.hasNext(); ) {
      if ( ( (XmlData) i.next()).getKid("description") != null) {
        originalVersion = "1.0";
        break;
      }
    }

    channel.addKids(removeKids("item"));
    channel.addKids(removeKids("image"));
    Collection netscape = removeKids("textinput"); // Netscape's 0.91
    Collection userland = removeKids("textInput"); // Userland's 0.91
    Collection textInputCollection = new HashSet();

    if (netscape != null) {
      textInputCollection.addAll(netscape);
    }
    if (userland != null) {
      textInputCollection.addAll(userland);

    }
    for (Iterator i = textInputCollection.iterator(); i.hasNext(); ) {
      BasicXmlData element = (BasicXmlData) i.next();
      element.setType("textInput");
      String url = element.getAttribute("rdf:resource");
      TextInput textInput = new TextInput(element);
      if (url == null) {
        if (textInput.getAllKids().size() == 0) {
          continue;
        }
      } else {
        if (textInput.getUrl() == null) {
          textInput.setUrl(url);
        }
      }
      channel.addKid(textInput);
    }

    String language = channel.getKidValue("dc:language");
    if (language != null) {
      channel.removeKids("dc:language");
      channel.addKid(new BasicXmlData("language", language));
    }
  }

  /**
   * gets original version<br>
   * Original version is the RSS version of source data
   *
   * @return String original version
   */
  public String getOriginalVersion() {
    return originalVersion;
  }

  /**
   * gets RSS version (which is 2.0)
   *
   * @return String current RSS version
   */
  public String getVersion() {
    return getAttribute("version");
  }

  /**
   * gets RSS title
   *
   * @return String title
   */
  public String getTitle() {
    return channel.getKidValue("title");
  }

  /**
   * sets RSS title
   *
   * @param title String
   */
  public void setTitle(String title) {
    channel.setKidValue("title", title);
  }

  /**
   * gets RSS description
   *
   * @return String description
   */
  public String getDescription() {
    return channel.getKidValue("description");
  }

  /**
   * sets RSS description
   *
   * @param description String
   */
  public void setDescription(String description) {
    if (description == null) {
      channel.removeKids("description");
    }
    else if (getDescription() == null) {
      channel.addKid(new BasicXmlData("description", description.trim()));
    }
    else {
      channel.getKid("description").setValue(description.trim());
    }
  }

  /**
   * gets RSS link
   * @return String link
   */
  public String getLink() {
    return channel.getKidValue("link");
  }

  /**
   * sets RSS link
   *
   * @param link String
   */
  public void setLink(String link) {
    channel.setKidValue("link", link);
  }

  /**
   * gets RSS language
   *
   * @return String RSS language
   */
  public String getLanguage() {
    return channel.getKidValue("language");
  }

  /**
   * sets RSS langauge
   *
   * @param language String
   */
  public void setLanguage(String language) {
    channel.setKidValue("language", language);
  }

  /**
   * gets RSS rating
   *
   * @return String rating
   */
  public String getRating() {
    return channel.getKidValue("rating");
  }

  /**
   * sets RSS rating
   *
   * @param rating String
   */
  public void setRating(String rating) {
    channel.setKidValue("rating", rating);
  }

  /**
   * gets copyright
   * @return String copyright
   */
  public String getCopyright() {
    return channel.getKidValue("copyright");
  }

  /**
   * sets copyright string
   * @param copyright String
   */
  public void setCopyright(String copyright) {
    channel.setKidValue("copyright", copyright);
  }

  /**
   * gets publication date
   * @return String publication date
   */
  public String getPubDate() {
    return channel.getKidValue("pubDate");
  }

  /**
   * sets publication date
   * @param pubDate String
   */
  public void setPubDate(String pubDate) {
    channel.setKidValue("pubDate", pubDate);
  }

  /**
   * gets last build date
   * @return String last build date
   */
  public String getLastBuildDate() {
    return channel.getKidValue("lastBuildDate");
  }

  /**
   * sets last build date
   *
   * @param lastBuildDate String
   */
  public void setLastBuildDate(String lastBuildDate) {
    channel.setKidValue("lastBuildDate", lastBuildDate);
  }

  /**
   * gets managing editor
   *
   * @return String managing editor
   */
  public String getManagingEditor() {
    return channel.getKidValue("managingEditor");
  }

  /**
   * sets managing editor
   *
   * @param managingEditor String
   */
  public void setManagingEditor(String managingEditor) {
    channel.setKidValue("managingEditor", managingEditor);
  }

  /**
   * gets webmaster
   *
   * @return String webmaster
   */
  public String getWebMaster() {
    return channel.getKidValue("webMaster");
  }

  public void setWebMaster(String webMaster) {
    channel.setKidValue("webMaster", webMaster);
  }

  /**
   * gets skip hours
   * @return String skip hours
   */
  public String getSkipHours() {
    return channel.getKidValue("skipHours");
  }

  /**
   * sets skip hours
   *
   * @param skipHours String
   */
  public void setSkipHours(String skipHours) {
    channel.setKidValue("skipHours", skipHours);
  }

  /**
   * gets skip days
   *
   * @return String skip days
   */
  public String getSkipDays() {
    return channel.getKidValue("skipDays");
  }

  /**
   * sets skip days
   *
   * @param skipDays String
   */
  public void setSkipDays(String skipDays) {
    channel.setKidValue("skipDays", skipDays);
  }

  /**
   * gets docs value
   *
   * @return String RSS docs
   */
  public String getDocs() {
    return channel.getKidValue("docs");
  }

  /**
   * sets docs value
   *
   * @param docs String
   */
  public void setDocs(String docs) {
    channel.setKidValue("docs", docs);
  }

  public void setImage(Image image) {
    channel.removeKids("image");
    channel.addKid(image);
  }

  /**
   * sets RSS Image element
   *
   * @param title String image title
   * @param description String image description
   * @param url String image url
   * @param link String image link
   * @param width String image width
   * @param height String image height
   */
  public void setImage(String title,
                       String description,
                       String url,
                       String link,
                       String width,
                       String height) {
    setImage(new Image(title, description, url, link, width, height));
  }

  /**
   * sets image from XmlData contents
   *
   * @param imageData XmlData data source
   */
  private void setImage(XmlData imageData) {
    if (imageData == null) {
      channel.removeKids("image");
    }
    else {
      setImage(imageData);
    }
  }

  /**
   * gets RSS image
   *
   * @return Image RSS image element
   */
  public Image getImage() {
    return (Image) channel.getKid("image");
  }

  /**
   * sets RSS textinput element
   *
   * @param textInput TextInput
   */
  public void setTextInput(TextInput textInput) {
    channel.removeKids("textInput");
    channel.addKid(textInput);
  }

  /**
   * sets RSS textinput element
   *
   * @param title String textinput title
   * @param description String textinput description
   * @param name String textinput name
   * @param link String textinput link
   */
  public void setTextInput(String title, String description, String name,
                           String link) {
    setTextInput(new TextInput(title, description, name, link));
  }

  /**
   * sets RSS textinput from XmlData
   *
   * @param data XmlData data source
   */
  private void setTextInput(XmlData data) {
    if (data == null) {
      channel.removeKids("textInput");
    }
    else {
      setTextInput(data);
    }
  }

  /**
   * gets RSS textinput
   *
   * @return TextInput
   */
  public TextInput getTextInput() {
    return (TextInput) channel.getKid("textInput");
  }

  /**
   * adds an item to RSS
   *
   * @param item Item
   */
  public void addItem(Item item) {
    Item oldOne = getItem(item.getTitle());
    if (oldOne != null) {
      channel.removeKid(oldOne);
    }
    channel.addKid(item);
    itemIndex.put(item.getTitle(), item);
  }

  /**
   * adds a new item to RSS
   *
   * @param title String item title
   * @param link String item link
   * @param description String item description
   */
  public void addItem(String title, String link, String description) {
    addItem(new Item(title, link, description));
  }

  /**
   * adds a new item to RSS
   *
   * @param data XmlData data source for new item
   */
  private void addItem(XmlData data) {
    addItem(new Item(data));
  }

  /**
   * gets a collection of all items in RSS
   *
   * @return Collection all RSS items
   */
  public Collection getItems() {
    return channel.getKids("item");
  }

  /**
   * Returns number of items in RSS
   *
   * @return int the number of items, 0 if none
   */
  public int getItemCount() {
    return channel.getKidCount("item");
  }

  /**
   * gets RSS item by title
   *
   * @param title String item title to look for
   * @return Item with specified title, or null if not found
   *
   * This should have better been named findItem, but Java tradition suggests using 'get'
   */
  public Item getItem(String title) {
    return (Item) itemIndex.get(title);
  }

  /**
   * sets RSS generator
   *
   * @param generator String
   */
  public void setGenerator(String generator) {
    channel.addKid(new BasicXmlData("generator", generator));
  }

  /**
   * gets RSS generator
   *
   * @return String generator
   */
  public String getGenerator() {
    return channel.getKidValue("generator");
  }

  /**
   * sets RSS category
   *
   * @param category String
   */
  public void setCategory(String category) {
    channel.setKidValue("category", category);
  }

  /**
   * gets category
   *
   * @return String category
   */
  public String getCategory() {
    return channel.getKidValue("category");
  }

  /**
   * sets RSS TTL (Time-To-Live, number of days RSS is valid)
   *
   * @param ttl String
   */
  public void setTtl(String ttl) {
    channel.setKidValue("ttl", ttl);
  }

  /**
   * gets RSS TTL (Time-To-Live, number of days RSS is valid)
   *
   * @return String RSS TTL
   */
  public String getTtl() {
    return channel.getKidValue("ttl");
  }

  /**
   * gets RSS cloud
   *
   * @return XmlData cloud (see 2.0 standard for description)
   */
  public XmlData getCloud() {
    return channel.getKid("cloud");
  }

  /**
   * sets RSS cloud
   *
   * @param cloud XmlData
   */
  public void setCloud(XmlData cloud) {
    channel.removeKids("cloud");
    channel.addKid(cloud);
  }

  /**
   * finds RSS item by its description
   *
   * @param description String
   * @return Item the first (random) item that has this description, or null
   */
  public Item findItemByDescription(String description) {
    String toLookFor = description.trim();

    Collection itemCollection = channel.getKids("item");
    if (itemCollection == null) {
      return null;
    }
    for (Iterator i = itemCollection.iterator(); i.hasNext(); ) {
      Item item = (Item) i.next();
      String itemDescription = item.getDescription();
      if (toLookFor.equals(itemDescription)) {
        return item;
      }
    }
    return null;
  }

  /**
   * finds an item by its guid
   *
   * @param guid String
   * @return Item any item that has this guid, or null if none found
   */
  public Item findItemByGuid(String guid) {
    Collection itemCollection = channel.getKids("item");
    if (itemCollection == null) {
      return null;
    }
    for (Iterator i = itemCollection.iterator(); i.hasNext(); ) {
      Item item = (Item) i.next();
      String itemGuid = item.getGuid();
      if (guid.equals(itemGuid)) {
        return item;
      }
    }
    return null;
  }

  /**
   * finds item by its url
   *
   * @param url String
   * @return Item an item that has specified url (or null if none)
   */
  public Item findItemByUrl(String url) {
    Collection itemCollection = channel.getKids("item");
    if (itemCollection == null) {
      return null;
    }
    for (Iterator i = itemCollection.iterator(); i.hasNext(); ) {
      Item item = (Item) i.next();
      if (url.equals(item.getSourceUrl()) ||
          url.equals(item.getEnclosureUrl())) {
        return item;
      }
    }
    return null;
  }
}
