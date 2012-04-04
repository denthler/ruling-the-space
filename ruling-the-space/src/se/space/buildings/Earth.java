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
	}
	public void update() {
		Rectangle area = new Rectangle(this.getX()-range/2,this.getY()-range/2,range,range);

		List<GameObject> objList = world.getAllUnits(area);
		objList.remove(this);
		boolean capture = false;
		Team captureTeam = this.getTeam();
		for(GameObject obj:objList){
			if(area.contains(obj.getX(), obj.getY())){
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
		super.update();
	}
}