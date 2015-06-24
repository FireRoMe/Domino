package control;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import javax.swing.JPanel;

import view.DominoLabel;
import view.MainWindow;
import data.Player;
import data.Stone;

public class DominoGame
{
	private Stone[] allStones = new Stone[28];
	private ArrayList<Stone> talon = new ArrayList<Stone>();
	private Player[] allPlayers;
	private int currentPlayerIndex;
	/** Ein Array, in dem die Punkte der offenen Enden gespeichert werden. <br>
	 * [0]: links<br>[1]: rechts<br>[2]: oben<br>[3]: unten */
	private int[] edgePoints = new int[4];
	/** Ein Array, in dem gespeichert wird, ob eine offene Kante doppelte Punkte bringt <br>
	 * [0]: links<br>[1]: rechts<br>[2]: oben<br>[3]: unten */
	private boolean[] doublePoints = new boolean[4];
	private boolean hasSpinner = false;
	private Stone spinner = null;
	/** Zaehlt, wieviele Steine bereits gelegt worden sind */
	private int playedDominoes = 0;
	private int round = 1;
	private MainWindow view;
	private MouseClickMotionListener mouseHandler = new MouseClickMotionListener();
	
	private final int winningPoints= 250;
	
	/**
	 * Erzeugt das Spiel
	 * @param view - Die View
	 */
	public DominoGame(MainWindow view, int numPlayers)
	{
		allPlayers = new Player[numPlayers];
		
		// Alle Kantenpunkte werden zu Anfang mit 7 initialisiert,
		// denn das ist fuer die Funktionalität des Punktesystems wichtig
		for (int i = 0; i < edgePoints.length; i++)
			edgePoints[i] = 7;
		
		this.view = view;
		view.textOut(edgePoints[0] + ", " + edgePoints[1] + ", " + edgePoints[2] + ", " + edgePoints[3]);
		
		initializeGame();
	}
	
	public void initializeGame()
	{
		view.textOut(edgePoints[0] + ", " + edgePoints[1] + ", " + edgePoints[2] + ", " + edgePoints[3]);
		initializeDominoes();
		initializePlayers();
		initializeHands();
		initializeTalon();
		view.initializeWindow(allStones, mouseHandler, new ButtonListener());
		currentPlayerIndex = chooseBeginner();
		
		view.showStartSplash();
		showRoundInfo("", "", 80f, 0);
		startMove();
	}
	
	private void showRoundInfo(String text1, String text2, float textSize, int delay)
	{
		String labelText1, labelText2;
		
		if (text1 == "" && text2 == "")
		{
			labelText1 = "Runde " + round;
			labelText2 = "Spieler " + (currentPlayerIndex+1) + " beginnt!";
		}
		else
		{
			labelText1 = text1;
			labelText2 = text2;
		}
		
		view.showGameInfo(labelText1, labelText2, textSize, delay, true);
	}
	
	private void startMove()
	{
		Player player = allPlayers[currentPlayerIndex];
		boolean blocked = false;
		int failCounter = 0;
		boolean showDominoes = false;
		boolean endRound = false;

		player.setDroppedStone(false);		
		
		view.textOut("Runde " + round + "!");
		view.textOut("gespielte Steine: " + playedDominoes);
		
		view.updatePlayerArrow(currentPlayerIndex, playedDominoes);
		player.setBlocked(false);
		
		// Schleife die prueft, ob der Spieler einen legbaren Stein hat
		for (Stone s: player.getHand())
		{
			if (!DominoRules.checkIfDroppable(s, edgePoints, spinner, playedDominoes))
				failCounter++;
		}
		
		// Wenn der Spieler keinen Stein legen kann ist er blockiert
		if (failCounter == player.getHand().size())
		{
			blocked = true;
			player.setBlocked(true);
		}
		
		// Wenn beide Spieler blockiert sind endet die Runde
		if (allPlayers[0].isBlocked() && allPlayers[1].isBlocked())
			endRound = true;
		
		// Wenn der Talon nicht leer ist, aber der Spieler geblockt ist darf er einen Stein ziehen
		if (!talon.isEmpty() && blocked == true)
			view.updateButton(true, "");
		else
			view.updateButton(false, "");
		
		if (!talon.isEmpty() || (talon.isEmpty() && blocked == false))
			showDominoes = true;
		else if (talon.isEmpty() && blocked == true)
			showDominoes = false;
		
		System.err.println("Stand showDominoes: " + showDominoes);
		if (!endRound)
			makeMove(showDominoes, player);
		else
			endRound();
	}
	
