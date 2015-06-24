package view;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import data.Stone;

/**
 * Ein JLabel, dass die Dominosteine des Spiels halten kann 
 */
public class DominoLabel extends JLabel
{
	private static final long serialVersionUID = 1L;
	private Stone stone;
	private boolean draggable = true;
	
	public DominoLabel(Stone stone)
	{
//		System.out.println("Konstruktor Domino");
		this.stone = stone;
		updateImage();
	}
	
	public Stone getStone()
	{
		return stone;
	}
	
	public boolean isDraggable()
	{
		return draggable;
	}

	public void setNotDraggable()
	{
		this.draggable = false;
	}

	public void updateImage()
	{
		this.setIcon(new ImageIcon(this.stone.getIcon()));
		this.setSize(this.getIcon().getIconWidth(), this.getIcon().getIconHeight());
	}
}