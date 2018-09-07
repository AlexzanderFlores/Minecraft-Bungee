package network;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class ServerListPingHandler extends Command implements Listener {
	private static String [] motd = new String [] {"", ""};
	
	public ServerListPingHandler() {
		super("motd");
		Register.listener(this);
		Register.command(this);
		loadData();
	}
	
	@Override
	public void execute(CommandSender sender, String [] arguments) {
		if(Network.getInstance().isOwner(sender)) {
			if(arguments.length == 0) {
				Message.send(sender, motd[0]);
				Message.send(sender, motd[1]);
				return;
			} else if(arguments.length == 1 && arguments[0].equalsIgnoreCase("reload") && !(sender instanceof ProxiedPlayer)) {
				loadData();
				return;
			} else if(arguments.length >= 2) {
				try {
					int line = Integer.valueOf(arguments[0]);
					if(line == 1 || line == 2) {
						String message = "";
						for(int a = 1; a < arguments.length; ++a) {
							message += arguments[a] + " ";
						}
						message = ChatColor.translateAlternateColorCodes('&', message.substring(0, message.length() - 1));
						DB.NETWORK_SERVER_LIST.updateString("data_value", message, "data_type", "line_" + (line == 1 ? "one" : "two"));
						CommandDispatcher.dispatch("motd reload");
						Message.send(sender, "Set line #" + line + " to \"" + message + "&a\"");
						return;
					}
				} catch(NumberFormatException e) {
					if(arguments[0].equalsIgnoreCase("max")) {
						try {
							DB.NETWORK_SERVER_LIST.updateInt("data_value", Integer.valueOf(arguments[1]), "data_type", "max_players");
							Message.send(sender, "Set max players to " + arguments[1]);
							return;
						} catch(NumberFormatException ex) {
							
						}
					}
				}
			}
			Message.send(sender, "/motd <1 | 2> <message>");
			Message.send(sender, "/motd max <max player count>");
		} else {
			Message.sendUnknownCommand(sender);
		}
	}
	
	@EventHandler
	public void onProxyPing(ProxyPingEvent event) {
		event.getResponse().setDescription(motd[0] + "\n" + motd[1]);
		event.getResponse().setPlayers(new Players(PlayerCountTracker.getMaxPlayers(), PlayerCountTracker.getPlayers(), null));
	}
	
	@EventHandler
	public void onLogin(LoginEvent event) {
		if(PlayerCountTracker.getPlayers() >= PlayerCountTracker.getMaxPlayers()) {
			UUID uuid = event.getConnection().getUniqueId();
			if(DB.PLAYERS_ACCOUNTS.isKeySet("uuid", uuid.toString()) && !DB.PLAYERS_ACCOUNTS.getString("uuid", uuid.toString(), "rank").equals("PLAYER")) {
				return;
			}
			event.setCancelReason(ChatColor.translateAlternateColorCodes('&', "&c1v1s has reached max compacity!\n&cTo join you must have &e[Premium]\n&bstore.1v1s.org"));
			event.setCancelled(true);
		}
	}
	
	private void loadData() {
		if(DB.NETWORK_SERVER_LIST.isKeySet("data_type", "line_one")) {
			motd[0] = DB.NETWORK_SERVER_LIST.getString("data_type", "line_one", "data_value");
		} else {
			motd[0] = ChatColor.translateAlternateColorCodes('&', "&6&l1v1s.org &e&l: 1.7 & 1.8");
			DB.NETWORK_SERVER_LIST.insert("'line_one', '" + motd[0] + "'");
		}
		if(DB.NETWORK_SERVER_LIST.isKeySet("data_type", "line_two")) {
			motd[1] = DB.NETWORK_SERVER_LIST.getString("data_type", "line_two", "data_value");
		} else {
			motd[1] = ChatColor.translateAlternateColorCodes('&', "&eAn Immerse &6competitive &ePVP server");
			DB.NETWORK_SERVER_LIST.insert("'line_two', '" + motd[1] + "'");
		}
		if(DB.NETWORK_SERVER_LIST.isKeySet("data_type", "max_players")) {
			PlayerCountTracker.setMaxPlayers(DB.NETWORK_SERVER_LIST.getInt("data_type", "max_players", "data_value"));
		} else {
			DB.NETWORK_SERVER_LIST.insert("'max_players', '1000'");
		}
	}
}