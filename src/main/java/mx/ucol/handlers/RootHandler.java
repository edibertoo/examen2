package mx.ucol.handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class RootHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        byte[] response = "API endpoint not found.".getBytes();
        exchange.sendResponseHeaders(404, response.length);

        OutputStream output = exchange.getResponseBody();
        output.write(response);
        output.close();
    }
}
