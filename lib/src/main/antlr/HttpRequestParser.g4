parser grammar HttpRequestParser;

options { tokenVocab = HttpRequestLexer; }

// Request header rules defined in RFC 7230, section 3:
// https://datatracker.ietf.org/doc/html/rfc7230#section-3
fieldName   :                                 TOKEN;
fieldValue  :           QUOTED_STRING | FIELD_VALUE;
headerField : fieldName COLON WSP* fieldValue? WSP*;

httpVersion : Version_NAME major=DIGIT Version_SEP minor=DIGIT;
requestLine :        METHOD SP ORIGIN_FORM SP httpVersion CRLF;
httpRequest :             requestLine (headerField CRLF)* CRLF; // message body omitted
