package se.space.buildings;

import java.util.HashMap;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

import se.space.Game;
import se.space.GameObject;
import se.space.Team;
import se.space.Timer;
import se.space.World;

public class Spacestation extends se.space.GameObject {
	private static int defDamage = 5;
	private static int defHealth = 2000;
	public Spacestation(World tempWorld, int x, int y, String imgPath,
			int tempSpeed, Team tempTeam, String tempType) {
		super(tempWorld, x, y, imgPath, tempSpeed, tempTeam, tempType);
		setDefaultValues();
	}
	public Spacestation(int x, int y, String imgPath){
		super(x,y,imgPath);
		setDefaultValues();
	}
	private void setDefaultValues(){
		super.setDamage(defDamage);
		super.setHealth(defHealth);
		super.setBuilding(true);
	}
	public void drawBuildInterface(Graphics g,HashMap<String,Rectangle> buttons){
		// TODO Auto-generated method stub
		
		for(String s:buttons.keySet()){
			if(world.getGame().getMyTeam().getGold()<world.getGame().objectList.get(s).getPrice()){
				g.setColor(Color.red);
			}
			else
				g.setColor(Color.green);
			Rectangle rct = buttons.get(s);
			g.fill(rct);
			g.drawImage(Game.objectList.get(s).getSprite(), rct.getX()+5, rct.getY()+5);
			g.setColor(Color.yellow);
			g.drawString(""+world.getGame().objectList.get(s).getPrice(), rct.getX()+10, rct.getMaxY()+5);
		}
	}
	
	public void build(GameObject obj,int time){
		if(this.team.getGold()>obj.getPrice()){
			if(this.buildTimer==null){
				this.buildTimer = Timer.createTimer(time);
			}
			this.team.setGold(this.team.getGold()-obj.getPrice());
			this.buildQueue.add(obj);
		}
	}
}