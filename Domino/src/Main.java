import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;


public class Main
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("TestFenster");
		Container pane = new Container();
		
		pane.setBackground(Color.GREEN);
		frame.setBounds(400, 300, 800, 600);
		frame.setContentPane(pane);
		frame.setVisible(true);
	}

}
