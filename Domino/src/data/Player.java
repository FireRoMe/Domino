package data;

import java.util.ArrayList;

public class Player
{
	private ArrayList<Stone> hand = new ArrayList<Stone>();
	private String name;
	private boolean firstMove = true;
	private boolean dropppedStone = false;
	private int points;
	
	public Player(String name)
	{
		if (name != null && name != "")
			this.name = name;
	}
	
	public void addStone(Stone stone)
	{
		hand.add(stone);
	}
	
	public void deleteStone(Stone stone)
	{
		hand.remove(stone);
	}
	
	public ArrayList<Stone> getHand()
	{
		return hand;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void increasePoints(int points)
	{
		this.points += points;
	}
	
	public int getPoints()
	{
		return points;
	}
	
	public Stone placeStone(Stone s)
	{
		return s;
	}
	
	public void setfirstMove(boolean isFirstMove)
	{
		this.firstMove = isFirstMove;
	}
	
	public boolean isFirstMove()
	{
		return firstMove;
	}

	public boolean isDropppedStone()
	{
		return dropppedStone;
	}

	public void setDropppedStone(boolean dropppedStone)
	{
		this.dropppedStone = dropppedStone;
	}
}
