import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneId;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    private static final String DATE_TIME_PATTERN = "HH:mm:ss";
    private static final String TIME_ZONE = "UTC+01:00";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(4080), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            StringBuilder responseBuilder = new StringBuilder("Hello World from java!\n");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(TIME_ZONE));
            responseBuilder.append(dateTimeFormatter.format(zonedDateTime));
            String response = responseBuilder.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
	        System.out.println("Served hello world...");
        }
    }

}
