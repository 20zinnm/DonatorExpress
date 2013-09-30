package net.targetcraft.donatorexpress;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
	
	static Main plugin;
	Connection con;

	public Database(Main config) {
		plugin = config;
	}
	
	public boolean connect()
	{
		String dbUsername = plugin.getConfig().getString("db-username");
		String dbPassword = plugin.getConfig().getString("db-password");
		String dbHost = plugin.getConfig().getString("db-host");
		String dbName = plugin.getConfig().getString("db-name");
		String dbURL = "jdbc:mysql://" + dbHost + "/" + dbName;
		try {
			con = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
			return true;
		} catch (SQLException e) {
			Logger.getLogger(plugin.getDataFolder()+"log.log").log(Level.SEVERE, "Error while trying to connect to the database");			
			e.printStackTrace();
			Logger.getLogger(plugin.getDataFolder()+"log.log").log(Level.SEVERE, "Error while trying to connect to the database.");
		    
			if(plugin.getConfig().getBoolean("disable-on-database-error"))
			{
				plugin.getPluginLoader().disablePlugin(plugin);
				Logger.getLogger(plugin.getDataFolder()+"log.log").log(Level.SEVERE, "DonatorExpress disabled. Reload server to re enable");
			}
			return false;

		}
	}
	
	public void executeStatement(String statement)
	{
		if(connect())
		{
			
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
		{
			
		}
	}
	
	public void executeUpdate(String statement)
	{
		
	}
	
	public void disconnect()
	{
		
	}

}
