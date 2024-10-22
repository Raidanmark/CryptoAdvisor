package Bot.Bot;

import Bot.WebSocket.MACDProcessor;
import Bot.WebSocket.WebSocketmain;

public class Main {
    public static void main(String[] args) {

        BotLaunch botLaunch = new BotLaunch();
        WebSocketmain webSocket = new WebSocketmain();

        // Install BotLaunch in MACDProcessor
        MACDProcessor.setBotLaunch(botLaunch);

        //Create first thread, that calls the first method
        Thread thread1 = new Thread(() ->botLaunch.start());

        //Create second thread, that calls the second menthod
        Thread thread2 = new Thread(() -> webSocket.WebSocket());

        // Launch both threads
        thread1.start();
        thread2.start();

    }
}