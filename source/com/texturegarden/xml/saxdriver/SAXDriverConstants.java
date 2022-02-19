package com.texturegarden.xml.saxdriver;

interface SAXDriverConstants {
  String NAMESPACE = "http://www.w3.org/XML/1998/namespace";
  String VALIDATION = "http://xml.org/sax/features/validation";
  String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
  String NAMESPACES = "http://xml.org/sax/features/namespaces";

  String ENTITY_STRING = "#amp;&pos;'quot;\"gt;>lt;<";

  //String ENTITIES = ("\u0001");
  String ENTITIES = ("\u0001\u000b\u0006\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff\u00ff" +
  //  #     a     m     p     ;     &     p     o     s     ;     '
  //  0     1     2     3     4     5     6     7     8     9     a
  "\u0011\u00ff\u00ff\u00ff\u00ff\u00ff\u0015\u00ff\u00ff\u00ff" +
  //  q     u     o     t     ;     "     g     t     ;     >
  //  b     b     d     e     f     10    11    12    13    14
  "\u00ff\u00ff\u00ff");

  byte[] CHAR_CLASSES = {
    //  EOF
        13,
    //                                      \t  \n          \r
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, -1, -1, 12, -1, -1,
    //
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    //  SP   !   "   #   $   %   &   '   (   )   *   +   ,   -   .   /
        12,  8,  7, 14, 14, 14,  3,  6, 14, 14, 14, 14, 14, 11, 14,  2,
    //   0   1   2   3   4   5   6   7   8   9   :   ;   <   =   >   ?
        14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,  0,  5,  1,  4,
    //
        14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,
    //                                               [   \   ]
        14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14,  9, 14, 10
    };

  String CDATA = "CDATA";

  int incLevel = 10;

  int inSkipping = 0;
  int inSTag = 1;
  int inPossiblyAttribute = 2;
  int inNextAttribute = 3;
  int inAttribute = 4;
  int inAttribute1 = 5;
  int inAttributeValue = 6;
  int inAttributeQuoteValue = 7;
  int inAttributeQuotesValue = 8;
  int inETag = 9;
  int inETag1 = 10;
  int inMTTag = 11;
  int inTag = 12;
  int inTag1 = 13;
  int inPI = 14;
  int inPI1 = 15;
  int inPossiblySkipping = 16;
  int inCharData = 17;
  int inCDATA = 18;
  int inCDATA1 = 19;
  int inComment = 20;
  int inDTD = 21;

  int DO_END_START_NAME = 0;
  int DO_EMIT_START_ELEMENT = 1;
  int DO_EMIT_END_ELEMENT = 2;
  int DO_POSSIBLY_EMIT_CHARACTERS = 3;
  int DO_EMIT_CHARACTERS = 4;
  int DO_EMIT_CHARACTERS_SAVE = 5;
  int DO_SAVE_ATTRIBUTE_NAME = 6;
  int DO_SAVE_ATTRIBUTE_VALUE = 7;
  int DO_START_COMMENT = 8;
  int DO_END_COMMENT = 9;
  int DO_DEC_LEVEL = 11;
  int DO_START_CDATA = 12;
  int DO_END_CDATA = 13;
  int DO_PROCESS_CHAR_REF = 14;
  int DO_WRITE_CDATA = 15;
  int DO_EXIT_PARSER = 16;
  int DO_PARSE_ERROR = 17;
  int DO_DISCARD_AND_CHANGE = 18;
  int DO_DISCARD_SAVE_AND_CHANGE = 19;
  int DO_SAVE_AND_CHANGE = 20;
  int DO_CHANGE = 21;

  int defaultInitialBufferSize = 256;
  int defaultBufferIncrement = 256;
}
