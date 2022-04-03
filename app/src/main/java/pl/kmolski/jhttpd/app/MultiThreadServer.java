package pl.kmolski.jhttpd.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadServer {

    public static final byte[] SERVER_ID =
            ("Server: jHTTPd/0.0.1 (" + System.getProperty("os.name") + ")\n").getBytes();

    public static final byte[] CHARSET_ID =
            ("; charset=" + Charset.defaultCharset() + "\n").getBytes();

    public static final byte[] HTTP_VERSION_ID = "HTTP/1.1 ".getBytes();

    public static final byte[] HTTP_OK = "200 OK".getBytes();
    public static final byte[] HTTP_NOT_FOUND = "404 Not Found".getBytes();

    public static final Logger LOGGER = Logger.getLogger(MultiThreadServer.class.getName());

    public static void main(String[] args) throws IOException {
        var listenSock = new ServerSocket();
        listenSock.bind(new InetSocketAddress("::1", 8080));

        while (true) {
            var inSock = listenSock.accept();
            var handler = new Thread(() -> {
                try (inSock; var inStream = inSock.getInputStream();
                     var stream = new BufferedReader(new InputStreamReader(inStream));
                     var outStream = inSock.getOutputStream()) {

                    System.out.println("Accepted connection from " + inSock.getInetAddress());

                    String[] request = stream.readLine().split(" ");

                    System.out.println("Method: " + request[0]);
                    System.out.println("Path: " + request[1]);
                    System.out.println("Protocol: " + request[2]);

                    System.out.println("Request headers: ");
                    while (stream.ready()) {
                        System.out.println(stream.readLine());
                    }

                    var method = request[0].trim();
                    switch (method) {
                        case "GET": {
                            var requestPath = request[1].trim();
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
                            break;
                        }

                        default: {
                            System.out.println("METHOD NOT IMPLEMENTED: " + method);
                        }
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error on request: ", e);
                }
            });
            handler.start();
        }
    }
}
