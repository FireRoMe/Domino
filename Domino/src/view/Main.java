package view;

import control.DominoGame;

/**
 * Main Domino 
 */
public class Main
{
	public static void main(String[] args)
	{
		MainWindow view = new MainWindow();
		new DominoGame(view, 2);
	}
}