package Bot.Bot;

import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import Bot.SMA_Method.SMAMain;

public class RecommendSMA {

   public void Recommendsma(MessageChannelUnion channel) {
      // Send a message to the channel indicating that analysis is in progress
      channel.sendMessage("Please wait, analyzing top cryptocurrencies. This may take a few minutes...").queue();

      //Call SMAMain
      SMAMain SMA = new SMAMain();
      String recommendation = SMA.SMAmain();
      if (recommendation.isEmpty()) {
           // If no recommendations were found, send a message indicating this
           channel.sendMessage ("No recommendations for purchasing at the moment.").queue();
      } else {
           // Otherwise, send the recommendations to the channel
           channel.sendMessage("Purchase recommendations:\n" + recommendation).queue();
      }
   }
}
