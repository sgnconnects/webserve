parser grammar HttpRequestParser;

options { tokenVocab = HttpRequestLexer; }

// Request header rules defined in RFC 7230, section 3:
// https://datatracker.ietf.org/doc/html/rfc7230#section-3
fieldName   :                                 TOKEN;
fieldValue  :           QUOTED_STRING | FIELD_VALUE;
headerField : fieldName COLON WSP* fieldValue? WSP*;

requestLine : METHOD SP ORIGIN_FORM SP HTTP_version CRLF;
httpRequest :       requestLine (headerField CRLF)* CRLF; // message body omitted
