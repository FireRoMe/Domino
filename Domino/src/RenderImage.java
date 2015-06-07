import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class RenderImage
{
	private BufferedImage img;
	private double degrees;
	private Dimension pos;
	private Dimension size;
	
	public BufferedImage getImg()
	{
		return img;
	}
	
	public void setImg(BufferedImage img)
	{
		this.img = img;
	}
	
	public double getDegrees()
	{
		return degrees;
	}
	
	public void setDegrees(double degrees)
	{
		this.degrees = degrees;
	}
	
	public Dimension getPos()
	{
		return pos;
	}
	
	public void setPos(Dimension pos)
	{
		this.pos = pos;
	}
	
	public Dimension getSize()
	{
		return size;
	}
	
	public void setSize(Dimension size)
	{
		this.size = size;
	}
}
