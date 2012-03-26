package se.space;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;


public class Tile{
	public static final int DIMENSIONS = 20;
	private boolean walkable;
	private String tileName;
	private Rectangle area;
	public Tile(String s) throws SlickException{
		setTileName(s);
		walkable=true;
	}
	public Tile(String s,int x, int y) throws SlickException{
		this(s);
		setArea(new Rectangle(x,y,Tile.DIMENSIONS,Tile.DIMENSIONS));
	}
	public boolean isWalkable(){
		return walkable;
	}
	public void setWalkable(boolean b){
		walkable = b;
	}
	public void setTileName(String tileName) {
		this.tileName = tileName;
	}
	public String getTileName() {
		return tileName;
	}
	public void setArea(Rectangle area) {
		this.area = area;
	}
	public Rectangle getArea() {
		return area;
	}
}
