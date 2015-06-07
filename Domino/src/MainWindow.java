import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainWindow
{
	private PaintingComponent paint = new PaintingComponent();
	private Stone[] allStones = new Stone[28];
	private ArrayList<RenderImage> renderedImages = new ArrayList<RenderImage>();
	
	public MainWindow()
	{
		initializeWindow();
	}
	
	public void initializeWindow()
	{
		JFrame frame = new JFrame("TestFenster");
		Container contentPane = frame.getContentPane();
		
		initializeDominoes();
		
		BufferedImage img = null;
		BufferedImage imgTP = null;
		BufferedImage imgTP2 = null;
		
		ImageIcon iIcon1 = new ImageIcon();
		ImageIcon iIcon2 = new ImageIcon();
		ImageIcon iIcon3 = new ImageIcon();
		ImageIcon iIcon4 = new ImageIcon();
		
		try {
			img = ImageIO.read(new File("ImageSrc/SteinTest.jpg"));
			imgTP = ImageIO.read(new File("ImageSrc/transparenzTest3.png"));
			imgTP2 = ImageIO.read(new File("ImageSrc/transparenzTest2.png"));
			
			BufferedImage steinIcon = allStones[15].getIcon();
			
			iIcon1.setImage(img.getScaledInstance(50, 100, Image.SCALE_DEFAULT));
			iIcon2.setImage(imgTP.getScaledInstance(75, 150, Image.SCALE_DEFAULT));
			//iIcon3.setImage(imgTP2.getScaledInstance(36, 75, Image.SCALE_DEFAULT));
			iIcon4.setImage(steinIcon.getScaledInstance(75, 150, Image.SCALE_DEFAULT));
			
			JLabel imageLabel = new JLabel(iIcon1);
			JLabel imageLabel2 = new JLabel(iIcon2);
			JLabel imageLabel3 = new JLabel(iIcon4);
			
			contentPane.add(paint);
			contentPane.add(imageLabel);
			//contentPane.add(imageLabel2);
			//contentPane.add(imageLabel3);
			imageLabel.setBounds(15, 50, 50, 100);
			
			Rectangle rect = imageLabel.getBounds();
			
			int x = rect.x;
			int y = rect.y;
			int h = rect.height;
			int w = rect.width;
			
			imageLabel2.setBounds(x+w+1, y, w, h);
			imageLabel3.setBounds(x+2*(w+1), y, w, h);
			
			paint.setSize(new Dimension(800, 600));
			
			prepareRender(allStones[0].getIcon(), 0, new Dimension(x+w+1, (int) (y+0.75*h)), new Dimension(w, h));
			prepareRender(allStones[1].getIcon(), 0, new Dimension((int) (x+2*w+1), (int) (y+0.75*h)), new Dimension(w, h));
			prepareRender(allStones[2].getIcon(), 0, new Dimension((int) (x+3*w+1), (int) (y+0.75*h)), new Dimension(w, h));
			prepareRender(allStones[3].getIcon(), 0, new Dimension((int) (x+4*w+1), (int) (y+0.75*h)), new Dimension(w, h));
			
			/*
			paint.setImage(steinIcon);
			paint.setDegrees(270);
			paint.setImageSize(w, h);
			paint.setPosX(x+w+1);		// X F�r Drehung um 270 Grad (anhand Position vom linken Nachbarn)
			paint.setPosY(y+0.75*h);	// Y F�r Drehung um 270 Grad (anhand Position vom linken Nachbarn)
			//paint.setPosX(x+3*w+1);	// X F�r Drehung um 90 Grad (anhand Position vom linken Nachbarn)
			//paint.setPosY(y+0.25*h);	// Y F�r Drehung um 90 Grad (anhand Position vom linken Nachbarn)
			*/
			
			System.out.println(rect.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		contentPane.setBackground(Color.GRAY);
		contentPane.setVisible(true);
		contentPane.setSize(1000, 1000);
		contentPane.setLayout(null);
		frame.setBounds(400, 300, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void prepareRender(BufferedImage img, double degrees, Dimension pos, Dimension size)
	{
		RenderImage next = new RenderImage();
		next.setImg(img);
		next.setDegrees(degrees);
		next.setPos(pos);
		next.setSize(size);
		
		renderedImages.add(next);
	}
	
	private void initializeDominoes()
	{
		int x = 0;
		int y = 0;
		int current = 0;
		
		for (x = 0; x <= 6; x++)
		{
			for (y = 0; y <= 6; y++)
			{
				if (y == 0)
					y = x;
				
				allStones[current] = new Stone();
				allStones[current].setPips1(x);
				allStones[current].setPips2(y);
				allStones[current].loadIcon();
				
				current++;
			}
		}
		
		for (Stone n : allStones)
		{
			System.out.println(n.getPips1() + "|" + n.getPips2() + " = " + n.getValue() + " | DS: " + n.isDoublestone());
		}
	}

	class PaintingComponent extends JComponent
	{
		private static final long serialVersionUID = 1L;
		private BufferedImage img;
		private double degrees;
		private double posX;
		private double posY;
		private int sizeX;
		private int sizeY;
		
		/**
		 * Darf nicht vom Programmierer aufgerufen werden! Java ruft diese Methode bei bedarf selbst auf.
		 */
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);	// Bevor gezeichnet wird, wird die Zeichenfl�che geleert.
			
			/*
			AffineTransform at = AffineTransform.getTranslateInstance(posX, posY);
			at.rotate(Math.toRadians(degrees));
			
			// Da die Methode automatisch aufgerufen wird muss ueberprueft werden, ob die Instanzvariablen gefuellt wurden.
			if (img != null) 
			{
				Graphics2D g2d = (Graphics2D) g;	// Das Graphics-Objekt wird in ein Graphics2D Objekt gecastet, um mehr zu ermoeglichen.
				
				Image image = img.getScaledInstance(sizeX, sizeY, Image.SCALE_SMOOTH);
				
				g2d.drawImage(image, at, null);
				g2d.dispose();
			}
			*/
			AffineTransform[] at = new AffineTransform[renderedImages.size()];
			Image[] images = new Image[renderedImages.size()];
			Graphics2D g2d = (Graphics2D) g;
			int i = 0;
			
			for (RenderImage ri: renderedImages)
			{
				at[i] = AffineTransform.getTranslateInstance(ri.getPos().getWidth(), ri.getPos().getHeight());
				at[i].rotate(Math.toRadians(ri.getDegrees()));
				
				images[i] = ri.getImg().getScaledInstance(ri.getSize().width, ri.getSize().height, Image.SCALE_SMOOTH);
				
				g2d.drawImage(images[i], at[i], null);
				
				i++;
			}
			
			g2d.dispose();
		}
		
		/**
		 * Das Bild angeben
		 * @param img - Das Bild
		 */
		public void setImage (BufferedImage img)
		{
			this.img = img;
		}
		
		/**
		 * Den Winkel bestimmen, um den das Bild gedreht werden soll
		 * @param degrees - Der Winkel in Grad
		 */
		public void setDegrees (double degrees)
		{
			this.degrees = degrees;
		}
		
		/**
		 * Die neue X-Koordinate, an der das Bild gezeichnet werden soll
		 * @param posX - Die X-Koordinate
		 */
		public void setPosX (double posX)
		{
			this.posX = posX;
		}
		
		/**
		 * Die neue Y-Koordinate, an der das Bild gezeichnet werden soll
		 * @param posY - Die Y-Koordinate
		 */
		public void setPosY (double posY)
		{
			this.posY = posY;
		}
		
		/**
		 * Die Gr��e des zu zeichnenden Bildes festlegen
		 * @param sizeX - Die Breite
		 */
		public void setImageSize (int width, int height)
		{
			this.sizeX = width;
			this.sizeY = height;
		}
	}
}
