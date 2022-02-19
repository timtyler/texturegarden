/*
 * Copyright (c) 1999 - 2001 Thomas Weidenfeller
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer
 *     in the documentation and/or other materials provided with the
 *     distribution.
 *
 *   * Neither name of the copyright holders nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package XMLtp;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/**
 * A tiny XML parser/processor. This parser is suitable for usage in own
 * applications which read their data from XML files, or from a network
 * connection. See the
 * <a href="API_users_guide.html">Users Guide</a>. for details.
 *
 * @author	Thomas Weidenfeller
 * @version	1.7
 **/
public class XMLtp
{
//////////////////// PUBLIC Interface - Constructors /////////////////////////

	/**
	 * Construct an XMLtp XML processor/parser, which understands certain
	 * text entity references.
	 * @param	entities	List of text entities and their
	 *				substitution text. An entity
	 *				name (without the leading '&' and
	 *				without the trainling ';') serves as
	 *				a key in the Hashtable, while the
	 *				Object associated with the key is the
	 *				replacement text.
	 **/
	public XMLtp(Hashtable entities)
	{
		this.entities = entities == null ? new Hashtable(8) : entities;

		//
		// Load the entities hash table with the well-formed defined
		// entities
		//
		addTextEntity("amp",  "&");
		addTextEntity("lt",   "<");
		addTextEntity("gt",   ">");
		addTextEntity("apos", "'");
		addTextEntity("quot", "\"");
	}

	/**
	 * Construct an XMLtp XML processor/parser
	 **/
	public XMLtp() {
		this((Hashtable)null);
	}

	/**
	 * Construct an XMLtp XML processor/parser, use ElementDesc(riptions)
	 * for the list of parsable elements, and entity descriptions.
	 * @param	eds	Array of element description
	 * @param	entities	List of text entities and their
	 *				substitution text. An entity
	 *				name (without the leading '&' and
	 *				without the trainling ';') serves as
	 *				a key in the Hashtable, while the
	 *				Object associated with the key is the
	 *				replacement text.
	 **/
	public XMLtp(ElementDesc eds[], Hashtable entities) {
		this(entities);
		addElements(eds);
	}

	/**
	 * Construct an XMLtp XML processor/parser, use element prototypes
	 * for the list of parsable elements, and entity descriptions.
	 * @param	prototypes	Array of Element prototype instances
	 * @param	entities	List of text entities and their
	 *				substitution text. An entity
	 *				name (without the leading '&' and
	 *				without the trainling ';') serves as
	 *				a key in the Hashtable, while the
	 *				Object associated with the key is the
	 *				replacement text.
	 **/
	public XMLtp(Element prototypes[], Hashtable entities) {
		this(entities);
		addElements(prototypes);
	}

	/**
	 * Construct an XMLtp XML processor/parser, use ElementDesc(riptions)
	 * for the list of parsable elements.
	 * @param	eds	Array of element description
	 **/
	public XMLtp(ElementDesc eds[]) {
		this(eds, null);
	}

	/**
	 * Construct an XMLtp XML processor/parser, use element prototypes
	 * for the list of parsable elements.
	 * @param	prototypes	Array of Element prototype instances
	 **/
	public XMLtp(Element prototypes[]) {
		this(prototypes, null);
	}

//////// PUBLIC Interface - Methods ///////////////////////////////////////////

