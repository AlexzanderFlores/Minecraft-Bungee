package network;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.util.concurrent.TimeUnit;

public class PlayerCountTracker extends Command implements Listener {
	private static int players = 0;
	private static int maxPlayers = 0;
	
	public PlayerCountTracker() {
		super("players");
		Register.listener(this);
		Register.command(this);
		if(DB.NETWORK_POPULATIONS.isKeySet("server", Network.getInstance().getServer())) {
			DB.NETWORK_POPULATIONS.updateInt("population", 0, "server", Network.getInstance().getServer());
		} else {
			DB.NETWORK_POPULATIONS.insert("'" + Network.getInstance().getServer() + "', '0'");
		}
		loadData();
		BungeeCord.getInstance().getScheduler().schedule(Network.getInstance(), new Runnable() {
			@Override
			public void run() {
				int online = BungeeCord.getInstance().getPlayers().size();
				DB.NETWORK_POPULATIONS.updateInt("population", online, "server", Network.getInstance().getServer());
				loadData();
			}
		}, 5, 5, TimeUnit.SECONDS);
	}
	
	public static int getPlayers() {
		return players;
	}
	
	public static int getMaxPlayers() {
		return maxPlayers;
	}
	
	public static void setMaxPlayers(int maxPlayers) {
		PlayerCountTracker.maxPlayers = maxPlayers;
	}
	
	private void loadData() {
		int players = 0;
		for(String server : DB.NETWORK_POPULATIONS.getAllStrings("server")) {
			players += DB.NETWORK_POPULATIONS.getInt("server", server, "population");
		}
		PlayerCountTracker.players = players;
		PlayerCountTracker.maxPlayers = DB.NETWORK_SERVER_LIST.getInt("data_type", "max_players", "data_value");
	}
	
	@Override
	public void execute(CommandSender sender, String [] arguments) {
		Message.send(sender, "Players online: &e" + players);
	}
}
