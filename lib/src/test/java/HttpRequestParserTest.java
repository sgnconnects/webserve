import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import pl.kmolski.webserve.parser.HttpRequestLexer;
import pl.kmolski.webserve.parser.HttpRequestParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestParserTest {

    HttpRequestParser getParser(String filePath) throws IOException {
        var inputStream = new FileInputStream(filePath);
        var charStream = CharStreams.fromStream(inputStream, StandardCharsets.ISO_8859_1);

        var lexer = new HttpRequestLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        return new HttpRequestParser(tokenStream);
    }

    @Test
    void testValidPostRequest() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("post_example.http");
        var parser = getParser(fileUrl.getFile());
        var request = parser.httpRequest();

        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    void testValidPostRequest2() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("post_example2.http");
        var parser = getParser(fileUrl.getFile());
        var request = parser.httpRequest();

        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    void testValidGetRequest() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("get_example.http");
        var parser = getParser(fileUrl.getFile());
        var request = parser.httpRequest();

        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    void testValidGetRequest2() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("get_example2.http");
        var parser = getParser(fileUrl.getFile());
        var request = parser.httpRequest();

        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    void testValidGetRequest3() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("get_example3.http");
        var parser = getParser(fileUrl.getFile());
        var request = parser.httpRequest();

        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    void testValidGetRequest4() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("get_example4.http");
        var parser = getParser(fileUrl.getFile());
        var request = parser.httpRequest();

        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    void testValidDeleteRequest() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("delete_example.http");
        var parser = getParser(fileUrl.getFile());
        var request = parser.httpRequest();

        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    void testValidPutRequest() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("put_example.http");
        var parser = getParser(fileUrl.getFile());
        var request = parser.httpRequest();

        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }

    @Test
    void testValidPutRequest2() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("put_example2.http");
        var parser = getParser(fileUrl.getFile());
        var request = parser.httpRequest();

        assertEquals(0, parser.getNumberOfSyntaxErrors());
    }
}