	/**
	 * Parse the input, generate the corresponding elements and returns
	 * the root element.
	 * This is the main method of the XMLtp processor/parser, it
	 * does the actual work.
	 * <p>
	 * Depending on the mode in which the parser is run
	 * ({@link #setStreamingMode}), the method behaves slightly different:
	 * <p>
	 * <b>Normal Mode</b>
	 * <p>
	 * In normal mode, the parser continues to read from the input to
	 * either fully process the first element (the root element), or the
	 * regular end-of-file. If a root element has been found, there are no
	 * additional elements allowed between the end tag of that root element
	 * and end-of-file. This behavior is standard conformant, since the
	 * standard requires that there is at maximum one root element in an
	 * XML document.
	 * <p>
	 * In normal mode the method can only be called once on
	 * a reader. The method returns either with the root element,
	 * <code>null</code> if there is not even a root element in the XML
	 * document, or an exception. In all this tree cases the input reader
	 * is either in an undefined state/position, or at end-of-file, and
	 * should be closed.
	 * <p>
	 * <b>Streaming Mode</b>
	 * <p>
	 * In streaming mode the parser returns as soon as parsing is back at
	 * the top level of the XML document. The parser does not continue to
	 * read until end-of-file is reached. This allowes to use the parser
	 * e.g. in network protocols where a stream of XML elements is
	 * recieved, and where there are in fact multiple root elements in
	 * such a stream.
	 * <p>
	 * In streaming mode the {@link #process} method can be called multiple
	 * times on a reader, until either end-of-file or an error occures.
	 * The method either returns a root element for each call,
	 * <code>null</code> if there was no root element (e.g. processing
	 * instructions can result in returning of <code>null</code>, which is not
	 * an error indication itself), or an exception. Unless there has been
	 * an exception, the method can be called again on the same reader.
	 *
	 *
	 * @param	reader	The input stream the parser takes its input
	 *			XML from. The provided reader is wraped into
	 *			a PushpackReader by the parser.
	 *
	 * @return	If in normal mode, the root element (aka document
	 *              element) of the parsed data. In streaming mode, just
	 *              another root element. <code>null</code> if there is
	 *              nor root element in the input data (this is not an
	 *              error indication itself).
	 *
	 * @exception   XMLException    Error while parsing the data.
	 * @exception   IOException     Error while reading data from the
	 *                              <code>Reader</code>.
	 * @see #setStreamingMode
	 **/
	public Element process(Reader reader)
					throws XMLException, IOException
	{
		return process(new PushbackReader(reader));
	}

	/**
	 * Parse the input, generate the corresponding elements and returns
	 * the root element.
	 * This is the main method of the XMLtp processor/parser, it
	 * does the actual work.<p>
	 * See {@link #process(Reader reader)} for a detailed description of the
	 * parsing modes.
	 *
	 * @param	reader	The input stream the parser takes its input
	 *			XML from.
	 *
	 * @return	If in normal mode, the root element (aka document
	 *              element) of the parsed data. In streaming mode, just
	 *              another root element. <code>null</code> if there is
	 *              no root element in the input data (this is not an
	 *              error indication itself).
	 *
	 * @exception   XMLException    Error while parsing the data.
	 * @exception   IOException     Error while reading data from the
	 *                              <code>PushbackReader</code>.
	 * @see #setStreamingMode
	 * @see #process(Reader reader)
	 **/
	public synchronized Element process(PushbackReader reader)
					throws XMLException, IOException
	{
		//
		// Initialize this processing run
		//
		in      = reader;
		current = null;
		root    = null;
		lineNbr = 1;

		char c;
		while(true) {
			try {
				skipWhitespace();
			} catch(PrematureEOF eof) {
				// this could be a normal EOF also,
				// exit our main loop and check
				break;
			}

			c = read();
			switch(c) {
			case '<':
				//
				// Begin of some tag, figure out which, first
				// assume we have an element name
				//
				c = read();
				switch(c) {
				case '/':	// end tag
					processETag();
					break;
				case '!':	// CDATA, comment, declaration,
						// etc.
					processMisc();
					break;
				case '?':	// processing instruction
					skipUntil("?>");
					break;
				default:	// check for the name
					unread(c);
					processSTag();
					break;
				}
				break;
			default:
				//
				// No markup stuff (well, ok, maybe an entity),
				// process it as a value
				//
				unread(c);
				processValue();
				break;
			}

			//
			// Are we back at the top of the tree?
			//
			if(streamingMode && current == null) {
				return root; // leave remaining data in file
			}
		}

		//
		// If traversing back to the top is necessary, we have a
		// missing closing tag.
		//
		if(current != null)
			throw new MissingETag(lineNbr,
					current.getParentElement().getElementName());

		return root;
	}

	/**
	 * Add a text entity reference to XMLtp. This allows for decoding more
	 * then the standard defined well-formed entities.
	 *
	 * @param	name	Name of the text entity
	 * @param	value	Value (replacement text) of the entity
	 **/
	public synchronized void addTextEntity(String name, String value) {
		entities.put(name, value);
	}

