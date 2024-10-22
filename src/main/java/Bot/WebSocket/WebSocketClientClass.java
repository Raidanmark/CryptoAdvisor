
package Bot.WebSocket;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.io.FileWriter;


class WebSocketClientClass extends WebSocketClient {

    public WebSocketClientClass(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Соединение установлено");
    }

    @Override
    public void onMessage(String message) {



// Парсинг JSON-сообщения
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

        // Проверка, что тип сообщения "update"
        if (jsonObject.has("type") && "update".equals(jsonObject.get("type").getAsString())) {
            // Извлечение массива данных
            JsonArray dataArray = jsonObject.getAsJsonArray("data");

            if (dataArray != null && dataArray.size() > 0) {
                JsonObject dataObject = dataArray.get(0).getAsJsonObject();

                // Проверка наличия тикера и цены "last"
                if (dataObject.has("symbol") && dataObject.has("last")) {
                    String ticker = dataObject.get("symbol").getAsString();
                    double lastPrice = dataObject.get("last").getAsDouble();

                    // Формируем имя файла на основе тикера (заменяем "/" на "_")
                    String fileName = ticker.replace("/", "_") + "_log.txt";

                    // Запись тикера и цены в файл
                    writeToFile(fileName, "Ticker: " + ticker + ", Last: " + lastPrice);
                    System.out.println("Записано в файл " + fileName + ": Ticker: " + ticker + ", Last: " + lastPrice);
                }
            }
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private void writeToFile(String filePath, String text) {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(text);
            printWriter.flush(); // Немедленно записать данные на диск
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
