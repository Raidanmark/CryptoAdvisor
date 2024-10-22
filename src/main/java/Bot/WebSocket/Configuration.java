package Bot.WebSocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Configuration {

    static int  maxSize = 35;
    static URI WEBSOCKET_URL;
    static int fastPeriod = 12;
    static int slowPeriod = 26;
    static int signalPeriod = 9;

    static {
        try {
            WEBSOCKET_URL = new URI("wss://ws.kraken.com/v2");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static final Map<String, Deque<Double>> tickerClosingPrices = new HashMap<>();

    public Configuration() throws URISyntaxException {
    }

    public static List<String> getTickers() {
        return Arrays.asList("ETH/USD", "BNB/USD", "BTC/USD", "SOL/USD", "XRP/USD");
    }
}
