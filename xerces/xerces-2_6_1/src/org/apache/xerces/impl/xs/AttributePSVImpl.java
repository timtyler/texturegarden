/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.impl.xs;

import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.XSConstants;

/**
 * Attribute PSV infoset augmentations implementation.
 * The PSVI information for attributes will be available at the startElement call.
 *
 * @author Elena Litani IBM
 * @version $Id: AttributePSVImpl.java,v 1.20 2003/11/12 23:17:33 sandygao Exp $
 */
public class AttributePSVImpl implements AttributePSVI {

    /** attribute declaration */
    protected XSAttributeDeclaration fDeclaration = null;

    /** type of attribute, simpleType */
    protected XSTypeDefinition fTypeDecl = null;

    /** If this attribute was explicitly given a 
     * value in the original document, this is false; otherwise, it is true */
    protected boolean fSpecified = false;

    /** schema normalized value property */
    protected String fNormalizedValue = null;
    
    /** schema actual value */
    protected Object fActualValue = null;

    /** schema actual value type */
    protected short fActualValueType = XSConstants.UNAVAILABLE_DT;

    /** actual value types if the value is a list */
    protected ShortList fItemValueTypes = null;

    /** member type definition against which attribute was validated */
    protected XSSimpleTypeDefinition fMemberType = null;

    /** validation attempted: none, partial, full */
    protected short fValidationAttempted = AttributePSVI.VALIDATION_NONE;

    /** validity: valid, invalid, unknown */
    protected short fValidity = AttributePSVI.VALIDITY_NOTKNOWN;

    /** error codes */
    protected String[] fErrorCodes = null;

    /** validation context: could be QName or XPath expression*/
    protected String fValidationContext = null;

    //
    // AttributePSVI methods
    //

    /**
     * [schema default]
     *
     * @return The canonical lexical representation of the declaration's {value constraint} value.
     * @see <a href="http://www.w3.org/TR/xmlschema-1/#e-schema_default>XML Schema Part 1: Structures [schema default]</a>
     */
    public String getSchemaDefault() {
        return fDeclaration == null ? null : fDeclaration.getConstraintValue();
    }

    /**
     * [schema normalized value]
     *
     *
     * @see <a href="http://www.w3.org/TR/xmlschema-1/#e-schema_normalized_value>XML Schema Part 1: Structures [schema normalized value]</a>
     * @return the normalized value of this item after validation
     */
    public String getSchemaNormalizedValue() {
        return fNormalizedValue;
    }

    /**
     * [schema specified] 
     * @see <a href="http://www.w3.org/TR/xmlschema-1/#e-schema_specified">XML Schema Part 1: Structures [schema specified]</a>
     * @return true - value was specified in schema, false - value comes from the infoset
     */
    public boolean getIsSchemaSpecified() {
        return fSpecified;
    }


    /**
     * Determines the extent to which the document has been validated
     *
     * @return return the [validation attempted] property. The possible values are
     *         NO_VALIDATION, PARTIAL_VALIDATION and FULL_VALIDATION
     */
    public short getValidationAttempted() {
        return fValidationAttempted;
    }

    /**
     * Determine the validity of the node with respect
     * to the validation being attempted
     *
     * @return return the [validity] property. Possible values are:
     *         UNKNOWN_VALIDITY, INVALID_VALIDITY, VALID_VALIDITY
     */
    public short getValidity() {
        return fValidity;
    }

    /**
     * A list of error codes generated from validation attempts.
     * Need to find all the possible subclause reports that need reporting
     *
     * @return list of error codes
     */
    public StringList getErrorCodes() {
        if (fErrorCodes == null)
            return null;
        return new StringListImpl(fErrorCodes, fErrorCodes.length);
    }

    // This is the only information we can provide in a pipeline.
    public String getValidationContext() {
        return fValidationContext;
    }

    /**
     * An item isomorphic to the type definition used to validate this element.
     * 
     * @return  a type declaration
     */
    public XSTypeDefinition getTypeDefinition() {
        return fTypeDecl;
    }

    /**
     * If and only if that type definition is a simple type definition
     * with {variety} union, or a complex type definition whose {content type}
     * is a simple thype definition with {variety} union, then an item isomorphic
     * to that member of the union's {member type definitions} which actually
     * validated the element item's normalized value.
     * 
     * @return  a simple type declaration
     */
    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return fMemberType;
    }

    /**
     * An item isomorphic to the attribute declaration used to validate
     * this attribute.
     * 
     * @return  an attribute declaration
     */
    public XSAttributeDeclaration getAttributeDeclaration() {
        return fDeclaration;
    }
    
    /* (non-Javadoc)
     * @see org.apache.xerces.xs.ItemPSVI#getActualNormalizedValue()
     */
    public Object getActualNormalizedValue() {
        return this.fActualValue;
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.ItemPSVI#getActualNormalizedValueType()
     */
    public short getActualNormalizedValueType() {
        return this.fActualValueType;
    }

    /* (non-Javadoc)
     * @see org.apache.xerces.xs.ItemPSVI#getItemValueTypes()
     */
    public ShortList getItemValueTypes() {
        return this.fItemValueTypes;
    }

    /**
     * Reset() 
     */
    public void reset() {
        fNormalizedValue = null;
        fActualValue = null;
        fActualValueType = XSConstants.UNAVAILABLE_DT;
        fItemValueTypes = null;
        fDeclaration = null;
        fTypeDecl = null;
        fSpecified = false;
        fMemberType = null;
        fValidationAttempted = AttributePSVI.VALIDATION_NONE;
        fValidity = AttributePSVI.VALIDITY_NOTKNOWN;
        fErrorCodes = null;
        fValidationContext = null;
    }
}
