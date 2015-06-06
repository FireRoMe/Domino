import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Main
{
	private PaintingComponent paint = new PaintingComponent();
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("TestFenster");
		Container contentPane = frame.getContentPane();
		
		BufferedImage img = null;
		
		ImageIcon iIcon1 = new ImageIcon();
		ImageIcon iIcon2 = new ImageIcon();
		ImageIcon iIcon3 = new ImageIcon();
		
		try {
			img = ImageIO.read(new File("ImageSrc/SteinTest.jpg"));

			iIcon1.setImage(img.getScaledInstance(75, 150, Image.SCALE_DEFAULT));
			iIcon2.setImage(img.getScaledInstance(75, 150, Image.SCALE_DEFAULT));
			iIcon3.setImage(img.getScaledInstance(75, 150, Image.SCALE_DEFAULT));
			JLabel imageLabel = new JLabel(iIcon1);
			JLabel imageLabel2 = new JLabel(iIcon2);
			JLabel imageLabel3 = new JLabel(iIcon3);
			
			contentPane.add(imageLabel);
			contentPane.add(imageLabel2);
			contentPane.add(imageLabel3);
			imageLabel.setBounds(15, 10, 75, 150);
			
			Rectangle rect = imageLabel.getBounds();
			
			int x = rect.x;
			int y = rect.y;
			int h = rect.height;
			int w = rect.width;
			
			imageLabel2.setBounds(x+w+1, y, w, h);
			imageLabel3.setBounds(x+2*(w+1), y, w, h);
			
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
	
	class PaintingComponent extends JComponent
	{
		private static final long serialVersionUID = 1L;
		private Shape shape;
		private Color color;
		
		/**
		 * Darf nicht vom Programmierer aufgerufen werden! Java ruft diese Methode bei bedarf selbst auf.
		 */
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);	// Bevor gezeichnet wird, wird die Zeichenfläche geleert.
			
			// Da die Methode automatisch aufgerufen wird muss ueberprueft werden, ob die Instanzvariablen gefuellt wurden.
			if (shape != null && color != null) 
			{
				Graphics2D g2d = (Graphics2D) g;	// Das Graphics-Objekt wird in ein Graphics2D Objekt gecastet, um mehr zu ermoeglichen.
				g2d.setColor(color);	// Die Farbe des zu zeichnenden Objekts festlegen.
				g2d.fill(shape);	// Die Form gefuellt zeichnen.
			}
		}
		
		/**
		 * Die Form bestimmen
		 * @param shape - Die geometrische Grundform
		 */
		public void setShape (Shape shape)
		{
			this.shape = shape;
		}
		
		/**
		 * Die Farbe bestimmen
		 * @param color - Die Farbe, die das Objekt haben soll
		 */
		public void setColor (Color color)
		{
			this.color = color;
		}
	}
}
