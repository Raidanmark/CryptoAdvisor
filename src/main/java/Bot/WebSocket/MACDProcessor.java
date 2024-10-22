package Bot.WebSocket;

import Bot.Bot.BotLaunch;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MACDProcessor {

    // Глобальная переменная для отслеживания текущего сигнала каждого тикера
    private static Map<String, String> tickerSignals = new HashMap<>();
    private static BotLaunch botLaunch; // Ссылка на BotLaunch

    // Метод для установки экземпляра BotLaunch
    public static void setBotLaunch(BotLaunch bot) {
        botLaunch = bot;
    }

    // Рассчитываем EMA для списка цен
    private static List<Double> calculateEMA(List<Double> prices, int period) {
        List<Double> emaValues = new ArrayList<>();
        double multiplier = 2.0 / (period + 1);
        double ema = prices.get(0); // Начинаем с первого значения

        // Рассчитываем EMA для каждого значения
        emaValues.add(ema);
        for (int i = 1; i < prices.size(); i++) {
            ema = ((prices.get(i) - ema) * multiplier) + ema;
            emaValues.add(ema);
        }

        return emaValues;
    }

    // Метод для расчёта MACD, сигнальной линии и гистограммы для указанного тикера
    public static void calculateMACD(Deque<Double> closingPrices, String ticker) {
        if (closingPrices.size() < Configuration.slowPeriod) {
            System.out.println("Недостаточно данных для расчёта MACD для тикера: " + ticker);
            return;
        }

        // Готовим переменные для расчёта
        List<Double> prices = new ArrayList<>(closingPrices);
        List<Double> fastEMA = calculateEMA(prices, Configuration.fastPeriod);
        List<Double> slowEMA = calculateEMA(prices, Configuration.slowPeriod);
        List<Double> macdLine = new ArrayList<>();

        System.out.println("Test MACD 1");
        System.out.println(slowEMA.size());

        // Вычисляем минимальное количество элементов, которое можем обработать безопасно
        int minSize = Math.min(slowEMA.size(), fastEMA.size() - (Configuration.slowPeriod - Configuration.fastPeriod));

        for (int i = 0; i < minSize; i++) {
            macdLine.add(fastEMA.get(i + Configuration.slowPeriod - Configuration.fastPeriod) - slowEMA.get(i));
        }

        System.out.println("Test MACD 2");

        List<Double> signalLine = calculateEMA(macdLine, Configuration.signalPeriod);
        List<Double> histogram = new ArrayList<>();

        int histogramMinSize = Math.min(signalLine.size(), macdLine.size() - (Configuration.signalPeriod - 1));

        for (int i = 0; i < histogramMinSize; i++) {
            histogram.add(macdLine.get(i + Configuration.signalPeriod - 1) - signalLine.get(i));
        }

        System.out.println("Test MACD 3");

        // Получаем текущий сигнал для тикера, если он ещё не установлен, то устанавливаем "Sell" по умолчанию
        tickerSignals.putIfAbsent(ticker, "Sell");
        String previousSignal = tickerSignals.get(ticker);

        // Логика сигналов: определяем "Buy" или "Sell" при изменении сигнала
        for (int i = 1; i < signalLine.size(); i++) {
            double macdCurrent = macdLine.get(i);
            double macdPrevious = macdLine.get(i - 1);
            double signalCurrent = signalLine.get(i);
            double signalPrevious = signalLine.get(i - 1);

            System.out.println("Test MACD 4");

            if (macdPrevious < signalPrevious && macdCurrent > signalCurrent) {
                // Пересечение снизу вверх - сигнал "Buy"
                if (!previousSignal.equals("Buy")) {
                    System.out.println("Buy сигнал на индексе " + i + " для тикера: " + ticker);
                    tickerSignals.put(ticker, "Buy");

                    // Отправка сообщения в бот, если установлен экземпляр BotLaunch
                    if (botLaunch != null) {
                        botLaunch.sendSignalMessage(ticker, "Buy");
                    }
                }
            } else if (macdPrevious > signalPrevious && macdCurrent < signalCurrent) {
                // Пересечение сверху вниз - сигнал "Sell"
                if (!previousSignal.equals("Sell")) {
                    System.out.println("Sell сигнал на индексе " + i + " для тикера: " + ticker);
                    tickerSignals.put(ticker, "Sell");

                    // Отправка сообщения в бот, если установлен экземпляр BotLaunch
                    if (botLaunch != null) {
                        botLaunch.sendSignalMessage(ticker, "Sell");
                    }
                }
            }
        }

        // Вывод текущего индикатора сигнала для тикера
        System.out.println("Текущий индикатор для " + ticker + ": " + tickerSignals.get(ticker));
    }
}