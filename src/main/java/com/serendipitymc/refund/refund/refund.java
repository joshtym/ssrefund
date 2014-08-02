package com.serendipitymc.refund.refund;

import org.bukkit.plugin.java.JavaPlugin;

public class refund extends JavaPlugin{
	@Override
	public void onEnable() {
		getLogger().info("Successfully enabled refund plugin");
	}
	
	public void onDisable() {
		getLogger().info("Successfully disabled refund plugin");
	}
}
