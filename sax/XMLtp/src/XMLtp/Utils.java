/*
 * Copyright (c) 2000 Thomas Weidenfeller
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
 * Various utilities to make life with XML easier.
 **/
public class Utils {

	/**
	 * Encode a string, so that it does not contain any characters which
	 * would make it unsuitable for usage as an attribute or contents in
	 * an XML file.
	 * @param result        Result of the encoding is appended to this
	 *                      <code>StringBuffer</code>.
	 * @param s             String to encode.
	 **/
	public final static void XMLEncode(StringBuffer result, String s)
	{
		if(s == null)
			return;

		int sLength = s.length();
		char c;
		for(int i = 0; i < sLength; i++) {
			c = s.charAt(i);
			switch(c) {
			case '&':
				result.append("&amp;");
				break;
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '\'':
				result.append("&apos;");
				break;
			case '"':
				result.append("&quot;");
				break;

			default:
				result.append(c);
				break;
			}
		}
	}

	/**
	 * Encode a string, so that it does not contain any characters which
	 * would make it unsuitable for usage as an attribute or contents in
	 * an XML file.
	 * @param s     String to encode.
	 * @return      Encoded string.
	 **/
	public final static String XMLEncode(String s) {
		if(s == null)
			return "";	// XXX: Should we return null instead?
		StringBuffer result = new StringBuffer(s.length() + 10);
		XMLEncode(result, s);
		return result.toString();
	}

	/**
	 * Encode a string as CDATA, if possible. Throws an exception if the
	 * string itself contains the sequence "]]>" which marks the end of
	 * a CDATA section, or some illegal Unicode char.
	 * @param       result  <code>StringBuffer</code> to which the result
	 *                      should be appended.
	 * @param       s       String to encode.
	 * @exception   IllegalArgumentException String contains characters or
	 *                      character sequences which can not be encoded
	 *                      as CDATA.
	 **/
	public final static void XMLEncodeCDATA(StringBuffer result, String s)
		throws java.lang.IllegalArgumentException
	{
		if(s == null || s.length() == 0)
			return;         // nothing to encode
		if(   s.indexOf("]]>")  >= 0
		   || s.indexOf(0xFFFE) >= 0
		   || s.indexOf(0xFFFF) >= 0
		) {
			// XXX: Everything above 0x7FFFFFFF would also be illegal,
			//      but we are currenty to lazy to test this.
			throw new java.lang.IllegalArgumentException("illegal char sequence");
		}
		result.append("<![CDATA[");
		result.append(s);
		result.append("]]>");
	}

	/**
	 * Encode a string as CDATA, if possible. Throws an exception if the
	 * string itself contains the sequence "]]>" which marks the end of
	 * a CDATA section, or some illegal Unicode char.
	 * @return          Encoded data.
	 * @param       s       String to encode.
	 * @exception   IllegalArgumentException String contains characters or
	 *                      character sequences which can not be encoded
	 *                      as CDATA.
	 **/
	public final static String XMLEncodeCDATA(String s)
		throws java.lang.IllegalArgumentException
	{
		if(s == null)
			return "";
		StringBuffer result = new StringBuffer(s.length() + 12);
		XMLEncodeCDATA(result, s);
		return result.toString();
	}

	/**
	 * Check if a class implements a certain interface.
	 * @param	clss	Class to check.
	 * @param	intrfc	Interface to look for.
	 *			If <code>MYInterface</code> is the interface to
	 *			check for, then the required method parameter
	 *			can be obtained by <code>MYInterface.class</code>.
	 * @return	<code>true</code> if <code>clss</code> or one of its
	 *		super-classes implements the interface.
	 **/
	public static boolean ImplementsInterface(Class clss, Class intrfc)
	{
		if(intrfc != null) {
			//
			// NOTE: Class.getClasses() is not implemented in a
			//	hell of a lot of Java implementations. So
			//	we have to run our own...
			//
			Class interfaces[];
			int i;
			Class c = clss;
			while(c != null) {
				interfaces = c.getInterfaces();
				for(i = 0; i < interfaces.length; i++)
				{
					// System.out.println(interfaces[i]);
					if(interfaces[i].equals(intrfc))
					{
						return true;
					}
				}
				c = c.getSuperclass();
			}
		}
		return false;
	}

	/**
	 * Check if a class implements the XMLtp.Element interface.
	 * @param	clss	Class to check.
	 * @return	<code>true</code> if <code>clss</code> or one of its
	 *		super-classes implements the Element interface.
	 **/
	public static boolean ImplementsElementInterface(Class clss)
	{
		return ImplementsInterface(clss, Element.class);
	}
}
