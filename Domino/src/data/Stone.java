package data;
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
	private boolean doublestone;
	private boolean spinner;
	private boolean vertical;
	private Image icon;
	private Stone leftNeighbour;
	private Stone rightNeighbour;
	private Stone topNeighbour;
	private Stone bottomNeighbour;
	private Dimension imageSize;
	private Player player;
	
	public Stone(int pips1, int pips2, Dimension imageSize)
	{
		this.pips1 = pips1;
		this.pips2 = pips2;
		this.imageSize = imageSize;
		
		calculateValue();
		setDoublestone();
	}

	public void loadIcon()
	{
		BufferedImage image;
		
		//String fileName = new String("ImageSrc/transparenzTest" + pips1 + "_" + pips2 + ".png");
		//String fileName = new String("ImageSrc/transparenzTest" + 2 + "_" + 4 + "_new" + ".png");
		String fileName = new String("ImageSrc/" + pips1 + "_" + pips2 + ".png");
		System.out.println("Dateiname: " + fileName);
		try {
			//if (pips1 == 2 && pips2 == 4)		// TODO passendes Bild raussuchen
			//{
				image = ImageIO.read(new File(fileName));
				
				icon = image.getScaledInstance(imageSize.width, imageSize.height, Image.SCALE_AREA_AVERAGING);
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
	
	public int getPips2()
	{
		return pips2;
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
	
	public Image getIcon()
	{
		return icon;
	}
	
	public void setPlayer(Player p)
	{
		if (p != null)
			this.player = p;
	}
	
	public Player getPlayer()
	{
		return player;
	}
}
