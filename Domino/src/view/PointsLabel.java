package view;

import javax.swing.JLabel;

public class PointsLabel extends JLabel
{
	private static final long serialVersionUID = 1L;
	private int[] points = new int[4];
	
	public PointsLabel(String string)
	{
		this.setText(string);
	}

	public int[] getPoints()
	{
		return points;
	}
	
	public void setPoints(int[] points)
	{
		if (points.length == 4)
		{
			this.points = points;
		}
	}
}
