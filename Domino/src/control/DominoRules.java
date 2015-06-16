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

	public static boolean snapRight(DominoLabel draggedStone, DominoLabel target)
	{
		Stone dStone = draggedStone.getStone();
		Stone tStone = target.getStone();
		
		if (tStone.getPips2() == dStone.getPips1())
		{
			if(tStone.getRightNeighbour() == null)
				return true;
			else
				return false;
		}	
			
		else
			return true; //TODO
	}
}
