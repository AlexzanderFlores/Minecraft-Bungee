package network;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

public class Register {
	public static void listener(Listener listener) {
		BungeeCord.getInstance().getPluginManager().registerListener(network.Network.getInstance(), listener);
	}
	
	public static void command(Command command) {
		BungeeCord.getInstance().getPluginManager().registerCommand(network.Network.getInstance(), command);
	}
}