	private void makeMove(boolean showDominoes, Player player)
	{
		if (showDominoes)
		{
			view.textOut("aktueller Spieler: " + player.getName());
			drawStonesOnView(currentPlayerIndex);
		}
		else
		{
			int otherPlayer = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
			String text1 = "Spieler " + (currentPlayerIndex+1) + " kann nicht legen";
			String text2 = "Spieler " + (otherPlayer+1) + " ist am Zug";
			view.showGameInfo(text1, text2, 40f, 0, true);
			currentPlayerIndex = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
			allPlayers[currentPlayerIndex].setDroppedStone(false);
			startMove();
		}
	}
	
	private void endRound()
	{
		int winnerPoints = DominoRules.calculateRoundPoints(allPlayers, true);
		int winner = DominoRules.calculateRoundPoints(allPlayers, false);

		String winnerString = "Spieler " + (winner+1) + " gewinnt die Runde";
		String pointsString = "und erhält " + winnerPoints + " Punkte";
		
		view.showGameInfo(winnerString, pointsString, 40f, 0, true);
		view.updatePlayerPoints(allPlayers[winner], winner);
		
		if (allPlayers[winner].getPoints() < winningPoints)
		{
			resetRound();
			
			initializeDominoes();
			initializeHands();
			initializeTalon();
			currentPlayerIndex = chooseBeginner();
			
			round++;
			
			startMove();
		}
		else
			endGame(winner+1);
	}

	/**
	 * 
	 * @param winner
	 */
	private void endGame(int winner)
	{
		view.resetWindow();
		String text1 = "Herzlichen Glückwunsch Spieler " + winner;
		String text2 = "Sie haben gewonnen!";
		view.updateButton(false, "Spiel beendet");
		view.showGameInfo(text1, text2, 60f, 0, true);
		view.showGameInfo("Toll gemacht", "Vielen Dank für's Spielen!", 80f, 2100, false);
	}
	
