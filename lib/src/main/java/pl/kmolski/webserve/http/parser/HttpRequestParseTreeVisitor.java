package pl.kmolski.webserve.http.parser;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.ParseTree;
import pl.kmolski.webserve.http.HttpMethod;
import pl.kmolski.webserve.http.HttpRequest.HttpRequestBuilder;
import pl.kmolski.webserve.http.parser.HttpRequestParser.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.kmolski.webserve.http.parser.HttpRequestLexer.FIELD_VALUE;
import static pl.kmolski.webserve.http.parser.HttpRequestLexer.QUOTED_STRING;

@RequiredArgsConstructor
public class HttpRequestParseTreeVisitor extends HttpRequestParserBaseVisitor<HttpRequestBuilder> {

    private final HttpRequestBuilder builder;
    private final Map<String, List<String>> fields = new HashMap<>();

    @Override
    public HttpRequestBuilder visitField(FieldContext ctx) {
        this.visitChildren(ctx);
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

        this.fields.computeIfAbsent(fieldName, __ -> new ArrayList<>()).add(fieldValue);
        this.fields.compute(fieldName, (key, list) -> {
            if (list == null) {
                var newList = new ArrayList<String>();
                newList.add(fieldValue);
                return newList;
            } else {
                list.add(fieldValue);
                return list;
            }
        });
        return this.builder;
    }

    @Override
    public HttpRequestBuilder visitHttpVersion(HttpVersionContext ctx) {
        this.visitChildren(ctx);
        var majorVersion = Integer.parseInt(ctx.major.getText());
        var minorVersion = Integer.parseInt(ctx.minor.getText());
        return this.builder.majorVersion(majorVersion).minorVersion(minorVersion);
    }

    @Override
    public HttpRequestBuilder visitRequestLine(RequestLineContext ctx) {
        this.visitChildren(ctx);
        var method = HttpMethod.valueOf(ctx.METHOD().getText());
        var requestUri = URI.create(ctx.ORIGIN_FORM().getText());
        return this.builder.method(method).uri(requestUri);
    }

    @Override
    public HttpRequestBuilder visit(ParseTree tree) {
        this.builder.fields(this.fields);
        return super.visit(tree);
    }
}