	/**
	 * Add an element to the list of elements the parser recognizes and can
	 * constructs.
	 * @param	name	Name of the element as supposed to be recognised
	 *			by the parser. Since this is XML and not HTML,
	 *			case matters. The parser, however does not check
	 *			if the provided name is a valid XML element
	 *			name.
	 *  @param	eClass	The class of an element type which should be
	 *			generated when an element with the <i>name</i>
	 *			is found. <i>eClass</i> should better be an
	 *			implementation of the interface <i>Element</i>
	 **/
	public  void addElement(String name, Class eClass)
	{
		// to check if we really have a Class that implements the
		// Element interface, we construct an ElementDesc(ription),
		// and add that instead.
		addElement(new ElementDesc(name, eClass));
	}

	/**
	 * Add an element to the list of elements the parser recognizes and can
	 * constructs.
	 * @param	ed	Description of the element to add.
	 **/
	public synchronized void addElement(ElementDesc ed)
	{
		elements.put(ed.name, ed.elementClass);
	}

	/**
	 * Add an element to the list of elements the parser recognizes and can
	 * constructs.
	 * @param	prototype	Prototype instance of the element to add.
	 **/
	public void addElement(Element prototype) {
		// use an ElementDesc to get the class name and the element name
		addElement(new ElementDesc(prototype));
	}

	/**
	 * Add elements to the list of elements the parser recognizes and can
	 * constructs.
	 * @param	eds	Array of element desription of the elements
	 *			 to add.
	 **/
	public void addElements(ElementDesc eds[]) {
		for(int i = 0; i < eds.length; i++) {
			addElement(eds[i]);
		}
	}

	/**
	 * Add elements to the list of elements the parser recognizes and can
	 * constructs.
	 * @param	prototypes	Array of prototype instances of the
	 *				elements to add.
	 **/
	public void addElements(Element prototypes[]) {
		for(int i = 0; i < prototypes.length; i++) {
			addElement(prototypes[i]);
		}
	}

//////// Misc. Public Methods /////////////////////////////////////////////////

	/**
	 * Automatically generate Element instances for unknown elements.
	 * When set to a class supporting the Element interface, and(!)
	 * providing a constructor with a single String argument (the Element's
	 * name), the parser will not throw an exception when it
	 * encounters an unknown element. Instead, it will automagically
	 * generate an element description itself and use it.
	 * If a null pointer is provided, the automatic element generation is
	 * turned off.
	 * This is pure fun, because it allows to read in XML documents without
	 * knowing the elements at all.
	 * @param	e	Class of the Element to generate automatically
	 * @see XMLtp.jfc#GenericElementTreeNode
	 **/
	public synchronized void setAutoGenerateElement(Class e)
		throws NoSuchMethodException
		,      IllegalArgumentException
	{
		if(e != null) {
			if(!Utils.ImplementsElementInterface(e))
				throw new IllegalArgumentException(e +
				" doesn't implement the XMLtp.Element interface.");
			// check if we have our required constructor type,
			// if not, getConstructor throws an exception which we
			// don't catch here.
			e.getConstructor(new Class[] { String.class });
		}
		autoGenerateElement = e;
	}

	/**
	 * Answer which element class is used to automatically generate unknown
	 * elements. If null, automatic generation is turned off.
	 **/
	public Class getAutoGenerateElement()
	{
		return autoGenerateElement;
	}

	/**
	 * How to handle unknown text entitiy references in the input stream.
	 * If set to true, the parser will not throw an exception when it
	 * encounters an unresolvable text entity reference. Instead, the
	 * reference itself will be inserted literally, quoted with '|'s.
	 * @param	f       State of the ignore mechanism
	 * @see XMLtp#getIgnoreUnknownEntities
	 **/
	public synchronized void setIgnoreUnknownEntities(boolean f)
	{
		ignoreUnknownEntities = f;
	}

	/**
	 * Don't throw an exception for unknown text entitiy references in the
	 * input stream.
	 * The parser will not throw an exception when it
	 * encounters an unresolvable text entity reference. Instead, the
	 * reference itself will be inserted litteraly, quoted with '|'s.
	 * @see XMLtp#getIgnoreUnknownEntities
	 **/
	public void setIgnoreUnknownEntities()
	{
		setIgnoreUnknownEntities(true);
	}

	/**
	 * Current state of the unknown entities flag.
	 * @see XMLtp#setIgnoreUnknownEntities
	 **/
	public boolean isIgnoreUnknownEntities() {
		return ignoreUnknownEntities;
	}

