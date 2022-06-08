lexer grammar HttpRequestLexer;

METHOD : ('GET' | 'HEAD' | 'POST' | 'PUT' | 'DELETE' | 'CONNECT' | 'OPTIONS' | 'TRACE' | 'PATCH') -> pushMode(URI);

Version_CRLF : CRLF -> type(CRLF), pushMode(Header);
Version_NAME :                              'HTTP/';
Version_SEP  :                                  '.';

// Core rules defined in RFC 5234, appendix B.1:
// https://datatracker.ietf.org/doc/html/rfc5234#appendix-B.1
DIGIT : [0-9];
CRLF  : CR LF;
SP    :   ' ';

fragment ALPHA  : [a-zA-Z];
fragment CR     :     '\r';
fragment HTAB   :     '\t';
fragment LF     :     '\n';
fragment DQUOTE :      '"';

// URI rules defined in RFC 3986 and RFC 9110, section 4.1 - URI References:
// https://datatracker.ietf.org/doc/html/rfc3986
// https://datatracker.ietf.org/doc/html/rfc9110#section-4.1
mode URI;

ORIGIN_FORM : ABSOLUTE_PATH ('?' QUERY*)? -> popMode;
URI_SP      :                         SP -> type(SP);

fragment ABSOLUTE_PATH :                                ('/' SEGMENT*)+;
fragment QUERY         :                          (SEGMENT | '/' | '?');
fragment SEGMENT       : (UNRESERVED | SUB_DELIMS | PCT_ENCODED | [:@]);
fragment PCT_ENCODED   :                              '%' HEXDIG HEXDIG;
fragment UNRESERVED    :                         ALPHA | DIGIT | [-.~_];
fragment SUB_DELIMS    :                                  [!$&'()*+,;=];
fragment HEXDIG        :                                  DIGIT | [A-F];

// Header rules defined in RFC 9110, section 5 - Fields:
// https://datatracker.ietf.org/doc/html/rfc9110#section-5
mode Header;

TOKEN       :                       TCHAR+;
COLON       : ':' -> pushMode(HeaderValue);
Header_CRLF :           CRLF -> type(CRLF);

fragment TCHAR : ALPHA | DIGIT | [-&`^$!#.|+%'*~_];


mode HeaderValue;

QUOTED_STRING : DQUOTE (QDTEXT | QUOTED_PAIR)* DQUOTE -> popMode;
FIELD_VALUE   :                        FIELD_CONTENT+ -> popMode;
WSP           :                                      (SP | HTAB);

fragment QDTEXT      : WSP | [\u0021] | [\u0023-\u005B] | [\u005D-\u007E] | OBS_TEXT;
fragment QUOTED_PAIR :                                 '\\' (WSP | VCHAR | OBS_TEXT);

fragment FIELD_CONTENT : FIELD_VCHAR ((WSP | FIELD_VCHAR)+ FIELD_VCHAR)?;
fragment FIELD_VCHAR   :                                VCHAR | OBS_TEXT;
fragment VCHAR         :                                 [\u0021-\u007E];
fragment OBS_TEXT      :                                 [\u0080-\u00FF];
