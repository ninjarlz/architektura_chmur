import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Objects;
import java.time.ZonedDateTime;
import java.time.ZoneId;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    private static final String HELLO_WORLD_RESPONSE = "Hello World from java!";
    private static final String CMD_PARAM = "cmd";
    private static final String STR_PARAM = "str";
    private static final String TIME_PARAM_VALUE = "time";
    private static final String REV_PARAM_VALUE = "rev";

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
            String query = t.getRequestURI().getQuery();
            String response = getResponse(query);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
	        System.out.println("Served response...");
        }
    }

    private static String getResponse(String query) {
        Map<String, String> queryMap = queryToMap(query);
        if (!queryMap.containsKey(CMD_PARAM)) {
            return HELLO_WORLD_RESPONSE;
        }
        String cmdParamValue = queryMap.get(CMD_PARAM);
        switch (cmdParamValue) {
            case TIME_PARAM_VALUE:
                return getFormattedTime();
            case REV_PARAM_VALUE:
                if (!queryMap.containsKey(STR_PARAM)) {
                    return HELLO_WORLD_RESPONSE;
                }
                return new StringBuilder(queryMap.get(STR_PARAM)).reverse().toString();
            default:
                return HELLO_WORLD_RESPONSE;
                
        }
    }

    private static String getFormattedTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(TIME_ZONE));
        return dateTimeFormatter.format(zonedDateTime);
    }

    private static Map<String, String> queryToMap(String query) {
        if (Objects.isNull(query) || query.isEmpty()) {
            return Collections.emptyMap();
        }
        return Stream.of(query.split("&"))
            .filter(s -> !s.isEmpty())
            .map(kv -> kv.split("=", 2)) 
            .collect(Collectors.toMap(x -> x[0], x-> x[1]));
    }
}