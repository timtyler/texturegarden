#
# Copyright (c) 1999 - 2001 Thomas Weidenfeller
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
#   * Redistributions of source code must retain the above copyright
#     notice, this list of conditions and the following disclaimer.
#
#   * Redistributions in binary form must reproduce the above copyright
#     notice, this list of conditions and the following disclaimer
#     in the documentation and/or other materials provided with the
#     distribution.
#
#   * Neither name of the copyright holders nor the names of its
#     contributors may be used to endorse or promote products derived
#     from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
# FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
# COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
# INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
# BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
# LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
# ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

#
# Set up source code, compilation location and compiler
#
PACKAGE	= XMLtp#			Name of the package
ROOT	= ../..
CLASSDIR= $(ROOT)/classes#		Were to place the compiler result
LIBDIR	= $(ROOT)/lib#			Were to generate the jar file
SRCDIR  = $(ROOT)/src
PACKAGEDIR= $(CLASSDIR)/$(PACKAGE)
LIB	= $(LIBDIR)/$(PACKAGE).jar#	The Java archive to create

#
# Compiler and tools
#
JAVA_HOME=
JC	= $(JAVA_HOME)javac
JAR	= jar
JFLAGS	= -g
JLIBS	= $(LIBDIR)

SRCS	= \
	Element.java			\
	ElementAdapter.java		\
	ElementDesc.java		\
	EmptyElementTermination.java	\
	EndTagDoesNotMatch.java		\
	EndTagDoesntMatch.java		\
	GenericElement.java		\
	jfc/GenericElementTreeNode.java	\
	IllegalAttrib.java		\
	IllegalChar.java                \
	IllegalChild.java		\
	IllegalValue.java		\
	IncompleteElement.java		\
	MissingETag.java		\
	MultipleDocumentElement.java	\
	NoAttribAllowed.java		\
	NoChildAllowed.java		\
	NoName.java			\
	NoQuote.java			\
	NoSuchElement.java		\
	NoValueAllowed.java		\
	Null.java			\
	PrematureEOF.java		\
	ReferenceIllegal.java		\
	ReferenceNotTerminated.java	\
	ReferenceRecursion.java		\
	ReferenceUnknown.java		\
	SyntaxError.java		\
	TagNotClosed.java		\
	Utils.java			\
	XMLException.java		\
	XMLtp.java

#
# If your make doesn't support pattern replacement macro references,
# the OBJS macro must be written in the form
#	OBJS=	$(PACKAGEDIR)/Element.class  		\
#		$(PACKAGEDIR)/ElementAdapter.class	\
#			:
#			.
#
OBJS	= $(SRCS:%.java=${PACKAGEDIR}/%.class)
JAROBJS	= $(SRCS:%.java=${PACKAGE}/%.class)

#
# My make doesn't support VPATH, but maybe your's does
#
VPATH	= $(ROOT):$(CLASSDIR):$(PACKAGEDIR)

#
# Do some work
#
default:	all

all:	compile jar

compile: $(SRCS)
	-@mkdir $(CLASSDIR) 2>/dev/null
	@echo Compiling package $(PACKAGE)...
	@$(COMPILE.J) $(SRCS)


jar lib $(LIB):	$(OBJS)
	-@mkdir $(LIBDIR) 2>/dev/null
	@echo Creating $(LIB)...
	@(PWD=`pwd`; cd $(CLASSDIR); $(JAR) cf0 $$PWD/$(LIB) $(JAROBJS);)

clean:
	@echo Cleaning up...
	@rm -f $(OBJS) $(LIB) || exit 0


################################################################################
# General handling of Java files
#
.SUFFIXES: .class .java

.java.class:
	 $(COMPILE.J) $<

COMPILE.J	= CLASSPATH=$(SRCDIR):$(JLIBS) $(JC) $(JFLAGS) -d $(CLASSDIR)