	/**
	 * Setzt alle relevante Instanzvariablen zurueck, bevor die naechste Runde
	 * gestartet wird
	 */
	private void resetRound()
	{
		for (int i = 0; i < edgePoints.length; i++)
		{
			edgePoints[i] = 7;
			doublePoints[i] = false;
		}
		
		for (Player p: allPlayers)
		{
			p.getHand().clear();
			p.setBlocked(false);
			p.setfirstMove(true);
			p.setDroppedStone(false);
			p.resetNoStones();
		}
		
		playedDominoes = 0;
		mouseHandler.panelOffsetX = 0;
		
		view.resetWindow();
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
	
	private int chooseBeginner()
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
		
		int beginnerIndex = 0;
		
		for (Player p: allPlayers)
		{
			if (p == highest.getPlayer())
				break;
			else
				beginnerIndex++;
		}
		
		return beginnerIndex;
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
		talon.clear();
		
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
	 * Uebergibt die aktuelle Hand des aktiven Spielers zum<br>
	 * darstellen an das Hauptfenster
	 * @param PlayerIndex - Der Index des Spielers, dessen Steine gezeichnet werden sollen
	 */
	private void drawStonesOnView(int PlayerIndex)
	{
		// geht durch alle Steine, die der Spieler auf der Hand hat
		for (Stone s: allPlayers[PlayerIndex].getHand())
		{
			// uebergibt den aktuellen Stein an eine Methode des Hauptfensters,
			// die die Steine auf die handPane zeichnet
			view.addDominoeToHand(s, allPlayers[PlayerIndex].isFirstMove());
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
		/** Die derzeit gedueckte Maustaste */
		private int pressedButton;
		private int draggedAtX, draggedAtY;
		private int panelOffsetX = 0, panelOffsetY = 0;
		
		@Override
		public void mouseDragged(MouseEvent e)
		{
			mousePos = MouseInfo.getPointerInfo();
			int mouseX = mousePos.getLocation().x;
			int mouseY = mousePos.getLocation().y;
			int offsetX, offsetY;
			
			view.showMousePosition(mouseX, mouseY);
			
			if (pressedButton == 1)
			{
				if (e.getSource() instanceof DominoLabel)
				{
					DominoLabel dLabel = (DominoLabel) e.getSource();
					
					if (dLabel.isDraggable())
					{
						Point origin = view.getFrameCoordinates();
						
						draggedStone = dLabel;
						
						// Offset berechnen, wenn der Stein horizontal gedreht ist
						if (!dLabel.getStone().isDoublestone() && !dLabel.getStone().isVertical())
						{
							offsetX = panelOffsetX + dLabel.getWidth()/2 - (origin.x + 102);
							offsetY = panelOffsetY + dLabel.getHeight() - (origin.y + 100);
						}
						// Offset berechnen, wenn der Stein vertikal gedreht ist
						else
						{
							offsetX = panelOffsetX + dLabel.getWidth()/2 - (origin.x + 52);
							offsetY = panelOffsetY + dLabel.getHeight() - (origin.y + 175);
						}
						
						
						dLabel.setLocation(offsetX + mouseX, offsetY + mouseY);
						
						view.checkIntersection(draggedStone, false, edgePoints, doublePoints);
						target = view.getCurrentTarget();
						
//						if (target == null)
//							draggedStone.getStone().clearNeighbours();
					}
					else
						view.textOut("Dieser Stein laesst sich nicht mehr verschieben");
				}
			}
			else if (pressedButton == 3)
			{
				if (e.getSource() instanceof JPanel)
				{
					view.textOut("JPanel gedraggt");
					JPanel p = (JPanel) e.getSource();
					Point origin = view.getFrameCoordinates();
					
					view.textOut("OnScreen: " + p.getLocationOnScreen());
					view.textOut("MausPos: " + mousePos.getLocation().x + "|" + mousePos.getLocation().y);
					e.translatePoint(draggedAtX, draggedAtY);
					p.setLocation(mouseX - (draggedAtX + origin.x) + 320, mouseY - (draggedAtY + origin.y) + 178);
					
					panelOffsetX = - p.getLocation().x;
					panelOffsetY = - p.getLocation().y;
					view.textOut("panelOffset: " + panelOffsetX + "|" + panelOffsetY);
				}
			}
			
			view.updatePanels();
			e.consume();
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			mousePos = MouseInfo.getPointerInfo();
			view.showMousePosition(mousePos.getLocation().x, mousePos.getLocation().y);
			
			if (e.getSource() instanceof JPanel && panelOffsetX == 0)
			{
				JPanel p = (JPanel) e.getSource();
				panelOffsetX = - p.getLocation().x;
				panelOffsetY = - p.getLocation().y;
				
				view.textOut("panelOffset gesetzt");
			}
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			view.textOut("Geklickt!");
			
			if (e.getButton() == 1)
			{
				Object c = e.getSource();
				
				if (c instanceof DominoLabel)
				{
					DominoLabel clickedStoneLabel = (DominoLabel) c;
					Stone clickedStone = clickedStoneLabel.getStone();
					
					// Es wird geprueft, ob der angeklickte Stein noch auf der Hand des Spielers ist
					if (clickedStoneLabel.getParent().getName() == "Hand")
					{
						System.err.println("DroppedStone: " + allPlayers[currentPlayerIndex].isDroppedStone());
						if (DominoRules.checkIfDroppable(clickedStone, edgePoints, spinner, playedDominoes) && 
															!allPlayers[currentPlayerIndex].isDroppedStone())
						{
							view.textOut("Kann gelegt werden");
							view.dropFromHand(clickedStoneLabel, panelOffsetX, panelOffsetY, playedDominoes);
							clickedStone.getPlayer().deleteStone(clickedStone);
							allPlayers[currentPlayerIndex].setDroppedStone(true);
							
							// Wird nur beim ersten Stein der gelegt wird aufgerufen
							if (playedDominoes == 0)
							{
								playedDominoes++;
								clickedStoneLabel.setNotDraggable();
								DominoRules.firstStone(clickedStone, edgePoints, doublePoints);
								hasSpinner = clickedStone.isSpinner();
								
								if (hasSpinner)
									spinner = clickedStone;
								
								view.firstPoints(edgePoints, doublePoints);
								int points = view.updatePoints(true);
								
								if (!DominoRules.calculatePlayerPoints(points, allPlayers[currentPlayerIndex]))
									System.err.println("Schade, leider diesmal keine Punkte");
								
								if (allPlayers[currentPlayerIndex].getPoints() >= winningPoints)
									endGame(currentPlayerIndex+1);
								
								System.err.println("Punkte " + allPlayers[currentPlayerIndex].getName() + ": " + allPlayers[currentPlayerIndex].getPoints());
								view.updatePlayerPoints(allPlayers[currentPlayerIndex], currentPlayerIndex);
								
								view.clearHand();
								currentPlayerIndex = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
								startMove();
							}
						}
						else
							view.textOut("Kann nicht gelegt werden");
					}
					
					System.err.println("Yeah, ich habe auf ein DominoLabel geklickt!");
					view.textOut("Stein " + clickedStone.getPips1() + "|" + clickedStone.getPips2() + " vertikal: " + clickedStone.isVertical() + ", spinner: " + clickedStone.isSpinner());
				}
				
				else if (c instanceof JLabel)
					view.textOut("Yeah, ich habe auf ein JLabel geklickt!");
				
				
				if (c instanceof JPanel)
				{
					JPanel p = (JPanel) c;
					
					
					if (p.getName() != "Hand")
					{
						// Nur für Debugging
						/*
						int index = allPlayers[0].getHand().size() - 1;
						if (index >= 0)
						{
							view.addDominoe(allPlayers[0].getHand().get(index), e.getPoint());
							allPlayers[0].getHand().remove(index);
						}
						else
							view.textOut("Dieser Spieler hat keine Steine mehr");
							
						*/
					}
					
					else
						view.textOut("Man klickt nicht auf die Hand, das tut weh!");
				}
			}
			view.textOut(edgePoints[0] + ", " + edgePoints[1] + ", " + edgePoints[2] + ", " + edgePoints[3]);
			view.textOut(doublePoints[0] + ", " + doublePoints[1] + ", " + doublePoints[2] + ", " + doublePoints[3]);
			
			e.consume();			
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			if (e.getSource() instanceof DominoLabel)
			{
				DominoLabel label = (DominoLabel) e.getSource();
				
				if (label.getParent().getName() == "Hand")
				{
					view.textOut("" + DominoRules.checkIfDroppable(label.getStone(), edgePoints, spinner, playedDominoes));
					view.textOut("" + label.getStone().toString());
				}
			}
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getSource() instanceof JPanel)
			{
				draggedAtX = e.getX() + 323;
				draggedAtY = e.getY() + 206;
			}
			
			view.textOut("draggedAt: " + draggedAtX + "|"+ draggedAtY);
			pressedButton = e.getButton();
			
			if (pressedButton == 3 && e.getSource() instanceof JPanel)
			{
				JPanel p = (JPanel) e.getSource();
				p.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
				
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			boolean endRound = false;
			boolean endGame = false;
			
			if (e.getSource() instanceof JPanel)
			{
				JPanel p = (JPanel) e.getSource();
				p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
			if (pressedButton == 1 && playedDominoes > 0)
			{
				// bekommt von checkIntersections zurueck, ob der Stein gesnappt hat
				boolean hasSnapped = false;
				
				if (draggedStone != null)
					view.textOut("DraggedStone: " + draggedStone.getStone().getPips1() + "|" + draggedStone.getStone().getPips2());
				
				if (target != null && draggedStone != null && draggedStone.getParent().getName() != "Hand")
				{
					view.textOut("Es gibt ein target");
					
					if (DominoRules.checkPossibleMove(target.getStone(), draggedStone))
					{
						if (spinner == null)
						{
							spinner = DominoRules.checkSpinner(draggedStone, target, hasSpinner);
							if (spinner != null)
								hasSpinner = true;
						}
						
						hasSnapped = view.checkIntersection(draggedStone, true, edgePoints, doublePoints);
						view.textOut("Target: " + target.getStone().getPips1() + "|" + target.getStone().getPips2());
					}
					
					// soll nur ausgefuehrt werden, wenn ein Stein angelegt wurde
					if (hasSnapped)
					{
						playedDominoes++;
						
						if (!DominoRules.calculatePlayerPoints(edgePoints, doublePoints, allPlayers[currentPlayerIndex]))
							System.err.println("Schade, leider diesmal keine Punkte");
						
						System.err.println("Punkte " + allPlayers[currentPlayerIndex].getName() + ": " + allPlayers[currentPlayerIndex].getPoints());
						
						if (allPlayers[currentPlayerIndex].getPoints() >= winningPoints)
							endGame = true;
						
						view.updatePlayerPoints(allPlayers[currentPlayerIndex], currentPlayerIndex);
						view.clearHand();
						
						// wenn ein Spieler keine Steine mehr hat, endet die Runde
						if (allPlayers[currentPlayerIndex].isNoStones())
						{
							endRound = true;
						}
						else
						{
							currentPlayerIndex = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
							startMove();
						}
					}
					else
						if (target != null)
						{
							draggedStone.setLocation(target.getX() + 150, target.getY() + 150);
							view.checkIntersection(draggedStone, false, edgePoints, doublePoints);
						}
				}
				else
				{
					view.textOut("Es gibt leider kein target");
				}
				
				draggedStone = null;
			}
			view.textOut(edgePoints[0] + ", " + edgePoints[1] + ", " + edgePoints[2] + ", " + edgePoints[3]);
			view.textOut(doublePoints[0] + ", " + doublePoints[1] + ", " + doublePoints[2] + ", " + doublePoints[3]);
			
			if (endGame == true)
				endGame(currentPlayerIndex+1);
			
			if (endRound == true)
			{
				endRound();
				showRoundInfo("", "", 80f, 2100);
			}
		}
	}
	
	public class ButtonListener	implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Stone stone = talon.get(0);
			Player player = allPlayers[currentPlayerIndex];
			
			talon.remove(stone);
			stone.setPlayer(player);
			player.addStone(stone);
			view.addDominoeToHand(stone, true);
			
			if (DominoRules.checkIfDroppable(stone, edgePoints, spinner, playedDominoes))
			{
				view.updateButton(false, "");
				player.setBlocked(false);
			}
			else
			{
				view.textOut("Spieler blockiert: " + player.isBlocked());
			}
			
			if (talon.isEmpty())
			{
				view.updateButton(false, "Der Talon ist leer");
				
				if (player.isBlocked())
				{
					int otherPlayer = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
					String text1 = "Spieler " + (currentPlayerIndex+1) + " kann nicht legen";
					String text2 = "Spieler " + (otherPlayer+1) + " ist am Zug";
					view.showGameInfo(text1, text2, 40f, 0, true);
					DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
					startMove();
				}
			}
		}
	}
}