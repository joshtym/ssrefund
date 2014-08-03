package com.serendipitymc.refund.managers;


import com.serendipitymc.refund.command.addCommand;
import com.serendipitymc.refund.command.signCommand;
import com.serendipitymc.refund.command.refundCommand;
import com.serendipitymc.refund.refund.refund;

public class CommandManager {
	private refund plugin;
	private addCommand add;
	private signCommand sign;
	private refundCommand refundCommand;
	
	 public CommandManager(refund instance) {
	        plugin = instance;
	        add = new addCommand(plugin);
	        sign = new signCommand(plugin);
	        refundCommand = new refundCommand(plugin);
	 }
	 
	 public void init() {
		 plugin.getCommand("refund-add").setExecutor(add);
		 plugin.getCommand("refund-sign").setExecutor(sign);
		 plugin.getCommand("refund").setExecutor(refundCommand);
	 }
}
