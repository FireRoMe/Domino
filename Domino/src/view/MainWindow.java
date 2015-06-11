package view;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import data.Stone;
import control.DominoGame.MouseClickMotionListener;

public class MainWindow
{
	private PaintingComponent paintingComponent = new PaintingComponent();
	private ArrayList<RenderImage> renderedImages = new ArrayList<RenderImage>();
	private MouseClickMotionListener mouseHandler;
	private JLabel lbl_mouseX = new JLabel();
	private JLabel lbl_mouseY = new JLabel();
	private DominoLabel[] dLabels = new DominoLabel[28];
	
	public void initializeWindow(Stone[] allStones, MouseClickMotionListener mouseHandler)
	{
		this.mouseHandler = mouseHandler;
		JFrame frame = new JFrame("TestFenster");
		Container contentPane = frame.getContentPane();

		lbl_mouseX.addMouseMotionListener(mouseHandler);
		lbl_mouseX.setBounds(0, 0, 50, 20);
		lbl_mouseY.addMouseMotionListener(mouseHandler);
		lbl_mouseX.setBounds(0, 20, 50, 20);
		
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setVisible(true);
		contentPane.setSize(800, 600);
		contentPane.setLayout(null);
		contentPane.add(lbl_mouseX);
		contentPane.add(lbl_mouseY);
		contentPane.addMouseListener(mouseHandler);
		contentPane.addMouseMotionListener(mouseHandler);
		
		BufferedImage img = null;
		BufferedImage imgTP = null;
		BufferedImage imgTP2 = null;
		
		ImageIcon iIcon1 = new ImageIcon();
		ImageIcon iIcon2 = new ImageIcon();
		ImageIcon iIcon3 = new ImageIcon();
		ImageIcon iIcon4 = new ImageIcon();
		
		try {
			img = ImageIO.read(new File("ImageSrc/SteinTest.jpg"));
			imgTP = ImageIO.read(new File("ImageSrc/transparenzTest4.png"));
			imgTP2 = ImageIO.read(new File("ImageSrc/transparenzTest2.png"));
			
			Image steinIcon = allStones[15].getIcon();
			
			iIcon1.setImage(img.getScaledInstance(50, 100, Image.SCALE_DEFAULT));
			iIcon2.setImage(imgTP.getScaledInstance(150, 75, Image.SCALE_DEFAULT));
			//iIcon3.setImage(imgTP2.getScaledInstance(36, 75, Image.SCALE_DEFAULT));
			iIcon4.setImage(steinIcon.getScaledInstance(75, 150, Image.SCALE_DEFAULT));
			
			JLabel imageLabel = new JLabel(iIcon1);
			JLabel imageLabel2 = new JLabel(iIcon2);
			JLabel imageLabel3 = new JLabel(iIcon4);
			
			imageLabel.addMouseListener(mouseHandler);
			
			dLabels[0] = new DominoLabel(allStones[0]);
			dLabels[1] = new DominoLabel(allStones[1]);
			dLabels[2] = new DominoLabel(allStones[2]);
			
			dLabels[0].setLocation(contentPane.getWidth()/2 - dLabels[0].getWidth()/2, contentPane.getHeight()/2 - dLabels[0].getHeight()/2);
			dLabels[0].addMouseListener(mouseHandler);
			dLabels[0].addMouseMotionListener(mouseHandler);
			dLabels[1].setLocation(contentPane.getWidth()/2 - dLabels[0].getWidth(), contentPane.getHeight()/2 + dLabels[0].getHeight()/2 +5);
			dLabels[1].addMouseListener(mouseHandler);
			dLabels[1].addMouseMotionListener(mouseHandler);
			dLabels[2].setLocation((int) (contentPane.getWidth()/2 - dLabels[0].getWidth()*1.5), contentPane.getHeight()/2 + dLabels[1].getHeight()*2);
			dLabels[2].addMouseListener(mouseHandler);
			dLabels[2].addMouseMotionListener(mouseHandler);
			
			contentPane.add(dLabels[0]);
			contentPane.add(dLabels[1]);
			contentPane.add(dLabels[2]);
			contentPane.add(paintingComponent);
			contentPane.add(imageLabel);
			//contentPane.add(imageLabel3);
			imageLabel.setBounds(15, 50, 100, 50);
			
			Rectangle rect = imageLabel.getBounds();
			
			int x = rect.x;
			int y = rect.y;
			int h = rect.height;
			int w = rect.width;
			
			imageLabel2.setBounds(x+w+1, y, w, h);
			imageLabel3.setBounds(x+2*(w+1), y, w, h);
			
			paintingComponent.setSize(new Dimension(800, 600));
			
			prepareRender(allStones[0].getIcon(), 0, new Dimension(x+w, (int) (y+0.75*h)), new Dimension(w, h));
			prepareRender(allStones[1].getIcon(), 0, new Dimension((int) (x+2*w-5), (int) (y+0.75*h + 10)), new Dimension(w, h));
			prepareRender(allStones[2].getIcon(), 0, new Dimension((int) (x+3*w+2), (int) (y+0.75*h)), new Dimension(w, h));
			prepareRender(allStones[3].getIcon(), 0, new Dimension((int) (x+4*w+3), (int) (y+0.75*h)), new Dimension(w, h));
			
			/*
			paint.setImage(steinIcon);
			paint.setDegrees(270);
			paint.setImageSize(w, h);
			paint.setPosX(x+w+1);		// X Für Drehung um 270 Grad (anhand Position vom linken Nachbarn)
			paint.setPosY(y+0.75*h);	// Y Für Drehung um 270 Grad (anhand Position vom linken Nachbarn)
			//paint.setPosX(x+3*w+1);	// X Für Drehung um 90 Grad (anhand Position vom linken Nachbarn)
			//paint.setPosY(y+0.25*h);	// Y Für Drehung um 90 Grad (anhand Position vom linken Nachbarn)
			*/
			
			System.out.println(rect.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame.setBounds(400, 300, contentPane.getWidth(), contentPane.getHeight());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void prepareRender(Image img, double degrees, Dimension pos, Dimension size)
	{
		RenderImage next = new RenderImage(img, degrees, pos, size);
		
		renderedImages.add(next);
	}

	class PaintingComponent extends JComponent
	{
		private static final long serialVersionUID = 1L;
		private Object[] intersections;
		
		/**
		 * Darf nicht vom Programmierer aufgerufen werden! Java ruft diese Methode bei bedarf selbst auf.
		 */
		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);	// Bevor gezeichnet wird, wird die Zeichenfläche geleert.
			
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
			AffineTransform[] at2 = new AffineTransform[renderedImages.size()];
			Image[] images = new Image[renderedImages.size()];
			Rectangle[] rects = new Rectangle[renderedImages.size()];
			Shape[] shapes = new Shape[renderedImages.size()];
			Graphics2D g2d = (Graphics2D) g;
			int i = 0;
			
			for (RenderImage ri: renderedImages)
			{
				double x = ri.getPos().getWidth();
				double y = ri.getPos().getHeight();
				double w = ri.getSize().getWidth();
				double h = ri.getSize().getHeight();
				
				at[i] = AffineTransform.getTranslateInstance(x, y);
				at2[i] = new AffineTransform();
				at[i].rotate(Math.toRadians(ri.getDegrees()));
				at2[i].rotate(Math.toRadians(ri.getDegrees()), x, y);
				
				images[i] = ri.getImg().getScaledInstance((int) w, (int) h, Image.SCALE_SMOOTH);
				rects[i] = new Rectangle((int)x, (int)y, (int)w, (int)h);
				
				Path2D.Double path = new Path2D.Double();
				path.append(rects[i], false);
				shapes[i] = path.createTransformedShape(at2[i]);
				
				g2d.setColor(Color.GREEN);
				g2d.draw(shapes[i]);
				g2d.drawImage(images[i], at[i], null);
				
				if (i >= 1)
				{
					if (shapes[i].intersects(shapes[i-1].getBounds2D()));
					{
						g2d.setColor(Color.RED);
						g2d.draw(shapes[i].getBounds().intersection(shapes[i-1].getBounds()));
					}
				}
				
				i++;
			}
			
			if (intersections != null)
			{
				System.out.println("Intersections: " + intersections.length);
				for (Object s: intersections)
				{
					g2d.setColor(Color.RED);
					g2d.fill((Shape)s);
				}
			}
			
			g2d.dispose();
		}
		
		public void setShapes (Object[] o)
		{
			if (o != null)
				this.intersections = o;
		}
	}
	
	public void textOut(String s)
	{
		System.out.println(s);
	}
	
	public void showMousePosition(int x, int y)
	{
		lbl_mouseX.setText("Position X: " + x);
		lbl_mouseY.setText("Position Y: " + y);
		
		lbl_mouseX.setSize(lbl_mouseX.getText().length() * 6, 20);
		lbl_mouseY.setSize(lbl_mouseY.getText().length() * 6, 20);
	}
	
	public void checkIntersection()
	{
		ArrayList<Shape> intersections = new ArrayList<Shape>();
		
		for (int i = 0; i < dLabels.length; i++)
		{
			if (dLabels[i] == null || dLabels[i+1] == null)
				break;
			else
			{
				for (int j = i+1; j < dLabels.length; j++) 
				{
					if (dLabels[i].getBounds().intersects(dLabels[j].getBounds()))
					{
						intersections.add(dLabels[i].getBounds().intersection(dLabels[j].getBounds()));
					}
					if (dLabels[j+1] == null)
						break;
				}
			}
		}
		
		if (!intersections.isEmpty())
		{
			paintingComponent.setShapes(intersections.toArray());
		}
	}
}