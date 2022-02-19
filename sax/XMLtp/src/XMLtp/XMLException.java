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
 * Base class for all XMLtp exception.
 **/
public class XMLException extends Exception {

	public XMLException(long lineNbr) {
		super();
		this.lineNbr = lineNbr;
	}

	public XMLException(long lineNbr, String s)
	{
		super(s);
		this.lineNbr = lineNbr;
	}

	public XMLException(long lineNbr, char c)
	{
		this(lineNbr, "'" + c + "'");
	}

	public XMLException(String s)
	{
		this(NO_LINE_NBR, s);
	}

	public XMLException()
	{
		this(NO_LINE_NBR);
	}

	/**
	 * Error description
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(getClass().getName());
		result.append(": XMLtp");
		if(lineNbr != NO_LINE_NBR) {
			result.append(": line ");
			result.append(lineNbr);
		}

		String message = getMessage();
		if(message != null) {
			result.append(": ");
			result.append(message);
		}
		return result.toString();
	}



	/**
	 * Line in the input stream at which the parser threw the exception
	 **/
	public long getLineNbr()
	{
		return lineNbr;
	}

	/**
	 * Allow the parser to insert the line number in exceptions receive
	 * from delegation objects
	 **/
	void setLineNbr(long lineNbr)
	{
		this.lineNbr = lineNbr;
	}

	protected long lineNbr;

	public static final int NO_LINE_NBR	= -1;
}
