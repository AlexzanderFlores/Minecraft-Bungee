package network;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Message {
	public static void send(CommandSender sender, String message) {
		message = message.replace("&x", "&2");
		sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&2" + message)));
	}
	
	public static void sendUnknownCommand(CommandSender sender) {
		send(sender, "&fUnknown command. Type \"/help\" for help.");
	}
	
	public static void alert(String message) {
		for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
			send(player, message);
		}
		BungeeCord.getInstance().getLogger().info(ChatColor.stripColor(message));
	}
}
