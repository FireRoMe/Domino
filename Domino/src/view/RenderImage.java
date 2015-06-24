package view;
import java.awt.Dimension;
import java.awt.Image;

/**
 * Sollte die Bilder darstellen
 * Zum loeschen vorgesehen 
 */
public class RenderImage
{
	private Image img;
	private double degrees;
	private Dimension pos;
	private Dimension size;
	
	public RenderImage(Image img, double degrees, Dimension pos, Dimension size)
	{
		this.img = img;
		this.degrees = degrees;
		this.pos = pos;
		this.size = size;
	}
	
	public Image getImg()
	{
		return img;
	}
	
	public double getDegrees()
	{
		return degrees;
	}
	
	public Dimension getPos()
	{
		return pos;
	}
	
	public Dimension getSize()
	{
		return size;
	}
}
