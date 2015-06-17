package control;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JLabel;

import view.DominoLabel;
import view.MainWindow;
import data.Player;
import data.Stone;

public class DominoGame
{
	private Stone[] allStones = new Stone[28];
	private ArrayList<Stone> talon = new ArrayList<Stone>();
	private Player[] allPlayers;
	/** Ein Array, in dem die Punkte der offenen Enden gespeichert werden. <br>
	 * [0]: links<br>[1]: rechts<br>[2]: oben<br>[3]: unten */
	private int[] edgePoints = new int[4];
	private boolean hasSpinner = false;
	private MainWindow view;
	
	/**
	 * Erzeugt das Spiel
	 * @param view - Die View
	 */
	public DominoGame(MainWindow view, int numPlayers)
	{
		allPlayers = new Player[numPlayers];
		
		for (int e: edgePoints)
		{
			e = new Integer(0);
		}
		
		this.view = view;
		initializeGame();
	}
	
	public void initializeGame()
	{
		initializeDominoes();
		initializePlayers();
		initializeHands();
		initializeTalon();
		view.initializeWindow(allStones, new MouseClickMotionListener());
		view.textOut(chooseBeginner().getName());
		
	}

	private void initializeDominoes()
	{
		int x = 0;
		int y = 0;
		int w = 100;
		int h= 50;
		int current = 0;
		
		for (x = 0; x <= 6; x++)
		{
			for (y = 0; y <= 6; y++)
			{
				if (y == 0)
					y = x;
				
				allStones[current] = new Stone(x, y, new Dimension(w, h));
				allStones[current].loadIcon();
				
				current++;
			}
		}
		
		// Ausgabe bevor gemischt wurde
		for (Stone n : allStones)
		{
			System.out.println(n.getPips1() + "|" + n.getPips2() + " = " + n.getValue() + " | DS: " + n.isDoublestone());
		}
		
		shuffleDominoes();
		
		// Ausgabe nachdem gemischt wurde
		System.out.println("Gemischt!:");
		
		for (Stone n : allStones)
		{
			System.out.println(n.getPips1() + "|" + n.getPips2() + " = " + n.getValue() + " | DS: " + n.isDoublestone());
		}
	}
	
	/**
	 * Mischt die Dominosteine
	 */
	private void shuffleDominoes()
	{
		ArrayList<Stone> stoneShuffler = new ArrayList<Stone>();
		
		for(Stone s : allStones)
		{
			stoneShuffler.add(s);
		}
		
		Collections.shuffle(stoneShuffler);
		int z = 0;
		
		for (Stone s : stoneShuffler)
		{
			allStones[z] = s;
			z++;
		}
	}
	
	public Player chooseBeginner()
	{
		Stone highest = null;
		
		for (Player p: allPlayers)
		{
			for (Stone s: p.getHand())
			{
				if (s.isDoublestone() == true)
				{
						if (highest != null)
						{
							if (s.getValue() > highest.getValue())
								highest = s;
						}
						else
							highest = s;
				}
			}
		}
		
		if (highest == null)
		{
			for (Player p: allPlayers)
			{
				for (Stone s: p.getHand())
				{
					if (s.getValue() > highest.getValue())
						highest = s;
				}
			}
		}
		
		return highest.getPlayer();
	}
	
	/**
	 * Erzeugt die Spieler
	 */
	private void initializePlayers()
	{
		for (int i = 0; i < allPlayers.length; i++)
		{
			allPlayers[i] = new Player("Player_" + (i+1));
			view.textOut("Neuer Spieler: " + allPlayers[i].getName());
		}
	}
	
