package Bot.Data;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class CoinGeckoApiClient {
    // Base URL for the CoinGecko API
    private static final String BASE_URL = "https://api.coingecko.com/api/v3";
    private final OkHttpClient client = new OkHttpClient(); // HTTP client for making requests
    private final Gson gson = new Gson(); // Gson instance for JSON parsing

    // Create a Bot.Data.RateLimiter with a limit of 1 request every 15 seconds
    private static final int MAX_REQUESTS_PER_MINUTE = 1; //Maximum requests per minute
    private static final long TIME_WINDOW_MILLIS = 15_000L; // Time window in milliseconds (15 seconds)
    private final RateLimiter rateLimiter = new RateLimiter(MAX_REQUESTS_PER_MINUTE, TIME_WINDOW_MILLIS); // Rate limiter instance

    public List<Map<String, String>> getTopCoinsData(int limit) throws IOException {
        // Use Bot.Data.RateLimiter before executing the request
        rateLimiter.acquire();


        // Construct the URL for the API request
        String url = BASE_URL + "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=" + limit + "&page=1&sparkline=false";
        Request request = new Request.Builder().url(url).build(); // Build the request

        try (Response response = client.newCall(request).execute()) { // Execute the request and obtain the response
            if (!response.isSuccessful()) {
                throw new IOException("Ошибка при запросе данных: " + response); // Throw an exception if the response is not successful
            }

            String jsonResponse = response.body().string(); // Convert the response body to a string

            // Define the type for deserializing the JSON response
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> coinsData = gson.fromJson(jsonResponse, listType);  // Deserialize JSON into a list of maps

            List<Map<String, String>> coinList = new ArrayList<>(); // Initialize a list to hold the coin data
            for (Map<String, Object> coin : coinsData) { // Iterate through the coins data
                String id = (String) coin.get("id"); // Get the coin ID
                String name = (String) coin.get("name"); // Get the coin name
                Map<String, String> coinInfo = new HashMap<>(); // Create a new map for coin info
                coinInfo.put("id", id); // Add the ID to the map
                coinInfo.put("name", name); // Add the name to the map
                coinList.add(coinInfo); // Add the coin info map to the list
            }
            return coinList; // Return the list of coin data
        }
    }

    // Create a new thread to perform the analysis (it will be in next updates)
    public List<PriceData> getClosingPrices(String coinId, int days) throws IOException {
        // Use Bot.Data.RateLimiter before executing the request
        rateLimiter.acquire();

        // Construct the URL for the API request
        String url = BASE_URL + "/coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days + "&interval=daily";
        Request request = new Request.Builder().url(url).build(); // Build the request

        try (Response response = client.newCall(request).execute()) { // Execute the request and obtain the response
            if (!response.isSuccessful()) {
                throw new IOException("\n" + "Error requesting data: " + response); // Throw an exception if the response is not successful
            }

            String jsonResponse = response.body().string(); // Convert the response body to a string

            // Define the type for deserializing the JSON response
            Type type = new TypeToken<Map<String, List<List<Double>>>>() {}.getType();
            Map<String, List<List<Double>>> marketData = gson.fromJson(jsonResponse, type); // Deserialize JSON into a map

            List<List<Double>> prices = marketData.get("prices"); // Get the list of prices from the market data
            List<PriceData> closingPrices = new ArrayList<>(); // Initialize a list to hold closing prices

            for (List<Double> pricePoint : prices) { // Iterate through the price points
                long timestamp = pricePoint.get(0).longValue(); // Get the timestamp
                double price = pricePoint.get(1); // Get the price

                // Convert timestamp to date
                Date date = new Date(timestamp); // Create a new Date object from the timestamp
                closingPrices.add(new PriceData(date, price)); // Add the price data to the list
            }

            return closingPrices; // Return the list of closing prices
        }
    }
}
