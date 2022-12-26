package pl.kmolski.webserve.http;

import lombok.Builder;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import pl.kmolski.webserve.http.parser.HttpRequestLexer;
import pl.kmolski.webserve.http.parser.HttpRequestParseTreeVisitor;
import pl.kmolski.webserve.http.parser.HttpRequestParser;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Builder
public record HttpRequest(HttpMethod method, URI uri,
                          int majorVersion, int minorVersion,
                          Map<String, List<String>> fields) {

    public static HttpRequestParser getParser(CharStream charStream) {
        var lexer = new HttpRequestLexer(charStream);
        lexer.setTokenFactory(new CommonTokenFactory(true));

        var tokenStream = new UnbufferedTokenStream<>(lexer);
        return new HttpRequestParser(tokenStream);
    }

    public static HttpRequest fromCharStream(CharStream charStream) {
        var parser = HttpRequest.getParser(charStream);
        var parserContext = parser.httpRequest();

        var builder = new HttpRequestBuilder();
        var visitor = new HttpRequestParseTreeVisitor(builder);

        visitor.visit(parserContext);
        return builder.build();
    }
}
