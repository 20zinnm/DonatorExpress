package net.targetcraft.donatorexpress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CommandListener implements Listener, CommandExecutor {

	static Main plugin;

	public CommandListener(Main config) {
		plugin = config;
	}
	
	public static Economy econ = null;
	
	HashMap<String, String> confirmDel = new HashMap<String, String>();
	HashMap<String, List<String>> confirmDel2 = new HashMap<String, List<String>>();
	HashMap<String, Boolean> confirmDelBoolean = new HashMap<String, Boolean>();
		
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {		
		if(cmd.getName().equalsIgnoreCase("donate"))
		{			
			try{
			if(args[0].equalsIgnoreCase("add"))
			{		
				if(sender.hasPermission("donexpress.admin.add"))
				{
				if(!(args[1]==null))
				{
					File packages = new File(plugin.getDataFolder()+File.separator, "packages.yml");
					FileConfiguration packagesConfig=null;
					packagesConfig=new YamlConfiguration();
					packagesConfig.load(packages);
					
				    boolean configAdd=false;
				    boolean rename=false;
				    
				    try{
					sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("ADDATTEMPTING")+args[1]+"...");
					
					List<String>ranks = packagesConfig.getStringList("packages");
					List<String>ranks2=new ArrayList<String>(ranks);
					 
					 int count = 0;
				     if(ranks2.contains(args[1]))
				     {
				         ranks2.remove(args[1]);
				         count++;
				     }
				     if(count==1)
				     {
				    	 sender.sendMessage(prefix()+ChatColor.DARK_RED+Language.getPhrase("ALREADYEXISTS"));
				     }
				     else
				     {
				    	 ranks.add(args[1].toLowerCase());
				    	 packagesConfig.set("packages", ranks);
				    	 
				    	 File newFile = new File(plugin.getDataFolder()+"/packages"+File.separator ,args[1].toLowerCase()+".yml");
				    	 File newFileLocation = new File(plugin.getDataFolder()+"/packages"+File.separator);
				    	 
				    	 if(!newFileLocation.exists())
				    	 {
				    		 newFileLocation.mkdirs();
				    	 }
				    	 if(!newFile.exists())
				    	 {
				    		 
						     OutputStream out = null;
							 InputStream defaultStream = plugin.getResource("configs/[defaultPackageConfiguration].yml");
							 File defaultPackage = new File(plugin.getDataFolder()+"/packages"+File.separator, "[defaultPackageConfiguration].yml");
							 

					         try {
					            	out = new FileOutputStream(defaultPackage);
					                int read = 0;
					                byte[] bytes = new byte[1024];

									while((read = defaultStream.read(bytes)) != -1) {
									    out.write(bytes, 0, read);
									}
								    out.close();
								    
									if(defaultPackage.renameTo(newFile))
									{
										rename=true;
									}
									else
									{
										rename=false;
									}
								} catch (IOException e1) {
									e1.printStackTrace();
								} catch (NullPointerException e1)
								{
									e1.printStackTrace();
								} catch (SecurityException e1)
								{
									e1.printStackTrace();
								}
						     
						     configAdd=true;
				    	 }
				    	 else
				    	 {
				    		 sender.sendMessage(prefix()+ChatColor.DARK_RED+Language.getPhrase("ALREADYEXISTS"));
				    	 }  
				     }
				     if(configAdd||rename)
				     {
				    	 
						 packagesConfig.save(packages);
						 
				    	 sender.sendMessage(prefix()+ChatColor.GREEN+"Successfully added "+args[1]);
				     }
				     else
				     {
				    	 sender.sendMessage(prefix()+ChatColor.DARK_RED+"Major error. If the plugin created a file called, [defaultPackageConfiguration].yml NOTIFY THE DEVELOPER RIGHT AWAY");
				    	 sender.sendMessage(prefix()+ChatColor.RED+"Otherwise, delete "+args[1]+" in packages.yml, and try again");
				     }
				     				
				} catch (ArrayIndexOutOfBoundsException e)
				{
					configAdd=false;
					sender.sendMessage(prefix()+"Error. Invalid syntax. Type "+ChatColor.GREEN+"/donate help for commands");
				} catch (IOException e) {
					sender.sendMessage(prefix()+ChatColor.DARK_RED+"Error. forumConfig doesn't exist o.O Try to reload the server and try again");
					e.printStackTrace();
				}
				}
				
				else
				{
					noPermission(sender);
				}
				}
			}
			
			else if (args[0].equalsIgnoreCase("delete"))
			{
				if(sender.hasPermission("donexpress.admin.delete"))
				{
				if(!(args[1]==null))
				{
					try {
						File packages = new File(plugin.getDataFolder()+File.separator, "packages.yml");
						FileConfiguration packagesConfig=null;
						packagesConfig=new YamlConfiguration();
						packagesConfig.load(packages);
						
						File newFile = new File(plugin.getDataFolder()+"/packages"+File.separator ,args[1].toLowerCase()+".yml");				    	
						FileConfiguration newFileConfig=null;
			    		newFileConfig=new YamlConfiguration();
			    		newFileConfig.load(newFile);
						
						sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("REMOVEATTEMPTING")+args[1]+"...");
						List<String>ranks = packagesConfig.getStringList("packages".toLowerCase());
						List<String>ranks2=new ArrayList<String>(ranks);
					 
						int count = 0;
						if(!ranks2.contains(args[1]))
					     {
					         count++;
					     }
						if(count==1)
						{
							sender.sendMessage(prefix()+ChatColor.DARK_RED+Language.getPhrase("PACKAGENOTTHERE"));
				    	}
						else
						{
							ranks.remove(args[1].toLowerCase());
							confirmDel.put("confirm", args[1]);
							confirmDel2.put("confirm2", ranks);
							confirmDelBoolean.put("confirm", true);
						 
							sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("REMOVE1")+ChatColor.GREEN+"/donate confirmdel");
							sender.sendMessage(ChatColor.RED+Language.getPhrase("REMOVE2"));
							sender.sendMessage(ChatColor.RED+Language.getPhrase("REMOVE3")+ChatColor.GREEN+"/donate cancel"+ChatColor.RED+Language.getPhrase("REMOVE4"));
						}	
				
				}catch (ArrayIndexOutOfBoundsException e)
				{
					sender.sendMessage(prefix()+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help "+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX2"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
				} 
				
				}else
				{
					noPermission(sender);
				}
			}
			else if(args[0].equalsIgnoreCase("confirmdel"))
			{
				if(sender.hasPermission("donexpress.admin.delete"))
				{
					String packageName=confirmDel.get("confirm");
					List<String> stuffToRemove=confirmDel2.get("confirm2");
					
					File packages = new File(plugin.getDataFolder()+File.separator, "packages.yml");
					FileConfiguration packagesConfig=null;
					packagesConfig=new YamlConfiguration();
					packagesConfig.load(packages);
					
					File newFile = new File(plugin.getDataFolder()+"/packages"+File.separator ,packageName.toLowerCase()+".yml");
			    	FileConfiguration newFileConfig=null;
		    		newFileConfig=new YamlConfiguration();
		    		newFileConfig.load(newFile);
		    		
		    		if(confirmDelBoolean.get("confirm"))
		    		{
		    			packagesConfig.set("packages", stuffToRemove);
			    		newFile.delete();
			    		
			    		packagesConfig.save(packages);
			    		
			    		sender.sendMessage(prefix()+ChatColor.GREEN+Language.getPhrase("SUCCESSREMOVE")+packageName);
			    		confirmDel.clear();
			    		confirmDel2.clear();
			    		confirmDelBoolean.clear();
		    		}
		    		else
		    		{
		    			sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("REQUESTREMOVENOTDONE"));
		    		}
		    		
				}
			}
			else if(args[0].equalsIgnoreCase("list"))
			{
				if(sender.hasPermission("donexpress.user"))
				{
					File packages = new File(plugin.getDataFolder()+File.separator, "packages.yml");
					FileConfiguration packagesConfig=null;
					packagesConfig=new YamlConfiguration();
					packagesConfig.load(packages);
					
					List<String>ranks = packagesConfig.getStringList("packages".toLowerCase());
					sender.sendMessage(prefix()+ChatColor.AQUA+Language.getPhrase("CURRENTLISTPACKAGES2"));
					sender.sendMessage(prefix()+ChatColor.AQUA+Language.getPhrase("CURRENTLISTPACKAGES1"));
					for(String s:ranks)
					{
						sender.sendMessage(ChatColor.YELLOW+s);
					}
				}
				else
				{
					noPermission(sender);
				}
			}
			else if(args[0].equalsIgnoreCase("info"))
			{
				if(sender.hasPermission("donexpress.user"))
				{
					File packages = new File(plugin.getDataFolder()+File.separator, "packages.yml");
					FileConfiguration packagesConfig=null;
					packagesConfig=new YamlConfiguration();
					packagesConfig.load(packages);
					
					File newFile = new File(plugin.getDataFolder()+"/packages"+File.separator ,args[1].toLowerCase()+".yml");
					FileConfiguration newFileConfig=null;
		    		newFileConfig=new YamlConfiguration();
		    		newFileConfig.load(newFile);
		    		
					List<String>rank = packagesConfig.getStringList("packages");
			        List<String>rankCopy=new ArrayList<String>(rank);
			        
			        String datRankName=args[1].toLowerCase();
					int count = 0;
				    if(rankCopy.contains(datRankName))
				    {
				        rankCopy.remove(datRankName);
				        count++;
				    }
				    if(count==1)
				    {
				    	List<String> description=newFileConfig.getStringList("description");
				    	for(String s:description)
				    	{
				    		sender.sendMessage(colourize(s));
				    	}
				    }
				}
				else
				{
					noPermission(sender);
				}
			}
			else if (args[0].equalsIgnoreCase("check"))
			{
				Database.connect();
				if(sender.hasPermission("donexpress.user"))
				{
					if(sender instanceof Player)
					{
						String VCName=plugin.getConfig().getString("currency-name");
						String website=plugin.getConfig().getString("portal-location");
						String username="'"+sender.getName()+"'";
						try {						
							ResultSet result=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
							if(result.next())
							{
								sender.sendMessage(prefix()+ChatColor.AQUA+Language.getPhrase("TOKENSRETURN")+result.getString(1)+" "+VCName);
							}
							else
							{
								sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("NOACCOUNT1")+website+Language.getPhrase("NOACCOUNT2"));
							}
						} catch (SQLException e) {
							sender.sendMessage(prefix()+ChatColor.YELLOW+"The database returned an error. Please tell an Admin or Owner about this problem so they can investigate further");
							e.printStackTrace();
						} catch (ArrayIndexOutOfBoundsException e)
						{
							sender.sendMessage(prefix()+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help "+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX2"));
						}
					}
				}
				else
				{
					noPermission(sender);
				}
				Database.close();
			}
			else if (args[0].equalsIgnoreCase("checkvc"))
			{
				Database.connect();
				
				if(sender.hasPermission("donexpress.admin.checkvc"))
				{
				String VCName=plugin.getConfig().getString("currency-name");
			    String website=plugin.getConfig().getString("portal-location");			
				if(!(args[1]==null))
				{
					String username="'"+args[1]+"'";
					try {
						ResultSet result=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
						if(result.next())
						{
							sender.sendMessage(prefix()+ChatColor.AQUA+args[1]+" currently has "+result.getString(1)+" "+VCName);
						}
						else
						{
							sender.sendMessage(prefix()+ChatColor.YELLOW+"I could not find that username in the database. Please tell the player to register at "+website);
						}
					} catch (SQLException e) {
						sender.sendMessage(prefix()+ChatColor.YELLOW+"The database returned an error. Please tell an Admin or Owner about this problem so they can investigate further");
						e.printStackTrace();
					} catch (ArrayIndexOutOfBoundsException e)
					{
						sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help "+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX2"));
					}
				}
				else
				{
					
				}
				}
				else
				{
					noPermission(sender);
				}
				Database.close();
			}
			else if(args[0].equalsIgnoreCase("checkp"))
			{
				Database.connect();
				
				if(sender.hasPermission("donexpress.admin.checkp"))
				{
					try {
						String username="'"+args[1]+"'";
						ResultSet result=Database.executeStatement("SELECT `rank`, `date` FROM packages_purchased  WHERE username = '"+username+"'");
						sender.sendMessage(prefix()+args[1]+"'s package buys:");
						while(result.next())
						{
							sender.sendMessage(ChatColor.RED+"Package: "+result.getString(1));
							sender.sendMessage(ChatColor.RED+"Date: "+result.getString(2));
						}
					} catch (SQLException e) {
						sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("DBERROR"));
						e.printStackTrace();
					}
					
				}
				Database.close();
			}
			else if(args[0].equalsIgnoreCase("addvc"))
			{
				Database.connect();
				
				if(sender.hasPermission("donexpress.admin.addvc"))
				{
				String website=plugin.getConfig().getString("portal-location");
				String VCName=plugin.getConfig().getString("currency-name");
				try {
					String username="'"+args[1]+"'";
					int currentTokens = 0;
					ResultSet result1=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
					if(result1.next())
					{
						String tokens=result1.getString(1);
						currentTokens=Integer.parseInt(tokens);
					}
					else
					{
						sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("USERNOTFOUND")+website);
					}
					int tokensToAdd=Integer.parseInt(args[2]);
					int tokensFinal=currentTokens+tokensToAdd;
					sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("ATTEMPTINGTOGIVE")+args[1]+" "+args[2]+" "+VCName);
					String tokens="'"+tokensFinal+"'";
					Database.executeUpdate("UPDATE dep SET tokens="+tokens+"where username="+username);
					
					ResultSet result=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
					if(result.next())
					{
						sender.sendMessage(prefix()+ChatColor.GREEN+Language.getPhrase("SUCCESS"));
						sender.sendMessage(prefix()+ChatColor.AQUA+args[1]+Language.getPhrase("NOWHAS")+result.getString(1)+" "+VCName);
					}
				} catch (SQLException e) {
					sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("DBERROR"));
					} catch (ArrayIndexOutOfBoundsException e)
				{
					sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help "+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX2"));
				} catch (NullPointerException e)
				{
					sender.sendMessage(prefix()+"Error. config.yml is invalid. Check the database configuration and try again.");
				}
				}
				else
				{
					
				}
				Database.close();
			}
			else if(args[0].equalsIgnoreCase("setvc"))
			{
				Database.connect();
				
				if(sender.hasPermission("donexpress.admin.setvc"))
				{
					String website=plugin.getConfig().getString("portal-location");
					String VCName=plugin.getConfig().getString("currency-name");
					try {
						String username="'"+args[1]+"'";
						ResultSet result1=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
						if(result1.next())
						{
							sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("ATTEMPTINGTOSET1")+args[1]+"'s "+VCName+Language.getPhrase("ATTEMPTINGTOSET2")+args[2]+" "+VCName);
							String tokens="'"+args[2]+"'";
							Database.executeUpdate("UPDATE dep SET tokens="+tokens+"where username="+username);
							
							ResultSet result=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
							if(result.next())
							{
								sender.sendMessage(prefix()+ChatColor.GREEN+Language.getPhrase("SUCCESS"));
								sender.sendMessage(prefix()+ChatColor.AQUA+args[1]+Language.getPhrase("NOWHAS")+result.getString(1)+" "+VCName);
							}
						}
						else
						{
							sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("USERNOTFOUND")+website);
						}
					} catch (SQLException e)
					{
						sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("DBERROR"));
						} catch (ArrayIndexOutOfBoundsException e)
					{
						sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help"+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX2"));
					}
				}
				Database.close();
			}
			
			else if(args[0].equalsIgnoreCase("removevc"))
			{
				Database.connect();
				
				if(sender.hasPermission("donexpress.admin.removevc"))
				{
					String website=plugin.getConfig().getString("portal-location");
					String VCName=plugin.getConfig().getString("currency-name");
					try {
						String username="'"+args[1]+"'";
						ResultSet result1=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
						if(result1.next())
						{
							int currentTokens=0;
							String tokens=result1.getString(1);
							currentTokens=Integer.parseInt(tokens);
							
							sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("REMOVEATTEMPTING")+args[2]+" "+VCName+Language.getPhrase("FROM")+args[1]+"'s account");
							
							int tokensToAdd=Integer.parseInt(args[2]);
							int tokensFinal1=currentTokens-tokensToAdd;
							if(tokensFinal1>=0)
							{
								String tokensFinal="'"+tokensFinal1+"'";
								
								Database.executeUpdate("UPDATE dep SET tokens="+tokensFinal+"where username="+username);
								
								ResultSet result=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
								if(result.next())
								{
									sender.sendMessage(prefix()+ChatColor.GREEN+Language.getPhrase("SUCCESS"));
									sender.sendMessage(prefix()+ChatColor.AQUA+args[1]+Language.getPhrase("NOWHAS")+result.getString(1)+" "+VCName);
								}
							}
							else
							{
								sender.sendMessage(prefix()+ChatColor.RED+"You're so silly. A user can not have a negative balance.");
								sender.sendMessage(prefix()+ChatColor.RED+"The user would have had "+tokensFinal1+" "+VCName+" if I didn't catch your mistake");
							}
						}
						else
						{
							sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("USERNOTFOUND")+website);
						}
					} catch (SQLException e)
					{
						sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("DBERROR"));
						} catch (ArrayIndexOutOfBoundsException e)
					{
						sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help"+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX2"));
					}
				}
				
				Database.close();
			}
			
			else if(args[0].equalsIgnoreCase("buy"))
			{
				Database.connect();
				
				String VCName=plugin.getConfig().getString("currency-name");
				if(sender.hasPermission("donexpress.user"))
				{
				if(sender instanceof Player==true)
				{
					if(!(args[1]==null))
					{
						boolean continuePurchase=true;
						File packages = new File(plugin.getDataFolder()+File.separator, "packages.yml");
						FileConfiguration packagesConfig=null;
						packagesConfig=new YamlConfiguration();
						packagesConfig.load(packages);
						
						File newFile = new File(plugin.getDataFolder()+"/packages"+File.separator ,args[1].toLowerCase()+".yml");				    	
						FileConfiguration newFileConfig=null;
			    		newFileConfig=new YamlConfiguration();
			    		try
			    		{
			    			newFileConfig.load(newFile);
			    			
			    		}catch(FileNotFoundException e)
			    		{
			    			//Do nothing as it is handled down below. 
			    		}
			    		
			    		File userDataFolder = new File(plugin.getDataFolder()+"/userdata"+File.separator);
			    		File userData = new File(plugin.getDataFolder()+"/userdata"+File.separator, sender.getName().toString().toLowerCase()+".yml");
			    		FileConfiguration userDataConfig = null;
			    		userDataConfig = new YamlConfiguration();
			    		
			    		if(!userDataFolder.exists())
			    		{
			    			userDataFolder.mkdir();
			    		}
			    		if(newFileConfig.getBoolean("one-time-purchase"))
			    		{
			    			if(userData.exists())
			    			{
			    				userDataConfig.load(userData);
			    				List<String>upgradeCheck = userDataConfig.getStringList("one-time-purchases");
			    				for(String s:upgradeCheck)
			    				{
			    					if(s.equalsIgnoreCase(args[1].toLowerCase()))
			    					{
			    						continuePurchase=false;
			    					}
			    				}
			    				
			    			}
			    			else
			    			{
			    				continuePurchase = true;
			    			}
			    		}
			    		else
			    		{
			    			continuePurchase=true;
			    		}
			    		if(continuePurchase)
			    		{
			    			List<String>rank = packagesConfig.getStringList("packages");
					        List<String>rankCopy=new ArrayList<String>(rank);
					        
					        String datRankName=args[1].toLowerCase();
							int count = 0;
						    if(rankCopy.contains(datRankName))
						    {
						        rankCopy.remove(datRankName);
						        count++;
						    }
						    if(count==1)
						    {
								String username="'"+sender.getName()+"'";
								try {										
									ResultSet result=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
									if(result.next())
									{
										String tokens=result.getString(1);
										int rankInt=Integer.parseInt(newFileConfig.getString("price"));//Gets the amount of tokens needed for the specific rank
										int tokensInt=Integer.parseInt(tokens);//Gets the amount of tokens that a user currently has
										
										if(!(rankInt>=tokensInt+1))
										{
											String rankSend=args[1];
											sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("HOWMUCH1")+rankInt+" "+VCName+Language.getPhrase("HOWMUCH2")+ChatColor.GREEN+"/donate confirm "+ChatColor.YELLOW+Language.getPhrase("HOWMUCH3")+ChatColor.GREEN+"/donate cancel");
								 
											userDataConfig.createSection("confirm.confirm");
											userDataConfig.createSection("confirm.packagePrice");
											userDataConfig.createSection("confirm.package");
											userDataConfig.createSection("confirm.tokensInt");
											
											userDataConfig.set("confirm.confirm", true);
											userDataConfig.set("confirm.packagePrice", rankSend);
											userDataConfig.set("confirm.package", rankSend);
											userDataConfig.set("confirm.tokensInt", tokensInt);
											/**
										    confirm.put(sender, true);
											rankIntMap.put("rankInt", rankInt);
											tokensIntMap.put("tokensInt", tokensInt);
											rankString.put(sender, rankSend);
											*/
										}
										else
										{
											sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("NOTENOUGH")+VCName);
										}
										
									}
									else
									{
										sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("NOACCOUNT1")+plugin.getConfig().getString("website")+Language.getPhrase("NOACCOUNT2"));
									}
								} catch (SQLException e) {
									sender.sendMessage(prefix()+ChatColor.YELLOW+"DBERROR");								
									e.printStackTrace();
								} catch (NullPointerException e)
								{
									sender.sendMessage(prefix()+"Hm. I can't connect to the database. Your server owner may have incorrectly setup the config. Please let him/her know right now!");
									e.printStackTrace();
								} catch (ArrayIndexOutOfBoundsException e)
								{
									sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help "+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX2"));
								}
							}
						    else
						    {
						    	 sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("PACKAGENOTFOUND"));
						    }
			    		}
			    		else
			    		{
			    			sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("PACKAGEALREADYPURCHASED"));
			    		}
										    	 
					}
				}
				else
				{
					sender.sendMessage(prefix()+ChatColor.DARK_RED+"Error. This command can only be preformed by a player");
				}
				}
				else
				{
					noPermission(sender);
				}
				Database.close();
			}
			else if(args[0].equalsIgnoreCase("upgrade"))
			{
				Database.connect();
				
				if(sender.hasPermission("donexpress.user"))
				{
					if(sender instanceof Player)
					{
						String VCName=plugin.getConfig().getString("currency-name");
						File packages = new File(plugin.getDataFolder()+File.separator, "packages.yml");
						FileConfiguration packagesConfig=null;
						packagesConfig=new YamlConfiguration();
						packagesConfig.load(packages);
			    		
			    		File userData = new File(plugin.getDataFolder()+"/userdata"+File.separator, sender.getName().toString()+".yml");
			    		FileConfiguration userDataConfig=null;
			    		userDataConfig=new YamlConfiguration();
			    		if(userData.exists())
			    		{
				    		userDataConfig.load(userData);
				    		String currentRank = userDataConfig.getString("current-package");
							
							File oldFile = new File(plugin.getDataFolder()+"/packages"+File.separator ,currentRank+".yml");				    	
							FileConfiguration oldFileConfig=null;
				    		oldFileConfig=new YamlConfiguration();
				    		oldFileConfig.load(oldFile);
				    		
				    		String nextRank = oldFileConfig.getString("next-package".toLowerCase());
				    		if(nextRank.equalsIgnoreCase("null"))
				    		{
				    			sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("PACKAGENOTPURCHASED"));
				    		}
				    		else
				    		{
				    			File newFile = new File(plugin.getDataFolder()+"/packages"+File.separator ,nextRank+".yml");				    	
								FileConfiguration newFileConfig=null;
					    		newFileConfig=new YamlConfiguration();
					    		newFileConfig.load(newFile);
					    		
					    		List<String>rank = packagesConfig.getStringList("packages");
						        List<String>rankCopy=new ArrayList<String>(rank);
						        
								int count = 0;
							    if(rankCopy.contains(nextRank))
							    {
							        rankCopy.remove(nextRank);
							        count++;
							    }
							    if(count==1)
							    {
									String username="'"+sender.getName()+"'";
									try {											
										ResultSet result=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
										if(result.next())
										{
											String tokens=result.getString(1);
											int rankInt=Integer.parseInt(oldFileConfig.getString("next-package-price"));//Gets the amount of tokens needed for the specific rank
											int tokensInt=Integer.parseInt(tokens);//Gets the amount of tokens that a user currently has
											
											if(!(rankInt>=tokensInt+1))
											{
												String rankSend=oldFileConfig.getString("next-package");
												sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("UPGRADETO1")+rankSend+Language.getPhrase("UPGRADETO2")+rankInt+" "+VCName+Language.getPhrase("HOWMUCH2")+ChatColor.GREEN+"/donate confirm "+ChatColor.YELLOW+Language.getPhrase("HOWMUCH3")+ChatColor.GREEN+"/donate cancel");
												
												userDataConfig.createSection("confirm.confirm");
												userDataConfig.createSection("confirm.packagePrice");
												userDataConfig.createSection("confirm.package");
												userDataConfig.createSection("confirm.tokensInt");
												
												userDataConfig.set("confirm.confirm", true);
												userDataConfig.set("confirm.packagePrice", rankSend);
												userDataConfig.set("confirm.package", rankSend);
												userDataConfig.set("confirm.tokensInt", tokensInt);
											}
											else
											{
												sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("NOTENOUGH")+VCName);
											}
											
										}
										else
										{
											sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("NOACCOUNT1")+plugin.getConfig().getString("website")+Language.getPhrase("NOACCOUNT2"));
										}
									} catch (SQLException e) {
										sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("DBERROR"));								
										e.printStackTrace();
									} catch (NullPointerException e)
									{
										sender.sendMessage(prefix()+"Hm. I can't connect to the database. Your server owner may have incorrectly setup the config, or the database is having problems. Please let him/her know right now!");
										e.printStackTrace();
									} catch (ArrayIndexOutOfBoundsException e)
									{
										sender.sendMessage(ChatColor.RED+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help "+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX2"));
									}
								}
							    else
							    {
							    	 sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("PACKAGENOTFOUND"));
							    }
				    		}
				    		
				    		
			    		}
			    		else
			    		{
			    			sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("PACKAGENOTPURCHASED"));
			    		}

					}
					else
					{
						sender.sendMessage(prefix()+ChatColor.RED+"Error. You need to be a player to do this command");
					}
				}
				else
				{
					noPermission(sender);
				}
				Database.close();
			}
			
			else if(args[0].equalsIgnoreCase("confirm"))
			{
				//TODO
				Database.connect();
				
				File userData = new File(plugin.getDataFolder()+"/userdata"+File.separator, sender.getName().toString()+".yml");
	    		FileConfiguration userDataConfig=null;
	    		userDataConfig=new YamlConfiguration();
				
				String VCName=plugin.getConfig().getString("currency-name");
				if(sender.hasPermission("donexpress.user"))
				{
				if(sender instanceof Player)
				{
				try {
					
					/**if(confirmUser.get(sender).equals(sender))
					{
						userDataConfig.set("confirm.confirm", true);
						userDataConfig.set("confirm.packagePrice", rankSend);
						userDataConfig.set("confirm.package", rankSend);
						userDataConfig.set("confirm.tokensInt", tokensInt);
					}*/
					
				userDataConfig.load(userData);
				
				if(userDataConfig.getBoolean("confirm.confirm"))
				{
					String rank=userDataConfig.getString("confirm.package");
					File newFile = new File(plugin.getDataFolder()+"/packages"+File.separator ,rank.toLowerCase()+".yml");
			    	FileConfiguration newFileConfig=null;
		    		newFileConfig=new YamlConfiguration();
		    		newFileConfig.load(newFile);
		    		
		    		File forum = new File(plugin.getDataFolder()+File.separator, "forumConfig.yml");
		    		FileConfiguration forumConfig=null;
		    		forumConfig=new YamlConfiguration();
		    		forumConfig.load(forum);

					List<String> rankCommand=newFileConfig.getStringList(("commands").replace("%player", sender.getName()));
					boolean sendMessage=false;
					
					Database.execute("CREATE TABLE IF NOT EXISTS `packages_purchased` (`id` int NOT NULL AUTO_INCREMENT, `username` varchar(24) NOT NULL, `tokens` varchar(16) NOT NULL, `rank` varchar(16) NOT NULL, `date` varchar(64) NOT NULL, PRIMARY KEY (id))");
					Database.execute("CREATE TABLE IF NOT EXISTS `expire_packages` (`id` int NOT NULL AUTO_INCREMENT, `username` varchar(24) NOT NULL, `package` varchar(50) NOT NULL, `date` varchar(64) NOT NULL, PRIMARY KEY (id))");
					
					int rankInt=0;
					rankInt=userDataConfig.getInt("confirm.tokensInt");
					
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
					Calendar cal = Calendar.getInstance();
					String date=dateFormat.format(cal.getTime());
					
					for(String s:rankCommand)
					{
						plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), s.replace("%player", sender.getName()));
												
						sendMessage=true;
					}
					Database.execute("INSERT INTO packages_purchased (username, tokens, rank, date) VALUES ('"+sender.getName()+"', '"+rankInt+"', '"+rank+"', '"+date+"')");
					
					if(newFileConfig.getBoolean("expire")||newFileConfig.getString("expire").equals("true"))
					{
						Database.execute("INSERT INTO expire_packages (username, package, date) VALUES ('"+sender.getName()+"', '"+rank+"', '"+date+"')");
					}
					
					if(sendMessage==true)
					{
						String username="'"+sender.getName()+"'";						
						int tokensInt=0;
						int rankInt2=0;
						tokensInt=userDataConfig.getInt("confirm.packagePrice");
						rankInt2=userDataConfig.getInt("confirm.packagePrice");
						String rankIntString=userDataConfig.getString("confirm.packagePrice");
						int finalTokens=tokensInt-rankInt2;
						Database.executeUpdate("UPDATE dep SET tokens='"+finalTokens+"' where username='"+sender.getName()+"'");
						
						ResultSet result=Database.executeStatement("SELECT `tokens`, `username` FROM dep  WHERE username = "+username);
						if(result.next())
						{
							sender.sendMessage(prefix()+ChatColor.AQUA+Language.getPhrase("NOWHAVE")+result.getString(1)+" "+VCName);
						}
						
						String donateMessage=plugin.getConfig().getString("donate-message");
						donateMessage=donateMessage.replace("%player", sender.getName());
						donateMessage=donateMessage.replace("%package", rank);
						donateMessage=donateMessage.replace("%currency", VCName);
						donateMessage=donateMessage.replace("%amount", rankIntString);
						Bukkit.broadcastMessage(colourize(donateMessage));
						
						if(forumConfig.getBoolean("enabled")||forumConfig.getString("enabled").equals("true"))
						{
							syncForum(sender, rank);
						}
						
						File userDataFolder = new File(plugin.getDataFolder()+"/userdata"+File.separator);
			    		userDataConfig = new YamlConfiguration();
			    		
			    		if(!userDataFolder.exists())
			    		{
			    			userDataFolder.mkdir();
			    		}
			    		if(newFileConfig.getBoolean("one-time-purchase"))
			    		{
			    			if(!userData.exists())
			    			{
			    				userData.createNewFile();
			    				userDataConfig.load(userData);
			    				
			    				userDataConfig.createSection("current-package");
			    				userDataConfig.createSection("one-time-purchases");
			    				
			    				List<String>oneTimePurchase = userDataConfig.getStringList("one-time-purchase");
			    				oneTimePurchase.add(rank);
			    				
			    				userDataConfig.set("current-package", rank);
			    				userDataConfig.set("one-time-purchases", oneTimePurchase);
			    				
			    				userDataConfig.save(userData);
			    			}
			    			else if(userData.exists())
			    			{
			    				userDataConfig.load(userData);
			    				List<String>oneTimePurchase = userDataConfig.getStringList("one-time-purchases");
			    				oneTimePurchase.add(rank);
			    				
			    				userDataConfig.set("current-package", rank);
			    				userDataConfig.set("one-time-purchases", oneTimePurchase);
			    				userDataConfig.set("confirm.confirm", false);
								userDataConfig.set("confirm.packagePrice", null);
								userDataConfig.set("confirm.package", null);
								userDataConfig.set("confirm.tokensInt", null);
			    				userDataConfig.save(userData);
			    			}
			    		}
					}
				}
				else
				{
					sender.sendMessage(prefix()+ChatColor.RED+"Error. You have not started the purchase of a package");		
				}
				
				}catch (SQLException e)
				{
					e.printStackTrace();
					sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("DBERROR"));
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e)
				{
					sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help "+ChatColor.RED+Language.getPhrase("INVALIDSYNXTAX2"));
				} catch (NullPointerException e)
				{
					e.printStackTrace();
					sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("PACKAGENOTPURCHASED"));
				}
				}
			}
				Database.close();
			}
			
			/**
			else if(args[0].equalsIgnoreCase("edit"))
			{
				String VCName=plugin.getConfig().getString("currency-name");
				if(sender.hasPermission("donexpress.admin.edit"))
				{
					try {
						File packages = new File(plugin.getDataFolder()+File.separator, "packages.yml");
						FileConfiguration packagesConfig=null;
						packagesConfig=new YamlConfiguration();
						packagesConfig.load(packages);
						
						File newFile = new File(plugin.getDataFolder()+"/packages"+File.separator ,args[1].toLowerCase()+".yml");			    	FileConfiguration newFileConfig=null;
			    		newFileConfig=new YamlConfiguration();
			    		newFileConfig.load(newFile);
			    		
					 sender.sendMessage(prefix()+ChatColor.YELLOW+"Attempting to replace "+args[1]+"'s amount of "+VCName+" with "+args[2]);
					 List<String>ranks = packagesConfig.getStringList("packages");			
					 List<String>ranks2=new ArrayList<String>(ranks);
					 
					 String datRankName=args[1].toLowerCase();
					 int count = 0;
				     if(ranks2.contains(datRankName))
				     {
				         ranks2.remove(datRankName);
				         count++;
				     }
				     if(count==1)
				     {
				    	 int price=Integer.parseInt(args[2]);
				    	 newFileConfig.set("price", price);
				    	 boolean allClear=true;
				    	 try{
				    		 
				    	 @SuppressWarnings("unused")
						 int checkForWords=Integer.parseInt(newFileConfig.getString("price"));
				    	 }catch(NumberFormatException e)
				    	 {
				    		allClear=false; 
				    	 }
				    	 if(allClear==true)
				    	 {
				    		 newFileConfig.save(newFile);
				    		 
							 sender.sendMessage(prefix()+ChatColor.GREEN+"Successful");
				    	 }
				    	 else
				    	 {
				    		 sender.sendMessage(prefix()+ChatColor.YELLOW+"You can't have decimals or letters as a price. You fool. You almost broke the plugin.");
				    	 }
						 
				     }
				     else
				     {
				    	 sender.sendMessage(prefix()+ChatColor.DARK_RED+"Error. Existing package could not be found");
				     }	
				}catch (ArrayIndexOutOfBoundsException e)
				{
					sender.sendMessage(prefix()+ChatColor.RED+"Error. Invalid syntax. Type "+ChatColor.GREEN+"/donate help "+ChatColor.RED+"for commands");
				}
				} 
				else
				{
					noPermission(sender);
				}
			}
			*/
			else if(args[0].equalsIgnoreCase("cancel"))
			{
				Database.connect();
				
				if(sender.hasPermission("donexpress.user"))
				{
					File userData = new File(plugin.getDataFolder()+"/userdata"+File.separator, sender.getName().toString()+".yml");
		    		FileConfiguration userDataConfig=null;
		    		userDataConfig=new YamlConfiguration();
		    		userDataConfig.load(userData);
		    		
					sender.sendMessage(ChatColor.RED+prefix()+ChatColor.YELLOW+Language.getPhrase("CANCEL1"));
					if(sender instanceof Player)
					{
						if(userDataConfig.getBoolean("confirm.confirm"))
						{
							userDataConfig.set("confirm.confirm", false);
							userDataConfig.set("confirm.packagePrice", null);
							userDataConfig.set("confirm.package", null);
							userDataConfig.set("confirm.tokensInt", null);
						}
						else
						{
							boolean no=true;
							if(confirmDelBoolean.get("confirm"))
							{
								confirmDel.clear();
							    confirmDel2.clear();
							    confirmDelBoolean.clear();
							    
							    sender.sendMessage(ChatColor.RED+prefix()+Language.getPhrase("CANCEL2"));
							    no=false;
							}
							else
							{
								sender.sendMessage(ChatColor.RED+prefix()+Language.getPhrase("CANCEL3"));
							}
							if(no)
							{
								sender.sendMessage(ChatColor.RED+prefix()+Language.getPhrase("CANCEL4"));
							}
						}
					}
					else
					{
						boolean no=true;
						try
						{
							
						if(!confirmDelBoolean.get("confirm"))
						{
							sender.sendMessage(ChatColor.RED+prefix()+Language.getPhrase("CANCEL5"));
							no=false;
						}
						else
						{
							confirmDel.clear();
						    confirmDel2.clear();
						    confirmDelBoolean.clear();
						    
						    sender.sendMessage(ChatColor.RED+prefix()+Language.getPhrase("CANCEL6"));
						    no=false;
						}
						
						}catch(NullPointerException e)
						{
							e.printStackTrace();
							no=false;
							sender.sendMessage(ChatColor.RED+prefix()+Language.getPhrase("CANCEL7"));
						}

						if(no)
						{
							sender.sendMessage(ChatColor.RED+prefix()+Language.getPhrase("CANCEL8"));
						}
						
					}
				}
				else
				{
					noPermission(sender);
				}
				Database.close();
			}
			else if(args[0].equalsIgnoreCase("recent"))
			{
				Database.connect();
				
				if(sender.hasPermission("donexpress.admin.recent"))
				{
					String VCName=plugin.getConfig().getString("currency-name");
					try {
						ResultSet result=Database.executeStatement("SELECT * FROM packages_purchased ORDER BY `id` DESC LIMIT 5");
						sender.sendMessage(prefix()+ChatColor.YELLOW+"Last 5 transactions");
						while(result.next())
						{
							sender.sendMessage(ChatColor.GOLD+"*********************************");
							sender.sendMessage(ChatColor.GOLD+"ID: "+result.getString(1));
							sender.sendMessage(ChatColor.GOLD+"Email: "+result.getString(2));
							sender.sendMessage(ChatColor.GOLD+VCName+"'s purchased: "+result.getString(3));
							sender.sendMessage(ChatColor.GOLD+"Amount paid: "+result.getString(4));
							sender.sendMessage(ChatColor.GOLD+"Name: "+result.getString(5)+" "+result.getString(6));
							sender.sendMessage(ChatColor.GOLD+"Email: "+result.getString(7));
							sender.sendMessage(ChatColor.GOLD+"Date: "+result.getString(8));
							sender.sendMessage(ChatColor.GOLD+"*********************************");
							sender.sendMessage(ChatColor.GOLD+"");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (ArrayIndexOutOfBoundsException e)
					{
						sender.sendMessage(prefix()+ChatColor.RED+Language.getPhrase("INVALIDSYNTAX1")+ChatColor.GREEN+"/donate help"+Language.getPhrase("INVALIDSYNTAX2"));
					}
				}
				Database.close();
			}
			else if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.hasPermission("donexpress.admin.reload"))
				{
				plugin.reloadConfig();
				sender.sendMessage(prefix()+ChatColor.GREEN+"Reload successful!");
				}
				else
				{
					noPermission(sender);
				}
			}
			else if(args[0].equalsIgnoreCase("about"))
			{
				sender.sendMessage(ChatColor.YELLOW+"***************************************");
				sender.sendMessage(ChatColor.RED+"");
				sender.sendMessage(ChatColor.AQUA+"DonatorExpress Version 1.6");
				sender.sendMessage(ChatColor.AQUA+"Plugin developed by: aman207");
				sender.sendMessage(ChatColor.AQUA+"Webportal developed by: AzroWear");
				sender.sendMessage(ChatColor.AQUA+"http://bit.ly/DonExp");
				sender.sendMessage(ChatColor.RED+"");
				sender.sendMessage(ChatColor.YELLOW+"***************************************");
			}
			else if(args[0].equalsIgnoreCase("help"))
			{
				commandUsage(sender);
			}
			

			else
			{
				commandUsage(sender);
				return true;
			}
		}catch(ArrayIndexOutOfBoundsException e)
		{
			commandUsage(sender);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		}
		return true;
	}
	public void syncForum(CommandSender sender, String group)
	{
		Database.connect();
		
		File forumGroup = new File(plugin.getDataFolder()+"/packages"+File.separator, group.toLowerCase()+".yml");
		File forumConfig = new File(plugin.getDataFolder()+File.separator, "forumConfig.yml");
		
		YamlConfiguration forumGroupYaml = null;
		forumGroupYaml=new YamlConfiguration();
		YamlConfiguration forumConfigYaml = null;
		forumConfigYaml=new YamlConfiguration();
		
		try {
				Statement statement=null;
				Connection forumdb;
				forumConfigYaml.load(forumConfig);
				forumGroupYaml.load(forumGroup);
			
				String dbUsername = forumConfigYaml.getString("db-username");
				String dbPassword = forumConfigYaml.getString("db-password");
				String dbHost = forumConfigYaml.getString("db-host");
				String dbName = forumConfigYaml.getString("db-name");
				String dbURL = "jdbc:mysql://" + dbHost + "/" + dbName;
				forumdb = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
				
				statement=forumdb.createStatement();				
				if(forumConfigYaml.getString("mybb").equals("true")||forumConfigYaml.getBoolean("mybb"))
				{
					String groupName=forumGroupYaml.getString("forum-group");
					
					String prefix=forumConfigYaml.getString("db-prefix");
					String username=sender.getName().toString();
					if(forumConfigYaml.getString("username-mode").equals("true")||forumConfigYaml.getBoolean("username-mode"))
					{
						statement.executeUpdate("UPDATE "+prefix+"users SET usergroup='"+groupName+"' WHERE username='"+username+"'");
					}
					else if(forumConfigYaml.getString("email-mode").equals("true")||forumConfigYaml.getBoolean("email-mode"))
					{
						ResultSet result=Database.executeStatement("SELECT `email` FROM dep  WHERE username = '"+username+"'");
						String email=null;
						while(result.next())
						{
							email=result.getString(1);
						}
						
						statement.executeUpdate("UPDATE "+prefix+"users SET usergroup='"+groupName+"' WHERE email='"+email+"'");
					}
				}
				else if(forumConfigYaml.getString("xenforo").equals("true")||forumConfigYaml.getBoolean("xenforo"))
				{
					String groupName=forumGroupYaml.getString("forum-group");
					String prefix=forumConfigYaml.getString("db-prefix");
					String username=sender.getName().toString();
					if(forumConfigYaml.getString("username-mode").equals("true")||forumConfigYaml.getBoolean("username-mode"))
					{
						statement.executeUpdate("UPDATE "+prefix+"user SET user_group_id='"+groupName+"' WHERE username='"+username+"'");
					}
					else if(forumConfigYaml.getString("email-mode").equals("true")||forumConfigYaml.getBoolean("email-mode"))
					{
						ResultSet result=Database.executeStatement("SELECT `email` FROM dep  WHERE username = '"+username+"'");
						String email=null;
						while(result.next())
						{
							email=result.getString(1);
						}
						
						statement.executeUpdate("UPDATE "+prefix+"user SET user_group_id='"+groupName+"' WHERE email='"+email+"'");
					}
				}
				else if(forumConfigYaml.getString("ipboard").equals("true")||forumConfigYaml.getBoolean("ipboard"))
				{
					String groupName=forumGroupYaml.getString("forum-group");
					String prefix=forumConfigYaml.getString("db-prefix");
					String username=sender.getName().toString();
					if(forumConfigYaml.getString("username-mode").equals("true")||forumConfigYaml.getBoolean("username-mode"))
					{
						statement.executeUpdate("UPDATE "+prefix+"members SET member_group_id='"+groupName+"' WHERE name='"+username+"'");
					}
					else if(forumConfigYaml.getString("email-mode").equals("true")||forumConfigYaml.getBoolean("email-mode"))
					{
						ResultSet result=Database.executeStatement("SELECT `email` FROM dep  WHERE username = '"+username+"'");
						String email=null;
						while(result.next())
						{
							email=result.getString(1);
						}
						
						statement.executeUpdate("UPDATE "+prefix+"members SET member_group_id='"+groupName+"' WHERE email='"+email+"'");
					}
				}
				/**
				//TODO
				else if(forumConfigYaml.getString("phpbb").equals("true"))
				{
					String groupName=forumGroupYaml.getString(group+"-group");
					String prefix=forumConfigYaml.getString("db-prefix");
					String username=sender.toString();
					if(forumConfigYaml.getString("username-mode").equals("true"))
					{
						statement.executeUpdate("UPDATE "+prefix+"users SET group_id='"+groupName+"' WHERE usernamename='"+username+"'");
					}
					else if(forumConfigYaml.getString("email-mode").equals("true"))
					{
						Statement forumStatement=null;
						forumStatement=con.createStatement();
						ResultSet result=forumStatement.executeQuery("SELECT `email` FROM dep  WHERE username = '"+username+"'");
						String email=null;
						while(result.next())
						{
							email=result.getString(1);
						}
						
						statement.executeUpdate("UPDATE "+prefix+"users SET group_id='"+groupName+"' WHERE user_email='"+email+"'");
					}
				}
				*/
				else if(forumConfigYaml.getString("simplemachines").equals("true")||forumConfigYaml.getBoolean("simplemachines"))
				{
					String groupName=forumGroupYaml.getString("forum-group");
					String prefix=forumConfigYaml.getString("db-prefix");
					String username=sender.getName().toString();
					if(forumConfigYaml.getString("username-mode").equals("true")||forumConfigYaml.getBoolean("username-mode"))
					{
						statement.executeUpdate("UPDATE "+prefix+"members SET id_group='"+groupName+"' WHERE member_name='"+username+"'");
					}
					else if(forumConfigYaml.getString("email-mode").equals("true")||forumConfigYaml.getBoolean("email-mode"))
					{
						ResultSet result=Database.executeStatement("SELECT `email` FROM dep  WHERE username = '"+username+"'");
						String email=null;
						while(result.next())
						{
							email=result.getString(1);
						}
						
						statement.executeUpdate("UPDATE "+prefix+"members SET id_group='"+groupName+"' WHERE email_address='"+email+"'");
					}
				}
				else if(forumConfigYaml.getString("vbulletin").equals("true")||forumConfigYaml.getBoolean("vbulletin"))
				{
					String groupName=forumGroupYaml.getString("forum-group");
					String prefix=forumConfigYaml.getString("db-prefix");
					String username=sender.getName().toString();
					if(forumConfigYaml.getString("username-mode").equals("true")||forumConfigYaml.getBoolean("username-mode"))
					{
						statement.executeUpdate("UPDATE "+prefix+"user SET usergroupid='"+groupName+"' WHERE username='"+username+"'");
					}
					else if(forumConfigYaml.getString("email-mode").equals("true")||forumConfigYaml.getBoolean("email-mode"))
					{
						ResultSet result=Database.executeStatement("SELECT `email` FROM dep  WHERE username = '"+username+"'");
						String email=null;
						while(result.next())
						{
							email=result.getString(1);
						}
						
						statement.executeUpdate("UPDATE "+prefix+"user SET usergroupid='"+groupName+"' WHERE email='"+email+"'");
					}
				}
				else
				{
					
				}
				Database.close();
				forumdb.close();
		} catch (SQLException e) {
			sender.sendMessage(prefix()+ChatColor.RED+"Uh oh. I could not add you to a forum group. " +
					"This could be because the server owner has improperly configured DonatorExpress or because you have not signed up on the forums");
			sender.sendMessage(ChatColor.RED+"Please contact the server owner!");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		
	}

	public String colourize(String message) {
		return message.replaceAll("&([l-o0-9a-f])", "\u00A7$1");
	}
	
	public String prefix()
	{
		String prefix=plugin.getConfig().getString("prefix");
		return colourize(prefix);
	}

	public void commandUsage(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "Correct command usage");
		sender.sendMessage(ChatColor.GOLD + "/donate buy [package]");
		sender.sendMessage(ChatColor.GOLD + "/donate confirm");
		sender.sendMessage(ChatColor.GOLD + "/donate cancel");
		sender.sendMessage(ChatColor.GOLD + "/donate check");
		sender.sendMessage(ChatColor.GOLD + "/donate list");
		sender.sendMessage(ChatColor.GOLD + "/donate info [package]");
		sender.sendMessage(ChatColor.GOLD + "/donate upgrade");
		if (sender.hasPermission("donexpress.admin.checkvc")) {
			sender.sendMessage(ChatColor.GOLD + "/donate checkvc [username]");
		}
		if (sender.hasPermission("donexpress.admin.add")) {
			sender.sendMessage(ChatColor.GOLD + "/donate add [package]");
		}
		if (sender.hasPermission("donexpress.admin.delete")) {
			sender.sendMessage(ChatColor.GOLD + "/donate delete [package]");
			sender.sendMessage(ChatColor.GOLD + "/donate confirmdel");
		}
		if (sender.hasPermission("donexpress.admin.check["))
		{
			sender.sendMessage(ChatColor.GOLD + "/donate checkp [player]");
		}		if (sender.hasPermission("donexpress.admin.addvc")) {
			sender.sendMessage(ChatColor.GOLD
					+ "/donate addvc [username] [amount]");
		}
		if (sender.hasPermission("donexpress.admin.setvc")) {
			sender.sendMessage(ChatColor.GOLD
					+ "/donate setvc [username] [amount]");
		}
		if (sender.hasPermission("donexpress.admin.reload")) {
			sender.sendMessage(ChatColor.GOLD + "/donate reload");
		}
	}

	public void noPermission(CommandSender sender) {
		sender.sendMessage(prefix()+ChatColor.YELLOW+Language.getPhrase("NOPERMISSION"));
	}
	
	@EventHandler
	public void onPlayerLogoutEvent(PlayerQuitEvent e)
	{		
		File userData = new File(plugin.getDataFolder()+"/userdata"+File.separator, e.getPlayer().getName().toString()+".yml");
		FileConfiguration userDataConfig=null;
		userDataConfig=new YamlConfiguration();
		try {
			userDataConfig.load(userData);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		
		userDataConfig.set("confirm.confirm", false);
		userDataConfig.set("confirm.packagePrice", null);
		userDataConfig.set("confirm.package", null);
		userDataConfig.set("confirm.tokensInt", null);
		
		try {
			userDataConfig.save(userData);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
        confirmDel.clear();
        confirmDel2.clear();
        confirmDelBoolean.clear();
	}
}