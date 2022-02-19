package tinyxml.util;

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
 
/**
 * This class implements some methods, that are
 * not included in the CLDC/KVM 1.0B3 and needed
 * by the tinyXML Parser.
 * Default coding is ASCII.
 *
 * @author: Christian Sauer
 */
public final class CharacterUtility {
/**
 * Returns true if character is a letter, false if not..
 * 
 * @return boolean
 * @param c char
 */
public static boolean isLetter(char ch) {
	int ascii = ch;
	if ((ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122))
		return true;
	else
		return false;
}
/**
 * Returns true if character is a letter or a digit, false if not.
 * 
 * @return boolean
 * @param c char
 */
public static boolean isLetterOrDigit(char ch) {
	int ascii = ch;
	if ((ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122) || (ascii >= 48 && ascii <= 57))
		return true;
	else
		return false;
}
}
