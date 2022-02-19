/*
 * Copyright (c) 1999, 2001 Thomas Weidenfeller
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


package XMLtp.jfc;	// XMLtp stuff that depends on Swing/JFC

import XMLtp.*;
import java.util.*;
import javax.swing.tree.*;

/**
 * Implements an Element class, that (a) can hold elements (and
 * their sub-hierarchy) with whatever element name has been found by the
 * parser, and (b) supports the Swing MutableTreeNode interface, so the element
 * and its contained elements can be visualized directly by a JTree
 * widget.
 */
public class GenericElementTreeNode
	extends GenericElement
	implements MutableTreeNode {

	public GenericElementTreeNode(String name) {
		super(name);
	}

	public GenericElementTreeNode() {
		super("[genericTN-unknown]");	// XXX: i18n?
	}

//////// INTERFACE TreeNode ////////////////////////////////////////////////////


	public TreeNode getChildAt(int childIndex) {
		return (TreeNode) data.elementAt(childIndex);
	}

	public int getChildCount() {
		return data.size();
	}

	public TreeNode getParent() {
		return (TreeNode) parent;
	}

	public int getIndex(TreeNode node) {
		return data.indexOf(node);
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return data.isEmpty();
	}

	public Enumeration children() {
		return data.elements();
	}

//////// INTERFACE MutableTreeNode /////////////////////////////////////////////

	public synchronized void insert(MutableTreeNode child, int index)
	{
		data.insertElementAt(child, index);
		child.setParent(this);
	}

	public synchronized void remove(int index)
	{
		TreeNode tn = getChildAt(index);
		if(tn != null) {
			data.removeElementAt(index);
			if(tn instanceof MutableTreeNode) {
				((MutableTreeNode)tn).setParent(null);
			} else if (tn instanceof Element) {
				((Element)tn).setParentElement(null);
			}
		}
	}

	public synchronized void remove(MutableTreeNode node)
	{
		remove(data.indexOf(node));
	}

	public void setUserObject(Object object)
	{
		try {
			addValue(object.toString());
		} catch(Exception e) {
		}
	}

	public synchronized void removeFromParent()
	{
		TreeNode parent = getParent();
		if(parent != null && parent instanceof MutableTreeNode)
		{
				((MutableTreeNode)parent).remove(this);
		}
	}

	public void setParent(MutableTreeNode newParent)
	{
		//
		// Will give an exception if newParent doesn't implement element
		//
		setParentElement((Element)newParent);
	}

//////// New public methods ////////////////////////////////////////////////////

	/**
	 * Add a children to the element.
	 * @param	child		The child to add
	 * @exception	IllegalChild	Child or child type was wrong
	 * @exception	NoChildAllowed	No children are allowed at all
	 **/
	public synchronized void addChild(MutableTreeNode child)
				throws IllegalChild, NoChildAllowed
	{
		insert(child, data.size());
	}

	public synchronized void addValue(MutableTreeNode value)
				throws IllegalValue, NoValueAllowed
	{
		insert(value, data.size());
	}

//////// Overwritten superclass methods ////////////////////////////////////////

	public void addChild(Element child) throws IllegalChild, NoChildAllowed
	{
		//
		// Ensure that the child is of (sub)type MutableTreeNode by
		// just atempting to cast it
		addChild((MutableTreeNode)child);
	}

	/**
	 * Add the elements value to the object.
	 * @exception IllegalValue	The value in not valit for this element
	 * @exception NoValueAllowed	The element doesn't accept values at all
	 */
	public void addValue(String value) throws IllegalValue, NoValueAllowed
	{
		//
		// Values as identified by the parser are strings. In order to
		// make it possible to use the generic stuff here for tree
		// representation, we have to make a TreeNode out of the
		// string. The DefaultMutableTreeNode will do the trick, although
		// it's a little bit overdone...
		//
		addValue(new DefaultMutableTreeNode(value, false));
	}

	/**
	 * Return a string representation of the element.
	 * In fact, this does not return a string representation of the element,
	 * but instead a string representation of the start tag of the element.
	 * This is necessary, because JTree (ab)uses toString to get a text line
	 * to be displayed in the tree.
	 */
	public String toString() {
		StringBuffer result = new StringBuffer(16);
		result.append('<');
		result.append(getElementName());

		Enumeration keys = attributes.keys();
		Object	v;
		String n;
		while(keys.hasMoreElements()) {
			n  = (String) keys.nextElement();
			result.append(' ');
			result.append(n);

			v = attributes.get(n);
			if(v != null && !(v instanceof Null)) {
				result.append("=\"");
				Utils.XMLEncode(result, v.toString());
				result.append('"');
			}
		}
		if(data.isEmpty())
			result.append('/');
		result.append('>');
		return result.toString();
	}
}


