package data;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Der Dominostein 
 */
public class Stone
{
	/** linke bzw. obere Augenzahl */
	private int pips1 = 36;
	/** rechte bzw. untere Augenzahl */
	private int pips2 = 36;
	/** Wert des Steins */
	private int value;
	/** Doppelstein ja/nein */
	private boolean doublestone;
	/** Spinner ja/nein */
	private boolean spinner;
	/** Vertikal gelegt ja/nein */
	private boolean vertical;
	/** Das Bild des Dominosteins */
	private Image icon;
	/** Das Originalbild */
	private BufferedImage rawImage;
	/** linker Nachbar */
	private Stone leftNeighbour;
	/** rechter Nachbar */
	private Stone rightNeighbour;
	/** oberer Nachbar */
	private Stone topNeighbour;
	/** unterer Nachbar */
	private Stone bottomNeighbour;
	/** Groesse des Bildes auf dem Stein */
	private Dimension imageSize;
	/** Der Spieler, der den Stein zuerst auf der Hand haelt */
	private Player player;
	
	/**
	 * Konstruktor Dominostein
	 * @param pips1 - Augenzahl 1
	 * @param pips2 - Augenzahl 2
	 * @param imageSize - Bildgroesse
	 */
	public Stone(int pips1, int pips2, Dimension imageSize)
	{
		this.pips1 = pips1;
		this.pips2 = pips2;
		this.imageSize = imageSize;
		
		// Wert des Steins berechnen
		calculateValue();
		// Pruefen, ob der Stein ein Doppelstein ist
		setDoublestone();
	}

	public void loadIcon()
	{
		BufferedImage image;
		
		// Es wird ein String erzeugt, der auf die passende Bilddatei verweist
		String fileName = new String("ImageSrc/" + "new" + pips1 + "_" + pips2 + ".png");
		System.out.println("Dateiname: " + fileName);
		
		// Das Bild laden und eventuellen Fehler ausgeben
		try {
			image = ImageIO.read(new File(fileName));
			rawImage = image;
			icon = Toolkit.getDefaultToolkit().createImage(image.getSource());
		} catch (IOException e) {
			System.err.println("Bild: " + fileName + " konnte nicht geladen werden");
			e.printStackTrace();
		}
	}
	
	public void rotateImage(int angle)
	{
		/* Das Bild wird bei jedem Schleifendurchlauf um
		90 Grad gedreht */
		int z = angle / 90;
		for (int i = 1; i<=z; i++)
		{
			double degrees = 90;
			
			double radians = Math.toRadians(degrees);
			double sin = Math.abs(Math.sin(radians));
			double cos = Math.abs(Math.cos(radians));
			// Berechnungen zur neuen Breite und Hoehe des Steins
			int newWidth = (int)Math.round(icon.getWidth(null) 
											* cos + icon.getHeight(null) * sin);
			int newHeight = (int)Math.round(icon.getWidth(null) 
											* sin + icon.getHeight(null) * cos);
			// Bild auf Stein mittig ausrichten
			int x = (newWidth - icon.getWidth(null)) / 2;
			int y = (newHeight - icon.getHeight(null)) / 2;
			
			System.out.println("Breite: " + newWidth + ", Hoehe: " + newHeight);
			
			/* neues BufferedImage erstellen, auf das das rotierte Bild
			gezeichnet werden soll */
			BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight,
														BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = rotatedImage.createGraphics();
			AffineTransform at = new AffineTransform();
			
			// Bild rotieren
			at.setToRotation(radians, x + (icon.getWidth(null) / 2), y 
											+ (icon.getHeight(null) / 2));
			// Bild verschieben
			at.translate(x, y);
			// Drehung und Verschiebung ausfuehren
			g2d.setTransform(at);
			// Transformiertes Bild zeichnen
			g2d.drawImage(rawImage, 0, 0, null);
			g2d.dispose();
			
			// Originalbild speichern (zum erneuten rotieren)
			rawImage = rotatedImage;
			
			// Das Icon fuer das DominLabel aus dem rotierten Bild erstellen 
			icon = Toolkit.getDefaultToolkit().createImage(rotatedImage.getSource());
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

	/**
	 * Ueberprueft, ob ein Stein horizontal gespiegelt werden muss
	 * @param target - der Stein, an den angelegt werden soll
	 * @param snapRight - ob der Stein rechts angelegt werden soll
	 * @return <b>true</b> - wenn der Stein gedreht wurde
	 */
	public boolean checkRotationHorizontal(Stone target, boolean snapRight)
	{
		// wenn der Stein rechts angelegt werden soll
		if (snapRight)
		{
			/* wenn die Augenzahlen nicht uebereinstimmen
			muss der Stein gespiegelt werden*/
			if (pips1 != target.getPips2())
			{
				// Drehung um 180 Grad
				rotateImage(180);
				// Augenzahlen tauschen
				togglePips();
				
				return true;
			}
			else 
				return false;
		}
		// wenn der Stein links angelegt werden soll
		else
		{
			if (pips2 != target.getPips1())
			{
				rotateImage(180);
				togglePips();
				
				return true;
			}
			else 
				return false;
		}
	}
	
	/**
	 * Ueberprueft, ob ein Stein vertikal gespiegelt werden muss
	 * @param target - der Stein, an den angelegt werden soll
	 * @param snapRight - ob der Stein rechts angelegt werden soll
	 * @return <b>true</b> - wenn der Stein gedreht wurde
	 */
	public boolean checkRotationVertical(Stone target, boolean snapTop)
	{
		setVertical(true);
		rotateImage(90);
		if (snapTop)
		{
			if (pips2 != target.getPips1())
			{
				rotateImage(180);
				togglePips();
				
				return true;
			}
			else
				return false;
		}
		else
		{
			if (pips1 != target.getPips2())
			{
				rotateImage(180);
				togglePips();
				
				return true;
			}
			else
				return false;
		}
	}
	
	/**
	 * Augenzahlen tauschen
	 */
	private void togglePips()
	{
		int temp = pips1;
		pips1 = pips2;
		pips2 = temp;
	}

	public Stone getLeftNeighbour()
	{
		return leftNeighbour;
	}
	
	public void setLeftNeighbour(final Stone s)
	{
		leftNeighbour = s;
	}

	public Stone getRightNeighbour()
	{
		return rightNeighbour;
	}

	public void setRightNeighbour(final Stone s)
	{
		this.rightNeighbour = s;
	}

	public Stone getTopNeighbour()
	{
		return topNeighbour;
	}

	public void setTopNeighbour(final Stone s)
	{
		this.topNeighbour = s;
	}

	public Stone getBottomNeighbour()
	{
		return bottomNeighbour;
	}

	public void setBottomNeighbour(final Stone s)
	{
		this.bottomNeighbour = s;
	}
	
	public void setSpinner(boolean isSpinner)
	{
		this.spinner = isSpinner;
	}
	
	public boolean isSpinner()
	{
		return spinner;
	}
	
	public void setVertical (boolean isVertical)
	{
		this.vertical = isVertical;
	}
	
	public boolean isVertical()
	{
		return vertical;
	}
}