package com.chen;

import java.io.Serializable;

public class DataBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String playeruuid;
	private String playername;
	private byte[] inventory;
	private byte[] armor;
	private String serverarea;
	private int playerlevel;
	private String playerjob;
	private byte[] savegoods;
	public byte[] getSavegoods() {
		return savegoods;
	}
	public void setSavegoods(byte[] savegoods) {
		this.savegoods = savegoods;
	}
	public String getPlayeruuid() {
		return playeruuid;
	}
	public void setPlayeruuid(String playeruuid) {
		this.playeruuid = playeruuid;
	}
	public String getPlayername() {
		return playername;
	}
	public void setPlayername(String playername) {
		this.playername = playername;
	}
	public byte[] getInventory() {
		return inventory;
	}
	public void setInventory(byte[] inventory) {
		this.inventory = inventory;
	}
	public byte[] getArmor() {
		return armor;
	}
	public void setArmor(byte[] armor) {
		this.armor = armor;
	}
	public String getServerarea() {
		return serverarea;
	}
	public void setServerarea(String serverarea) {
		this.serverarea = serverarea;
	}
	public int getPlayerlevel() {
		return playerlevel;
	}
	public void setPlayerlevel(int playerlevel) {
		this.playerlevel = playerlevel;
	}
	public String getPlayerjob() {
		return playerjob;
	}
	public void setPlayerjob(String playerjob) {
		this.playerjob = playerjob;
	}
	
}
