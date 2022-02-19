/*
 * Copyright (c) 1999 Thomas Weidenfeller
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
 * A simple implementation of an element, provides the absolute minimal
 * code to work as an element. It doesn't allow children, it doesn't allows
 * values, and it doesn't allow attributes.
 *
 * @see		Element
 * @author	Thomas Weidenfeller
 **/
public abstract class ElementAdapter implements Element, Cloneable
{
	/**
	 * Construct a simple ElementAdapter. The element's name is set
	 * in the constructor.
	 * @param	name	Element's name.
	 **/
	protected ElementAdapter(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * Clone the object. We do a shallow copy, with regards to the
	 * parent. This keeps the tree structure intact from a child point
	 * of view.
	 **/
	public synchronized Object clone() {
		try {
			ElementAdapter ea = (ElementAdapter)super.clone();
			ea.closed 	= this.closed;
			ea.elementName 	= this.elementName == null ?
					    null : new String(this.elementName);
			ea.parent	= this.parent;
			return (Object)ea;
		} catch(Exception e) {
			// Shouldn't happen
			throw new InternalError();
		}

	}

	/**
	 * @see Element#addChild
	 **/
	public void addChild(Element child) throws IllegalChild, NoChildAllowed {
		throw new NoChildAllowed(child);
	}

	/**
	 * @see Element#addValue
	 **/
	public void addValue(String value) throws IllegalValue, NoValueAllowed {
		throw new NoValueAllowed(value);
	}

	/**
	 * @see Element#addAttrib
	 **/
	public void addAttrib(String attrib, String value) throws IllegalAttrib, NoAttribAllowed {
		throw new NoAttribAllowed(attrib, value);
	}

	/**
	 * @see Element#setParentElement
	 **/
	public void setParentElement(Element parent) {
		//
		// XXX: This currently allows re-parenting. Maybe we should
		// change this?
		// if(this.parent != null && this.parent != parent) throw something
		//
		this.parent = parent;
	}

	/**
	 * @see Element#getParentElement
	 **/
	public Element getParentElement() {
		return parent;
	}

	/**
	 * @see Element#getElementName
	 **/
	public String getElementName() {
		return elementName;
	}

	/**
	 * @see Element#close
	 * @see ElementAdapter#closed
	 **/
	public void close() throws IncompleteElement {
		closed = true;
	}

	/**
	 * The element's parent - if any
	 **/
	protected Element parent = null;
	/**
	 * The element's name.
	 **/
	protected String  elementName;
	/**
	 * Element's close state. Set via close().
	 * @see	ElementAdapter#close
	 **/
	protected boolean closed = false;
}
