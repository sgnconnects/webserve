package pl.kmolski.webserve.app;

import org.antlr.v4.runtime.CodePointBuffer;
import org.antlr.v4.runtime.CodePointCharStream;
import pl.kmolski.webserve.http.HttpRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadServer {

    public static final byte[] SERVER_ID =
            ("Server: webserve/0.0.1 (" + System.getProperty("os.name") + ")\n").getBytes();

    public static final byte[] CHARSET_ID =
            ("; charset=" + Charset.defaultCharset() + "\n").getBytes();

    public static final byte[] HTTP_VERSION_ID = "HTTP/1.1 ".getBytes();

    public static final byte[] HTTP_OK = "200 OK".getBytes();
    public static final byte[] HTTP_NOT_FOUND = "404 Not Found".getBytes();

    public static final Logger LOGGER = Logger.getLogger(MultiThreadServer.class.getName());
    public static final Charset SYSTEM_ENCODING = Charset.defaultCharset();

    public static final int MAX_REQUEST_HEADERS_SIZE = 8192;

    public static void main(String[] args) throws IOException {
        var listenSock = new ServerSocket();
        listenSock.bind(new InetSocketAddress("::1", 8080));

        LOGGER.setLevel(Level.ALL);

        while (true) {
            var inSock = listenSock.accept();
            var handler = new Thread(() -> {
                var buffer = ByteBuffer.allocate(MAX_REQUEST_HEADERS_SIZE);
                try (inSock; var inStream = inSock.getInputStream();
                     var outStream = inSock.getOutputStream()) {

                    var bytesRead = inStream.read(buffer.array());
                    buffer.limit(bytesRead);

                    LOGGER.info("Accepted connection from " + inSock.getInetAddress());
                    var charStream = CodePointCharStream.fromBuffer(CodePointBuffer.withBytes(buffer));
                    var request = HttpRequest.fromCharStream(charStream);

                    LOGGER.info("Method: " + request.method());
                    LOGGER.info("URI: " + request.uri());
                    LOGGER.info("Protocol: HTTP/" + request.majorVersion() + "." + request.minorVersion());

                    LOGGER.info("Request headers: " + request.headers());

                    switch (request.method()) {
                        case GET -> {
                            var requestPath = request.uri().getPath();
                            var filesystemPath = Paths.get(requestPath.equals("/") ? "index.html" : requestPath);
                            var absPath = Paths.get(".", filesystemPath.toString()).toAbsolutePath();

                            outStream.write(HTTP_VERSION_ID);
                            try (var fileStream = Files.newInputStream(absPath)) {
                                outStream.write(HTTP_OK);
                                outStream.write('\n');

                                var mimeType = Files.probeContentType(absPath); // TODO: cache mime type later
                                outStream.write("Content-Type: ".getBytes());
                                outStream.write((mimeType != null ? mimeType : "text/plain").getBytes());
                                outStream.write('\n');
                                outStream.write(CHARSET_ID);

                                outStream.write(("Content-Length: " + absPath.toFile().length()).getBytes());
                                outStream.write('\n');

                                outStream.write(SERVER_ID);
                                outStream.write("Connection: close\n\n".getBytes());

                                fileStream.transferTo(outStream);
                            } catch (NoSuchFileException e) {
                                outStream.write(HTTP_NOT_FOUND);
                                outStream.write('\n');

                                outStream.write(SERVER_ID);
                                outStream.write("Connection: close\n\n".getBytes());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            outStream.flush();
                        }
                        default -> LOGGER.warning("METHOD NOT IMPLEMENTED: " + request.method());
                    }
                } catch (IOException e) {
                    LOGGER.severe("Error on request: " + e);
                }
            });
            handler.start();
        }
    }
}