	/**
	 * Current state of the unknown entities flag.
	 * @deprecated  Use the bean-conformant method
	 *              {@link #isIgnoreUnknownEntities()} instead.
	 * @see XMLtp#setIgnoreUnknownEntities
	 * @see XMLtp#isIgnoreUnknownEntities
	 **/
	public boolean getIgnoreUnknownEntities()
	{
		return ignoreUnknownEntities;
	}

	/**
	 * Run the parser in <i>streaming</i>, or in <i>normal</i> mode.
	 * The two modes differ in the way the parser reacts when it is back
	 * at the top level of some element hierarch.
	 *
	 * @param       mode    <code>true</code> to turn on streaming mode,
	 *                      <code>false</code> to run in normal mode.
	 * @see #process(Reader reader)
	 * @since 1.7
	 */
	 public void setStreamingMode(boolean mode) {
		streamingMode = mode;
	 }

	 /**
	  * Return the streaming mode flag.
	  * @result      <code>true</code> if running in streaming mode,
	  *              <code>false</code> if running in normal mode.
	  * @see #setStreamingMode
	  * @see #process
	  * @since 1.7
	  */
	public boolean isStreamingMode() {
		return streamingMode;
	}


//////// Element and value processing /////////////////////////////////////////

	/**
	 * We have found a start tag. Now read its names and attributes, and
	 * construct a corresponding element instance.
	 **/
	 protected void processSTag() throws XMLException, IOException
	{
		String name = readName();

		//
		// Look up the element class and construct an element instance
		//
		// If anything goes wrong, we just throw a NoSuchElement
		// error, and don't care for the details here.
		//
		Element element;
		Class   ec = (Class) elements.get(name);
		if(ec == null) {
			//
			// Let's have some real fun :-)
			//
			ec = getAutoGenerateElement();
			if(ec == null) {
				throw new NoSuchElement(lineNbr, name);
			}
		}
		try {
			Constructor co = ec.getConstructor(new Class[] { String.class });
			element = (Element) co.newInstance(new Object[] { name });
		} catch(Exception e) {
			try {
				element  = (Element) ec.newInstance();
			} catch(Exception e1) {
			       throw new NoSuchElement(lineNbr, name, e1);
			}
		}


		element.setParentElement(current);
		if(current == null)
		{
			if(root == null)
				root = element;
			else
				throw new MultipleDocumentElement(lineNbr, name);
		} else {
			try {
				current.addChild(element);
			} catch (XMLException xmle) {
				xmle.setLineNbr(lineNbr);
				throw xmle;
			}
		}
		current = element;

		//
		// Continue parsing for attributes and end of tag
		//
		char c;
		while(true) {
			skipWhitespace();
			c = read();
			switch(c) {
			case '/':
				//
				// Empty element tag, look for the closing '>'
				//
				closeTag();
				/* FALLTHROUGH! */
			case '>':
				return;
			default:
				//
				// Have some fun with attributes
				//

				unread(c);
				processAttribute();
				break;
			}
		}
	}

	/**
	 * Look out for an attributes. Add it to the
	 * current element.
	 **/
	protected void processAttribute() throws XMLException, IOException
	{
		String name;
		char c;

		name = readName();
		skipWhitespace();
		c = read();
		if(c == '=') {
			//
			// Attribute with value
			//
			skipWhitespace();
			c = read();
			if(c != '"' && c != '\'')
				throw new NoQuote(lineNbr, c);
			char endc = c;
//			StringBuffer sbvalue = new StringBuffer(8);
			StringBuffer sbresult = new StringBuffer(8);
			while((c = read()) != endc) {
				if(c == '&') {
					processReference(sbresult);
				} else {
					sbresult.append(c);
				}
			}
			try {
				current.addAttrib(name, sbresult.toString());
			} catch (XMLException xmle) {
				xmle.setLineNbr(lineNbr);
				throw xmle;
			}

		} else {
			//
			// Attribute without value
			//
			unread(c);
			try {
				current.addAttrib(name, null);
			} catch (XMLException xmle) {
				xmle.setLineNbr(lineNbr);
				throw xmle;
			}

		}
	}

