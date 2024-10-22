package Bot.Bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotLaunch {
    private final Config config;
    private JDA jda; // Поле для хранения экземпляра JDA

    public BotLaunch() {
        config = new Config(); // Загружаем конфигурацию
    }

    // Метод для запуска бота
    public void start() {
        String token = config.getDiscordToken(); // Получаем токен из конфигурации

        // Инициализация и запуск бота
        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new BotListener())
                .build();

        System.out.println("Bot successfully launched!"); // Индикация успешного запуска бота
    }

    // Метод для отправки сообщения при изменении сигнала
    public void sendSignalMessage(String ticker, String signal) {
        if (jda == null) {
            System.out.println("Bot is not initialized.");
            return;
        }
// Проверяем, есть ли доступные текстовые каналы
        if (jda.getTextChannels().isEmpty()) {
            System.out.println("No text channels available for sending messages.");
            return;
        }

        String message = "Сигнал для " + ticker + ": " + signal;

        // Вывод отладочного сообщения
        System.out.println("Sending message: " + message);

        // Отправляем сообщение во все активные текстовые каналы
        jda.getTextChannels().forEach(channel -> {
            if (channel.canTalk()) { // Проверяем, может ли бот отправлять сообщения в канал
                channel.sendMessage(message).queue(
                        success -> System.out.println("Message sent successfully to " + channel.getName()),
                        error -> System.out.println("Failed to send message to " + channel.getName() + ": " + error.getMessage())
                );
            } else {
                System.out.println("Bot does not have permission to send messages in channel: " + channel.getName());
            }
        });
    }
}
