parser grammar HttpRequestParser;

options { tokenVocab = HttpRequestLexer; }

// Request field rules defined in RFC 9110, section 5:
// https://datatracker.ietf.org/doc/html/rfc9110#section-5
fieldName  :                                 TOKEN;
fieldValue :    text=(QUOTED_STRING | FIELD_VALUE);
field      : fieldName COLON WSP* fieldValue? WSP*;

// Request rules defined in RFC 9112, section 2.1 - Message Format:
// https://datatracker.ietf.org/doc/html/rfc9112#section-2.1
httpVersion : Version_NAME major=DIGIT Version_SEP minor=DIGIT;
requestLine :        METHOD SP ORIGIN_FORM SP httpVersion CRLF;
httpRequest :                   requestLine (field CRLF)* CRLF; // message body omitted
