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
import javax.swing.JPanel;

import view.DominoLabel;
import view.MainWindow;
import data.Player;
import data.Stone;

/**
 * Die Hauptklasse des Spiels 
 */
public class DominoGame
{
	/** Haelt alle Steine im Spiel */
	private Stone[] allStones = new Stone[28];
	/** Der Pool aus dem die Spieler Steine ziehen*/
	private ArrayList<Stone> talon = new ArrayList<Stone>();
	/** Haelt alle Spieler*/
	private Player[] allPlayers;
	/** Der Index des aktuellen Spielers*/
	private int currentPlayerIndex;
	/** Ein Array, in dem die Punkte der offenen Enden gespeichert werden. <br>
	 * [0]: links<br>[1]: rechts<br>[2]: oben<br>[3]: unten */
	private int[] edgePoints = new int[4];
	/** Ein Array, in dem gespeichert wird, ob eine offene Kante doppelte Punkte bringt <br>
	 * [0]: links<br>[1]: rechts<br>[2]: oben<br>[3]: unten */
	private boolean[] doublePoints = new boolean[4];
	/** Gibt an, ob ein Spinner auf dem Feld liegt */
	private boolean hasSpinner = false;
	/** Speichert den Spinner */
	private Stone spinner = null;
	/** Zaehlt, wieviele Steine bereits gelegt worden sind */
	private int playedDominoes = 0;
	/** Gibt die aktuelle Runde an */
	private int round = 1;
	/** Das Hauptfenster, in dem das Spiel dargestellt wird */
	private MainWindow view;
	/** Der Listener fuer alle Mauseingaben */
	private MouseClickMotionListener mouseHandler = new MouseClickMotionListener();
	/** Die Anzahl der benoetigten Punkte zum gewinnen des Spiels */
	private final int winningPoints = 250;
	
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
		
		// das Hauptfenster wird im Spiel gespeichert
		this.view = view;
		
