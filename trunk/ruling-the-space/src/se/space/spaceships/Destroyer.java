package se.space.spaceships;

import java.util.LinkedList;

import org.newdawn.slick.Image;

import se.space.GameObject;
import se.space.Team;
import se.space.World;

public class Destroyer extends se.space.spaceships.StandardShip {
	private int defDamage = 3;
	private int defHealth = 300;
	private int defPrice = 400;
	private double defSpeed = 1;
	public Destroyer(World tempWorld, int x, int y, String imgPath,String imgIconPath,
			int tempSpeed, Team tempTeam, String tempType) {
		super(tempWorld, x, y, imgPath,imgIconPath, tempSpeed, tempTeam, tempType);
		setDefaultValues();
	}
	public Destroyer(int x, int y, String imgPath,String imgIconPath){
		super(x,y,imgPath, imgIconPath);
		setDefaultValues();
	}
	protected void setDefaultValues(){
		setDamage(defDamage);
		setHealth(defHealth);
		setPrice(defPrice);
		setSpeed(defSpeed);
		setShip(true);
		setType("destroyer");
		//System.out.println(super.getDamage()+"-"+super.getHealth()+"-"+super.getPrice());
	}
}
