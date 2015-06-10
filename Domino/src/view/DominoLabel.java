package view;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import data.Stone;

public class DominoLabel extends JLabel
{
	private static final long serialVersionUID = 1L;
	private Stone stone;
	
	public DominoLabel(Stone stone)
	{
		System.out.println("Konstruktor Domino");
		this.stone = stone;
		this.setIcon(new ImageIcon(this.stone.getIcon()));
		this.setSize(this.getIcon().getIconWidth(), this.getIcon().getIconHeight());
	}
}
