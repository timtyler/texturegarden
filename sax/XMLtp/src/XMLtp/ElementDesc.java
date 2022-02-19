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
 * Describes an element for the parser. That is, associates an element name
 * with the class that is supposed to represent such an element.
 * <p>ElementDesc is meta-data about an element. Unfortunately, there is still
 * some code in the parser which does not work on ElementDesc(riptions) but
 * repeatedly query an Elementn instance for this data. This is a principle
 * design error in the parser and will maybe fixed in some future release.
 *
 * @see 	Element
 *
 * @author	Thomas Weidenfeller
 **/
public class ElementDesc {

	/**
	 * Create an element description. The name provided here
	 * should match what instances of elementClass return when
	 * asked for their name via
	 * <a href="XMLtp.Element#getName">Element.getName()</a>.
	 * The class provided should implement the Element interface.
	 * @param	name	The element's name for the parser
	 * @param	elementClass The class which should be instantiated
	 *			if an element with the name is found by the
	 *			parser.
	 * @see		Element
	 **/
	public ElementDesc(String name, Class elementClass)
	{

		//
		// Figure out if we have a class which implements the
		// element interface.
		//
		if(!Utils.ImplementsElementInterface(elementClass))
			throw new IllegalArgumentException(elementClass +
				" doesn't implement the XMLtp.Element interface.");

		this.name         = name;
		this.elementClass = elementClass;
	}


	/**
	 * Create an element description. The element description
	 * is created from an instance of a class implementing the
	 * Element interface. This instance serves as a prototype, and
	 * is not itself used.
	 * @param	prototype	A prototype instance of an Element
	 * @see		Element
	 **/
	public ElementDesc(Element prototype) {
		name         = prototype.getElementName();
		//
		// HACK: Workaround for IMHO some broken VM's which return a
		// 	null for getClass() ... when applied to an interface
		//	type.
		//
		elementClass = prototype.getClass();
		if(elementClass == null) {
			elementClass = ((Object)prototype).getClass();
		}
	}

	/**
	 * Element's name
	 **/
	String name;

	/**
	 * Element's class
	 **/
	Class  elementClass;
}