		initializeGame();
	}
	
	/**
	 * Initialisiert das Spiel
	 */
	public void initializeGame()
	{
		initializeDominoes();
		initializePlayers();
		initializeHands();
		initializeTalon();
		// Initialisierung und anzeigen des Hauptfensters
		view.initializeWindow(allStones, mouseHandler, new ButtonListener());
		// Auswahl des Spielers der beginnen soll
		currentPlayerIndex = chooseBeginner();
		
		view.showStartSplash();
		showRoundInfo("", "", 80f, 0);
		startMove();
	}
	
	/**
	 * Zeigt die aktuelle Rundenzahl und den beginnenden Spieler
	 * oder einen beliebigen Text an und blendet ihn nach
	 * einer bestimmten Zeit wieder aus
	 * @param text1 - der Text der im oberen Label stehen soll
	 * @param text2 - der Text der im unteren Label stehen soll
	 * @param textSize - die Textgroesse
	 * @param delay - Verzoegerung in Millisekunden nach der der Text angezeigt werden soll
	 */
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
	
	/**
	 * Initialisierung des Spielzuges
	 */
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
		
		// wenn der Talon leer ist oder der Spieler trotzdem legen kann...
		if (!talon.isEmpty() || (talon.isEmpty() && blocked == false))
			showDominoes = true;	// ...werden die Steine im Fenster angezeigt
		// wenn der Talon leer ist und der Spieler nicht legen kann...
		else if (talon.isEmpty() && blocked == true)
			showDominoes = false;	// ...werden die Steine nicht angezeigt
		
		System.err.println("Stand showDominoes: " + showDominoes);
		// wenn die Runde nicht blockiert ist
		if (!endRound)
			makeMove(showDominoes, player); 
		else
			endRound();
	}
	
	/**
	 * Es werden entweder die Steine des aktuellen Spielers angezeigt
	 * oder wenn der Spieler nicht legen ist der naechste Spieler an der Reihe
	 * @param showDominoes - wenn true sollen die Steine angezeigt werden<br>
	 * wenn false ist der naechste Spieler an der Reihe
	 * @param player - der aktuelle Spieler
	 */
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
	
	/**
	 * Beendet eine Runde
	 */
	private void endRound()
	{
		// Berechnung des Gewinners und dessen Punkte
		int winnerPoints = DominoRules.calculateRoundPoints(allPlayers, true);
		int winner = DominoRules.calculateRoundPoints(allPlayers, false);

		String winnerString = "Spieler " + (winner+1) + " gewinnt die Runde";
		String pointsString = "und erhält " + winnerPoints + " Punkte";
		
		// Anzeige des Gewinners und seiner Punkte auf dem Bildschirm
		view.showGameInfo(winnerString, pointsString, 40f, 0, true);
		view.updatePlayerPoints(allPlayers[winner], winner);
		
		// wenn der Rundengewinner weniger Punkte hat als die zum
		// Spielgewinn benoetigten, wird die naechste Runde gestartet
		if (allPlayers[winner].getPoints() < winningPoints)
		{
			resetRound();
			
			initializeDominoes();
			initializeHands();
			initializeTalon();
			// Spieler auswaehlen, der die naechste Runde beginnt
			currentPlayerIndex = chooseBeginner();
			
			// Runde um 1 erhoehen
			round++;
			
			startMove();
		}
		// sonst steht der Gewinner des Spiels fest und das Spiel endet
		else
			endGame(winner+1);
	}

	/**
	 * Das Spiel ist beendet und der Gewinner wird bekannt gegeben
	 * @param winner - Index des Gewinners + 1
	 */
	private void endGame(int winner)
	{
		// Das Fenster zuruecksetzen, damit der Text besser lesbar ist
		view.resetWindow();
		String text1 = "Herzlichen Glückwunsch Spieler " + winner;
		String text2 = "Sie haben gewonnen!";
		view.updateButton(false, "Spiel beendet");
		// Bekanntgabe des Gewinners
		view.showGameInfo(text1, text2, 60f, 0, true);
		view.showGameInfo("Toll gemacht", "Vielen Dank für's Spielen!", 80f, 2100, false);
	}
	
	/**
	 * Setzt alle relevanten Instanzvariablen zurueck, bevor die naechste Runde
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
		
		hasSpinner = false;
		spinner = null;
		playedDominoes = 0;
		mouseHandler.panelOffsetX = 0;
		
		view.resetWindow();
	}
	
	/**
	 * Erstellt die 28 Dominosteine
	 */
	private void initializeDominoes()
	{
		int w = 100;
		int h= 50;
		int current = 0;
		
		// Aeussere Schleife (linke Augenzahl)
		for (int x = 0; x <= 6; x++)
		{	// Innere Schleife (rechte Augenzahl)
			for (int y = 0; y <= 6; y++)
			{
				/* wenn die innere Schleife neu beginnt, wird
				 deren Schleifenzaehler auf den aktuellen 
				 Wert des aeusseren Zaehlers gesetzt */
				if (y == 0)
					y = x;
				
				// Erzeugt den Dominostein und legt Augenzahlen und Groesse fest
				allStones[current] = new Stone(x, y, new Dimension(w, h));
				// Laedt das passende Bild
				allStones[current].loadIcon();
				
				// Dieser Zaehler zaehlt durch die Schleifen bedingt von 0 bis 27
				current++;
			}
		}
		
		// Konsolenausgabe bevor gemischt wurde
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
		// Anlegen einer ArrayList, die spaeter gemischt wird
		ArrayList<Stone> stoneShuffler = new ArrayList<Stone>();
		
		// Laedt alle Dominosteine in die ArrayList
		for(Stone s : allStones)
		{
			stoneShuffler.add(s);
		}
		
		// Mischen der Steine mithilfe der shuffle Methode fuer ArrayLists
		Collections.shuffle(stoneShuffler);
		
		// Zurueckschieben der Steine in das Array
		int z = 0;
		
		for (Stone s : stoneShuffler)
		{
			allStones[z] = s;
			z++;
		}
	}
	
	/**
	 * Auswahl des Spielers, der die Runde beginnt
	 * Der Spieler mit dem hoechsten Doppelstein beginnt
	 * Hat kein Spieler einen Doppelstein, beginnt der
	 * Spieler der ansonsten den hoechstwertigsten Stein hat
	 * @return - Der Index des Beginners
	 */
	private int chooseBeginner()
	{
		// Anlegen einer Variable fuer den hoechsten Stein
		Stone highest = null;
		
		// Schleife durch alle Spieler
		for (Player p: allPlayers)
		{
			// Schleife durch alle Steine auf der Hand
			for (Stone s: p.getHand())
			{
				// wenn der Stein ein Doppelstein ist
				if (s.isDoublestone() == true)
				{
					// wenn es bereits einen hoechsten Stein gibt
					if (highest != null)
					{
						/* wird ersetzt, wenn sein Wert hoeher ist
						 * als der Wert des aktuell hoechsten Steins */
						if (s.getValue() > highest.getValue())
							highest = s; 
					}
					// wenn noch kein Stein der hoechstwertigste ist
					else
						// wird der aktuelle Stein der hoechstwertigste
						highest = s;
				}
			}
		}
		
		/* wenn kein Spieler einen Doppelstein auf der Hand hat
		werden die normalen Steine ueberprueft*/
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
		
		// auswerten der vorrangegangenen Pruefung und bestimmen des Beginners
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
	
	/**
	 * Initialisiert die Haende der Spieler
	 */
	private void initializeHands()
	{
		int numStones;
		int numPlayers = allPlayers.length;
		int playerID = 0;
		
		// wenn 2 Spieler spielen bekommt jeder zu Anfang 7 Steine
		if (numPlayers == 2)
			numStones = 7;
		// sonst bekommt jeder 5 Steine
		else
			numStones = 5;
		
		// Schleife durch alle Spieler
		for (Player p: allPlayers)
		{
			view.textOut("");
			// Schleife durch alle Spieler
			for (int i = 0; i < numStones; i++)
			{
				/* um zu vermeiden, dass mehrere Spieler identische Steine
				bekommen koennen bekommt zum Beispiel bei 2 Spielern jeder
				Spieler jeden zweiten Stein aus allStones bei 3 Spielern
				jeden dritten usw.*/
				int pos = playerID + (numPlayers*i);
				
				p.addStone(allStones[pos]);
				allStones[pos].setPlayer(p);
				
				view.textOut(p.getName() + " Stein " + (i+1) + ": "
				+ allStones[pos].getPips1() + "|" + allStones[pos].getPips2());
			}
			playerID++;
		}
	}
	
	/**
	 * Initialisierung des Pools aus dem die Spieler ziehen koennen,
	 * wenn sie nicht legen koennen
	 */
	private void initializeTalon()
	{
		/* den Talon zu beginn leeren, weil zu Beginn einer neuen
		Runde noch Steine enthalten sein koennten */
		talon.clear();
		
		// Schleife durch alle Steine
		for (Stone s: allStones)
		{
			// wenn ein Stein keinem Spieler gehoert kann er in den Talon
			if (s.getPlayer() == null)
				talon.add(s);
		}
		
		view.textOut("");
		
		for (Stone s: talon)
		{
			view.textOut("Talon " + (talon.indexOf(s)+1) + ": " 
						+ s.getPips1() + "|" + s.getPips2());
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
		/** Speichert die Mausposition */
		private PointerInfo mousePos;
		/** Der Stein, den der Spieler gerade legt */
		private DominoLabel draggedStone;
		/** Der Stein, an den der Spieler anlegen will */
		private DominoLabel target;
		/** Die derzeit gedrueckte Maustaste */
		private int pressedButton;
		/** Die Koordinaten bei denen begonnen wurde das Spielfeld zu verschieben */
		private int draggedAtX, draggedAtY;
		/** Die Versatz des Ursprungs des Spielfeldes zum Ursprung des Haupfensters */
		private int panelOffsetX = 0, panelOffsetY = 0;
		
		@Override
		public void mouseDragged(MouseEvent e)
		{	
			// erhalten der aktuellen Koordinaten des Cursors
			mousePos = MouseInfo.getPointerInfo();	
			int mouseX = mousePos.getLocation().x;
			int mouseY = mousePos.getLocation().y;
			int offsetX, offsetY;
			
			view.showMousePosition(mouseX, mouseY);
			
			// wenn die linke Maustaste gedrueckt gehalten wird
			if (pressedButton == 1)
			{	
				/* wenn die Maustaste ueber einem Stein gedrueckt gehalten und
				die Maus verschoben wird */
				if (e.getSource() instanceof DominoLabel)
				{
					DominoLabel dLabel = (DominoLabel) e.getSource();

					// wenn der Stein verschiebbar ist
					if (dLabel.isDraggable())
					{
						// Ursprungskoordinaten des Hauptfensters holen
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
						
						// Stein an die Position des Mauszeigers verschieben
						// (Stein "klebt" am Cursor)
						dLabel.setLocation(offsetX + mouseX, offsetY + mouseY);
						
						// pruefen, ob sich Steine ueberschneiden
						view.checkIntersection(draggedStone, false, edgePoints, doublePoints);
						/* laesst sich den Stein zurueckgeben mit dem sich der Stein,
						der "an der Maus klebt" gerade ueberschneidet */
						target = view.getCurrentTarget();
					}
					else
						view.textOut("Dieser Stein laesst sich nicht mehr verschieben");
				}
			}
			// wenn die rechte Maustaste gedrueckt gehalten wird
			else if (pressedButton == 3)
			{
				/* wenn die rechte Maustaste auf einer freien Flaeche des Spielfeldes
				gedrueckt gehalten wird */
				if (e.getSource() instanceof JPanel)
				{
					view.textOut("JPanel gedraggt");
					JPanel p = (JPanel) e.getSource();
					Point origin = view.getFrameCoordinates();
					
					view.textOut("OnScreen: " + p.getLocationOnScreen());
					view.textOut("MausPos: " + mousePos.getLocation().x + "|" + mousePos.getLocation().y);
					
					/* den Ursprungsort der Verschiebung auf den Punkt setzen, an dem man angefangen hat
					die rechte Maustaste gedrueckt zu halten */
					e.translatePoint(draggedAtX, draggedAtY);
					// hier "klebt" das Spielfeld am Cursor und kann verschoben werden
					p.setLocation(mouseX - (draggedAtX + origin.x) + 320, mouseY - (draggedAtY + origin.y) + 178);
					
					/* das Offset wird auch mit verschoben, damit es bei weiterem Verschieben des Spielfeldes
					nicht zu Sprüngen kommt */
					panelOffsetX = - p.getLocation().x;
					panelOffsetY = - p.getLocation().y;
					view.textOut("panelOffset: " + panelOffsetX + "|" + panelOffsetY);
				}
			}
			
			view.updatePanels();	// Die Grafik des Fensters auffrischen, um Anzeigefehlern vorzubeugen
			e.consume();
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			mousePos = MouseInfo.getPointerInfo();
			view.showMousePosition(mousePos.getLocation().x, mousePos.getLocation().y);
			
			// das Offset des Spielfeldes neu berechnen, wenn es noch nicht berechnet wurde
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
			// wenn die rechte Maustaste geklickt wurde
			if (e.getButton() == 1)
			{
				Object c = e.getSource();
				// wenn ein Spielstein angeklickt wurde
				if (c instanceof DominoLabel)
				{
					DominoLabel clickedStoneLabel = (DominoLabel) c;
					Stone clickedStone = clickedStoneLabel.getStone();
					Player player = allPlayers[currentPlayerIndex];
					
					// Es wird geprueft, ob der angeklickte Stein noch auf der Hand des Spielers ist
					if (clickedStoneLabel.getParent().getName() == "Hand")
					{
						// wenn der angeklickte Stein aufs Feld gelegt werden kann
						if (DominoRules.checkIfDroppable(clickedStone, edgePoints, spinner, playedDominoes) && 
															!player.isDroppedStone())
						{
							view.textOut("Kann gelegt werden");
							// der Stein wird von der Hand aufs Spielfeld gelegt
							view.dropFromHand(clickedStoneLabel, panelOffsetX, panelOffsetY, playedDominoes);
							// der Stein wird von der Hand des Spielers geloescht
							clickedStone.getPlayer().deleteStone(clickedStone);
							// der Spieler hat einen Stein gelegt und kann in diesem Zug keinen weiteren mehr legen
							player.setDroppedStone(true);
							
							// wird nur beim ersten Stein der gelegt wird aufgerufen
							if (playedDominoes == 0)
							{
								// gelegte Steine um 1 erhoehen
								playedDominoes++;
								// der Stein laesst sich nicht mehr verschieben
								clickedStoneLabel.setNotDraggable();
								// Punktzahlen setzen
								DominoRules.firstStone(clickedStone, edgePoints, doublePoints);
								// wenn der gelegte Stein ein Spinner ist wird hasSpinner auf true gesetzt
								hasSpinner = clickedStone.isSpinner();
								
								// ist ein Spinner auf dem Feld wird dieser im Spiel gespeichert
								if (hasSpinner)
									spinner = clickedStone;
								
								// Punkte im Fenster anzeigen
								view.firstPoints(edgePoints, doublePoints);
								view.updatePoints(true);
								
								// Spielerpunkte berechnen
								if (!DominoRules.calculatePlayerPoints(edgePoints, doublePoints, player, true))
									System.err.println("Schade, leider diesmal keine Punkte");
								
								// wenn der Spieler genug Punkte hat, um das Spiel zu gewinnen endet das Spiel
								if (player.getPoints() >= winningPoints)
									endGame(currentPlayerIndex+1);
								
								// Spielerpunkte im Fenster anzeigen
								view.updatePlayerPoints(player, currentPlayerIndex);
								
								// Hand im Fenster loeschen, Spieler wechseln und naechsten Zug starten
								view.clearHand();
								currentPlayerIndex = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
								startMove();
							}
						}
					}
				}
				
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
				}
			}
			view.textOut(edgePoints[0] + ", " + edgePoints[1] 
						+ ", " + edgePoints[2] + ", " + edgePoints[3]);
			view.textOut(doublePoints[0] + ", " + doublePoints[1] 
						+ ", " + doublePoints[2] + ", " + doublePoints[3]);
			
			e.consume();			
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			// wenn der Cursor ueber einem Stein ist
			if (e.getSource() instanceof DominoLabel)
			{
				DominoLabel label = (DominoLabel) e.getSource();
				
				// Auf der Konsole ausgeben, ob ein Stein legbar ist oder nicht (fuer Debugging)
				if (label.getParent().getName() == "Hand")
				{
					view.textOut("" + DominoRules.checkIfDroppable(label.getStone(),
									edgePoints, spinner, playedDominoes));
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
			/* wenn eine Maustaste auf einer frei Flaeche
			des Spielfeldes grueckt gehalten wird, aber die Maus
			nicht verschoben wird */
			if (e.getSource() instanceof JPanel)
			{
				draggedAtX = e.getX() + 323;
				draggedAtY = e.getY() + 206;
			}
			
			view.textOut("draggedAt: " + draggedAtX + "|"+ draggedAtY);
			
			// gedrueckte Maustaste speichern fuer spaetere Abfragen
			pressedButton = e.getButton();
			
			/* Cursor aendern, wenn die rechte Maustaste ueber dem
			Spielfeld gedrueckt gehalten wird */
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
			
			/* wenn die Maustaste ueber dem Spielfeld losgelassen wurde
			wird der Standardcursor wiederhergestellt */
			if (e.getSource() instanceof JPanel)
			{
				JPanel p = (JPanel) e.getSource();
				p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
			// wenn die linke Maustaste losgelassen wurde und bereits Steine gelegt wurden
			if (pressedButton == 1 && playedDominoes > 0)
			{
				// bekommt von checkIntersections zurueck, ob der Stein gesnappt hat
				boolean hasSnapped = false;
				Player player = allPlayers[currentPlayerIndex];
				
				if (draggedStone != null)
					view.textOut("DraggedStone: " + draggedStone.getStone().getPips1()
									+ "|" + draggedStone.getStone().getPips2());
				
				// wenn ein Stein ueber einem anderen Stein auf dem Spielfeld losgelassen wurde
				if (target != null && draggedStone != null 
					&& draggedStone.getParent().getName() != "Hand")
				{
					view.textOut("Es gibt ein target");
					
					// wenn der Zug moeglich ist
					if (DominoRules.checkPossibleMove(target.getStone(), draggedStone))
					{
						// wenn noch kein Spinner auf dem Feld liegt
						if (spinner == null)
						{
							/* wenn der gelegte Stein ein Spinner ist, wird er
							dem Spiel hinzugefuegt */
							spinner = DominoRules.checkSpinner(draggedStone, target, hasSpinner);
							if (spinner != null)
								hasSpinner = true;
						}
						
						// wenn ein Stein angelegt wurde, wird hasSnapped auf true gesetzt
						hasSnapped = view.checkIntersection(draggedStone, true,
															edgePoints, doublePoints);
						view.textOut("Target: " + target.getStone().getPips1()
										+ "|" + target.getStone().getPips2());
					}
					
					// wenn ein Stein angelegt wurde
					if (hasSnapped)
					{
						playedDominoes++;	// gelegte Steine um 1 erhoehen
						
						// Berechnung der Spielerpunkte und Debugausgabe
						if (!DominoRules.calculatePlayerPoints(edgePoints, doublePoints,
																		player, false))
							System.err.println("Schade, leider diesmal keine Punkte");
						
						System.err.println("Punkte " + player.getName() + ": "
											+ player.getPoints());
						
						/* wenn ein Spieler genug Punkte hat, um das Spiel zu
						gewinnen endet das Spiel*/
						if (player.getPoints() >= winningPoints)
							endGame = true;
						
						// Punkte im Fenster anzeigen
						view.updatePlayerPoints(player, currentPlayerIndex);
						view.clearHand();
						
						// wenn ein Spieler keine Steine mehr hat, endet die Runde
						if (player.isNoStones() && endGame == false)
						{
							endRound = true;
						}
						// sonst ist der naechste Spieler am Zug
						else
						{
							currentPlayerIndex = DominoRules.switchPlayer(allPlayers,
																		currentPlayerIndex);
							startMove();
						}
					}
					// wenn der Stein nicht angelegt wurde springt er ein Stueck zur Seite
					else
						if (target != null)
						{
							draggedStone.setLocation(target.getX() + 150, target.getY() + 150);
							view.checkIntersection(draggedStone, false, edgePoints, doublePoints);
						}
				}
				
				// die Variable fuer den zu legenden Stein wieder auf null setzen
				draggedStone = null;
			}
			// Debugausgaben
			view.textOut(edgePoints[0] + ", " + edgePoints[1] + ", " 
							+ edgePoints[2] + ", " + edgePoints[3]);
			view.textOut(doublePoints[0] + ", " + doublePoints[1] + ", "
							+ doublePoints[2] + ", " + doublePoints[3]);
			
			// wenn das Spiel beendet werden soll
			if (endGame == true)
				endGame(currentPlayerIndex+1);
			
			// wenn die Runde beendet ist
			if (endRound == true)
			{
				endRound();
				showRoundInfo("", "", 80f, 2100);
			}
		}
	}
	
	/**
	 * Der Listener fuer den Button zum ziehen der Steine aus dem Talon 
	 */
	public class ButtonListener	implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// es wird der erste Stein aus dem Talon geholt
			Stone stone = talon.get(0);
			Player player = allPlayers[currentPlayerIndex];
			
			view.textOut(player.getName() + player.getHand().size());
			// der Stein wird aus dem Talon geloescht 
			talon.remove(stone);
			// der Stein wird dem Spieler hinzugefuegt
			player.addStone(stone);
			stone.setPlayer(player);
			// der Stein wird im Fenster angezeigt
			view.addDominoeToHand(stone, true);
			
			view.textOut(player.getName() + player.getHand().size());
			// wenn der Stein auf dem Feld angelegt werden kann
			if (DominoRules.checkIfDroppable(stone, edgePoints,
											 spinner, playedDominoes))
			{
				// der Button wird deaktiviert, damit man nicht mehr ziehen kann
				view.updateButton(false, "");
				// der Spieler ist nicht mehr blockiert
				player.setBlocked(false);
			}
			
			// wenn der Talon leer ist...
			if (talon.isEmpty())
			{
				// ...wird das auf dem Button angezeigt...
				view.updateButton(false, "Der Talon ist leer");
				
				// ...und der naechste Spieler ist am Zug
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