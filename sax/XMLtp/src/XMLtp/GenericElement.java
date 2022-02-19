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
import java.util.*;

/**
 * General purpose, forgiving Element implementation. This is mostly good
 * for early phases of a project, to avoid having to implement too many
 * attribute/value/structure checks.
 * <p>
 * A generic element is inefficient, esp. because it uses a hashtable for
 * attribute handling.
 * <p>
 * NOTE: To read the data which has actually parsed into a GenericElement,
 * you have to sub-class the generic element, write your own access methods to
 * the GenericElement.attributes and GenericElement.data fields, and do whatever
 * you want with the content of this fields. XMLtp usually is only concerned in
 * reading and parsing XML data into Elements, and not the interpretation or
 * processing of sad data.
 *
 * @see ElementAdapter
 * @see Element
 * @author Thomas Weidenfeller
 **/
public abstract class GenericElement extends ElementAdapter {

	/**
	 * Construct a generic Element. The element's name is set
	 * in the constructor.
	 * @param	name	Element's name.
	 **/
	public GenericElement(String name) {
		super(name);
	}

	/**
	 * @see Element#addChild
	 **/
	public void addChild(Element child) throws IllegalChild, NoChildAllowed {
		data.addElement(child);
	}

	/**
	 * @see Element#addValue
	 **/
	public void addValue(String value) throws IllegalValue, NoValueAllowed {
		data.addElement(value);
	}

	/**
	 * @see Element#addAttrib
	 **/
	public synchronized void addAttrib(String attrib, String value)
	throws IllegalAttrib, NoAttribAllowed
	{
		if(attributes.containsKey(attrib))
			throw new IllegalAttrib(attrib, value);

		if(value == null)
			attributes.put(attrib, new Null());
		else
			attributes.put(attrib, value);
	}



	/**
	 * Store values and embedded elements. One vector, instead of separate
	 * ones is used, to allow to identify the sequence of intermangled
	 * content and elements.
	 **/
	protected Vector data = new Vector(0);

	/**
	 * Stores attributes as key/value pairs
	 **/
	protected Hashtable attributes = new Hashtable(1);
}