	/**
	 * We have found an end tag. Now read its names and close down the
	 * current element.
	 **/
	protected void processETag() throws XMLException, IOException
	{
		String name = readName();
		if(!name.equals(current.getElementName()))
			throw new EndTagDoesNotMatch(lineNbr, name,
				current.getElementName());
		skipWhitespace(); // XXX  What does the XML spec say?
		closeTag();
	}

	/**
	 * Do the final processing of a potential end tag. Including the
	 * closing of the current element. NOTE: The closing is now done in
	 * a way so that the current element can unchain from its parent
	 * in the close() call. This is necessary in cases an element is
	 * actually not used by its parent. If the element could not unchain,
	 * we have a memory leak.
	 */
	protected void closeTag() throws IOException, XMLException
	{
		char c = read();
		if(c != '>')
			throw new TagNotClosed(lineNbr, c);
		Element parent = current.getParentElement();
		try {
			current.close();
		} catch (XMLException xmle) {
			xmle.setLineNbr(lineNbr);
			throw xmle;
		}
		current = parent;
	}


	/**
	 * We have something which looks like a value. That is, we have
	 * something which is supposed to be between a start and an end tag.
	 * Some people would call it PCDate (the W3C guys do :-), but we don't,
	 * since we intend to resolve entities here directly, and entities are
	 * not PCDate... It's also not <i>contents</i>, since this would include
	 * e.g. also nested elements.
	 **/
	protected void processValue() throws XMLException, IOException
	{
		//
		// Sanity check: Are we are least inside the document element?
		//
		if(current == null)
			throw new SyntaxError(read());

		StringBuffer sbv = new StringBuffer(40);
		char c;

		skipWhitespace(); // XXX: Fun with XML whitespace processing...

		while(true) {
			c = read();
			switch(c) {
			case '&':
				processReference(sbv);
				break;
			case '<':
				//
				// Start tag of another element found,
				// add the value to the current element
				// XXX: The trim is against the XML spec.,
				// but it makes the handling of the data
				// much more logical.
				//
				try {
					current.addValue(sbv.toString().trim());
				} catch (XMLException xmle) {
					xmle.setLineNbr(lineNbr);
					throw xmle;
				}
				unread(c);
				return;
			default:
				sbv.append(c);
				break;
			}
		}
	}

	/**
	 * We have something which looks like CDATA. Everything from this point
	 * up to the first occurence of "]]>" is taken literally and added to
	 * our "value" result. As an exemption, we do not skip whitespace here.
	 * Finally, the W3C whitespace handling seems to make sense :-)
	 * We use an hard-coded nested-if check to find the end of the CDATA.
	 * This is not very beautiful, but it's straight-forward to code.
	 * @since 1.5
	 **/
	protected void processCDATA() throws IOException, SyntaxError, XMLException
	{
		//
		// Sanity check: Are we are least inside the document element?
		//
		if(current == null)
			throw new SyntaxError(read());

		StringBuffer sbcd = new StringBuffer(128);
		char c;

		while(true) {
			c = read();
			if(c == ']') {
				c = read();
				if(c == ']') {
					c = read();
					if(c == '>') {
						try {
							// XXX: Maybe we should
							// extend the Element
							// interface with a
							// addCDATA() method?
							current.addValue(sbcd.toString());
						} catch (XMLException xmle) {
							xmle.setLineNbr(lineNbr);
							throw xmle;
						}
						return;
					} else {
						sbcd.append("]]");
					}
				} else {
					sbcd.append(']');
				}
			}
			sbcd.append(c);
		}
	}

	/**
	 * Process various XML constructs which we currenty want to ignore
	 * (Well, ok, we now process CDATA...).
	 * Boy, we are in trouble here, since esp. DOCTYPE allows nested
	 * declarations which we just don't correctly detect...
	 **/
	protected void processMisc()
		throws IOException, PrematureEOF, SyntaxError, XMLException
	{
		char c;
		switch(readChar()) {
		case '-':
			//
			// Maybe a comment
			//
			c = readChar();
			if(c != '-')
				throw new SyntaxError(c);
			skipUntil("-->");
			break;
		case '[':
			//
			// Maybe a CDATA section, but could also be a
			// conditional section. Fortunately, they all end
			// with the same literal.
			//
			String key = readName(); // readName() does more than
						 // needed, but will do for now :-)
			if(key.equals("CDATA") && readChar() == '[') {
				processCDATA();
				return;
			}
			// XXX: Should we try to detect syntax errors?
			skipUntil("]]>");
			break;
		default:
			//
			// Most likely something like <!KEYWORD ...>
			// And here the DOCTYPE declaration kills us, esp.
			// the variant were the markup declaration is given
			// locally.
			//

			skipUntil(">"); // XXX: THIS IS WRONG!
			break;
		}
	}

