Title:        TinyXML V0.7 Release Notes
Maintainer:   Tom Gibara (tom@srac.org)
Last updated: 4th January 2000


TinyXML is a very lightweight XML parser. It has been released under
the GPL (GNU GENERAL PUBLIC LICENCE) which is contained in the file
gpl.txt. Please read it document before using or distributing
TinyXML or any of its associated files.

This second release of TinyXML contains the following files:

  * Java class files for the package gd.xml
  * Java class files for the package gd.xml.tiny
  * Java class files for the XMLTest application
  * Java class files for the TinyXMLTest command line tool
  * Java source files for all supplied classes
  * The text file DevelopmentDiary-TinyXML.txt
  * javadoc generated documentation for the gd.xml package

Consult the development diary file for details of known bugs, XML1.0
conformance failings and work in progress.

Consult the html documents within the JavaDoc directory for
information on how to use the TinyXML package, or look at
TinyXMLTest.java (it's very simple). Note that the ParsedXML
interface will be enriched significantly in the next release.
The XmlResponder interface provided by the gd.xml package may be
considered stable.

The XMLTest application requires that the Swing classes be available
under the javax package to be run. The TinyXML classes themselves
should run under JVM versioned at 1.1 and above.

Major changes since last release include:

  * A new event orientated parsing interface has been provided onto
    which the old interface has been layered.

  * Support for UTF-8 and UTF-16 has been added.

  * Added parsing of Document type definitions (both internal and
    external).

  * Added support for external entities.

  * Newlines now normalized to 0x0A's

  * Collection of different bugs fixed.

  * Improved conformance to XML 1.0 specification greatly.