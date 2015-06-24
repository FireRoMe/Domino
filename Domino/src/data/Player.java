package data;

import java.util.ArrayList;

/**
 * Der Spieler 
 */
public class Player
{
	/** Die Hand des Spielers */
	private ArrayList<Stone> hand = new ArrayList<Stone>();
	/** Der Name des Spielers */
	private String name;
	/** Erster Zug des Spielers (true/false) */
	private boolean firstMove = true;
	/** Wird benoetigt um festzustellen, ob ein Spieler 
	bereits einen Stein aufs Feld gelegt hat*/
	private boolean droppedStone = false;
	/** true, wenn der Spieler nicht legen kann */
	private boolean blocked = false;
	/** true, wenn der Spieler keine Steine mehr auf der Hand hat */
	private boolean noStones = false;
	/** Die Punkte des Spielers */
	private int points = 0;
	
	/** 
	 * Der Konstruktor mit Uebergabe des Namens
	 * @param name - Der Name des Spielers
	 */
	public Player(String name)
	{
		if (name != null && name != "")
			this.name = name;
	}
	
	/**
	 * Fuegt dem Spieler einen Stein zur Hand hinzu
	 * @param stone - Der Stein der hinzugefuegt wird
	 */
	public void addStone(Stone stone)
	{
		hand.add(stone);
	}
	
	/**
	 * Loescht einen Stein von der Hand
	 * @param stone - Der Stein der geloescht werden soll
	 */
	public void deleteStone(Stone stone)
	{
		hand.remove(stone);
		
		if (hand.isEmpty())
			noStones = true;
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

	public boolean isDroppedStone()
	{
		return droppedStone;
	}

	public void setDroppedStone(boolean droppedStone)
	{
		this.droppedStone = droppedStone;
	}

	public boolean isBlocked()
	{
		return blocked;
	}

	public void setBlocked(boolean blocked)
	{
		this.blocked = blocked;
	}
	
	public boolean isNoStones()
	{
		return noStones;
	}
	
	public void resetNoStones()
	{
		noStones = false;
	}
}