package com.serendipitymc.refund.command;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.serendipitymc.refund.korik.SubCommandExecutor;
import com.serendipitymc.refund.korik.Utils;
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
    
    
	@command(minimumArgsLength = 2, permissions = {"ssrefund.create"}, usage = "/refund create <playername> <brief explanation>", description = "Creates refund request for the user")
	public void create (CommandSender sender, String[] args) {
		try {
			util = plugin.getUtil();
			refunds = plugin.getRH();
			String submitter;
			boolean realPlayer = false;
			Integer refundAmount = 0;
			String comment = Utils.join(args, " ", 1);
		
			if(sender instanceof Player) {
				submitter = sender.getName().toString().toLowerCase();
				realPlayer = true;
			} else {
				submitter = "console";
			}
			Timestamp date = new Timestamp(System.currentTimeMillis());
			String server = Bukkit.getServerName();
			refundAmount = refunds.countRefunds(args[0], server);
			if (refundAmount >= 1) {
				util.sendNotificationMessage(plugin, sender, realPlayer, "This user has too many pending refund requests");
				return;
			}

			refunds.createRefund(submitter, args[0].toLowerCase(), date, comment, server);
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
	
	@command(maximumArgsLength = 1, minimumArgsLength = 0, permissions = {"ssrefund.list"}, usage = "/refund list <page>", description = "Shows pending refunds")
	public void list(CommandSender sender, String[] args) {
		util = plugin.getUtil();
		refunds = plugin.getRH();
		boolean listAll = false;
		int page = 1;
		
        if (args.length == 1)
			if (args[0].equals("all"))
				listAll = true;
			else {
				try {
					page = java.lang.Integer.parseInt(args[0]);
				} catch (Exception e) {
					page = 1;
				}
			}
					
				
		if (sender instanceof Player) {
			try {
				refunds.listPendingApprovals((Player) sender, page, listAll);
			} catch (Exception e) {
				sender.sendMessage("Something went horribly wrong. Please quote error ssr104");
			}
		}
	}
	/* 
	// Only for testing
	@command(maximumArgsLength = 1, minimumArgsLength = 0, permissions = {"ssrefund.list"}, usage = "/refund offline <user>", description = "Shows offline person")
	public void offline(CommandSender sender, String[] args) {
		System.out.println("Running:");
		System.out.println(Bukkit.getOfflinePlayer(args[0]).getName());
	}
	*/
	
	@command(maximumArgsLength = 2, minimumArgsLength = 1, permissions = {"ssrefund.list"}, usage = "/refund detail <id> <page>", description = "Shows the full refund detail")
	public void detail(CommandSender sender, String[] args) {
		util = plugin.getUtil();
		refunds = plugin.getRH();
		int page = 1;
        if (args.length == 2) {
        	if (util.isNumeric(args[1])) {
        		page = java.lang.Integer.parseInt(args[1]);
        	} else {
        		page = 1;
        	}
        }
		if (sender instanceof Player) {
			try {
				if (util.isNumeric(args[0])) {
					refunds.getRefundDetailById((Player) sender, Integer.parseInt(args[0]), page);
				} else {
					util.sendMessageGG((Player) sender, "id needs to be a numeric");
				}
			} catch (Exception e) {
				sender.sendMessage("Something went horribly wrong. Please quote error ssr105");
			}
		}
	}
	
	@command(maximumArgsLength = 1, minimumArgsLength = 1, permissions = {"ssrefund.list"}, usage = "/refund history <player>", description = "Show refund history for a given playername")
	public void history(CommandSender sender, String[] args) {
		util = plugin.getUtil();
		refunds = plugin.getRH();
		if (sender instanceof Player) {
			try {
				refunds.showPlayerHistoryBrief((Player) sender, args[0]);
			} catch (Exception e) {
				sender.sendMessage("Something went wrong. Please report error ssr109");
			}
		}
	}
	@command(maximumArgsLength = 1, minimumArgsLength = 1, permissions = {"ssrefund.deny"}, usage = "/refund deny <id>", description = "Denies a refund request")
	public void deny(CommandSender sender, String[] args) {
		util = plugin.getUtil();
		refunds = plugin.getRH();
		if (sender instanceof Player) {
			try {
				if (util.isNumeric(args[0])) {
					refunds.denyRefundId(Integer.parseInt(args[0]), sender.getName().toLowerCase());
					sender.sendMessage(ChatColor.GOLD + "[Refunds] " + ChatColor.GRAY + "Successfully denied refund # " + args[0]);
				} else {
					util.sendMessageGG((Player) sender, "id needs to be a numeric");
				}
			} catch (Exception e) {
					sender.sendMessage("Something went boom. Please quote error ssr106");
				}
			}
		}
	
	@command(maximumArgsLength = 1, minimumArgsLength = 1, permissions = {"ssrefund.execute"}, usage = "/refund approve <id>", description = "Approves a refund request")
	public void approve(CommandSender sender, String[] args) {
		util = plugin.getUtil();
		refunds = plugin.getRH();
		if (sender instanceof Player) {
			try {
				if (util.isNumeric(args[0])) {
					refunds.approveRefundId(Integer.parseInt(args[0]), sender.getName().toLowerCase());
					sender.sendMessage(ChatColor.GOLD + "[Refunds] " + ChatColor.GRAY + "Successfully approved/executed refund # " + args[0]);
				} else {
					util.sendMessageGG((Player) sender, "id needs to be a numeric");
				}
			} catch (Exception e) {
					sender.sendMessage("Something went boom. Please quote error ssr107");
				}
			}
		}
	
	@command(maximumArgsLength = 1, minimumArgsLength = 1, permissions = {"ssrefund.testexecute"}, usage = "/refund test <id>", description = "Tests a refund request")
	public void test(CommandSender sender, String[] args) {
		util = plugin.getUtil();
		String serverName = Bukkit.getServerName();
		refunds = plugin.getRH();
		
		if (sender instanceof Player) {
			try {
				if (util.isNumeric(args[0]))
					refunds.testExecute(Integer.parseInt(args[0]), sender.getName().toLowerCase(), serverName);
				else
					util.sendMessageGG((Player) sender, "id needs to be a numeric");
			} catch (Exception e) {
					sender.sendMessage("Something went boom. Please quote error ssr108");
				}
			}
		}
	}

