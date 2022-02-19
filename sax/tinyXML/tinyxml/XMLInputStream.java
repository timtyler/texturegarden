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
 
/*
 * This Class simulates an InputStream, reading from a
 * normal String. The read() method is
 * overridden in order to read from the bytearray of the 
 * converted String instead of a 'normal' InputStream
 * like from a File or a URL.
 *
 * @author: Christian Sauer
 */
public class XMLInputStream extends java.io.InputStream {

	private byte[] myXMLDataBytes = null;
	private int myStreamIndex = 0;
	private int myAmountOfBytes = 0;
/**
 * Constructs a new XMLInputStream from a given string object.
 */
public XMLInputStream(String theXMLData) {
	super();
	myXMLDataBytes = theXMLData.getBytes();
	myAmountOfBytes = myXMLDataBytes.length;
}
/**
 * Do nothing than returning 0.
 * @return int
 */
public int available() {
	return 0;
}
/**
 * Method not necessary, but overridden for security reasons.
 */
public void close() {}
/**
 * Method not necessary, but overridden for security reasons.
 *
 * @param readlimit int
 */
public void mark(int readlimit) {}
/**
 * Marks are not supported by this InputStream.
 * @return boolean
 */
public boolean markSupported() {
	return false;
}
/**
 * Reads and returns the next byte of data, resulting from the String.
 */
public int read() throws java.io.IOException {
	int yByteValue = 0;
	if (myStreamIndex < myAmountOfBytes)
		return myXMLDataBytes[myStreamIndex++];
	else
		return -1;
}
/**
 * Do nothing here.
 */
public void reset() {
}
}
