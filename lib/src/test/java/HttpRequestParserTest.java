import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;
import pl.kmolski.webserve.http.HttpRequest;

import java.io.IOException;

import static pl.kmolski.webserve.http.HttpProtocolConstants.REQUEST_ENCODING;

public class HttpRequestParserTest {

    HttpRequest fromFile(String fileName) throws IOException {
        var charStream = CharStreams.fromFileName(fileName, REQUEST_ENCODING);
        return HttpRequest.fromCharStream(charStream);
    }

    @Test
    void testValidPostRequest2() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("post_example2.http");
        var request = fromFile(fileUrl.getFile());
    }

    @Test
    void testValidGetRequest() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("get_example.http");
        var request = fromFile(fileUrl.getFile());
    }

    @Test
    void testValidGetRequest2() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("get_example2.http");
        var request = fromFile(fileUrl.getFile());
    }

    @Test
    void testValidGetRequest3() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("get_example3.http");
        var request = fromFile(fileUrl.getFile());
    }

    @Test
    void testValidDeleteRequest() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("delete_example.http");
        var request = fromFile(fileUrl.getFile());
    }

    @Test
    void testValidPutRequest() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("put_example.http");
        var request = fromFile(fileUrl.getFile());
    }

    @Test
    void testValidPutRequest2() throws IOException {
        var fileUrl = getClass().getClassLoader().getResource("put_example2.http");
        var request = fromFile(fileUrl.getFile());
    }
}
