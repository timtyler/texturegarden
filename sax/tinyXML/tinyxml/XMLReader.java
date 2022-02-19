package tinyxml;

/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/** 
 * This class implements a Reader to read from the XMLInputStream.
 *
 */

class XMLReader extends Reader {
	// the InputStream to read from
	XMLInputStream input;
	StringBuffer buffer = new StringBuffer();
	int bufpos = 0;
XMLReader(InputStream is) {
	input = (XMLInputStream)is;
}
public void close() throws IOException {
	buffer = null;
	input = null;
}
public void push(char c) {
	buffer.insert(bufpos, c);
}
public void push(String s) {
	buffer.insert(bufpos, s);
}
public int read() throws IOException {
	return input.read();
}
public int read(char[] cbuf) throws IOException {
	return read(cbuf, 0, cbuf.length);
}
public int read(char[] cbuf, int off, int len) throws IOException {
	int n, c;
	for (n = 0; n < len; n++) {
		c = read();
		if (c == -1)
			break;
		cbuf[off + n] = (char) c;
	}
	return n;
}
}
