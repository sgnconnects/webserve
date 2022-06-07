package pl.kmolski.webserve.http.parser;

import pl.kmolski.webserve.http.HttpRequest;
import pl.kmolski.webserve.http.HttpMethod;
import pl.kmolski.webserve.http.parser.HttpRequestParser.*;

import java.net.URI;

public class HttpRequestParseTreeVisitor extends HttpRequestParserBaseVisitor<HttpRequest.Builder> {

    private final HttpRequest.Builder builder;

    public HttpRequestParseTreeVisitor(HttpRequest.Builder builder) {
        this.builder = builder;
    }

    @Override
    public HttpRequest.Builder visitHeaderField(HeaderFieldContext ctx) {
        visitChildren(ctx);
        var headerName = ctx.fieldName().getText();
        var headerValue = ctx.fieldValue().getText();
        return builder.header(headerName, headerValue);
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
