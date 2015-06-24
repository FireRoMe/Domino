package control;

import java.util.ArrayList;

import data.Player;
import data.Stone;
import view.DominoLabel;

public final class DominoRules
{
	/** Ueberprueft die grundlegende Kompatibilitaet zweier Steine
	 * 
	 * @param dragged - Der Stein, der geprueft werden soll
	 * @param target - Der Stein, der bereits auf dem Feld liegt
	 * @return <b>true</b> - wenn kompatibel <br>
	 * <b>false</b> - wenn nicht kompatibel
	 */
	public static boolean checkCompatibility(DominoLabel dragged, DominoLabel target)
	{
		int d_p1 = dragged.getStone().getPips1();
		int d_p2 = dragged.getStone().getPips2();
		int t_p1 = target.getStone().getPips1();
		int t_p2 = target.getStone().getPips2();
		
		// wenn einer Seite des einen Steins mit einer Seite des anderen uebereinstimmt
		if (d_p1 == t_p1 || d_p1 == t_p2 || d_p2 == t_p1 || d_p2 == t_p2)
			return true;	// sind die Steine grundlegend kompatibel
		else
			return false;
	}
	
	/**
	 * Prueft, ob an der rechten Seite des Steins angelegt werden kann
	 * @param draggedStone - Der Stein, der gelegt werden soll
	 * @param target - Der Stein, an den angelegt werden soll
	 * @return <b>true</b> - Der Stein soll rechts angelegt werden<br>
	 * <b>false</b> - Der Stein soll links angelegt werden
	 */
	public static boolean snapRight(DominoLabel draggedStone, DominoLabel target)
	{
		// Steine aus den DominoLabeln holen
		Stone dStone = draggedStone.getStone();
		Stone tStone = target.getStone();
		// Es wird geprueft, welche Seite des Zielsteins mit dem zu legenden Stein uebereinstimmt
		int equalPip = giveEqualPip(dStone, tStone);
		
		// wenn das Ziel ein Doppelstein ist
		if (equalPip == 0)
		{
			// wenn das Ziel rechts keinen Nachbarn hat
			if (tStone.getRightNeighbour() == null)
			{
				// wird geprueft, ob der Stein gedreht werden muss
				dStone.checkRotationHorizontal(tStone, true);
				draggedStone.updateImage();	// das Bild des Labels wird aktualisiert
				
				return true;	// der Stein kann rechts angelegt werden
			}
			// das Ziel hat rechts einen Nachbarn
			else
			{
				dStone.checkRotationHorizontal(tStone, false);
				draggedStone.updateImage();
				
				return false;	// der Stein kann links angelegt werden
			}
		}
		
		// wenn das Ziel rechts keinen Nachbarn hat und die uebereinstimmende Augenzahl
		// auf rechten Seite des Zielsteins ist 
		if(tStone.getRightNeighbour() == null && equalPip == 2)
		{
			// wenn der anzulegende Stein falsch gedreht ist, muss er gedreht werden
			if (tStone.getPips2() != dStone.getPips1())
			{
				dStone.checkRotationHorizontal(target.getStone(), true);
				draggedStone.updateImage();
			}
			
			return true;	// der Stein kann rechts angelegt werden
		}	
		// sonst soll links angelegt werden
		else
		{
			if (tStone.getPips1() != dStone.getPips2())
			{
				dStone.checkRotationHorizontal(target.getStone(), false);
				draggedStone.updateImage();
			}
			
			return false;	// der Stein kann links angelegt werden
		}
	}
	
	/**
	 * Ueberprueft fuer das horizontale anlegen, wo ein Stein Nachbarn hat
	 * @param dragged - Der anzulegende Stein
	 * @param target - Der Zielstein
	 * @param snapRight - boolean, ob rechts angelegt werden soll
	 * @return <b>true</b> - der Stein hat an der Zielseite keinen Nachbarn <br>
	 * <b>false</b> - sonst
	 */
	public static boolean checkNeighboursHorizontal(DominoLabel dragged, DominoLabel target, boolean snapRight)
	{
		// wenn rechts angelegt werden soll
		if (snapRight)
		{
			// wenn der Stein rechts keinen Nachbarn hat
			if (target.getStone().getRightNeighbour() == null)
				return true;	// kann rechts angelegt werden
			else
				return false;	// es kann nicht rechts angelegt werden
		}
		else
		{
			if (target.getStone().getLeftNeighbour() == null)
				return true;
			else
				return false;
		}
	}
	
