package network;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class NetworkRestart extends Command implements Listener {
	private CountDownUtil countDown = null;
	private ScheduledTask task = null;
	
	public NetworkRestart() {
		super("networkRestart");
		Register.listener(this);
		Register.command(this);
	}
	
	@Override
	public void execute(CommandSender sender, String[] arguments) {
		if(Network.getInstance().isOwner(sender)) {
			if(arguments.length == 1) {
				try {
					int minutes = Integer.valueOf(arguments[0]);
					startCountDown(minutes);
					return;
				} catch(NumberFormatException e) {
					if(arguments[0].equalsIgnoreCase("stop")) {
						startCountDown(-1);
						return;
					}
				}
			}
			Message.send(sender, "&f/networkRestart <minutes | stop>");
		} else {
			Message.sendUnknownCommand(sender);
		}
	}
	
	private void startCountDown(int seconds) {
		if(task != null) {
			task.cancel();
			task = null;
		}
		countDown = null;
		if(seconds < 0) {
			Message.alert("&2Network Restart Cancelled");
		} else {
			countDown = new CountDownUtil(seconds * 60);
			task = BungeeCord.getInstance().getScheduler().schedule(Network.getInstance(), new Runnable() {
				@Override
				public void run() {
					if(countDown.getCounter() == 0) {
						task.cancel();
						task = null;
						countDown = null;
						for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
							player.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&2Network Restarting")));
						}
						BungeeCord.getInstance().getScheduler().schedule(Network.getInstance(), new Runnable() {
							@Override
							public void run() {
								BungeeCord.getInstance().stop();
							}
						}, 1, TimeUnit.SECONDS);
					} else {
						if(countDown.canDisplay()) {
							Message.alert("&2Network Restart in " + countDown.getCounterAsString());
						}
						countDown.decrementCounter();
					}
				}
			}, 1, 1, TimeUnit.SECONDS);
		}
	}
}