	/**
	 * Read an XML name from the input stream.
	 * @return	The name from the input stream. The name's length
	 *		might be zero, indicating that there was actually no
	 *		name.
	 **/
	protected String readName() throws IOException, NoName, PrematureEOF
	{

		//
		// We just pray that Java's understanding of a UNICODE letter
		// is in line with the XML standard definition of a letter.
		// At least, Java understands ideographics as letters.
		//
		char c = read();
		if(!(Character.isLetter(c) || c == '_' || c == ':')) {
			unread(c);
			throw new NoName(lineNbr);
		}

		//
		// Once more we are lazy. Instead of exactly matching the
		// valid chars within a name, we accept everything that can
		// be accepted according to the UNICODE standard, plus a few
		// XML specific chars.
		//
		StringBuffer sb = new StringBuffer(8);
		do {
			sb.append(c);
			c = read();
		} while(Character.isUnicodeIdentifierPart(c)
			|| c == '.'
			|| c == '-'
			|| c == '_');
		unread(c);
		return sb.toString();
	}


	/**
	 * Reads and decodes a character or entity reference. Parameter entity
	 * references are, however not decoded.
	 * On entry, the initial & char has already been read.
	 * @param       sb      String buffer which holds the result of the
	 *                      decoding.
	 **/
	protected void processReference(StringBuffer sb)
		throws IOException
		,	PrematureEOF
		,	ReferenceRecursion
		,	ReferenceNotTerminated
		,	NoName
		,	ReferenceIllegal
		,	ReferenceUnknown
	{
		char c = read();
		if(c == '#') {
			int radix = 10;
			int d;
			long val = 0;
			StringBuffer sbname = new StringBuffer(16);
			c = read();
			if(c == 'x') {
				radix = 16;
				c = read();
			}
			while((d = Character.digit(c, radix)) >= 0) {
				 val = val * radix + d;
				 c = read();
			}
			if(c != ';') {
				throw new ReferenceIllegal(lineNbr, "", c);
			}
			sb.append(new Character((char)val));
		} else {
			//
			// We have an entity reference
			//
			unread(c);
			String name = readName();
			c = read();
			if(c != ';')
				throw new ReferenceNotTerminated(lineNbr, name);

			String result = (String)(entities.get(name));
			if(result == null) {
				if(getIgnoreUnknownEntities())  {
					result = "|" + name + "|";
				} else {
					throw new ReferenceUnknown(lineNbr, name);
				}
			}
			sb.append(result);
		}
	}

///////////////////////////////////////////////////////////////////////////////
// Basic, low level character processing routines
///////////////////////////////////////////////////////////////////////////////


	/**
	 * Get the next character from the input stream. EOL characters
	 * are detected and substituted with a blank (no, this is not
	 * 100% XML conform, but IMHO they screwed up the white space
	 * processing in XML).
	 * We also count some lines.
	 **/
	protected char read() throws PrematureEOF, IOException
	{
		int c = in.read();

		//
		// NOTE: Reading from the system properties stuff
		//	the platform's EOL sequence would not help.
		//	Who says people always generate their XML
		//	on the same system they read it???
		//	The next one who will mention it, will be hit
		//	with a clue-by-four!
		//
		switch(c) {
		case '\r':
			//
			// Windoze or Mac EOL
			c = in.read();
			if(c != '\n') {
				in.unread(c);
			}
			/* FALLTHROUGH! */
		case '\n':
			//
			// Good ol' *nix stuff. That's how it has to be...
			//
			lineNbr++;
			return ' ';
		case EOF:
			throw new PrematureEOF(lineNbr);
/* THIS UNICODE STUFF IS FARE TO SLOW WHEN USED IN SUCH A BASIC LOW-LEVEL
   METHOD :-(
		default:
			if((Character.getType((char)c) & Character.LINE_SEPARATOR) != 0) {
				lineNbr++;
				return ' ';
			}
*/
		}
		return (char)c;
	}

