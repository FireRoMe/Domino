package view;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class RenderImage
{
	private BufferedImage img;
	private double degrees;
	private Dimension pos;
	private Dimension size;
	
	public RenderImage(BufferedImage img, double degrees, Dimension pos, Dimension size)
	{
		this.img = img;
		this.degrees = degrees;
		this.pos = pos;
		this.size = size;
	}
	
	public BufferedImage getImg()
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
