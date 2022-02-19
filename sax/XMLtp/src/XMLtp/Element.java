/*
 * Copyright (c) 1999, 2000 Thomas Weidenfeller
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

/**
 * Defines an Element's interface, so that it can be constructed by the parser.
 * Elements are the key syntax element of XML. They contain values ("content")
 * and have attributes. Since elements in XML can also nest (and usually do),
 * an element has a parent (if not the root element) and maybe also children.
 * In other words, an element in XML describes how markup for a specific type
 * of information has to look like.
 * <p>
 * It is highly recommended that classes implementing this interface provide
 * a constructor which accepts a single String argument, and use this argument
 * to set what is returned by the <a href="#getName">getName</a> method. You
 * are not supposed to understand this.
 * <pre>
 * public class MyElement implements XMLtp.Element
 * {
 *	public MyElement(String name) { this.name = name; }
 *
 *	// From XMLtp.Element:
 *	public String getName() { return name; }
 *
 *		:
 *		.
 *	protected String name;
 * }
 * </pre>
 * <p>
 * <b>NOTE:</b> Element does not provide an interface to actually retrive the
 * data from an Element implementation once the parser has constructed sad
 * Element instances. XMLtp is only concerned about how the data from an XML
 * file is parsed and read. It is not concerned in how the data is supposed to
 * be interpreted or processed any further. As a consequence, it is up to the
 * user of the Element interface to desing and implement access methods to the
 * parsed data, suitable for the intended purpose.
 * <p>
 * XMLtp.jfc.GenericElementTreeNode is an exception of this principle.
 * GenericElementTreeNode provides an interface for reading the parsed data. It
 * is actually the TreeNode interface suitable for usage with a JTree widget.
 * Own implementations of the Element interface could use a similar interface
 * to access the data (or a completely different one).
 * <p>
 * <b>NOTE:</b> ElementAdapter provides a basic implementation of the Element
 * interface. This implementation can serve as a base class for own Elements,
 * and simplifies the implementation of the principle Element behaviour.
 *
 * @see XMLtp.ElementAdapter
 * @see XMLtp.GenericElement
 * @see XMLtp.jfc.GenericElementTreeNode
 * @see XMLtp.ElementDesc
 *
 * @author Thomas Weidenfeller
 **/
public interface Element {

	/**
	 * Add a nested element to this element. The parser has found a nested
	 * element inside an element. That is, another starting tag has been
	 * found by the parser before the closing tag of the current element
	 * has been encountered.
	 * @param	child	Element instance representing the nested element.
	 * @exception	XMLtp.IllegalChild	The nested element is not
	 *			accepted. E.g. because such an element is not
	 *			supposed to be nested in the current element at
	 *			all.
	 * @exception	XMLtp.NoChildAllowed	The element doesn't have
	 *			nested elements at all.
	 **/
	void addChild(Element child) throws IllegalChild, NoChildAllowed;

	/**
	 * Add content to the element. The parser has found content (that is,
	 * text after the start-tag), and wants to add it to the element.
	 * The parser will call this method multiple times, if other elements
	 * are nested in between the element's contents.
	 * @param	value	The content which has been found. Entities are
	 *			already resolved.
	 * @exception	XMLtp.IllegalValue	The value is not leagal for
	 *			the element. E.g. an element only expecting one
	 *			numeric value might throw this exception if
	 *                      it receives other data.
	 * @exception	XMLtp.NoValueAllowed	The element is an empty element.
	 **/
	void addValue(String value)  throws IllegalValue, NoValueAllowed;

	/**
	 * Add an attribute/value pair to the element. the parser has found an
	 * attribute, or attribute/value pair in the start-tag, and wants to
	 * add it to the element.
	 * @param	attrib	The attribute name
	 * @param	value	The attribute's value. Might be null to
	 *			distinguishe an empty ("") value from a
	 *			non existing one.
	 * @exception	IllegalAttrib	The attribute, or its value is
	 *			not allowed.
	 * @exception	NoAttribAllowed	The element doesn't have
	 *			attributes at all.
	 **/
	void addAttrib(String attrib, String value) throws IllegalAttrib, NoAttribAllowed;

	/**
	 * The element is nested inside another element. The parser wants to
	 * inform the element about it's parent element.
	 * @param	parent	The parent element
	 **/
	void setParentElement(Element parent);

	/**
	 * Return this Element's parent element - if any-
	 * @return	The parent element, or null.
	 **/
	Element getParentElement();

	/**
	 * The element is closed. The parser has found the matching end-tag,
	 * and now informs the element about this. Afte close is called, no
	 * more data (values, attributes, children) will be added to the
	 * element.
	 * @exception	IncompleteElement	The element is not complete
	 *		(e.g. a required attribute or nested element is missing).
	 **/
	void close() throws IncompleteElement;

	/**
	 * Name of the element. The name is used by the parser to look for
	 * matching start and end-tags. Hovever, if the element was added to the
	 * parser via an ElementDesc instance, then the name provided in the
	 * ElementDesc instance takes precedence. Using a separate name in an
	 * ElementDesc instance is not a good idea, however. If the Element
	 * implementation contains a constructor with a single String argument,
	 * this constructor is called with the name provided in the ElementDesc,
	 * allowing the instance to allign to the ElementDesc name.
	 * <p>
	 * In general, the name of an Element should be a valid XML name,
	 * otherwise the parser will not be able to decode it.
	 * @return	The name of the element.
	 * @see		ElementDesc
	 **/
	String getElementName();
}