	/**
	 * Prueft, ob an der oberen Seite des Steins angelegt werden kann
	 * @param draggedStone - Der Stein, der gelegt werden soll
	 * @param target - Der Stein, an den angelegt werden soll
	 * @return <b>true</b> - Der Stein soll oben angelegt werden<br>
	 * <b>false</b> - Der Stein soll unten angelegt werden
	 */
	public static boolean snapTop(DominoLabel draggedStone, DominoLabel target)
	{
		// Steine aus den Labels holen
		Stone dStone = draggedStone.getStone();
		Stone tStone = target.getStone();
		// uebereinstimmende Seite berechnen
		int equalPip = giveEqualPip(dStone, tStone);
		
		// wenn der Stein ein Doppelstein ist
		if (equalPip == 0)
		{
			// wenn der Stein oben keinen Nachbarn hat
			if (tStone.getTopNeighbour() == null)
			{
				// Stein richtig drehen und Bild im Label aktualisieren
				dStone.checkRotationVertical(tStone, true);
				draggedStone.updateImage();
				
				return true;	// Der Stein soll oben angelegt werden
			}
			// wenn der Stein obenen einen Nachbarn hat
			else
			{
				dStone.checkRotationVertical(tStone, false);
				draggedStone.updateImage();
				
				return false;	// Der Stein soll unten angelegt werden
			}
		}
		
		// wenn der Stein oben keinen Nachbarn hat und die
		// obere Seite des Zielsteins uebereinstimmt
		if (tStone.getTopNeighbour() == null && equalPip == 1)
		{
			dStone.checkRotationVertical(tStone, true);
			draggedStone.updateImage();
			
			return true;	// Der Stein soll oben angelegt werden
		}
		else
		{
			dStone.checkRotationVertical(tStone, false);
			draggedStone.updateImage();
			
			return false;	// Der Stein soll oben angelegt werden
		}
	}
	
	/**
	 * Ueberprueft fuer das vertikale anlegen, wo ein Stein Nachbarn hat
	 * @param dragged - Der anzulegende Stein
	 * @param target - Der Zielstein
	 * @param snapRight - boolean, ob rechts angelegt werden soll
	 * @return <b>true</b> - der Stein hat an der Zielseite keinen Nachbarn <br>
	 * <b>false</b> - sonst
	 */
	public static boolean checkNeighboursVertical(DominoLabel draggedStone, DominoLabel target, boolean snapTop)
	{
		// wenn oben angelegt werden soll
		if (snapTop)
		{
			// wenn der Zielstein oben keinen Nachbarn hat
			if (target.getStone().getTopNeighbour() == null)
				return true;	// kann oben angelegt werden
			else
				return false;	// sonst kann nicht oben angelegt werden
		}
		else
		{
			// wenn der Zielstein unten keinen Nachbarn hat 
			if (target.getStone().getBottomNeighbour() == null)
				return true;	// kann unten angelegt werden
			else
				return false;	// sonst kann nicht unten angelegt werden
		}
	}
	
	/**
	 * Prueft, welche Seite des Zielsteins mit dem anzulegenden Stein kompatibel ist
	 * @param dragged - Der Stein, der gelegt werden soll
	 * @param target - Der Stein, an den angelegt werden soll
	 * @return <b>0</b> - wenn das Ziel ein Doppelstein ist <br>
	 * <b>1</b> - wenn die linke Seite des Zielsteins mit <br>
	 * einer Seite des zu legenden Steins uebereinstimmt <br> 
	 * <b>2</b> - sonst
	 */
	private static int giveEqualPip(Stone dragged, Stone target)
	{
		if (target.isDoublestone())
			return 0;
		
		if (target.getPips1() == dragged.getPips1() || target.getPips1() == dragged.getPips2())
			return 1;
		else
			return 2;
	}
	
