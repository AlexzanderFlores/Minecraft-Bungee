package network;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ServerPerformance extends Command {
	private static boolean hasStartedUpdate = false;
	private static int uptime = 0;
	
	public ServerPerformance() {
		super("networkPerformance");
		Register.command(this);
		BungeeCord.getInstance().getScheduler().schedule(Network.getInstance(), new Runnable() {
			@Override
			public void run() {
				uptime += 5;
				if(!hasStartedUpdate && getMemory() >= 80) {
					hasStartedUpdate = true;
					BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), "networkUpdate 30");
				}
			}
		}, 5, 5, TimeUnit.SECONDS);
	}
	
	@Override
	public void execute(CommandSender sender, String[] arguments) {
		if(Network.getInstance().isOwner(sender)) {
			Message.send(sender, "----------------- " + "&bNetwork Performance" + " &x-----------------");
			Message.send(sender, "&bInstance: &x" + Network.getInstance().getServer());
			Message.send(sender, "&bUsed Memory: &x" + getMemory() + "%");
			String message = null;
			if(uptime < 60) {
				message = uptime + " second(s)";
			} else if(uptime < (60 * 60)) {
				int minutes = getAbsoluteValue((uptime / 60));
				int seconds = getAbsoluteValue((uptime % 60));
				message = minutes + " minute(s) and " + seconds + " second(s)";
			} else {
				int hours = getAbsoluteValue((uptime / 60 / 60));
				int minutes = getAbsoluteValue((hours * 60) - (uptime / 60));
				int seconds = getAbsoluteValue((uptime % 60));
				message = hours + " hour(s) and " + minutes + " minute(s) and " + seconds + " second(s)";
			}
			Message.send(sender, "&bUptime: &x" + message);
			Message.send(sender, "&bConnected clients: &x" + BungeeCord.getInstance().getPlayers().size());
			Message.send(sender, "&bAverage ping: &x" + getAveragePing());
			if(sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer) sender;
				Message.send(sender, "&bYour ping: &x" + (player.getPing() / 2));
			}
			Message.send(sender, "-----------------------------------------------------");
		} else {
			Message.sendUnknownCommand(sender);
		}
	}
	
	private int getMemory() {
		double total = Runtime.getRuntime().totalMemory() / (1024 * 1024);
		double allocated = Runtime.getRuntime().maxMemory() / (1024 * 1024);
		return (int) (total * 100.0d / allocated + 0.5);
	}
	
	private int getAbsoluteValue(int value) {
		return value < 0 ? value * -1 : value;
	}
	
	private int getAveragePing() {
		int ping = 0;
		int online = 0;
		for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
			ping += player.getPing() / 2;
			++online;
		}
		return online == 0 ? 0 : ping / online;
	}
}
