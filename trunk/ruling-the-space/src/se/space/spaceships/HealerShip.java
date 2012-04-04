package se.space.spaceships;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

import se.space.GameObject;
import se.space.Team;
import se.space.Timer;
import se.space.World;

public class HealerShip extends se.space.spaceships.StandardShip {
	private int defDamage = 1;
	private int defHealth = 200;
	private int defPrice = 300;
	private double defSpeed = 1.2;
	private int healingRate = 20;
	private Timer healTimer;
	private int healCount = 0;
	private int maxHealCount = 200;
	public HealerShip(World tempWorld, int x, int y, String imgPath,String imgIconPath,
			int tempSpeed, Team tempTeam, String tempType) {
		super(tempWorld, x, y, imgPath, imgIconPath, tempSpeed, tempTeam, tempType);
		setDefaultValues();
	}
	public HealerShip(int x, int y, String imgPath,String imgIconPath){
		super(x,y,imgPath,imgIconPath);
		setDefaultValues();
	}
	protected void setDefaultValues(){
		setDamage(defDamage);
		setHealth(defHealth);
		setPrice(defPrice);
		setSpeed(defSpeed);
		setShip(true);
		setType("healer");
		//healTimer = Timer.createTimer(200);
		//System.out.println(super.getDamage()+"-"+super.getHealth()+"-"+super.getPrice());
	}
	
	
	@Override
	public void update() {
		super.update();
		healCount++;
		if(healCount>=maxHealCount){
	//	if(healTimer.isDone()){
			Rectangle area = new Rectangle(this.getX()-range/2,this.getY()-range/2,range,range);
			
			for(GameObject go : world.getMyUnits(area)){
				if(go.isShip() && go.getMaxHealth()>go.getHealth()){
					go.setCurHealth(Math.min(go.getHealth()+healingRate,go.getMaxHealth()));
				}
			}
			healCount=0;
		}
//			healTimer.reset();
	}
	@Override
	public void checkLevelUp(){}
	public void setExp(int exp) {}
}
