package se.ruling.space;

import java.io.Serializable;

import org.newdawn.slick.Color;

public class Team implements Serializable{
	private Color color;
	private double gold;
	public Team(Color c){
		setColor(c);
		setGold(200);
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	public void setGold(double gold) {
		this.gold = gold;
	}
	public double getGold() {
		return gold;
	}
}
