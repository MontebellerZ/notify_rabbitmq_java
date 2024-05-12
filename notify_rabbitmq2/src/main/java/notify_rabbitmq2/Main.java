package notify_rabbitmq2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Main {
    private final static String QUEUE_NAME = "notify_rabbitmq";

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer server = HttpServer.create(new InetSocketAddress(3001), 0);
        server.createContext("/notificar", new PaymentRoute());
        server.setExecutor(null);
        server.start();
    }

    static class PaymentRoute implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, Object> jsonMap = parseJson(t);

            String message = (String) jsonMap.get("message");

            ConnectionFactory factory = new ConnectionFactory();

            factory.setHost("localhost");

            try (Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel()) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);

                channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));

                String responseMessage = "{\"message\": \"[x] Sent '" + message + "'\"}";
                t.sendResponseHeaders(200, responseMessage.length());
                OutputStream os = t.getResponseBody();
                os.write(responseMessage.getBytes());
                os.close();
            } catch (TimeoutException e) {
                throw new IOException("timeoutException");
            }
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