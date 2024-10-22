package Bot.Bot;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class BotListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Check if the message author is a bot
        if (event.getAuthor().isBot()) {
            return; // Ignore messages from other bots
        }

        // Get the content of the received message
        String messageContent = event.getMessage().getContentRaw();
        MessageChannelUnion channel = event.getChannel();


        // Check if the message content is the command "!recommend"
        if (messageContent.equalsIgnoreCase("!recommend")) {

            // Create a new thread to perform the analysis
            new Thread(() -> {
                //Call method RecommendSMA
                RecommendSMA recommendSMA = new RecommendSMA();
                recommendSMA.Recommendsma(channel);

            }).start(); // Start the thread

            // Check if the message content is the command "!help"
        } else if (messageContent.equalsIgnoreCase("!help")) {
            // Send a list of available commands to the channel
            channel.sendMessage("Available commands:\n!recommend - get cryptocurrency purchase recommendations\n!help - list of commands").queue();
        }
    }


}
