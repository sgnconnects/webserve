package pl.kmolski.webserve.http;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import pl.kmolski.webserve.http.parser.HttpRequestLexer;
import pl.kmolski.webserve.http.parser.HttpRequestParseTreeVisitor;
import pl.kmolski.webserve.http.parser.HttpRequestParser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record HttpRequest(HttpMethod method, URI uri,
                          int majorVersion, int minorVersion,
                          Map<String, List<String>> headers) {

    public static class Builder {
        private HttpMethod method;
        private URI uri;
        private int majorVersion;
        private int minorVersion;
        private final Map<String, List<String>> headers;

        public Builder() {
            headers = new HttpHeaders<>();
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder majorVersion(int majorVersion) {
            this.majorVersion = majorVersion;
            return this;
        }

        public Builder minorVersion(int minorVersion) {
            this.minorVersion = minorVersion;
            return this;
        }

        public Builder header(String name, String value) {
            // TODO: Move to HttpHeaders class
            this.headers.compute(name, (key, list) -> {
                if (list == null) {
                    var newList = new ArrayList<String>();
                    newList.add(value);
                    return newList;
                } else {
                    list.add(value);
                    return list;
                }
            });
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(method, uri, majorVersion, minorVersion, headers);
        }
    }

    public static HttpRequestParser getParser(CharStream charStream) {
        var lexer = new HttpRequestLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        return new HttpRequestParser(tokenStream);
    }

    public static HttpRequest fromCharStream(CharStream charStream) {
        var parser = getParser(charStream);
        var parserContext = parser.httpRequest();

        var builder = new HttpRequest.Builder();
        var visitor = new HttpRequestParseTreeVisitor(builder);

        visitor.visit(parserContext);
        return builder.build();
    }
}
