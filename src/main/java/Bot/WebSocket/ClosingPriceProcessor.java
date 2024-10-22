package Bot.WebSocket;

import java.io.*;
import java.util.*;

public class ClosingPriceProcessor {

    public static void processFilesAndUpdatePrices() {
        System.out.println("Запуск processFilesAndUpdatePrices()...");

        //Choose derictory where will be saved data
        File folder = new File("."); // Current derictory
        File[] files = folder.listFiles((dir, name) -> name.endsWith("_log.txt"));

        if (files == null || files.length == 0) {
            System.out.println("No files found for processing.");
            return;
        }

        System.out.println("Found " + files.length + " files for processing.");

        for (File file : files) {
            String ticker = file.getName().replace("_log.txt", "").replace("_", "/");
            System.out.println("Processing: " + file.getName() + " for ticker: " + ticker);

            List<Double> prices = readClosingPricesFromFile(file);

            if (!prices.isEmpty()) {
                double closingPrice = prices.get(prices.size() - 1); // Last price in list
                System.out.println("The last counted price " + ticker + ": " + closingPrice);
                updateClosingPricesForTicker(ticker, closingPrice);
            } else {
                Deque<Double> closingPrices = Configuration.tickerClosingPrices.get(ticker);
                if (closingPrices != null && !closingPrices.isEmpty()) {
                    double lastKnownPrice = closingPrices.getLast();
                    System.out.println("Duplicate the previous value for " + ticker + ": " + lastKnownPrice);
                    updateClosingPricesForTicker(ticker, lastKnownPrice);
                } else {
                    System.out.println("For ticker " + ticker + " no initial value. Skip.");
                }
            }

            // Улучшенная очистка файла после чтения
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.print(""); // Очищаем содержимое файла
                System.out.println("File " + file.getName() + " cleaned.");
            } catch (IOException e) {
                System.out.println("Error when clearing the file: " + file.getName());
                e.printStackTrace();
            }

            System.out.println("Current array of closing prices for" + ticker + ": " + getClosingPricesForTicker(ticker));
        }
    }

    private static void updateClosingPricesForTicker(String ticker, double closingPrice) {
        Deque<Double> closingPrices = Configuration.tickerClosingPrices.computeIfAbsent(ticker, k -> new LinkedList<>());

        if (closingPrices.size() >= Configuration.maxSize) {
            closingPrices.removeFirst(); // Удаляем самое старое значение (FIFO)
        }

        closingPrices.addLast(closingPrice);
        // Выполняем расчет MACD только для текущего тикера
        calculateMACDForTicker(ticker);
        System.out.println("Обновлены цены закрытия для " + ticker + ": " + closingPrices);
    }

    private static void calculateMACDForTicker(String ticker) {
        Deque<Double> closingPrices = Configuration.tickerClosingPrices.get(ticker);
        if (closingPrices != null && closingPrices.size() >= Configuration.slowPeriod) {
            System.out.println("Proccessing calculation MACD for ticker: " + ticker);
            MACDProcessor.calculateMACD(closingPrices, ticker); // Передаем оба параметра
        } else {
            System.out.println("Not enough data for calculated MACD for ticker: " + ticker);
        }
    }

    public static Deque<Double> getClosingPricesForTicker(String ticker) {
        return Configuration.tickerClosingPrices.getOrDefault(ticker, new LinkedList<>());
    }

    private static List<Double> readClosingPricesFromFile(File file) {
        List<Double> prices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Last:")) {
                    String[] parts = line.split(", Last: ");
                    if (parts.length > 1) {
                        try {
                            double price = Double.parseDouble(parts[1].trim());
                            prices.add(price);
                            System.out.println("Добавлена цена: " + price + " из строки: " + line);
                        } catch (NumberFormatException e) {
                            System.out.println("Не удалось распознать значение цены: " + parts[1]);
                            e.printStackTrace();
                        }
                    }
                }
            }
            System.out.println("Считано " + prices.size() + " цен из файла " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return prices;
    }
}
