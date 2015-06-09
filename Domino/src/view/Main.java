package view;

import control.DominoGame;


public class Main
{

	public static void main(String[] args)
	{
		long time1 = System.currentTimeMillis();
		
		MainWindow view = new MainWindow();
		view.textOut("Zwischenzeit: " + (System.currentTimeMillis() - time1));
		new DominoGame(view, 3);
		
		view.textOut("Ladezeit: " + (System.currentTimeMillis() - time1));
	}

}
