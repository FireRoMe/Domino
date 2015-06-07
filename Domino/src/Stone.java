import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Stone
{
	private int pips1 = 36;
	private int pips2 = 36;
	private int value;
	private boolean edge_left;
	private boolean edge_right;
	private boolean edge_top;
	private boolean edge_bottom;
	private boolean doublestone;
	private boolean spinner;
	private BufferedImage icon;
	private Stone leftNeighbour;
	private Stone rightNeighbour;
	private Dimension position;
	
	public Stone()
	{
	}

	public void loadIcon()
	{
		BufferedImage image;
		
		//String fileName = new String("ImageSrc/transparenzTest" + pips1 + "_" + pips2 + ".png");
		String fileName = new String("ImageSrc/transparenzTest" + 2 + "_" + 4 + ".png");
		System.out.println("Dateiname: " + fileName);
		try {
			//if (pips1 == 2 && pips2 == 4)		// TODO passendes Bild raussuchen
			//{
				image = ImageIO.read(new File(fileName));
			
				icon = image;
			//}
		} catch (IOException e) {
			System.err.println("Fehler!");
			e.printStackTrace();
		}
	}
	
	public int getPips1()
	{
		return pips1;
	}
	
	public void setPips1(int augenzahl1)
	{
		this.pips1 = augenzahl1;
	}
	
	public int getPips2()
	{
		return pips2;
	}
	
	public void setPips2(int augenzahl2)
	{
		this.pips2 = augenzahl2;
		
		if (pips1 != 36)
			calculateValue();
			setDoublestone();
	}

	public int getValue()
	{
		return value;
	}

	private void calculateValue()
	{
		value = pips1+pips2;
	}

	public boolean isDoublestone()
	{
		return doublestone;
	}

	private void setDoublestone()
	{
		if (pips1 == pips2)
			this.doublestone = true;
	}
	
	public BufferedImage getIcon()
	{
		return icon;
	}
}
