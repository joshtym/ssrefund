package com.serendipitymc.refund.command;

import java.sql.Timestamp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.serendipitymc.refund.korik.SubCommandExecutor;
import com.serendipitymc.refund.managers.RefundHandler;
import com.serendipitymc.refund.refund.refund;
import com.serendipitymc.refund.util.SSUtil;

public class refundCommand extends SubCommandExecutor {
	private refund plugin;
	private RefundHandler refunds;
	private SSUtil util;

	public refundCommand(refund instance) {
		plugin = instance;
	}
	
    @Override
    public void onInvalidCommand(CommandSender sender, String[] args, String command) {
    	try {
    		sender.sendMessage("Invalid command");
    	} catch (Exception e) {
    		sender.sendMessage("Something went wrong. x102");
    		return;
    	}
    }
    
    
	@command(maximumArgsLength = 1, minimumArgsLength = 1, permissions = {"ssrefund.create"}, usage = "/refund create <playername>", description = "Creates refund request for the user")
	public void create (CommandSender sender, String[] args) {
		try {
			util = plugin.getUtil();
			refunds = plugin.getRH();
			String submitter;
			boolean realPlayer = false;
			Integer refundAmount = 0;
		
			if(sender instanceof Player) {
				submitter = sender.getName().toString().toLowerCase();
				realPlayer = true;
			} else {
				submitter = "console";
			}
			Timestamp date = new Timestamp(System.currentTimeMillis());
		
			refundAmount = refunds.countRefunds(args[0]);
			if (refundAmount >= 1) {
				util.sendNotificationMessage(plugin, sender, realPlayer, "This user has too many pending refund requests");
				return;
			}

			refunds.createRefund(submitter, args[0].toLowerCase(), date);
			util.notifyIfOnline(args[0].toLowerCase(), submitter);
			util.sendNotificationMessage(plugin, sender, realPlayer, "Created refund request successfully for: " + args[0].toLowerCase());
		} catch (Exception e) {
			if (e instanceof NullPointerException) {
				e.printStackTrace();
				return;
			} else {
				util.sendNotificationMessage(plugin, sender, true , "Couldn't create a refund request. Internal error ssr101");
				return;
			}
		}
	}
	
}
