package se.space.spaceships;

import java.util.LinkedList;

import org.newdawn.slick.Image;

import se.space.GameObject;
import se.space.Team;
import se.space.World;

public class StandardShip extends se.space.GameObject {
	public StandardShip(World tempWorld, int x, int y, Image img,
			int tempSpeed, Team tempTeam, String tempType) {
		super(tempWorld, x, y, img, tempSpeed, tempTeam, tempType);
	}
	public StandardShip(int x, int y, Image img){
		super(x,y,img);
	}
}
