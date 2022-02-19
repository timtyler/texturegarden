/**
 *
 * <p>Title: MyJavaTools: XML Reader</p>
 * <p>Description: Reads XML source into XmlData.</p>
 * <p>Copyright: This is public domain;
 * The right of people to use, distribute, copy or improve the contents of the
 * following may not be restricted.</p>
 *
 * @author Vlad Patryshev
 * @see http://www.w3.org/TR/REC-xml#sec-intro
 */

package com.myjavatools.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
//import com.sun.xml.parser.Resolver; (deprecated anyway)

public class XmlReader
{
  protected static interface ParserFactory {
    public Parser getParser();
  }

  protected static class SAX1ParserFactory implements ParserFactory {
    public Parser getParser() {
      try {
        return org.xml.sax.helpers.ParserFactory.makeParser();
      }
      catch (ClassNotFoundException ex) {
        return null;
      }catch (IllegalAccessException ex) {
        return null;
      }catch (InstantiationException ex) {
        return null;
      }catch (NullPointerException ex) {
        return null;
      }catch (ClassCastException ex) {
        return null;
      }
    }
  }

  protected static class SAX2ParserFactory implements ParserFactory {
    javax.xml.parsers.SAXParserFactory factory = null;
    public Parser getParser()
    {
      try {
        if (factory == null) {
          factory = javax.xml.parsers.SAXParserFactory.newInstance();
        }
        return factory == null ? null : factory.newSAXParser().getParser();
      }catch (Exception ex) {
        return null;
      }
    }
  }
  protected static ParserFactory[] parserPlant = { new SAX1ParserFactory(),
                                                   new SAX2ParserFactory() };
  protected static ParserFactory factory = null;


  protected InputSource src    = null;
  protected static Parser SAX_parser  = null;
  protected BasicXmlData   curElement  = null;
  protected BasicXmlData   lastElement = null;

  protected class Handler extends HandlerBase
  {
    private Stack branch = new Stack();
    private StringBuffer curValue = new StringBuffer(160);

    public BasicXmlData parse(String fileName)
    throws InstantiationException, IOException {
      try {
/* SAX 1.1, deprecated
       SAX_parser.parse( Resolver.createInputSource(new File(fileName)));
*/
       SAX_parser.parse( new InputSource(new FileReader(fileName)));
      } catch( SAXException e ) {
        e.printStackTrace();
        System.out.println("Error: " + e );
      } catch( InternalError e) {
        e.printStackTrace();
        System.out.println("Error: " + e );
      }
      return null;
    }

    public void characters(char text[], int pos, int len)
    {
      curValue.append(text, pos, len);
    }

    public void startElement(String name, AttributeList attr)
    {
//System.out.println("(" + name + " ");
      branch.push(curElement);
      curValue.delete(0, curValue.length());

      curElement = new BasicXmlData(name, "", attr);
    }

    public void endElement(String name)
    {
//System.out.println(" " + name + "[" + curValue.toString() + "]");
      curElement.setValue(curValue.toString());
      curValue.delete(0, curValue.length());
      curElement.trim();
      lastElement = curElement;
      curElement = (BasicXmlData)branch.pop();
      if (curElement != null) {
        curElement.addKid(lastElement);
      }
//System.out.println(") ");
    }
  }
  protected Handler handler;

  XmlReader() throws InstantiationException {
    try {
      if (factory == null) {
        for (int i = 0; i < parserPlant.length; i++) {
          ParserFactory candidate = parserPlant[i];
          SAX_parser = candidate.getParser();
          if (SAX_parser != null) {
            factory = candidate;
            break;
          }
        }
      } else {
        SAX_parser = factory.getParser();
      }
    } catch (Exception e) {
      System.err.println(e);
      e.printStackTrace(System.err);
      throw new InstantiationException("XmlReader: failed to create SAX parser.");
    }
    try {
      handler = new Handler();
    } catch (Exception e) {
      System.err.println(e);
      e.printStackTrace(System.err);
      throw new InstantiationException("XmlReader: failed to create SAX handler.");
    }
    try {
      SAX_parser.setDocumentHandler(handler);
    } catch (Exception e) {
      System.err.println(e);
      e.printStackTrace(System.err);
      throw new InstantiationException("XmlReader: failed to set SAX document handler.");
    }
  }

