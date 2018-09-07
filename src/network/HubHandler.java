package network;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HubHandler implements Listener {
	private static int hub = 1;
	private static int backupHub = 1;
	
	public HubHandler() {
		BungeeCord.getInstance().getScheduler().schedule(Network.getInstance(), new Runnable() {
			@Override
			public void run() {
				loadHub();
			}
		}, 0, 1, TimeUnit.SECONDS);
		Register.listener(this);
	}
	
	private static void loadHub() {
		for(String server : new String [] {"HUB"}) {
			List<String> servers = DB.NETWORK_SERVER_STATUS.getOrdered("players, server_number", "server_number", "game_name", server, 2);
			if(server.equals("HUB")) {
				if(servers != null && servers.size() >= 1) {
					hub = Integer.valueOf(servers.get(0));
				} else {
					hub = 1;
				}
				if(servers != null && servers.size() == 2) {
					backupHub = Integer.valueOf(servers.get(1));
				} else {
					backupHub = 1;
				}
			}
			servers.clear();
		}
	}
	
	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		if(event.getPlayer().getServer() == null || event.getTarget().getName().equalsIgnoreCase("hub")) {
			if(event.getPlayer().getServer() != null && event.getPlayer().getServer().getInfo().getName().equalsIgnoreCase("hub" + hub)) {
				event.setTarget(BungeeCord.getInstance().getServerInfo("hub" + backupHub));
			} else {
				event.setTarget(BungeeCord.getInstance().getServerInfo("hub" + hub));
			}
		}
	}
}
