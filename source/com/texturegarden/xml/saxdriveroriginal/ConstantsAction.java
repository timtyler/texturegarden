package com.texturegarden.xml.saxdriveroriginal;

interface ConstantsAction {
  int END_START_NAME = 0;
  int EMIT_START_ELEMENT = 1;
  int EMIT_END_ELEMENT = 2;
  int POSSIBLY_EMIT_CHARACTERS = 3;
  int EMIT_CHARACTERS = 4;
  int EMIT_CHARACTERS_SAVE = 5;
  int SAVE_ATTRIBUTE_NAME = 6;
  int SAVE_ATTRIBUTE_VALUE = 7;
  int START_COMMENT = 8;
  int END_COMMENT = 9;
  int INC_LEVEL = 10;
  int DEC_LEVEL = 11;
  int START_CDATA = 12;
  int END_CDATA = 13;
  int PROCESS_CHAR_REF = 14;
  int WRITE_CDATA = 15;
  int EXIT_PARSER = 16;
  int PARSE_ERROR = 17;
  int DISCARD_AND_CHANGE = 18;
  int DISCARD_SAVE_AND_CHANGE = 19;
  int SAVE_AND_CHANGE = 20;
  int CHANGE = 21;
}
