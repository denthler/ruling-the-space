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
	private double defSpeed = 0;
	private static int defPrice = 2000;
	private HashMap<String,Rectangle> buttons;
	public Spacestation(World tempWorld, int x, int y, String imgPath,String imgIconPath,
			int tempSpeed, Team tempTeam, String tempType) {
		super(tempWorld, x, y, imgPath, imgIconPath, tempSpeed, tempTeam, tempType);
		setDefaultValues();
	}
	public Spacestation(int x, int y, String imgPath,String imgIconPath){
		super(x,y,imgPath, imgIconPath);
		setDefaultValues();
	}
	private void setDefaultValues(){
		super.setDamage(defDamage);
		super.setHealth(defHealth);
		super.setSpeed(defSpeed);
		super.setPrice(defPrice);
		super.setBuilding(true);
		canBuild = true; // Can build objects
		setType("spacestation");
	}
	public void addButtons(float screenWidth,float screenHeight){
		buttons = new HashMap<String,Rectangle>();
		buttons.put("ship", new Rectangle((float) (screenWidth-280), screenHeight-160, 50, 50));
		buttons.put("destroyer", new Rectangle((float) (screenWidth-200), screenHeight-160, 50, 50));
		buttons.put("healer", new Rectangle((float) (screenWidth-120), screenHeight-160, 50, 50));
		buttons.put("builder", new Rectangle((float) (screenWidth-280), screenHeight-80, 50, 50));
		
	}
	public void drawInterface(Graphics g,float screenWidth,float screenHeight){
		// TODO Auto-generated method stub
		if(buttons==null)
			addButtons(screenWidth,screenHeight);
		
		for(String s:buttons.keySet()){
			if(world.getGame().getMyTeam().getGold()<world.getGame().objectList.get(s).getPrice()){
				g.setColor(Color.red);
			}
			else
				g.setColor(Color.green);
			Rectangle rct = buttons.get(s);
			g.fill(rct);
			g.drawImage(Game.objectList.get(s).getIcon(), rct.getX()+5, rct.getY()+5);
			g.setColor(Color.yellow);
			g.drawString(""+world.getGame().objectList.get(s).getPrice(), rct.getX()+10, rct.getMaxY()+5);
		}
	}
	
	public void checkButtonPressed(int x,int y,Timer t){
		for(String s:buttons.keySet()){
			Rectangle rct = buttons.get(s);
			if(rct.contains(x, y)){
				if(t.isDone()){
					GameObject tempObj;
					tempObj = GameObject.createObject(s, this.world, this.getX(), this.getY(), this.getTeam());
					tempObj.setCurSpeed(tempObj.getSpeed());
					tempObj.move((int)this.getMoveX(),(int)this.getMoveY());
					this.build(tempObj, 10000);
					t.reset();
				}
			}
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