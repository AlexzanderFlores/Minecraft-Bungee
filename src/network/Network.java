package network;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class Network extends Plugin {
	private static Network instance = null;
	private static String server = "N/A";
	private static MaintenanceHandler maintenanceHandler = null;
	
	@Override
	public void onEnable() {
		instance = this;

		server = "BUNGEE1";

//		File file = new File(BungeeCord.getInstance().getPluginsFolder().getPath());
//		String [] split = file.getAbsolutePath().split("\\\\");
//		server = split[split.length - 2].toUpperCase();

		DB.values();
		if(!DB.NETWORK_PROXIES.isKeySet("server", getServer())) {
			DB.NETWORK_PROXIES.insert("'" + getServer() + "'");
		}

		new ServerPerformance();
		new NetworkRestart();
		new ServerListPingHandler();
		new CommandDispatcher();
		new HubHandler();
		maintenanceHandler = new MaintenanceHandler();
		new PlayerCountTracker();
		new LogDeletion();
		BungeeCord.getInstance().getScheduler().schedule(getInstance(), new Runnable() {
			@Override
			public void run() {
				for(DB.Databases database : DB.Databases.values()) {
					database.connect();
				}
			}
		}, 5, 5, TimeUnit.SECONDS);
	}
	
	@Override
	public void onDisable() {
		maintenanceHandler.disableMaintenance();
		DB.NETWORK_PROXIES.delete("server", getServer());
		for(DB.Databases database : DB.Databases.values()) {
			database.disconnect();
		}
	}
	
	public static Network getInstance() {
		return instance;
	}
	
	public String getServer() {
		return server;
	}
	
	public boolean isOwner(CommandSender sender) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			String uuid = player.getUniqueId().toString();
			return uuid.equals("c5f7f0fe-b3f7-443b-850d-dd2561caea71") || uuid.equals("94427efa-215e-46ad-b827-0df4a48cc816");
		} else {
			return true;
		}
	}
}