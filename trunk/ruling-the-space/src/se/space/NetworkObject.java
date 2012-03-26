package se.ruling.space;

import java.io.Serializable;

public class NetworkObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object o;
	private String s;
	private int p;
	
	NetworkObject(String string, Object object, int playerid){
		o = object;
		s = string;
		p = playerid;
	}
	
	public Object getObject(){
		return o;
	}
	
	public int getPlayerid(){
		return p;
	}
	
	public String getString(){
		return s;
	}
}