	/**
	 * Prueft, ob vertikal angelegt werden muss
	 * @param target - Der Zielstein
	 * @return <b>true</b> - wenn vertikal angelegt werden muss<br>
	 * <b>false</b> - sonst
	 */
	public static boolean checkIfVertical(DominoLabel target)
	{
		// Stein aus dem Label holen
		Stone tStone = target.getStone();
		
		// wenn der Zielstein ein Spinner ist und horizontal nicht angelegt werden kann
		if (tStone.isSpinner() && tStone.getLeftNeighbour() != null && tStone.getRightNeighbour() != null)
			return true;
		
		// wenn der Zielstein bereits vertikal liegt
		else if (tStone.isVertical())
			return true;
		
		else
			return false;
	}
	
	/**
	 * Prueft, ob ein Stein der von der Hand aufs Feld gebracht wurde an den Stein angelegt<br>
	 * werden kann, über den er gelegt wurde
	 * @param target - Der Zielstein
	 * @param draggedStone - Der Stein der angelegt werden soll
	 * @return - <b>true</b> - wenn angelegt werden kann<br>
	 * <b>false</b> - wenn nicht angelegt werden kann
	 */
	public static boolean checkPossibleMove(Stone target, DominoLabel draggedStone)
	{
		String error = "Dieser Zug ist leider nicht moeglich";
		
		// Nachbarn des Zielsteins zum leichteren Zugriff aus Stein holen
		Stone left = target.getLeftNeighbour();
		Stone right = target.getRightNeighbour();
		Stone top = target.getTopNeighbour();
		Stone bottom = target.getBottomNeighbour();
		
		// wenn der Zielstein horizontal liegt
		if (!target.isVertical())
			if (!target.isSpinner())	// wenn der Zielstein kein Spinner ist	
			{
				// wenn der Zielstein links oder rechts keinen Nachbarn hat
				if (left == null || right == null)
				{
					return true;
				}
				else
				{
					System.out.println(error);
					return false;
				}
			}
			else
			{
				// wenn der Spinner links, rechts, oben oder unten keinen Nachbarn hat
				if (left == null || right == null || top == null || bottom == null)
				{
					return true;
				}
				else
				{
					System.out.println(error);
					return false;
				}
			}
		// wenn der Zielstein vertikal liegt
		else
		{
			// wenn der Zielstein oben und unten keinen Nachbarn hat
			if (top == null || bottom == null)
			{
				return true;
			}
			else
			{
				System.out.println(error);
				return false;
			}
		}
	}
	
	/**
	 * Prueft, ob der angelegte Stein oder der Zielstein ein Spinner werden soll
	 * @param draggedStone - Der angelegte Stein
	 * @param target - Der Stein, an den angelegt wurde
	 * @param hasSpinner - Gibt an, ob bereits ein Spinner auf dem Feld liegt
	 * @return - Den Stein, der der neue Spinner ist oder null
	 */
	public static Stone checkSpinner(DominoLabel draggedStone, DominoLabel target, boolean hasSpinner)
	{
		// wenn das Spiel keinen Spinner hat und der angelegte Stein ein Doppelstein ist
		if (!hasSpinner && draggedStone.getStone().isDoublestone())
		{
			// Im Steinobjekt den Stein zum Spinner machen
			draggedStone.getStone().setSpinner(true);
			return draggedStone.getStone();
		}
		else if (!hasSpinner && target.getStone().isDoublestone())
		{
			target.getStone().setSpinner(true);
			return target.getStone();
		}
		// wenn es schon einen Spinner gibt, oder der keiner der Steine ein Doppelstein ist
		else 
			return null;
	}
	
