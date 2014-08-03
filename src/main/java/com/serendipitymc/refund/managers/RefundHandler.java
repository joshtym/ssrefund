package com.serendipitymc.refund.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.bukkit.Bukkit;

import com.serendipitymc.refund.refund.refund;

public class RefundHandler {
	public static refund plugin = refund.getInstance();
    private Connection connection;
    
	public int getExecutableAmount() {
		return 5;
	}
	
	private Connection establishConnection() {
		try {
			if (connection != null)
				if (connection.isClosed() == false)
					return connection;
			String hostname = plugin.getConfig().getString("mysql.ip");
			String username = plugin.getConfig().getString("mysql.user");
			String password = plugin.getConfig().getString("mysql.password");
			String thRefund = plugin.getConfig().getString("mysql.tables.refunds");
			String thRefundDetail = plugin.getConfig().getString("mysql.tables.items");
			connection = DriverManager.getConnection("jdbc:mysql://" + hostname, username, password);
			Statement sh = connection.createStatement();
			sh.execute("CREATE TABLE IF NOT EXISTS " + thRefund + "(refund_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, opened_by VARCHAR(128) NOT NULL, player VARCHAR(128) NOT NULL, status ENUM('open', 'in progress', 'approved', 'signed off', 'executed', 'denied') DEFAULT 'open', final_decision_by VARCHAR(128), created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, KEY idx_player(player), KEY idx_opened_by(opened_by), KEY idx_status(status)) Engine=InnoDB;");
			sh.execute("CREATE TABLE IF NOT EXISTS " + thRefundDetail + "(detail_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, refund_id INT UNSIGNED NOT NULL, amount INT UNSIGNED NOT NULL, item_id INT UNSIGNED NOT NULL, item_meta INT UNSIGNED NOT NULL, KEY idx_lookup(refund_id, detail_id)) Engine=InnoDB;");
			connCleanup();
			return connection;
		} catch (Exception e) {
			plugin.getLogger().severe("Unable to establish a connection to the DB, disabling myself as I'm useless without it");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return null;
		}
	}
	
	private void connCleanup() {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 3L);
	}
	
	public boolean createRefund(String submitter, String beneficiary, Timestamp date) throws SQLException {
		Connection conn = establishConnection();
		String thRefund = plugin.getConfig().getString("mysql.tables.refunds");
		PreparedStatement ps = conn.prepareStatement("INSERT INTO " + thRefund + "(opened_by, player, created_at, updated_at) VALUES (?,?,?,?)");
		ps.setString(1, submitter);
		ps.setString(2, beneficiary);
		ps.setTimestamp(3, date);
		ps.setTimestamp(4, date);
		ps.execute();
		ps.close();
		return true;
	}
	
	public int countRefunds(String beneficiary) throws SQLException {
		Connection conn = establishConnection();
		String thRefund = plugin.getConfig().getString("mysql.tables.refunds");
		Statement sh = conn.createStatement();
		ResultSet rs = sh.executeQuery("SELECT COUNT(1) FROM " + thRefund + " WHERE player = '" + beneficiary + "' AND status IN ('open', 'in progress')");
		int refundRequests = 0;
		while (rs.next()) {
			refundRequests = rs.getInt(1);
		}
		rs.close();
		return refundRequests;
	}
	
	public int getLatestRefundId(String player) throws SQLException {
		Connection conn = establishConnection();
		String thRefund = plugin.getConfig().getString("mysql.tables.refunds");
		Statement sh = conn.createStatement();
		ResultSet rs = sh.executeQuery("SELECT refund_id FROM " + thRefund + " WHERE player = '" + player + "' AND status IN ('open', 'in progress')");
		int refundRequests = 0;
		while (rs.next()) {
			refundRequests = rs.getInt(1);
		}
		rs.close();
		return refundRequests;
	}
	
	public void addRefund(String player, Integer quantity, Integer itemid, short metaid, Integer refundId) throws SQLException {
		Connection conn = establishConnection();
		String thRefundDetail = plugin.getConfig().getString("mysql.tables.items");
		String thRefund = plugin.getConfig().getString("mysql.tables.refunds");
		PreparedStatement ps = conn.prepareStatement("UPDATE " + thRefund + " SET status = ?, updated_at = NOW() WHERE refund_id = ?");
		ps.setString(1, "in progress");
		ps.setInt(2, refundId);
		ps.execute();
		ps.close();
		ps = conn.prepareStatement("INSERT INTO " + thRefundDetail + " (refund_id, amount, item_id, item_meta) VALUES (?,?,?,?)");
		ps.setInt(1, refundId);
		ps.setInt(2, quantity);
		ps.setInt(3, itemid);
		ps.setInt(4, metaid);
		ps.execute();
		ps.close();		
	}
	
	public void signRefund(Integer refundId) throws SQLException {
		Connection conn = establishConnection();
		String thRefund = plugin.getConfig().getString("mysql.tables.refunds");
		Statement sh = conn.createStatement();
		PreparedStatement ps = conn.prepareStatement("UPDATE " + thRefund + " SET status = 'signed off', updated_at = NOW() WHERE refund_id = ?");
		ps.setInt(1, refundId);
		ps.execute();
		ps.close();
		return;
	}
	
	public void listPendingApprovals() throws SQLException {
		Connection conn = establishConnection();
		String thRefund = plugin.getConfig().getString("mysql.tables.refunds");
		String thRefundDetail = plugin.getConfig().getString("mysql.tables.items");
		
		
	}
}
