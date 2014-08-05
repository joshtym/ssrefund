package com.serendipitymc.refund.refund;


import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.serendipitymc.refund.managers.CommandManager;
import com.serendipitymc.refund.managers.RefundHandler;
import com.serendipitymc.refund.util.SSUtil;

public class refund extends JavaPlugin{

    public CommandManager commandManager;
    public YamlConfiguration Messages;
    private static refund plugin;
    public File configFile;
    private File messages;
    private RefundHandler refundHandler;
    private SSUtil ssUtil;
    
	@Override
	public void onEnable() {
		getLogger().info("Successfully enabled refund plugin");

        plugin = this;
        commandManager = new CommandManager(this);
        refundHandler = new RefundHandler();
        ssUtil = new SSUtil();
        
        parseconfig();

        commandManager.init();

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new RefundListener(this), this);

        PluginDescriptionFile pdfFile = this.getDescription();
        // 20 ticks per second
        new BukkitRunnable() {
			 public void run() {
				 System.out.println("Checking for any pending approved refunds");
				 try {
					 String server = Bukkit.getServerName();
					 refundHandler.executePendingRefund(server);
				 } catch (Exception e) {
					 // Caught an exception
					 e.printStackTrace();
				 }
			 }
		 }.runTaskTimer(this, 20, 600);
		 
		 
	}
	
	public void onDisable() {
		getLogger().info("Successfully disabled refund plugin");
	}
	
	public static refund getInstance() {
        return plugin;
    }
	
	public RefundHandler getRH() {
		return refundHandler;
	}
	
	public SSUtil getUtil() {
		return ssUtil;
	}
	
	public void parseconfig() {
		configFile = new File(getDataFolder().getAbsolutePath() + "/config.yml");
        if (!configFile.exists()) {
            populateConfig();
        }
	}
	
	public void populateConfig() {
		this.saveDefaultConfig();
	}
}
