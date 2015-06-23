package control;

import java.awt.Point;
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
		
		if (d_p1 == t_p1 || d_p1 == t_p2 || d_p2 == t_p1 || d_p2 == t_p2)
			return true;
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
		Stone dStone = draggedStone.getStone();
		Stone tStone = target.getStone();
		int equalPip = giveEqualPip(dStone, tStone);
		
		if (equalPip == 0)
		{
			if (tStone.getRightNeighbour() == null)
			{
				dStone.checkRotationHorizontal(tStone, true);
				draggedStone.updateImage();
				
				return true;
			}
			else
			{
				dStone.checkRotationHorizontal(tStone, false);
				draggedStone.updateImage();
				
				return false;
			}
		}
		
		if(tStone.getRightNeighbour() == null && equalPip == 2)
		{
			if (tStone.getPips2() != dStone.getPips1())
			{
				dStone.checkRotationHorizontal(target.getStone(), true);
				draggedStone.updateImage();
			}
			
			return true;
		}	
		else
		{
			if (tStone.getPips1() != dStone.getPips2())
			{
				dStone.checkRotationHorizontal(target.getStone(), false);
				draggedStone.updateImage();
			}
			
			return false;
		}
	}
	
	public static boolean checkNeighboursHorizontal(DominoLabel dragged, DominoLabel target, boolean snapRight)
	{
		if (snapRight)
		{
			if (target.getStone().getRightNeighbour() == null)
				return true;
			else
				return false;
		}
		else
		{
			if (target.getStone().getLeftNeighbour() == null)
				return true;
			else
				return false;
		}
	}
	
	public static boolean snapTop(DominoLabel draggedStone, DominoLabel target)
	{
		Stone dStone = draggedStone.getStone();
		Stone tStone = target.getStone();
		int equalPip = giveEqualPip(dStone, tStone);
		
		if (equalPip == 0)
		{
			if (tStone.getTopNeighbour() == null)
			{
				dStone.checkRotationVertical(tStone, true);
				draggedStone.updateImage();
				
				return true;
			}
			else
			{
				dStone.checkRotationVertical(tStone, false);
				draggedStone.updateImage();
				
				return false;
			}
		}
		
		if (tStone.getTopNeighbour() == null && equalPip == 1)
		{
			dStone.checkRotationVertical(tStone, true);
			draggedStone.updateImage();
			
			return true;
		}
		else
		{
			dStone.checkRotationVertical(tStone, false);
			draggedStone.updateImage();
			
			return false;
		}
	}

	public static boolean checkNeighboursVertical(DominoLabel draggedStone, DominoLabel target, boolean snapTop)
	{
		if (snapTop)
		{
			if (target.getStone().getTopNeighbour() == null)
				return true;
			else
				return false;
		}
		else
		{
			if (target.getStone().getBottomNeighbour() == null)
				return true;
			else
				return false;
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

	public static boolean checkIfVertical(DominoLabel target)
	{
		Stone tStone = target.getStone();
		
		if (tStone.getLeftNeighbour() != null && tStone.getRightNeighbour() != null)
		{
			System.err.println("checkIFVertical: \n\r"
					+ "Spinner: " + tStone.isSpinner() + "\n\r"
					+ "Links: " + tStone.getLeftNeighbour().getPips1() + "|" + tStone.getLeftNeighbour().getPips2() + "\n\r"
					+ "Rechts: " + tStone.getRightNeighbour().getPips1() + "|" + tStone.getRightNeighbour().getPips2());
		}
		else
			System.err.println("Kein Spinner");
		
		if (tStone.isSpinner() && tStone.getLeftNeighbour() != null && tStone.getRightNeighbour() != null)
			return true;
		
		else if (tStone.isVertical())
			return true;
		
		else
			return false;
	}
	
	public static boolean checkPossibleMove(Stone target, DominoLabel draggedStone, Point errorPoint)
	{
		String error = "Dieser Zug ist leider nicht moeglich";
		
		Stone left = target.getLeftNeighbour();
		Stone right = target.getRightNeighbour();
		Stone top = target.getTopNeighbour();
		Stone bottom = target.getBottomNeighbour();
		
		if (!target.isVertical())
			if (!target.isSpinner())
			{
				if (left == null || right == null)
				{
					return true;
				}
				else
				{
					System.out.println(error);
//					draggedStone.setLocation(errorPoint);
					return false;
				}
			}
			else
			{
				if (left == null || right == null || top == null || bottom == null)
				{
					return true;
				}
				else
				{
					System.out.println(error);
//					draggedStone.setLocation(errorPoint);
					return false;
				}
			}
		else
		{
			if (top == null || bottom == null)
			{
				return true;
			}
			else
			{
				System.out.println(error);
//				draggedStone.setLocation(errorPoint);
				return false;
			}
		}
	}
	
	public static Stone checkSpinner(DominoLabel draggedStone, DominoLabel target, boolean hasSpinner)
	{
		if (!hasSpinner && draggedStone.getStone().isDoublestone())
		{
			draggedStone.getStone().setSpinner(true);
			return draggedStone.getStone();
		}
		else if (!hasSpinner && target.getStone().isDoublestone())
		{
			target.getStone().setSpinner(true);
			return target.getStone();
		}
		else 
			return null;
	}
	
	/**
	 * Berechnet die Punkte, wenn ein Stein links angelegt wird
	 * @param draggedStone - Der Stein, der angelegt wird
	 * @param target - Der Stein, an den angelegt wird
	 * @param edgePoints - Die Punkte, die vom Spiel übergeben werden
	 * @param doublePoints - Gibt an, wo ein Doppelstein liegt
	 */
	public static void calculatePointsLeft(DominoLabel draggedStone, DominoLabel target, int[] edgePoints, boolean[] doublePoints)
	{
		edgePoints[0] = draggedStone.getStone().getPips1();
		if (draggedStone.getStone().isDoublestone())
		{
			doublePoints[0] = true;
//			if (edgePoints[2] == 0 && edgePoints[3] == 0)
//			{
//				edgePoints[2] = draggedStone.getStone().getPips1();
//				edgePoints[3] = draggedStone.getStone().getPips1();
//			}
		}
		else
			doublePoints[0] = false;
		
		if (target.getStone().getRightNeighbour() == null)
		{
			edgePoints[1] = target.getStone().getPips2();
			if (target.getStone().isDoublestone())
			{
				doublePoints[1] = true;
//				if (edgePoints[2] == 0 && edgePoints[3] == 0)
//				{
//					edgePoints[2] = target.getStone().getPips2();
//					edgePoints[3] = target.getStone().getPips2();
//				}
			}
			else
				doublePoints[1] = false;
		}
	}
	
	/**
	 * Berechnet die Punkte, wenn ein Stein rechts angelegt wird
	 * @param draggedStone - Der Stein, der angelegt wird
	 * @param target - Der Stein, an den angelegt wird
	 * @param edgePoints - Die Punkte, die vom Spiel übergeben werden
	 * @param doublePoints - Gibt an, wo ein Doppelstein liegt
	 */
	public static void calculatePointsRight(DominoLabel draggedStone, DominoLabel target, int[] edgePoints, boolean[] doublePoints)
	{
		edgePoints[1] = draggedStone.getStone().getPips2();
		if (draggedStone.getStone().isDoublestone())
		{
			doublePoints[1] = true;
//			if (edgePoints[2] == 0 && edgePoints[3] == 0)
//			{
//				edgePoints[2] = draggedStone.getStone().getPips2();
//				edgePoints[3] = draggedStone.getStone().getPips2();
//			}
		}
		else 
			doublePoints[1] = false;
		
		if (target.getStone().getLeftNeighbour() == null)
		{
			edgePoints[0] = target.getStone().getPips1();
			if (target.getStone().isDoublestone())
			{
				doublePoints[0] = true;
//				if (edgePoints[2] == 0 && edgePoints[3] == 0)
//				{
//					edgePoints[2] = target.getStone().getPips2();
//					edgePoints[3] = target.getStone().getPips2();
//				}
			}
			else
				doublePoints[0] = false;
		}
	}
	
	/**
	 * Berechnet die Punkte, wenn ein Stein unten angelegt wird
	 * @param draggedStone - Der Stein, der angelegt wird
	 * @param target - Der Stein, an den angelegt wird
	 * @param edgePoints - Die Punkte, die vom Spiel übergeben werden
	 * @param doublePoints - Gibt an, wo ein Doppelstein liegt
	 */
	public static void calculatePointsBottom(DominoLabel draggedStone, DominoLabel target, int[] edgePoints, boolean[] doublePoints)
	{
		edgePoints[3] = draggedStone.getStone().getPips2();
		if (draggedStone.getStone().isDoublestone())
			doublePoints[3] = true;
		else
			doublePoints[3] = false;
		
		if (target.getStone().getTopNeighbour() == null)
		{
			edgePoints[2] = target.getStone().getPips1();
			if (target.getStone().isDoublestone() && !target.getStone().isSpinner())
				doublePoints[2] = true;
			else
				doublePoints[2] = false;
		}
	}
	
	/**
	 * Berechnet die Punkte, wenn ein Stein oben angelegt wird
	 * @param draggedStone - Der Stein, der angelegt wird
	 * @param target - Der Stein, an den angelegt wird
	 * @param edgePoints - Die Punkte, die vom Spiel übergeben werden
	 * @param doublePoints - Gibt an, wo ein Doppelstein liegt
	 */
	public static void calculatePointsTop(DominoLabel draggedStone, DominoLabel target, int[] edgePoints, boolean[] doublePoints)
	{
		edgePoints[2] = draggedStone.getStone().getPips1();
		if (draggedStone.getStone().isDoublestone())
			doublePoints[2] = true;
		else
			doublePoints[2] = false;
		 
		if (target.getStone().getBottomNeighbour() == null && !target.getStone().isSpinner())
		{
			edgePoints[3] = target.getStone().getPips2();
			if (target.getStone().isDoublestone())
				doublePoints[3] = true;
			else
				doublePoints[3] = false;
		}
	}
	
	public static int calculatePoints(int[] edgePoints, boolean[] doublePoints, boolean firstStone)
	{
		int points = 0;
		int i = 0;
		
		if (!firstStone)
		{
			for (int p: edgePoints)
			{
				if (p != 7)		// Wenn p eine 7 ist, wurde an diese Kante noch nicht angelegt. Meist oben oder unten
				{
					if (doublePoints[i] == false)
						points += p;
					else
					{
		//				if (i != 0 && i != 1)
							points += p*2;
					}
				}
				
				i++;
			}
		}
		else
		{
			return edgePoints[0]+edgePoints[1];
		}
		return points;
	}

	/**
	 * Prueft, ob ein Stein von der Hand des Spielers auf dem Feld angelegt werden kann
	 * @param stone - Der Stein der gelegt werden soll
	 * @param edgePoints - Das Array, das die Punkte der offenen Enden beinhaltet
	 * @return <b>true</b> - wenn der Stein gelegt werden kann <br>
	 * <b>false</b> - wenn der Stein nicht gelegt werden kann
	 */
	public static boolean checkIfDroppable(Stone stone, int[] edgePoints, Stone spinner, int playedDominoes)
	{
		int i = 0;
		
		if (playedDominoes == 0)
			return true;
		
		for (int points: edgePoints)
		{
			System.out.println("Pruefstein: " + stone.getPips1() + ", " + stone.getPips2());
			if (i > 1 && spinner == null)	// Wenn es keinen Spinner gibt muessen nicht alle Moeglichkeiten geprueft werden
				break;
			
			if (points == stone.getPips1() || points == stone.getPips2())
				return true;
			
			i++;
		}
		
		if ((edgePoints[2] == 7 || edgePoints[3] == 7) && spinner != null)
		{
			if (spinner.getTopNeighbour() == null || spinner.getBottomNeighbour() == null)
			{
				if (stone.getPips1() == spinner.getPips1() || stone.getPips2() == spinner.getPips1())
					return true;
			}
		}
		
		return false;
	}

	public static int switchPlayer(Player[] allPlayers, int currentPlayerIndex)
	{
		allPlayers[currentPlayerIndex].setfirstMove(false);
		
		if ((allPlayers.length - 1) == currentPlayerIndex)
			return 0;
		else
			return ++currentPlayerIndex;
	}

	public static void firstStone(Stone stone, int[] edgePoints, boolean[] doublePoints)
	{
		edgePoints[0] = stone.getPips1();
		edgePoints[1] = stone.getPips2();
		
		if (stone.isDoublestone())
		{
			doublePoints[0] = true;
			stone.setSpinner(true);
		}
	}
	
	public static boolean calculatePlayerPoints(int[] edgePoints, boolean[] doublePoints, Player player)
	{
		int points = calculatePoints(edgePoints, doublePoints, false);
		
		if (points % 5 == 0)
		{
			player.increasePoints(points);
			return true;
		}
		
		return false;
	}

	public static boolean calculatePlayerPoints(int points, Player player)
	{
		if (points % 5 == 0)
		{
			player.increasePoints(points);
			return true;
		}
		
		return false;
	}

	public static int calculateRoundPoints(Player[] allPlayers, boolean returnPoints)
	{
		int winner = 0;
		int loser = 1;
		
		if (allPlayers[0].getHand().size() > allPlayers[1].getHand().size())
		{
			winner = 1;
			loser = 0;
		}
		
		int winnerPoints = calculateHandPoints(allPlayers[loser].getHand());
		
		if (returnPoints)
			return winnerPoints;
		else
		{
			allPlayers[winner].increasePoints(winnerPoints);
			return winner;
		}
	}

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