package pl.kmolski.webserve.http.parser

import com.fasterxml.jackson.databind.ObjectMapper
import org.antlr.v4.runtime.CharStreams
import pl.kmolski.webserve.http.HttpRequest
import spock.lang.Specification

import static pl.kmolski.webserve.http.HttpProtocolConstants.REQUEST_ENCODING

class HttpRequestParserSpecification extends Specification {

    def requestFromHttpFile(String fileName) {
        def filePath = getClass().getClassLoader().getResource(fileName)
        def charStream = CharStreams.fromFileName(filePath.getFile(), REQUEST_ENCODING)
        return HttpRequest.fromCharStream(charStream)
    }

    def requestFromJsonFile(String fileName) {
        def filePath = getClass().getClassLoader().getResource(fileName)
        def requestObject = new ObjectMapper().readValue(filePath, HttpRequest)
        return requestObject
    }

    def "when the HTTP message is parsed, then it matches the expected request object"() {
        when:
        def actual = requestFromHttpFile("valid/${requestFilePrefix}.http")
        def expected = requestFromJsonFile("valid/${requestFilePrefix}.json")

        then:
        actual == expected

        where:
        requestFilePrefix << ["complex", "no_headers", "quoted_headers"]
    }
}
