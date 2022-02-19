@ECHO OFF

SET CLASSES=D:\java\j2me1.0\lib\classes;.


ECHO compiling TinyXML

javac -d classes -O -bootclasspath %CLASSES%;lib tinyxml\DocumentHandler.java
javac -d classes -O -bootclasspath %CLASSES%;lib tinyxml\HandlerBase.java
javac -d classes -O -bootclasspath %CLASSES%;lib tinyxml\XMLInputStream.java
javac -d classes -O -bootclasspath %CLASSES%;lib tinyxml\XMLParser.java
javac -d classes -O -bootclasspath %CLASSES%;lib tinyxml\ParseException.java
javac -d classes -O -bootclasspath %CLASSES%;lib tinyxml\XMLReader.java


ECHO compiling Character Utility

javac -d classes -O -bootclasspath %CLASSES% tinyxml\util\CharacterUtility.java


ECHO preverifying TinyXML

preverify -classpath classes;%CLASSES% -d verified tinyxml.DocumentHandler
preverify -classpath classes;%CLASSES% -d verified tinyxml.HandlerBase
preverify -classpath classes;%CLASSES% -d verified tinyxml.XMLParser
preverify -classpath classes;%CLASSES% -d verified tinyxml.ParseException
preverify -classpath classes;%CLASSES% -d verified tinyxml.XMLReader
preverify -classpath classes;%CLASSES% -d verified tinyxml.XMLInputStream


ECHO preverifying Character Utility

preverify -classpath classes;%CLASSES% -d verified tinyxml.util.CharacterUtility


ECHO making ClassesDBPatch.pdb
java -classpath %CLASSES% palm.database.MakePalmDB -creator kJav -name tinyXML -classpath verified tinyxml.DocumentHandler tinyxml.HandlerBase tinyxml.XMLReader tinyxml.XMLInputStream tinyxml.ParseException tinyxml.XMLParser tinyxml.util.CharacterUtility

ECHO [DONE]