	/**
	 * Berechnet die Punkte, wenn ein Stein links angelegt wird
	 * @param draggedStone - Der Stein, der angelegt wird
	 * @param target - Der Stein, an den angelegt wird
	 * @param edgePoints - haelt die Punktzahlen der offenen Kanten
	 * @param doublePoints - Gibt an, an welchen Kanten Doppelsteine liegen
	 */
	public static void calculatePointsLeft(DominoLabel draggedStone, DominoLabel target, 
											int[] edgePoints, boolean[] doublePoints)
	{
		// holt sich die Punkte der linken Seite aus dem Stein und schreibt
		// sie das Punkte-Array an den Index 0 fuer die linke Seite
		edgePoints[0] = draggedStone.getStone().getPips1();
		// wenn der angelegte Stein ein Doppelstein ist
		if (draggedStone.getStone().isDoublestone())
			doublePoints[0] = true;	// wird gespeichert, dass links ein Doppelstein ist
		
		// ist der angelegte Stein kein Doppelstein
		else
			doublePoints[0] = false; // wird gespeichert, dass dort kein Doppelstein (mehr) ist
	}
	
	/**
	 * Berechnet die Punkte, wenn ein Stein rechts angelegt wird
	 * @param draggedStone - Der Stein, der angelegt wird
	 * @param target - Der Stein, an den angelegt wird
	 * @param edgePoints - haelt die Punktzahlen der offenen Kanten
	 * @param doublePoints - Gibt an, an welchen Kanten Doppelsteine liegen
	 */
	public static void calculatePointsRight(DominoLabel draggedStone, DominoLabel target,
											int[] edgePoints, boolean[] doublePoints)
	{
		// Kommentare aehnlich calculatePointsLeft
		edgePoints[1] = draggedStone.getStone().getPips2();
		if (draggedStone.getStone().isDoublestone())
			doublePoints[1] = true;
		else 
			doublePoints[1] = false;
	}
	
	/**
	 * Berechnet die Punkte, wenn ein Stein unten angelegt wird
	 * @param draggedStone - Der Stein, der angelegt wird
	 * @param target - Der Stein, an den angelegt wird
	 * @param edgePoints - haelt die Punktzahlen der offenen Kanten
	 * @param doublePoints - Gibt an, an welchen Kanten Doppelsteine liegen
	 */
	public static void calculatePointsBottom(DominoLabel draggedStone, DominoLabel target, 
											int[] edgePoints, boolean[] doublePoints)
	{
		//Kommentare aehnlich calculatePointsLeft
		edgePoints[3] = draggedStone.getStone().getPips2();
		if (draggedStone.getStone().isDoublestone())
			doublePoints[3] = true;
		else
			doublePoints[3] = false;
	}
	
	/**
	 * Berechnet die Punkte, wenn ein Stein oben angelegt wird
	 * @param draggedStone - Der Stein, der angelegt wird
	 * @param target - Der Stein, an den angelegt wird
	 * @param edgePoints - haelt die Punktzahlen der offenen Kanten
	 * @param doublePoints - Gibt an, an welchen Kanten Doppelsteine liegen
	 */
	public static void calculatePointsTop(DominoLabel draggedStone, DominoLabel target, 
											int[] edgePoints, boolean[] doublePoints)
	{
		// Kommentare aehnlich calculatePointsLeft
		edgePoints[2] = draggedStone.getStone().getPips1();
		if (draggedStone.getStone().isDoublestone())
			doublePoints[2] = true;
		else
			doublePoints[2] = false;
	}
	
