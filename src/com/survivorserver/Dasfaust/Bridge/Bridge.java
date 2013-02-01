/**
 * Super simple XenForo bridging plugin by Dasfaust. No license.
 */
package com.survivorserver.Dasfaust.Bridge;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Bridge extends JavaPlugin implements Listener {
	
	public String sqlUsername;
	public String sqlPass;
	public String sqlAddress;
	public String dbName;
	public String promoteTo;
	public boolean promoteOnLogin;
	public String whitelistKickMsg;
	public String invalidKickMsg;
	public boolean isWhitelist;
	public boolean requireValidEmail;
	Logger log;
	
	MySQLFunc sql;
	
	public Permission perms;
	
	public void onEnable() {
		log = getLogger();
		loadConfig();
		sql = new MySQLFunc();
		sql.DisableReconnect();
		boolean connected = sql.connect(sqlAddress, sqlUsername, sqlPass);
		if (!connected) {
			log.severe("Could not establish a connection to the database. Is the config correct?");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		boolean selected = sql.SelectDB(dbName);
		if (!selected) {
			log.severe("Could not select database. Is the config correct?");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getServer().getPluginManager().registerEvents(this, this);
		if (promoteOnLogin) {
			if (getServer().getPluginManager().getPlugin("Vault") == null) {
				log.severe("Could not find vault! Disabling promote on login. Continuing...");
				promoteOnLogin = false;
				return;
			}
			boolean permissions = getPerms();
			if (!permissions) {
				log.severe("Could not find a permissions provider! Disabling promote on login. Continuing...");
				promoteOnLogin = false;
			}
		}
	}
	
	public void onDisable() {
		sql.close();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		String name = event.getPlayer().getName();
		if (isWhitelist) {
			MySQLResult rs = sql.Query("SELECT * FROM `xf_user` WHERE `username`='" + name + "'");
			boolean registered = false;
			boolean valid = false;
			while (rs.next()) {
				registered = true;
				if (rs.getString("user_state").equalsIgnoreCase("valid")) {
					valid = true;
				}
			}
			if (!registered) {
				//event.disallow(Result.KICK_WHITELIST, whitelistKickMsg);
				event.setResult(Result.KICK_WHITELIST);
				event.setKickMessage(whitelistKickMsg);
				return;
			}
			if (requireValidEmail) {
				if (!valid) {
					//event.disallow(Result.KICK_WHITELIST, invalidKickMsg);
					event.setResult(Result.KICK_WHITELIST);
					event.setKickMessage(invalidKickMsg);
					return;
				}
			}
		}
		if (promoteOnLogin) {
			if (!perms.playerInGroup(player, promoteTo)) {
				perms.playerAddGroup(player, promoteTo);
				log.info("Promoting " + name + " to " + promoteTo);
			}
		}
	}
	
	public void loadConfig() {
		FileConfiguration conf = getConfig();
		if (!conf.isSet("bridge.conf_version")) {
			conf.set("bridge.conf_version", 1);
			conf.set("mysql.username", "root");
			conf.set("mysql.pass", " ");
			conf.set("mysql.address", "localhost");
			conf.set("mysql.dbname", "xenforo");
			conf.set("permissions.promote_on_login", true);
			conf.set("permissions.promote_to", "Regulars");
			conf.set("whitelist.enabled", true);
			conf.set("whitelist.require_verified_email", true);
			conf.set("whitelist.kick_msg", "Please register on our website at: someserver.com");
			conf.set("whitelist.unverified_email_kick", "Please verify your email on our website");
			saveConfig();
		}
		sqlUsername = conf.getString("mysql.username");
		sqlPass = conf.getString("mysql.pass");
		sqlAddress = conf.getString("mysql.address");
		dbName = conf.getString("mysql.dbname");
		promoteOnLogin = conf.getBoolean("permissions.promote_on_login");
		promoteTo = conf.getString("permissions.promote_to");
		isWhitelist = conf.getBoolean("whitelist.enabled");
		requireValidEmail = conf.getBoolean("whitelist.require_verified_email");
		whitelistKickMsg = conf.getString("whitelist.kick_msg");
		invalidKickMsg = conf.getString("whitelist.unverified_email_kick");
	}
	
	public boolean getPerms() {
		RegisteredServiceProvider<Permission> permsProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permsProvider != null) {
			perms = permsProvider.getProvider();
		}
		return (perms != null);
	}
}
