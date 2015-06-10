package control;

import java.awt.Component;
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
	private MainWindow view;
	
	/**
	 * Erzeugt das Spiel
	 * @param view - Die View
	 */
	public DominoGame(MainWindow view, int numPlayers)
	{
		allPlayers = new Player[numPlayers];
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
			numStones = 7;
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
	
	public class MouseClickMotionListener implements MouseListener, MouseMotionListener
	{
		private PointerInfo mousePos;
		
		@Override
		public void mouseDragged(MouseEvent e)
		{
			mousePos = MouseInfo.getPointerInfo();
			view.showMousePosition(mousePos.getLocation().x, mousePos.getLocation().y);
			
			if (e.getSource() instanceof DominoLabel)
			{
				view.textOut("Ich bin im MouseDragged");
				DominoLabel d = (DominoLabel) e.getSource();
				int offsetX = d.getTopLevelAncestor().getX() + d.getWidth()/2;
				int offsetY = d.getTopLevelAncestor().getY() + d.getHeight();
				
				d.setLocation(mousePos.getLocation().x - offsetX, mousePos.getLocation().y - offsetY);
				view.textOut("Autoscroll:" + d.getAutoscrolls());
			}
			
			
			e.consume();
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			mousePos = MouseInfo.getPointerInfo();
			view.showMousePosition(mousePos.getLocation().x, mousePos.getLocation().y);
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			view.textOut("Geklickt!");
			
			Object c = e.getSource();
			
			if (c instanceof JLabel)
				view.textOut("Yeah, ich habe auf ein JLabel geklickt!");
			
			if (c instanceof DominoLabel)
				System.err.println("Yeah, ich habe auf ein DominoLabel geklickt!");
			
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
			// TODO Auto-generated method stub
		}
		
	}
}