	/**
	 * Berechnet die Punkte der offenen Kanten mit Beruecksichtigung von Doppelsteinen
	 * @param edgePoints - haelt die Punktzahlen der offenen Kanten
	 * @param doublePoints - Gibt an, an welchen Kanten Doppelsteine liegen
	 * @param firstStone - true uebergeben, wenn diese Methode fuer den ersten Stein
	 * 					   der Runde aufgerufen wird
	 * @return - Die berechnete Punktzahl
	 */
	public static int calculatePoints(int[] edgePoints, boolean[] doublePoints, boolean firstStone)
	{
		// Deklaration und Initialisierung der Rueckgabevariable sowie des Schleifenzaehlers
		int points = 0;
		int i = 0;
		
		// wenn die Methode nicht fuer den ersten Zug aufgerufen wurde 
		if (!firstStone)
		{
			// Schleife durch alle Kantenpunkte
			for (int p: edgePoints)
			{
				// Wenn p eine 7 ist, wurde an diese Kante noch nicht angelegt. Meist oben oder unten
				if (p != 7)
				{
					if (doublePoints[i] == false)
						// Einfache Addition, wenn kein Doppelstein an der Kante liegt
						points += p;
					
					else
						// Verdoppelung der Punkte, wenn an der Kante ein Doppelstein liegt
						points += p*2;
				}
				i++;	// Inkrementieren des Schleifenzaehlers
			}
		}
		// wenn die Methode fuer den ersten Zug einer Runde aufgerufen wurde
		else
			// beim ersten Zug muessen nur die linke und rechte Kante addiert werden;
			return edgePoints[0]+edgePoints[1];
		
		return points;	// Rueckgabe der Punkte
	}

	/**
	 * Prueft, ob ein Stein von der Hand des Spielers auf dem Feld angelegt werden kann
	 * @param stone - Der Stein der gelegt werden soll
	 * @param edgePoints - Das Array, das die Punkte der offenen Enden beinhaltet
	 * @param spinner - Der Spinner, der auf dem Feld liegt. Kann auch <i>null</i> sein
	 * @param playedDominoes - Die Anzahl der bereits gelegten Steine
	 * @return <b>true</b> - wenn der Stein gelegt werden kann <br>
	 * <b>false</b> - wenn der Stein nicht gelegt werden kann
	 */
	public static boolean checkIfDroppable(Stone stone, int[] edgePoints, Stone spinner, int playedDominoes)
	{
		// Deklaration und Initialisierung der Zaehlvariable
		int i = 0;
		
		// wenn noch keine Steine auf dem Feld liegen kann der Stein auf jeden Fall gelegt werden
		if (playedDominoes == 0)
			return true;
		
		// Schleife durch alle Kantenpunkte
		for (int points: edgePoints)
		{
			// wenn es keinen Spinner gibt muessen die vertikalen Kanten nicht geprueft werden
			// wenn i groesser wird als 1 bedeutet das, dass die vertikalen Kanten geprueft werden 
			if (i > 1 && spinner == null)
				break;
			
			// wenn die Punktzahl mit einer der Augenzahlen uebereinstimmt
			if (points == stone.getPips1() || points == stone.getPips2())
				return true;	// der Stein kann gelegt werden
			
			i++;	// Schleifenzaehler inkrementieren
		}
		
		// wenn eine der vertikalen Kanten belegt ist und ein Spinner auf dem Feld liegt
		if ((edgePoints[2] == 7 || edgePoints[3] == 7) && spinner != null)
		{
			// wenn an eine der beiden Kanten des spinners angelegt werden kann (null) 
			if (spinner.getTopNeighbour() == null || spinner.getBottomNeighbour() == null)
			{
				// wenn eine der Augenzahlen des zu legenden Steins mit der Augenzahl des Spinners uebereinstimmt
				if (stone.getPips1() == spinner.getPips1() || stone.getPips2() == spinner.getPips1())
					return true;	// der Stein kann gelegt werden
			}
		}
		
		return false;	// der Stein kann nicht gelegt werden
	}

	/**
	 * Wechselt den aktiven Spieler
	 * @param allPlayers - Array, dass alle Spieler haelt
	 * @param currentPlayerIndex - Index des aktuellen Spielers
	 * @return Gibt den Index des neuen aktuellen Spieler zurueck
	 */
	public static int switchPlayer(Player[] allPlayers, int currentPlayerIndex)
	{
		// wenn die Spieler gewechselt werden hat der aktuelle Spieler seinen ersten Zug gemacht,
		// daher kann die Variable firstMove im Spieler auf false gesetzt werden
		allPlayers[currentPlayerIndex].setfirstMove(false);
		
		// wenn der letzte Spieler am Zug war, ist der erste Spieler an der Reihe
		if ((allPlayers.length - 1) == currentPlayerIndex)
			return 0;
		// sonst ist der naechste Spieler dran
		else
			return ++currentPlayerIndex;
	}
	
