package pl.kmolski.webserve.app;

import org.antlr.v4.runtime.UnbufferedCharStream;
import pl.kmolski.webserve.http.HttpRequest;
import pl.kmolski.webserve.util.HttpTerminatedCharStream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadServer {

    public static final byte[] SERVER_ID = ("Server: webserve\n").getBytes();

    public static final Charset SYSTEM_ENCODING = Charset.defaultCharset();
    public static final byte[] CHARSET_ID = ("; charset=%s\n".formatted(SYSTEM_ENCODING)).getBytes();

    public static final byte[] HTTP_VERSION_ID = "HTTP/1.1 ".getBytes();

    public static final byte[] HTTP_OK = "200 OK".getBytes();
    public static final byte[] HTTP_NOT_FOUND = "404 Not Found".getBytes();

    public static final Logger LOGGER = Logger.getLogger(MultiThreadServer.class.getName());

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(100);

    public static final int MAX_REQUEST_HEADERS_SIZE = 8192;

    public static void main(String[] args) throws IOException {
        var serverRoot = Path.of(".").normalize().toAbsolutePath();
        var listenSock = new ServerSocket();
        listenSock.bind(new InetSocketAddress("::1", 8080));

        LOGGER.setLevel(Level.ALL);

        while (true) {
            var incomingSocket = listenSock.accept();
            EXECUTOR_SERVICE.submit(() -> {
                try (incomingSocket; var inStream = incomingSocket.getInputStream();
                     var outStream = incomingSocket.getOutputStream()) {

                    var unbufferedCharStream = new UnbufferedCharStream(inStream);
                    var terminatedCharStream = new HttpTerminatedCharStream(unbufferedCharStream, MAX_REQUEST_HEADERS_SIZE);
                    var request = HttpRequest.fromCharStream(terminatedCharStream);
                    LOGGER.log(Level.INFO, "{}", request);

                    switch (request.method()) {
                        case GET -> {
                            var requestPath = request.uri().getPath().replaceFirst("^/", "");
                            var filesystemPath = Path.of(requestPath.isBlank() ? "index.html" : requestPath);
                            var absPath = serverRoot.resolve(filesystemPath).normalize().toAbsolutePath();
                            if (!absPath.startsWith(serverRoot)) {
                                throw new RuntimeException("No path traversal for you!");
                            }

                            outStream.write(HTTP_VERSION_ID);
                            try (var fileStream = Files.newInputStream(absPath)) {
                                outStream.write(HTTP_OK);
                                outStream.write('\n');

                                var mimeType = Files.probeContentType(absPath); // TODO: cache mime type later
                                outStream.write("Content-Type: ".getBytes());
                                outStream.write((mimeType != null ? mimeType : "text/plain").getBytes());
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
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
