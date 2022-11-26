import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Objects;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    private static final String HELLO_WORLD_RESPONSE = "Hello World from java!";
    private static final String STR_PARAM = "str";

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
        if (!queryMap.containsKey(STR_PARAM)) {
            return HELLO_WORLD_RESPONSE;
        }
        String strParamValue = queryMap.get(STR_PARAM);
        return buildResponse(strParamValue);
    }

    private static String buildResponse(String strParamValue) {
        StringBuilder sb = new StringBuilder("{\"lowercase\": ");
        long lowerCaseCount = strParamValue.chars()
            .filter(Character::isLowerCase)
            .count();
        sb.append(lowerCaseCount).append(", \"uppercase\": ");
        long upperCaseCount = strParamValue.chars()
            .filter(Character::isUpperCase)
            .count();
        sb.append(upperCaseCount).append(", \"digits\": ");
        long digitCount = strParamValue.chars()
            .filter(Character::isDigit)
            .count();
        sb.append(digitCount).append(", \"special\": ");
        long specialCount = strParamValue.chars()
            .filter(Test::isSpecialCharacter)
            .count();
        sb.append(specialCount).append("}");
        return sb.toString();
    }

    private static boolean isSpecialCharacter(int ch) {
        return !Character.isDigit(ch) && !Character.isLetter(ch);
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