	private void initializeHands()
	{
		int numStones;
		int numPlayers = allPlayers.length;
		int playerID = 0;
		
		if (numPlayers == 2)
			numStones = 14;
		else
			numStones = 5;
		
		for (Player p: allPlayers)
		{
			view.textOut("");
			
			for (int i = 0; i < numStones; i++)
			{
				int pos = playerID + (numPlayers*i);
				
				p.addStone(allStones[pos]);
				allStones[pos].setPlayer(p);
				
				view.textOut(p.getName() + " Stein " + (i+1) + ": " + allStones[pos].getPips1() + "|" + allStones[pos].getPips2());
			}
			playerID++;
		}
	}
	
	private void initializeTalon()
	{
		for (Stone s: allStones)
		{
			if (s.getPlayer() == null)
				talon.add(s);
		}
		
		view.textOut("");
		
		for (Stone s: talon)
		{
			view.textOut("Talon " + (talon.indexOf(s)+1) + ": " + s.getPips1() + "|" + s.getPips2());
		}
	}
	
	/**
	 *  Von diesem Listener werden alle Mausgesten ausgewertet und verarbeitet
	 */  
	public class MouseClickMotionListener implements MouseListener, MouseMotionListener
	{
		private PointerInfo mousePos;
		private DominoLabel draggedStone;
		private DominoLabel target;
		
		@Override
		public void mouseDragged(MouseEvent e)
		{
			mousePos = MouseInfo.getPointerInfo();
			view.showMousePosition(mousePos.getLocation().x, mousePos.getLocation().y);
			
			if (e.getSource() instanceof DominoLabel)
			{
				DominoLabel d = (DominoLabel) e.getSource();
				
				if (d.isDraggable())
				{
					draggedStone = d;
					int offsetX = d.getTopLevelAncestor().getX() + d.getWidth()/2;
					int offsetY = d.getTopLevelAncestor().getY() + d.getHeight();
					
					d.setLocation(mousePos.getLocation().x - offsetX, mousePos.getLocation().y - offsetY);
					
					target = view.checkIntersection(draggedStone, false, edgePoints);
					
					if (target == null)
						draggedStone.getStone().clearNeighbours();
				}
				else
					view.textOut("Dieser Stein laesst sich nicht mehr verschieben");
			}
			
			e.consume();
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			mousePos = MouseInfo.getPointerInfo();
			view.showMousePosition(mousePos.getLocation().x, mousePos.getLocation().y);
			
			Object o = e.getSource();
			
			if (o instanceof DominoLabel)
				draggedStone = (DominoLabel) o;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			view.textOut("Geklickt!");
			
			Object c = e.getSource();
			
			if (c instanceof DominoLabel)
			{
				draggedStone = (DominoLabel) c;
				Stone s = draggedStone.getStone();
				System.err.println("Yeah, ich habe auf ein DominoLabel geklickt!");
				view.textOut("Stein " + s.getPips1() + "|" + s.getPips2() + " vertikal: " + s.isVertical());
			}
			
			else if (c instanceof JLabel)
				view.textOut("Yeah, ich habe auf ein JLabel geklickt!");
			
			else
			{
				int index = allPlayers[0].getHand().size() - 1;
				if (index >= 0)
				{
					view.addDominoe(allPlayers[0].getHand().get(index), e.getPoint());
					allPlayers[0].getHand().remove(index);
				}
				else
					view.textOut("Dieser Spieler hat keine Steine mehr");
			}
			
			e.consume();			
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (draggedStone != null)
				view.textOut(draggedStone.getStone().getPips1() + "|" + draggedStone.getStone().getPips2());
			
			if (target != null)
			{
				view.textOut("Es gibt ein target");
				
				if (draggedStone.getStone().isDoublestone() && !hasSpinner)
					draggedStone.getStone().setSpinner(true);
				else if (target.getStone().isDoublestone() && !target.getStone().isSpinner() && !hasSpinner)
					target.getStone().setSpinner(true);
				
				view.checkIntersection(draggedStone, true, edgePoints);
				view.textOut("Target: " + target.getStone().getPips1() + "|" + target.getStone().getPips2());
			}
			else
			{
				view.textOut("Es gibt leider kein target");
			}
		}
		
	}
}
