package se.space;

import java.awt.Point;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class GameObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double angle=0;
	private double xPos, yPos;
	private double moveX,moveY,speed=0;
	private int exp=0;
	private int maxHealth=200,health=200,range=400,damage=1;
	private Image sprite;
	private World world;
	private Team team;
	private String type;
	private Timer buildTimer, captureTimer;
	private int level;
	private transient Queue<GameObject> buildQueue;
	Point fireAt;
	private boolean alive=true;

	public GameObject(int x, int y, Image img){
		setxPos(x);
		setyPos(y);
		setMoveX(x);
		setMoveY(y);
		setType("earth.png");
		setSprite(img);
		buildQueue= new LinkedList<GameObject>();
		setLevel(1);
	}
	public void checkLevelUp(){
		if(this.getExp()>100*this.getLevel()){
			
			this.setCurHealth(this.getHealth()+10);
			this.setMaxHealth(this.getMaxHealth()+10);
			this.setDamage(this.getDamage()+1);
			this.setExp(0);
			this.setLevel(this.getLevel() + 1);
		}
	}
	public void build(GameObject obj,int time){
		if(type.equals("spacestation.png")){
			if(this.team.getGold()>200){
				if(buildTimer==null)
					buildTimer = Timer.createTimer(time);
				this.team.setGold(this.team.getGold()-200);
				buildQueue.add(obj);
			}
		}
//		else if(type.equals("ship.png")){
//			if(this.getExp()>100*getLevel()){
//				this.setHealth(this.getMaxHealth()+50);
//				this.setDammage(dammage+1);
//				this.setExp(0);
//				this.setLevel(this.getLevel() + 1);
//			}
//		}
	}
	public GameObject(World tempWorld,int x, int y, Image img,int tempSpeed,Team tempTeam,String tempType){
		this(x, y, img);
		setType(tempType);
		setSpeed(tempSpeed);
		team=tempTeam;
		world=tempWorld;
	}
	public void setPos(int x, int y){
		setxPos(x);
		setyPos(y);
	}
	public int getX(){
		return (int)getxPos()-getSprite().getWidth()/2;
	}
	public int getY(){
		return (int)getyPos()-getSprite().getHeight()/2;
	}
	public void draw(Graphics g) {
		if(speed!=0)
			getSprite().setRotation((float) (360*angle/Math.PI/2)-90);
		for(int i=0;i<2;i++){
			getSprite().setColor(i, this.getTeam().getColor().getRed()/5, 
					this.getTeam().getColor().getGreen()/5, 
					this.getTeam().getColor().getBlue()/5, 10);
		}
		g.drawImage(getSprite(),(int)getX(),(int)getY());
		g.setColor(team.getColor());
		int randomOffset = (int) (Math.random()*getSprite().getWidth());
		if(fireAt!=null){
			g.drawLine(getX()+getSprite().getWidth()/2, getY()+getSprite().getWidth()/2, fireAt.x, fireAt.y);
		}
		g.setColor(Color.red);
		g.fillRect(getX(), getY(), sprite.getWidth(), 4);
		g.setColor(Color.green);
		g.fillRect(getX(), getY(), (sprite.getWidth()-(sprite.getWidth()*(getMaxHealth()-health)/getMaxHealth())), 4);
		g.setColor(Color.yellow);
		if(buildTimer!=null)
			g.fillRect(getX(), getY()+4, (sprite.getWidth()-(sprite.getWidth()*(10000-buildTimer.getTimeLeft())/10000)), 4);
		if(captureTimer!=null)
			g.fillRect(getX(), getY()+4, (sprite.getWidth()-(sprite.getWidth()*(20000-captureTimer.getTimeLeft())/20000)), 4);
		if(this.getType().equals("ship.png")){
			g.fillRect(getX(), getY()+4, (sprite.getWidth()-(sprite.getWidth()*(100*level-exp)/(100*level))), 4);
		}
		if(!this.buildQueue.isEmpty()){
			g.drawString(""+this.buildQueue.size(), getX(), getY()+20);
		}
		g.setColor(Color.yellow);
		if(this.type.equals("ship.png"))
			g.fillRect((float)xPos-sprite.getWidth()/2, (float)yPos+sprite.getHeight()/2-level, 5, -level);
	}
	public void update() {
		Rectangle area = new Rectangle(this.getX()-range/2,this.getY()-range/2,range,range);
		if(this.getType().equals("earth.png")){
			List<GameObject> objList = world.getAllUnits(area);
			objList.remove(this);
			boolean capture = false;
			Team captureTeam = this.getTeam();
			for(GameObject obj:objList){
				if(area.contains(obj.getX(), obj.getY())){
					if(!obj.getType().equals("earth.png")){
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
		}
		if(buildTimer!=null&&buildTimer.isDone()){
			World.addObject(buildQueue.remove());
			buildTimer.reset();
			if(buildQueue.isEmpty()){
				buildTimer=null;
			}
		}

		/*NetworkClient n;
		try {
			n = new NetworkClient(new Socket("localhost", portNr), "UPDATE");
			n.getResponse();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		if(getMoveX()!=getxPos()||getMoveY()!=getyPos()){
			if(Math.sqrt(Math.abs(Math.pow(getMoveX()-getxPos(), 2)+Math.pow(getMoveY()-getyPos(), 2)))>5){
				angle=Math.atan((getyPos()-getMoveY())/(getxPos()-getMoveX()));
				if(((getyPos()-getMoveY())<0&&(getxPos()-getMoveX())>0)){
					angle+=Math.PI;
				}
				if((getyPos()-getMoveY())>0&&(getxPos()-getMoveX())>0){
					angle+=Math.PI;
				}
				setxPos(getxPos()+Math.cos(angle)*getSpeed());
				setyPos(getyPos()+Math.sin(angle)*getSpeed());
			}
		}
		fireAt=null;
		//		if(world.getGameObjects()!=null)
		if(!getType().equals("earth.png"))
			for(GameObject obj:world.getGameObjects()){
				if(area.contains(obj.getX(), obj.getY())&&this.getTeam()!=obj.getTeam()){
					if(!obj.getType().equals("earth.png")){
						obj.dammage(damage);
						setExp(getExp() + 1);
						int randomOffset = (int) (Math.random()*obj.getSprite().getWidth());
						int randomOffset2 = (int) (Math.random()*obj.getSprite().getWidth());
						fireAt= new Point(obj.getX()+randomOffset,obj.getY()+randomOffset2);
						break;
					}
				}

			}
		if(getHealth()<=0){
			alive=false;
		}

	}
	public void dammage(int dmg){
		setExp(getExp() + 1);
		int random=(int) (Math.random()*100);
		if(random>20){
			health-=dmg;
		}
		else{
			setExp(getExp() + 1);
		}
	}
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean b){
		alive = b;
	}
	public void destroy() {
		if(world.getGame().getSelectedObjects()!=null)
			world.getGame().getSelectedObjects().remove(this);
		// TODO Auto-generated method stub

	}
	public void move(int mouseWorldX, int mouseWorldY) {
		setMoveX(mouseWorldX);
		setMoveY(mouseWorldY);

	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getSpeed() {
		return speed;
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	public Team getTeam() {
		return team;
	}
	public void setCurHealth(int health){
		this.health = health;
	}
	public void setHealth(int health) {
		this.setMaxHealth(health);
		this.health = health;
	}
	public int getHealth() {
		return health;
	}
	public void setSprite(Image sprite) {
		this.sprite = sprite;
	}
	public Image getSprite() {
		return sprite;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setExp(int exp) {
		if(this.exp<=100*level||exp==0)
		this.exp = exp;
	}
	public int getExp() {
		return exp;
	}
	public void setDamage(int damage) {
		this.damage = damage;
	}
	public int getDamage() {
		return damage;
	}
	public void setMoveX(double moveX) {
		this.moveX = moveX;
		if(this.buildQueue!=null)
			for(GameObject obj:this.buildQueue){
				obj.setMoveX(moveX);
			}
	}
	public double getMoveX() {
		return moveX;
	}
	public void setMoveY(double moveY) {
		this.moveY = moveY;
		if(this.buildQueue!=null)
			for(GameObject obj:this.buildQueue){
				obj.setMoveY(moveY);
			}
	}
	public double getMoveY() {
		return moveY;
	}
	public void setxPos(double xPos) {
		this.xPos = xPos;
	}
	public double getxPos() {
		return xPos;
	}
	public void setyPos(double yPos) {
		this.yPos = yPos;
	}
	public double getyPos() {
		return yPos;
	}

	public List<Object> getNetGameObject(){
		/*private double angle=0;
		private double xPos, yPos;
		private double moveX,moveY,speed=0;
		private int exp=0;
		private int maxHealth=200,health=200,range=400,dammage=1;
		private Image sprite;
		private World world;
		private Team team;
		private String type;
		private Timer buildTimer;
		private transient Queue<GameObject> buildQueue;
		Point fireAt;
		private boolean alive=true;*/

		List<Object> net = new	ArrayList<Object>();
		//String[] s;
		net.add(angle);
		net.add(xPos);
		net.add(yPos);
		net.add(moveX);
		net.add(moveY);
		net.add(speed);
		net.add(exp);
		net.add(getMaxHealth());
		net.add(health);
		net.add(range);
		net.add(damage);
		//net.add(sprite);
		//net.add(world);
		net.add(team);
		net.add(type);
		//net.add(buildTimer);
		//net.add(buildQueue);
		net.add(fireAt);
		net.add(alive);

		return net;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getLevel() {
		return level;
	}
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	public int getMaxHealth() {
		return maxHealth;
	}
	public void setAngle(int a){
		angle = a;
	}

}
