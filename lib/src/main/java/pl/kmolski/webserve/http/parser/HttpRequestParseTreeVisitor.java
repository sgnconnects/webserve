package pl.kmolski.webserve.http.parser;

import pl.kmolski.webserve.http.HttpRequest;
import pl.kmolski.webserve.http.HttpMethod;
import pl.kmolski.webserve.http.parser.HttpRequestParser.*;

import java.net.URI;

import static pl.kmolski.webserve.http.parser.HttpRequestLexer.FIELD_VALUE;
import static pl.kmolski.webserve.http.parser.HttpRequestLexer.QUOTED_STRING;

public class HttpRequestParseTreeVisitor extends HttpRequestParserBaseVisitor<HttpRequest.Builder> {

    private final HttpRequest.Builder builder;

    public HttpRequestParseTreeVisitor(HttpRequest.Builder builder) {
        this.builder = builder;
    }

    @Override
    public HttpRequest.Builder visitField(FieldContext ctx) {
        visitChildren(ctx);
        var fieldName = ctx.fieldName().getText();

        var valueToken = ctx.fieldValue().text;
        var valueRawText = valueToken.getText();
        var fieldValue = switch (valueToken.getType()) {
            case QUOTED_STRING -> {
                var withoutQuotes = valueRawText.substring(1, valueRawText.length() - 1);
                yield withoutQuotes.replaceAll("\\\\(.)", "$1");
            }
            case FIELD_VALUE -> valueRawText;
            default -> throw new IllegalStateException("Unexpected token: " + valueToken.getType());
        };
        return builder.field(fieldName, fieldValue);
    }

    @Override
    public HttpRequest.Builder visitHttpVersion(HttpVersionContext ctx) {
        visitChildren(ctx);
        var majorVersion = Integer.parseInt(ctx.major.getText());
        var minorVersion = Integer.parseInt(ctx.minor.getText());
        return builder.majorVersion(majorVersion).minorVersion(minorVersion);
    }

    @Override
    public HttpRequest.Builder visitRequestLine(RequestLineContext ctx) {
        visitChildren(ctx);
        var method = HttpMethod.valueOf(ctx.METHOD().getText());
        var requestUri = URI.create(ctx.ORIGIN_FORM().getText());
        return builder.method(method).uri(requestUri);
    }
}