  /**
   * Creates an XmlReader from a Reader
   *
   * @param in the reader to read data from
   *
   * @throws java.io.IOException
   * @throws InstantiationException
   */
  public XmlReader(Reader in)
    throws java.io.IOException, InstantiationException {
    this();

    src = new InputSource(in);
  }

  /**
   * Creates an XmlReader from an input stream. Encoding defaults to UTF-8.
   *
   * @param in the stream to read data from
   * @throws java.io.UnsupportedEncodingException
   * @throws java.io.IOException
   * @throws InstantiationException
   */
  public XmlReader(InputStream in)
    throws java.io.UnsupportedEncodingException,
           java.io.IOException,
           InstantiationException {
    this(new InputStreamReader(in, "UTF8"));
  }

  /**
   * Creates an XmlReader to read from a file. Encoding defaults to UTF-8.
   *
   * @param file the file to read data from
   * @throws java.io.UnsupportedEncodingException
   * @throws java.io.IOException
   * @throws InstantiationException
   */
  public XmlReader(File file)
    throws java.io.UnsupportedEncodingException,
           java.io.IOException,
           InstantiationException {
    this(new FileInputStream(file));
  }

  /**
   * Creates an XmlReader to read from a file. Encoding defaults to UTF-8.
   *
   * @param filename the name of the file to read data from
   * @throws java.io.UnsupportedEncodingException
   * @throws java.io.IOException
   * @throws InstantiationException
   */
  public XmlReader(String filename)
    throws java.io.UnsupportedEncodingException,
           java.io.IOException,
           InstantiationException {
    this(new FileInputStream(filename));
  }

  /**
   * Tells whether input is ready to be read.
   *
   * @return true if the next read() is guaranteed not to block for input, false otherwise. Note that returning false does not guarantee that the next read will block.
   * @throws java.io.IOException if an I/O error occurs.
   */
  public boolean ready() throws java.io.IOException {
    return src.getCharacterStream().ready();
  }

  /**
   * Reads the whole XML contents into XmlData
   *
   * @return XmlData obtained from input source.
   * @throws java.io.IOException if an I/O error occurs.
   */
  public BasicXmlData read()
    throws IOException {
    try {
      SAX_parser.parse(src);
      lastElement.trim();
      return lastElement;
    } catch( Exception e) {
      e.printStackTrace();
      System.out.println("Error: " + e);
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Reads the whole XML contents into XmlData
   *
   * @param in the stream to read data from
   * @return XmlData obtained from input source.
   * @throws java.io.IOException if an I/O error occurs.
   * @throws java.lang.InstantiationException if there are problems instantiating XmlData
   */
  public static BasicXmlData read(InputStream in)
    throws java.io.IOException,
           java.lang.InstantiationException {
    return (new XmlReader(in)).read();
  }

  /**
   * Reads the whole XML contents into XmlData
   *
   * @param in the file to read data from
   * @return XmlData obtained from input source.
   * @throws java.io.IOException if an I/O error occurs.
   * @throws java.lang.InstantiationException if there are problems instantiating XmlData
   */
  public static BasicXmlData read(File in)
    throws java.io.IOException,
           java.lang.InstantiationException {
    return (new XmlReader(in)).read();
  }

  /**
   * Reads the whole XML contents into XmlData
   *
   * @param in the reader to read data from
   * @return XmlData obtained from input source.
   * @throws java.io.IOException if an I/O error occurs.
   * @throws java.lang.InstantiationException if there are problems instantiating XmlData
   */
  public static BasicXmlData read(Reader in)
    throws java.io.IOException,
           java.lang.InstantiationException {
    return (new XmlReader(in)).read();
  }

  /**
   * Reads the whole XML contents into XmlData
   *
   * @param s the string containing the XmlData as text
   * @return XmlData obtained from input source.
   * @throws java.io.IOException if an I/O error occurs.
   * @throws java.lang.InstantiationException if there are problems instantiating XmlData
   */
  public static BasicXmlData readFromString(String s)
    throws java.io.IOException,
           java.lang.InstantiationException {
    return read(new StringReader(s));
  }
}

