package jenkins.branch.naming.githubapimock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class ResourceHandler<RESPONSE> implements HttpHandler {
    private static final Logger LOGGER = Logger.getLogger(ResourceHandler.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    private final String url;
    private final int statusCode;
    private final Object response;

    public ResourceHandler(final String url, final RESPONSE response) {
        this.url = url;
        this.statusCode = response == null ? 404 : 200;
        this.response = response == null ? "Oopsie, nothing there" : response;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        LOGGER.fine(String.format("Response for %s: %s", url, response));
        try (final OutputStream outputStream = exchange.getResponseBody()) {
            final String responseAsString =
                MAPPER.writeValueAsString(response); // Must be done on demand, mock objects need the stub URL!
            exchange.sendResponseHeaders(statusCode, responseAsString.length());
            outputStream.write(responseAsString.getBytes());
        }
    }
}
