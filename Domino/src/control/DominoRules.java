package control;

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
				
			}
			else
			{
				
			}
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Prueft, ob an der rechten Seite des Steins angelegt werden kann
	 * @param dragged - Der Stein, der gelegt werden soll
	 * @param target - Der Stein, an den angelegt werden soll
	 * @return false - true, wenn 
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
				
				return true;
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
	 * @return <b>1</b> - wenn die linke Seite des Zielsteins mit <br>
	 * einer Seite des zu legenden Steins uebereinstimmt <br> 
	 * <b>2</b> - sonst
	 * Ist das Ziel ein Doppelstein wird immer <b>2</b> zurueckgegeben
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
		
		if (tStone.isSpinner() && (tStone.getLeftNeighbour() != null && tStone.getRightNeighbour() != null))
			return true;
		
		else if (tStone.isVertical())
			return true;
		
		else
			return false;
	}
}