	/**
	 * Read a character from the input stream. This method exactly follows
	 * the 'Char' production of the XML spec. That is any ISO/IEC 10646 UCS-4
	 * code (FFFE and FFFF excluded) is valid, too.
	 * Line number counting is a little bit difficult here, but we try.
	 * This method is mostly needed to implement CDATA processing.
	 * @since 1.5
	 * @todo read() and readChar() should maybe be foulded into a single
	 *      method.
	 */
	protected char readChar() throws PrematureEOF, IllegalChar, IOException
	{
		int c = in.read();
		switch(c) {
		case '\r':       // CR
			// DOS or Mac new-line char.
			// Look ahead, just to avoid multiple counting of
			// the same line.
			int c2 = in.read();
			in.unread(c2);
			if(c2 == '\n') {
				return (char)c;
			}
		case '\n':       // LF
			//
			// Unix new-line, increment line counter
			//
			lineNbr++;
		case '\t':       // TAB
			return (char)c;
		case EOF:
			throw new PrematureEOF(lineNbr);
		}
		if( (c >= 0x20    && c <= 0xFFFD    ) ||
		    (c >= 0x10000 && c <= 0x7FFFFFFF))
			return (char)c;
		throw new IllegalChar(lineNbr, (int)c);
	}

	/**
	 * Put back one character into the input stream.
	 * @param	c	Character to store back.
	 **/
	protected void unread(char c) throws IOException {
		in.unread(c);
	}

	/**
	 * Skip whitespace in the input stream. This is the "S" production
	 * of the XML standard.
	 **/
	protected void skipWhitespace() throws IOException, PrematureEOF
	{
		char c;
		//
		// None of the Character.is...() methods exactly satisfies XML's
		// definition of a white space (isSpace() would almost do, but
		// is deprecated :-( ), so we test explicitely for the character
		// values...
		// Note: \n and \r are already handled by read();
		//
		do {
			c = read();
		} while(c == ' ' || c == '\t');
		unread(c);
	}

	/**
	 * Skip XML markup which we don't want to procees.
	 * @oparam	end	Read until the last character given has been
	 *			read.
	 **/
	protected void skipUntil(String end)
		throws IOException, PrematureEOF, IllegalChar
	{
		int needed = end.length();
		int matches = 0;
		char c;
		char first = end.charAt(0);

		while(matches < needed) {
			if((c = readChar()) == end.charAt(matches)) {
				matches++;
			} else {
				matches = c == first ? 1 : 0;
			}
		}
	}

//////// Instance variables ////////////////////////////////////////////////////

	/**
	 * Our input stream
	 **/
	volatile protected PushbackReader in;

	/**
	 * The element which is currently under construction.
	 **/
	protected Element current;

	/**
	 * The root element of the document. This is the first element found
	 * in the input stream.
	 **/
	protected Element root;

	/**
	 * Provide the mapping from an element name to its class
	 **/
	protected Hashtable elements = new Hashtable(8);

	/**
	 * Resolve text entity references
	 **/
	protected Hashtable entities;

	/**
	 * Which class to instantiate for an element if no proper element class
	 * for that particular element has been registered. If null, auto
	 * element generation is turned off.
	 * @see XMLtp#setAutoGenerateElement
	 * @see XMLtp#getAutoGenerateElement
	 **/
	protected Class autoGenerateElement = null;

	/**
	 * Ignore entities not known by the parser. If an entity has a
	 * valid syntax, but can not be resolves, don't throw an exception
	 * if set to true
	 * @see XMLtp#setIgnoreUnknownEntities
	 * @see XMLtp#isIgnoreUnknownEntities
	 **/
	protected boolean ignoreUnknownEntities = false;

	/**
	 * Run in normal or in streaming mode.
	 * @see XMLtp#setStreamingMode
	 * @see XMLtp#isStreamingMode
	 * @since 1.7
	 */
	protected boolean streamingMode = false;

	/**
	 * Current input line number. XML of course doesn't talk about
	 * input in lines (all fscking modern languages don't - boy, was
	 * FORTRAN great), but users somehow like to get error messages
	 * mentioning the line were the problem occured. So we count lines.
	 **/
	volatile protected long lineNbr;

	/**
	 * End-of-file indication as returned by Reader.read().
	 * @see java.io.Reader#read
	 **/
	public static final int EOF = -1; // Someone forgot this in java.io
}

