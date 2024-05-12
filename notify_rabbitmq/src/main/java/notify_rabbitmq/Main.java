package notify_rabbitmq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);
        server.createContext("/pagar", new PaymentRoute());
        server.setExecutor(null);
        server.start();
    }

    static class PaymentRoute implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, Object> jsonMap = parseJson(t);

            Set<String> keys = jsonMap.keySet();

            Set<String> mustKeys = new HashSet<>();
            mustKeys.add("paymentId");
            mustKeys.add("payer");
            mustKeys.add("value");

            if (!keys.containsAll(mustKeys)) {
                String errorMessage = "{\"message\": \"O corpo da requisicao deve ter os atributos em formato JSON: paymentId (int), payer (string), value (double)\"}";
                t.sendResponseHeaders(200, errorMessage.length());
                OutputStream os = t.getResponseBody();
                os.write(errorMessage.getBytes());
                os.close();
                return;
            }

            int paymentId = (int) jsonMap.get("paymentId");
            String payer = (String) jsonMap.get("payer");
            double value = (double) jsonMap.get("value");

            String messageJson = "{\"message\": \"" + payer + " pagou o valor " + value + " para a conta " + paymentId
                    + "\"}";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3001/notificar"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(messageJson))
                    .build();

            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
            }

            t.sendResponseHeaders(200, response.body().length());
            OutputStream os = t.getResponseBody();
            os.write(response.body().getBytes());
            os.close();
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> parseJson(HttpExchange t) throws IOException {
            InputStream requestBody = t.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonBody = stringBuilder.toString();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(jsonBody, Map.class);

            return map;
        }
    }
}