	/**
	 * Setzt die Punktzahlen fuer den ersten Stein, der gelegt wurde
	 * @param stone - Der Stein, der gelegt wurde
	 * @param edgePoints - haelt die Punktzahlen der offenen Kanten
	 * @param doublePoints - speichert, an welcher offenen Kante ein Doppelstein liegt
	 */
	public static void firstStone(Stone stone, int[] edgePoints, boolean[] doublePoints)
	{
		// Punktzahlen fuer linke und rechte Kante setzen
		edgePoints[0] = stone.getPips1();
		edgePoints[1] = stone.getPips2();
		
		// wenn der erste Stein ein Doppelstein ist wird gespeichert, dass an der linken
		// Kante ein Doppelstein liegt, weil der zweite Stein rechts angelegt wird
		if (stone.isDoublestone())
		{
			doublePoints[0] = true;
			// da es der erste Doppelstein ist der gelegt wurde ist dieser der Spinner
			stone.setSpinner(true);
		}
	}
	
	/**
	 * 
	 * @param edgePoints - haelt die Punktzahlen der offenen Kanten
	 * @param doublePoints - speichert, an welcher offenen Kante ein Doppelstein liegt
	 * @param player - der Spieler fuer den die Punkte berechnet werden sollen
	 * @param firstMove - true, wenn die Methode fuer den ersten Zug aufgerufen wird
	 * @return <b>true</b> - wenn der Spieler Punkte bekommen hat<br>
	 * <b>false</b> - wenn der Spieler keine Punkte bekommen hat
	 */
	public static boolean calculatePlayerPoints(int[] edgePoints, boolean[] doublePoints
												, Player player, boolean firstMove)
	{
		int points = calculatePoints(edgePoints, doublePoints, firstMove);
		
		// wenn die Punkte glatt durch 5 teilbar sind...
		if (points % 5 == 0)
		{
			// ...werden die Punkte des Spielers um diesen Wert erhoeht 
			player.increasePoints(points);
			return true;
		}
		
		return false;
	}

	/**
	 * Prueft welcher Spieler eine Runde gewinnt
	 * @param allPlayers - Array, das alle Spieler haelt
	 * @param returnPoints - wenn true uebergeben wird, werden die Punkte zurueckgegeben
	 * @return wenn returnPoints true - Der Index des Gewinners<br>  
	 * wenn returnPoints false - Die Punkte, die der Gewinner gutgeschrieben bekommt
	 */
	public static int calculateRoundPoints(Player[] allPlayers, boolean returnPoints)
	{
		// vor der Pruefung gewinnt Spieler 1
		int winner = 0;
		int loser = 1;
		
		// wenn Spieler 1 mehr Steine auf der Hand hat als Spieler 2...
		if (allPlayers[0].getHand().size() > allPlayers[1].getHand().size())
		{
			// gewinnt Spieler 2 die Runde
			winner = 1;
			loser = 0;
		}
		
		// die Werte aller Steine auf der Hand des Verlierers werden aufsummiert 
		int winnerPoints = calculateHandPoints(allPlayers[loser].getHand());
		
		// Rueckgabe des jeweiligen Wertes abhaengig von returnPoints
		if (returnPoints)
			return winnerPoints;	// Punkte des Gewinners
		else
		{
			// nur wenn returnPoints false ist werden dem Spieler die Punkte gutgeschrieben
			allPlayers[winner].increasePoints(winnerPoints);
			return winner;	//	der Index des Gewinners
		}
	}

	/**
	 * Berechnet die Summe der Werte aller Steine auf der Hand eines Spielers
	 * @param hand - Die Hand eines Spielers
	 * @return Die berechnete Summe
	 */
	private static int calculateHandPoints(ArrayList<Stone> hand)
	{
		int points = 0;
		
		for (Stone s: hand)
			points += s.getValue();
		
		while (points % 5 != 0)
			points += 1;
			
		return points;
	}
}