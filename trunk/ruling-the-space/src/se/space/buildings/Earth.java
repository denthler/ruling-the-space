package se.space.buildings;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

import se.space.GameObject;
import se.space.Team;
import se.space.Timer;
import se.space.World;

public class Earth extends se.space.GameObject {
	private static int defDamage = 0;
	private static int defHealth = 100;
	private double defSpeed = 0;
	public Rectangle area;
	public Earth(World tempWorld, int x, int y, String imgPath, String imgIconPath,
			int tempSpeed, Team tempTeam, String tempType) {
		super(tempWorld, x, y, imgPath, imgIconPath, tempSpeed, tempTeam, tempType);
		setDefaultValues();
	}
	public Earth(int x, int y, String imgPath, String imgIconPath){
		super(x,y,imgPath,imgIconPath);
		setDefaultValues();
	}
	private void setDefaultValues(){
		super.setDamage(defDamage);
		super.setHealth(defHealth);
		super.setSpeed(defSpeed);
		super.setBuilding(true);
		setType("earth");
		//this.spriteRect.grow(range, range);
		area = new Rectangle((int)xPos-sprite.getWidth()/2,(int)yPos-sprite.getHeight()/2,sprite.getWidth(),sprite.getHeight());
		area.grow(range/4, range/4);
	}
	public void update() {
		//Rectangle area = new Rectangle(this.sprite.get-(this.sprite.getWidth()+range)/2,this.getY()-(this.sprite.getHeight()+range)/2,this.sprite.getWidth()+range,this.sprite.getHeight()+range);
		area.setBounds((int)xPos-sprite.getWidth()/2,(int)yPos-sprite.getHeight()/2,sprite.getWidth(),sprite.getHeight());
		area.grow(range/4, range/4);
		
		//area.setY((float) yPos);
		
		List<GameObject> objList = world.getAllUnits(area);
		objList.remove(this);
		boolean capture = false;
		Team captureTeam = this.getTeam();
		for(GameObject obj:objList){
			if(area.intersects(obj.getSpriteRectangle())){//area.contains(obj.getX(), obj.getY())){
				if(!obj.getType().equals("earth")){
					if(!obj.getTeam().equals(this.getTeam())){
						capture=true;
						captureTeam=obj.getTeam();
					}
					else{
						capture=false;
						break;
					}
				}
			}
		}
		if(capture){
			if(captureTimer==null){
				captureTimer = Timer.createTimer(20000);
			}
			if(captureTimer.isDone()){
				captureTimer=null;
				this.setTeam(captureTeam);
			}
		}
		else{
			captureTimer=null;
		}
		this.team.setGold(this.team.getGold()+0.1);
		//super.update();
	}
}