package network;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.BungeeCord;

public class LogDeletion {
	public LogDeletion() {
		BungeeCord.getInstance().getScheduler().schedule(Network.getInstance(), new Runnable() {
			@Override
			public void run() {
				File file = new File(BungeeCord.getInstance().getPluginsFolder().getPath() + "/../");
				String [] files = file.list(new FilenameFilter() {
					@Override
					public boolean accept(File current, String name) {
						return new File(current, name).isFile();
					}
				});
				for(String possibleLog : files) {
					File log = new File(file.getPath() + "/" + possibleLog);
					if(log.getPath().contains("proxy.log.")) {
						BungeeCord.getInstance().getLogger().info("Deleting " + log.getName());
						log.delete();
					}
				}
			}
		}, 0, 60, TimeUnit.MINUTES);
	}
}
