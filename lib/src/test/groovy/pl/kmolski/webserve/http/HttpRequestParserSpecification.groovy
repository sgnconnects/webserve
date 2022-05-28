package pl.kmolski.webserve.http

import org.antlr.v4.runtime.CharStreams
import spock.lang.Specification

import static pl.kmolski.webserve.http.HttpProtocolConstants.REQUEST_ENCODING

class HttpRequestParserSpecification extends Specification {

    def requestFromFile(String fileName) {
        var filePath = getClass().getClassLoader().getResource(fileName)
        var charStream = CharStreams.fromFileName(filePath.getFile(), REQUEST_ENCODING)
        return HttpRequest.fromCharStream(charStream)
    }

    def "post_example"() {
        when:
        def request = requestFromFile("post_example.http")

        then:
        request.method() == HttpRequestMethod.POST
        request.uri().toString() == "/url?sa=t&source=web&rct=j&url=https://zh.wikipedia.org/zh-hans/111&ved=2ahUKEwjhwLuRtbjiAhUPRK0KHRSjDpwQFjAKegQIAxAB"
        request.headers().get("Host").contains("www.google.com.hk")
        request.headers().get("Connection").contains("close")
        request.headers().get("Content-Length").contains("4")
        request.headers().get("Ping-From").contains("https://www.google.com.hk/search?safe=strict&ei=gx3qXOKuJ4a8tgX-ypWIDA&q=111&oq=111&gs_l=psy-ab.3..0l10.15337.16373..16590...0.0..0.783.890.0j1j6-1......0....1..gws-wiz.....0.hUqCCrrBI9s")
        request.headers().get("Origin").contains("https://www.google.com.hk")
        request.headers().get("Cache-Control").contains("max-age=0")
        request.headers().get("User-Agent").contains("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
        request.headers().get("Ping-To").contains("https://zh.wikipedia.org/zh-hans/111")
        request.headers().get("Content-Type").contains("text/ping")
        request.headers().get("Accept").contains("*/*")
        request.headers().get("X-Client-Data").contains("CIi2yQEIorbJAQjBtskBCKmdygEIqKPKAQjwpMoBCLGnygEI4qjKAQjxqcoBCK+sygEYz6rKAQ==")
        request.headers().get("Accept-Encoding").contains("gzip, deflate")
        request.headers().get("Accept-Language").contains("zh-CN,zh;q=0.9,en;q=0.8")
        request.headers().get("Cookie").contains("NID=184=VqX86iUz6p-H_b2qbuogwjkmsk096DB-48jilOI9Pquzq8WT-aRbKsaH8UnMfvF9uHtuUtHhnJ7Z3F74bcpMNstJ5ADYV_tv09sXOJiwf3Yu-xsZ1E588v2tX6zA-J4K6c1t6t_PQP3jvtbVSdqw_YJqgU1elwvqkjzj0kBbk0I; 1P_JAR=2019-05-26-05; DV=42xzl48Lt5gpEFuauBIUhN0LQjoor5YtIbbBr4x5AQIAAAA")
    }
}
