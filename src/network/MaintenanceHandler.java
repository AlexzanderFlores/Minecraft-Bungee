package network;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.TimeUnit;

public class MaintenanceHandler extends Command implements Listener {
	private boolean enabled = false;
	private CountDownUtil countDown = null;
	private ScheduledTask task = null;
	
	public MaintenanceHandler() {
		super("maintenance");
		Register.listener(this);
		Register.command(this);
        if(DB.NETWORK_SERVER_LIST.getInt("data_type", "maintenance", "data_value") == 1) {
        	enable();
        }
    }
    
    public void disableMaintenance() {
    	if(enabled) {
    		if(DB.NETWORK_SERVER_LIST.isKeySet("data_type", "maintenance")) {
    			DB.NETWORK_SERVER_LIST.updateInt("data_value", 1, "data_type", "maintenance");
    		} else {
    			DB.NETWORK_SERVER_LIST.insert("'maintenance', '1'");
    		}
    	} else {
    		DB.NETWORK_SERVER_LIST.delete("data_type", "maintenance");
    	}
    }
    
	@Override
	public void execute(CommandSender sender, String[] arguments) {
		if(Network.getInstance().isOwner(sender)) {
			if(sender instanceof ProxiedPlayer) {
				String command = "maintenance";
				for(String argument : arguments) {
					command += " " + argument;
				}
				CommandDispatcher.dispatch(command);
				return;
			} else {
				if(arguments.length == 1) {
					if(arguments[0].equalsIgnoreCase("on")) {
						enable();
						return;
					} else if(arguments[0].equalsIgnoreCase("off")) {
						disable();
						return;
					} else if(arguments[0].equalsIgnoreCase("stop")) {
						startCountDown(-1);
						return;
					}
				} else if(arguments.length == 2) {
					if(arguments[0].equalsIgnoreCase("start")) {
						try {
							startCountDown(Integer.valueOf(arguments[1]));
							return;
						} catch(NumberFormatException e) {
							
						}
					}
				}
			}
			Message.send(sender, "/maintenance <on | off | start | stop> [minutes]");
		} else {
			Message.sendUnknownCommand(sender);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onProxyPing(ProxyPingEvent event) {
		if(enabled) {
			event.getResponse().setDescription(ChatColor.translateAlternateColorCodes('&', "&2&lMaintenance Mode\n&aFollow &e@1v1sNetwork"));
		}
	}
	
	@EventHandler
	public void onLogin(LoginEvent event) {
		if(!event.isCancelled() && enabled) {
			if(DB.PLAYERS_ACCOUNTS.isUUIDSet(event.getConnection().getUniqueId())) {
				String rank = DB.PLAYERS_ACCOUNTS.getString("uuid", event.getConnection().getUniqueId().toString(), "rank");
				if(rank.equals("OWNER") || rank.equals("SENIOR_STAFF") || rank.equals("STAFF") || rank.equals("TRIAL")) {
					return;
				}
			}
			event.setCancelReason(ChatColor.translateAlternateColorCodes('&', "&2Maintenance Mode"));
			event.setCancelled(true);
		}
	}
	
	private void startCountDown(int seconds) {
		cancel();
		countDown = null;
		if(seconds < 0) {
			Message.alert("&2Maintenance Mode Cancelled");
		} else {
			countDown = new CountDownUtil(seconds * 60);
			task = BungeeCord.getInstance().getScheduler().schedule(Network.getInstance(), new Runnable() {
				@Override
				public void run() {
					if(countDown.getCounter() == 0) {
						cancel();
						countDown = null;
						enable();
					} else {
						if(countDown.canDisplay()) {
							Message.alert("&2Maintenance Mode in " + countDown.getCounterAsString());
						}
						countDown.decrementCounter();
					}
				}
			}, 1, 1, TimeUnit.SECONDS);
		}
	}
	
	private void cancel() {
		if(task != null) {
			task.cancel();
			task = null;
		}
	}
	
	private void enable() {
		enabled = true;
		for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
			player.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&2Maintenance mode enabled")));
		}
		BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), "alert &2Maintenance mode enabled");
	}
	
	private void disable() {
		enabled = false;
		BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), "alert &2Maintenance mode disabled");
	}
}
