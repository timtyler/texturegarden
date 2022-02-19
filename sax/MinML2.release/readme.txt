MinML2 is an XML parser written in Java. It was written to be used in embedded Java
systems where storage space is at a premium. The code is quite compact 
(~10.5Kb in .tini format ~ 17Kb as a .jar file) including a simple test program and all the necessary SAX2 classes).
However, MinML2 was also designed to minimise the heap space used by the program
when parsing a document. We believe that MinML2 is unique in this respect.

MinML2 does not implement all of the XML standard. The principle omissions are:

DTDs - MinML2 reads and ignores DTDs

In addition MinML2 is very lax in checking that the spelling tags and attribute
names conforms to the letter of the standard.

MinML implements the SAX2 API.

We have extended the API to allow the calling program to supply an instance of a subclass
of java.io.Writer at the start of an element. The parser will then write all the character
data to the Writer instance rather than pass it back using the SAX characters call.
Look int the uk.xml.org.sax package for deatils.

We have also added two functions which allow the ContentHandler to call back to the parser to resolve
namespace tags within elements.
This helps in situations like SOAP faltcode processing:
	<faultcode>SOAP-ENV:Server</faultcode>


We would be *very* grateful for any reports, good or bad, about the use of this
parser in real applications.

The source is released under a BSD style licence (see licence.txt).

The distribution also contains some files which are TINI specific:

MinML2Test.tini is a runnable version of the MinML2 test program.

mk.bat and MinML.cmd make MinML2Test.tini and ftps it to my test system. This
has lots of stuff which is specific to my system but it should be reasonably easy
to see what need changing.

25th November 2001

John Wilson
The Wilson Partnership
5 Market Hill, Whitchurch, Aylesbury, Bucks HP22 4JB, UK
email: tug@wilson.co.uk, URL: http://www.wilson.co.uk/


