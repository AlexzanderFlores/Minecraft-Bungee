package network;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

public class CommandDispatcher extends Command implements Listener {
	public CommandDispatcher() {
		super("dispatch");
		Register.listener(this);
		Register.command(this);
//		BungeeCord.getInstance().getScheduler().schedule(Network.getInstance(), new Runnable() {
//			@Override
//			public void run() {
//				if(DB.NETWORK_COMMAND_DISPATCHER.isKeySet("server", Network.getInstance().getServer())) {
//					for(String command : DB.NETWORK_COMMAND_DISPATCHER.getAllStrings("command", "server", Network.getInstance().getServer())) {
//						BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), command);
//					}
//					DB.NETWORK_COMMAND_DISPATCHER.delete("server", Network.getInstance().getServer());
//				}
//			}
//		}, 5, 5, TimeUnit.SECONDS);
	}
	
	@Override
	public void execute(CommandSender sender, String [] arguments) {
		if(Network.getInstance().isOwner(sender)) {
			if(arguments.length >= 1) {
				String command = "";
				for(String argument : arguments) {
					command += argument + " ";
				}
				dispatch(command.substring(0, command.length() - 1));
			} else {
				Message.send(sender, "&f/dispatch <command>");
			}
		} else {
			Message.send(sender, "Unknown command. Type \"/help\" for help.");
		}
	}
	
	public static void dispatch(String command) {
//		for(String server : DB.NETWORK_PROXIES.getAllStrings("server")) {
//			DB.NETWORK_COMMAND_DISPATCHER.insert("'" + server + "', '" + command + "'");
//		}
	}
}
