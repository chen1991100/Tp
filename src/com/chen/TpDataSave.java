package com.chen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;



public class TpDataSave extends JavaPlugin implements Listener {
	private String mysql_ip;
	private String mysql_dbname;
	private String mysql_dbuser;
	private String mysql_dbpass;
	private String serverarea;
	private String teleport;
	public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
	public void onLoad() {
		saveDefaultConfig();
		mysql_ip = getConfig().getString("mysql.ip");
		mysql_dbname = getConfig().getString("mysql.dbname");
		mysql_dbuser = getConfig().getString("mysql.dbuser");
		mysql_dbpass = getConfig().getString("mysql.dbpass");
		serverarea  = getConfig().getString("config.serverarea");
		teleport = getConfig().getString("config.teleport");
	}
	public void onEnable() {
		Connection conn = getNewConnection();
		try {			
			Statement statement = conn.createStatement();
			statement.executeUpdate(" CREATE TABLE IF NOT EXISTS tpdatasave (playeruuid varchar(64) NOT NULL,playername varchar(64),inventory blob, armor blob,serverarea varchar(64),playerlevel int,playerjob varchar(64),savegoods blob,alldeadnumber int,monthdeadnumber int,allkillnumber int,monthkillnumber int,monthkillnumber int,alldamage bigint,monthdamage bigint,PRIMARY KEY (playeruuid)) ");
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("§c连接数据库失败！");
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		getServer().getPluginManager().registerEvents(this, this);
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("tpdatasave")) {
			teleportmethod((Player)sender);
		}
		return true;
	}	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) { 
		Bukkit.getConsoleSender().sendMessage("§c玩家进入，开始恢复战利品。。。");
		Player player = e.getPlayer();
		fixedThreadPool.execute(new BukkitRunnable() {			
			@Override
			public void run() {
				DataBean dataBean = getDataByUuid(player);
				if(dataBean != null && dataBean.getSavegoods() != null) {					
					try {
						boolean deleteresult = deleteSaveGoodsByUuid(player);
						ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBean.getSavegoods());
						BukkitObjectInputStream bulBukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
						ItemStack[] inv = (ItemStack[]) bulBukkitObjectInputStream.readObject();
						bulBukkitObjectInputStream.close();						
						if(inv != null) {
							for(ItemStack item : inv) {
								player.getInventory().addItem(item);
							}
							player.sendMessage("§c战利品已经自动发放到背包。。。");
						}
						Bukkit.getConsoleSender().sendMessage("§c战利品数据恢复成功。。。");	
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	protected boolean deleteSaveGoodsByUuid(Player player) {
		Connection connection = getNewConnection();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement("UPDATE "+mysql_dbname+" set savegoods = ? where playeruuid =?");
			preparedStatement.setBytes(1, null);
			preparedStatement.setString(2, player.getUniqueId().toString());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	protected DataBean getDataByUuid(Player player) {
		DataBean bean = null;
		Connection connection = getNewConnection();
		PreparedStatement preparedStatement;
		ResultSet rs = null;
		try {
			preparedStatement = connection.prepareStatement("select * from "+mysql_dbname+" where playeruuid =?");
			preparedStatement.setString(1, player.getUniqueId().toString());
			rs = preparedStatement.executeQuery();
			if(rs.next()) {
				bean = new DataBean();
				bean.setPlayeruuid(rs.getString("playeruuid"));
				bean.setPlayername(rs.getString("playername"));
				bean.setInventory(rs.getBytes("inventory"));
				bean.setArmor(rs.getBytes("armor"));
				bean.setServerarea(rs.getString("serverarea"));
				bean.setPlayerlevel(rs.getInt("playerlevel"));
				bean.setPlayerjob(rs.getString("playerjob"));
				bean.setSavegoods(rs.getBytes("savegoods"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return bean;
	}
	private void teleportmethod(Player player) {
		player.sendMessage("§c服务器开始保存玩家数据，请耐心等待。。。！");
		player.sendMessage("§c服务器开始保存玩家数据，请耐心等待。。。！");
		player.sendMessage("§c服务器开始保存玩家数据，请耐心等待。。。！");
		fixedThreadPool.execute(new BukkitRunnable() {			
			@Override
			public void run() {
				try {
					ItemStack[] inv = player.getInventory().getContents();
					ItemStack[] arm = player.getInventory().getArmorContents();
					ByteArrayOutputStream inv_stream = new ByteArrayOutputStream();
					ByteArrayOutputStream arm_stream = new ByteArrayOutputStream();
					BukkitObjectOutputStream invObjOS = new BukkitObjectOutputStream(inv_stream);
				    invObjOS.writeObject(inv);
				    invObjOS.close();
				    BukkitObjectOutputStream armObjOS = new BukkitObjectOutputStream(arm_stream);
					armObjOS.writeObject(arm);
					armObjOS.close();
					
					boolean savedata = false;
					String playerjob = "0";
					if(player.hasPermission("zhuangzhi.shenwang")) {
						playerjob = "2";
					}else if(player.hasPermission("serversigns.use.zhuangzhitwo")) {
						playerjob = "1";
					}
					if(isExist(player.getUniqueId().toString())) {
						savedata = updateInvToDB(player,inv_stream.toByteArray(),arm_stream.toByteArray(),playerjob);
					}else {
						savedata = insertInvToDB(player,inv_stream.toByteArray(),arm_stream.toByteArray(),playerjob);
					}
					
					if(savedata) {
						 player.sendMessage("§c服务器玩家数据保存成功，开始传送中。。。");
						 teleportToServer(player);
						
					}else {
						player.sendMessage("§c服务器玩家数据保存失败，请联系腐竹处理！");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	protected void teleportToServer(Player player) {
		 ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	     DataOutputStream out = new DataOutputStream(byteArray);
	     try {
			out.writeUTF("Connect");
			out.writeUTF(teleport);
			player.sendPluginMessage(TpDataSave.this, "BungeeCord", byteArray.toByteArray());
		 } catch (IOException e) {
			e.printStackTrace();
			player.sendMessage("§c传送失败，请稍后重试！");
			player.sendMessage("§c传送失败，请稍后重试！");
			player.sendMessage("§c传送失败，请稍后重试！");
		 }
	     
	     
	}
	protected boolean insertInvToDB(Player player, byte[] inv_stream, byte[] arm_stream, String playerjob) {
		Connection connection = getNewConnection();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement("INSERT into "+mysql_dbname+" (playeruuid,playername, inventory, armor, serverarea,playerlevel,playerjob) VALUES (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, player.getUniqueId().toString());
			preparedStatement.setString(2, player.getName());
			preparedStatement.setBytes(3, inv_stream);
			preparedStatement.setBytes(4, arm_stream);
			preparedStatement.setString(5, serverarea);
			preparedStatement.setInt(6, player.getLevel());
			preparedStatement.setString(7, playerjob);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	protected boolean updateInvToDB(Player player, byte[] inv_stream, byte[] arm_stream, String playerjob) {
		Connection connection = getNewConnection();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement("UPDATE "+mysql_dbname+" set inventory = ?,armor = ?,serverarea = ?,playerlevel = ?,playerjob = ? where playeruuid =?");
			preparedStatement.setBytes(1, inv_stream);
			preparedStatement.setBytes(2, arm_stream);
			preparedStatement.setString(3, serverarea);
			preparedStatement.setInt(4, player.getLevel());
			preparedStatement.setString(5, playerjob);
			preparedStatement.setString(6, player.getUniqueId().toString());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	private Connection getNewConnection() {
	    try {
	    	  Connection connection = DriverManager.getConnection("jdbc:mysql://" + mysql_ip + "/" + mysql_dbname, mysql_dbuser, mysql_dbpass);
	          return connection;
	    } catch (SQLException e) {
	          e.printStackTrace();
	          return null;
	    }
	}
	protected boolean isExist(String uuid) {
		Connection connection = getNewConnection();
		Statement statement;
		ResultSet rs = null;
		try {
			statement = connection.createStatement();
			rs = statement.executeQuery("select * from "+mysql_dbname+" where playeruuid = "+uuid);
			if(rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
}
