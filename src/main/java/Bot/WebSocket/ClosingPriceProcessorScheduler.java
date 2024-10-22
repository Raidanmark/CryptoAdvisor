package Bot.WebSocket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClosingPriceProcessorScheduler {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void startProcessingInBackground() {
        // Запускаем задачу каждые 15 минут
        scheduler.scheduleAtFixedRate(
                ClosingPriceProcessor::processFilesAndUpdatePrices,
                0,  // Начальная задержка (0 минут)
                15,  // Интервал между запусками (1 минута для тестирования)
                TimeUnit.MINUTES
        );

    }

    public static void stopProcessing() {
        // Останавливаем выполнение
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(15, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public static void TimeStart() {
        // Запуск обработки данных в фоновом режиме
        startProcessingInBackground();

    }
}
