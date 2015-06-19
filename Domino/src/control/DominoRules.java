package control;

import java.awt.Point;

import data.Stone;
import view.DominoLabel;

public final class DominoRules
{
	public static boolean checkCompatibility(DominoLabel dragged, DominoLabel target)
	{
		int d_p1 = dragged.getStone().getPips1();
		int d_p2 = dragged.getStone().getPips2();
		int t_p1 = target.getStone().getPips1();
		int t_p2 = target.getStone().getPips2();
		
		if (d_p1 == t_p1 || d_p1 == t_p2 || d_p2 == t_p1 || d_p2 == t_p2)
		{
			if (!target.getStone().isDoublestone())
			{
				//TODO
			}
			else
			{
				//TODO
			}
			return true;
		}
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
	
	public static void calculatePointsLeft(DominoLabel draggedStone, DominoLabel target, int[] edgePoints)
	{
		if (!draggedStone.getStone().isDoublestone())
			edgePoints[0] = draggedStone.getStone().getPips1();
		else
			edgePoints[0] = draggedStone.getStone().getValue();
		
		if (target.getStone().getRightNeighbour() == null)
		{
			if (!target.getStone().isDoublestone())
				edgePoints[1] = target.getStone().getPips2();
			else
				edgePoints[1] = target.getStone().getValue();
		}
	}

	public static void calculatePointsRight(DominoLabel draggedStone, DominoLabel target, int[] edgePoints)
	{
		if (!draggedStone.getStone().isDoublestone())
			edgePoints[1] = draggedStone.getStone().getPips2();
		else
			edgePoints[1] = draggedStone.getStone().getValue();
		
		if (target.getStone().getLeftNeighbour() == null)
		{
			if (!target.getStone().isDoublestone())
				edgePoints[0] = target.getStone().getPips1();
			else
				edgePoints[0] = target.getStone().getValue();
		}
	}

	public static void calculatePointsBottom(DominoLabel draggedStone, DominoLabel target, int[] edgePoints)
	{
		if (!draggedStone.getStone().isDoublestone())
			edgePoints[3] = draggedStone.getStone().getPips2();
		else
			edgePoints[3] = draggedStone.getStone().getValue();
		
		if (target.getStone().getTopNeighbour() == null)
		{
			if (!target.getStone().isDoublestone())
				edgePoints[2] = target.getStone().getPips1();
			else
				edgePoints[2] = target.getStone().getValue();
		}
	}

	public static void calculatePointsTop(DominoLabel draggedStone, DominoLabel target, int[] edgePoints)
	{
		if (!draggedStone.getStone().isDoublestone())
			edgePoints[2] = draggedStone.getStone().getPips1();
		else
			edgePoints[2] = draggedStone.getStone().getValue();
		
		if (target.getStone().getBottomNeighbour() == null)
		{
			if (!target.getStone().isDoublestone())
				edgePoints[3] = target.getStone().getPips2();
			else
				edgePoints[3] = target.getStone().getValue();
		}
	}
}
