package Bot.WebSocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;

public class WebSocketmain {

    public static void WebSocket() {

        // Чтение JSON из файла
        String jsonString = readJsonFromFile("src/main/java/Bot/WebSocket/package.json");
        ClosingPriceProcessorScheduler.TimeStart();

        if (jsonString != null) {
            // Подключение к WebSocket-серверу
            try {

                WebSocketClientClass webSocketClient = new WebSocketClientClass(Configuration.WEBSOCKET_URL);

                // Подключение
                webSocketClient.connectBlocking();

                // Отправка JSON-строки
                webSocketClient.send(jsonString);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // Метод для чтения JSON из файла
    private static String readJsonFromFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
