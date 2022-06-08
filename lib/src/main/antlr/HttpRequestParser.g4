parser grammar HttpRequestParser;

options { tokenVocab = HttpRequestLexer; }

// Request header rules defined in RFC 9110, section 5:
// https://datatracker.ietf.org/doc/html/rfc9110#section-5
fieldName   :                                 TOKEN;
fieldValue  :    text=(QUOTED_STRING | FIELD_VALUE);
headerField : fieldName COLON WSP* fieldValue? WSP*;

httpVersion : Version_NAME major=DIGIT Version_SEP minor=DIGIT;
requestLine :        METHOD SP ORIGIN_FORM SP httpVersion CRLF;
httpRequest :             requestLine (headerField CRLF)* CRLF; // message body omitted
