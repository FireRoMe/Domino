package view;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.Stone;
import control.DominoGame.MouseClickMotionListener;
import control.DominoRules;

public class MainWindow
{
	/** Die paintingComponent rendert jeden Frame */
	private PaintingComponent paintingComponent = new PaintingComponent();
	/** Eine Liste der Bilder der Dominosteine, die gerendert werden sollen */
	private ArrayList<RenderImage> renderedImages = new ArrayList<RenderImage>();
	private JFrame frame;
	private MouseClickMotionListener mouseHandler;
	private JLabel lbl_mouseX = new JLabel();
	private JLabel lbl_mouseY = new JLabel();
	private ArrayList<DominoLabel> dLabels = new ArrayList<DominoLabel>() ;
	private JPanel contentPane;
	
	public void initializeWindow(Stone[] allStones, MouseClickMotionListener mouseHandler)
	{
		this.mouseHandler = mouseHandler;
		frame = new JFrame("TestFenster");
		contentPane = (JPanel) frame.getContentPane();

		lbl_mouseX.addMouseMotionListener(mouseHandler);
		lbl_mouseX.setBounds(0, 0, 50, 20);
		lbl_mouseY.addMouseMotionListener(mouseHandler);
		lbl_mouseY.setBounds(0, 20, 50, 20);
		
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setVisible(true);
		contentPane.setSize(1280, 720);
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
			
			addDominoe(allStones[25], contentPane.getWidth()/2 - 25, contentPane.getHeight()/2 - 25);
			addDominoe(allStones[26], contentPane.getWidth()/2 - 50, contentPane.getHeight()/2 + 30);
			addDominoe(allStones[27], contentPane.getWidth()/2 - 75, contentPane.getHeight()/2 + 100);
			
			contentPane.add(paintingComponent);
//			contentPane.add(imageLabel);
			//contentPane.add(imageLabel3);
			imageLabel.setBounds(15, 50, 100, 50);
			
			Rectangle rect = imageLabel.getBounds();
			
			int x = rect.x;
			int y = rect.y;
			int h = rect.height;
			int w = rect.width;
			
			imageLabel2.setBounds(x+w+1, y, w, h);
			imageLabel3.setBounds(x+2*(w+1), y, w, h);
			
			paintingComponent.setSize(new Dimension(contentPane.getWidth(), contentPane.getHeight()));
			
			prepareRender(allStones[0].getIcon(), 0, new Dimension(x+w, (int) (y+0.75*h)), new Dimension(w, h));
			prepareRender(allStones[1].getIcon(), 59, new Dimension((int) (x+2*w-5), (int) (y+0.75*h + 10)), new Dimension(w, h));
			prepareRender(allStones[2].getIcon(), 0, new Dimension((int) (x+3*w+2), (int) (y+0.75*h)), new Dimension(w, h));
			prepareRender(allStones[3].getIcon(), 0, new Dimension((int) (x+4*w+3), (int) (y+0.75*h)), new Dimension(w, h));
			
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
		/*
		 * Fenster anhand Bildschirmaufl�sung zentriert ausrichten
		 */
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Properties p = System.getProperties();

		textOut(p.getProperty("os.name"));

		String os = p.getProperty("os.name");

		textOut("Aufloesung: " + (int) screen.getWidth() + "x" + (int) screen.getHeight());
		int x = contentPane.getWidth() - (screen.width / 2);
		int y = contentPane.getHeight() - (screen.height / 2);
		textOut(os);
		if(os.toLowerCase().contains("mac"))
		{
			x = contentPane.getWidth() - screen.width;
			y = contentPane.getHeight() - screen.height;
		}
		
		frame.setBounds(x, y, contentPane.getWidth(), contentPane.getHeight());
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
		private Object[] intersectionColors;
		
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
				int z = 0;
				
//				textOut("Intersections: " + intersections.length);
				
				for (Object s: intersections)
				{
					if ((Boolean) intersectionColors[z] == true)
						g2d.setColor(Color.GREEN);
					else
						g2d.setColor(Color.RED);
					g2d.fill((Shape)s);
					
					z++;
				}
			}
//			else
//				textOut("Intersections: " + 0);
			
			g2d.dispose();
		}
		
		public void setIntersectionShapes (Object[] shapes, Object[] colors)
		{
			if (shapes != null)
			{
				this.intersections = shapes;
				this.intersectionColors = colors;
			}
			else
			{
				this.intersections = null;
				this.intersectionColors = null;
			}
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
	
	public void addDominoe(Stone s, Point p)
	{
		textOut("addDominoe wurde aufgerufen");
		
		if (s.isDoublestone() == true)
			s.rotateImage(90);
		
		DominoLabel d = new DominoLabel(s);
		dLabels.add(d);
		d.setLocation(p);
		d.addMouseListener(mouseHandler);
		d.addMouseMotionListener(mouseHandler);
		
		contentPane.add(d, 2);
		
		checkIntersection(d, false);
		contentPane.updateUI();
	}
	
	public void addDominoe(Stone s, int x, int y)
	{
		if (s.isDoublestone() == true)
			s.rotateImage(90);
		
		dLabels.add(new DominoLabel(s));
		DominoLabel d = dLabels.get(dLabels.size()-1);
		d.setLocation(x, y);
		d.addMouseListener(mouseHandler);
		d.addMouseMotionListener(mouseHandler);
		
		contentPane.add(d, 2);
		
		checkIntersection(d, false);
	}
	
	public DominoLabel checkIntersection(DominoLabel draggedStone, boolean released)
	{
		ArrayList<Shape> intersections = new ArrayList<Shape>();
		ArrayList<Boolean> intersectionColors = new ArrayList<Boolean>();
		DominoLabel target = null;
		
		int i = 0;
		int lastIndex = dLabels.size() - 1;
		
		for (DominoLabel d: dLabels)
		{
			if (d == null || (i+1) > lastIndex)
				break;
			else
			{
				for (int j = i+1; j < dLabels.size(); j++) 
				{
					if (dLabels.get(i).getBounds().intersects(dLabels.get(j).getBounds()))
					{
						intersections.add(dLabels.get(i).getBounds().intersection(dLabels.get(j).getBounds()));
						
						if (dLabels.get(j) == draggedStone)
						{
							target = dLabels.get(i);
							intersectionColors.add(DominoRules.checkCompatibility(dLabels.get(j), target));
						}
						else if (dLabels.get(i) == draggedStone)
						{
							target = dLabels.get(j);
							intersectionColors.add(DominoRules.checkCompatibility(dLabels.get(i), target));
						}
						else
							intersectionColors.add(DominoRules.checkCompatibility(dLabels.get(i), dLabels.get(j)));
					}
					if ((j+1) > lastIndex)
						break;
				}
			}
			i++;
		}
		
		if (!intersections.isEmpty())
		{
			paintingComponent.setIntersectionShapes(intersections.toArray(), intersectionColors.toArray());
			
			if (released == true)
			{
				moveStone(draggedStone, target, intersections, intersectionColors);
			}
			
			return target;
		}
		else
		{
			paintingComponent.setIntersectionShapes(null, null);
			return null;
		}
	}

	private void moveStone(DominoLabel draggedStone, DominoLabel target, ArrayList<Shape> intersections, ArrayList<Boolean> intersectionColors)
	{
		if (intersectionColors.get(0) == true)
		{
			int tPosX = target.getLocation().x;
			int tPosY = target.getLocation().y;
			int draggedWidth = draggedStone.getWidth();
			int draggedHeight = draggedStone.getHeight();
			int targetWidth = target.getWidth();
			int targetHeight = target.getHeight();
			
			if (DominoRules.snapRight(draggedStone, target))
			{
				if (draggedStone.getStone().checkRotation(target.getStone()))
					draggedStone.updateImage();
				
				if (!draggedStone.getStone().isDoublestone())
				{
					if (!target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX+draggedWidth, tPosY);
					else
						draggedStone.setLocation(tPosX+(draggedWidth/2), tPosY+(draggedHeight/2));
				}
				else
				{
					draggedStone.setLocation(tPosX+targetWidth, tPosY-(targetHeight/2));
				}
				
				target.getStone().setRightNeighbour(draggedStone.getStone());
				draggedStone.getStone().setLeftNeighbour(target.getStone());
			}
			else
			{
				if (!draggedStone.getStone().isDoublestone())
				{
					if (!target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX-draggedWidth, tPosY);
					else
						draggedStone.setLocation(tPosX-draggedWidth, tPosY-(targetHeight/2));
				}
				else
				{
					draggedStone.setLocation(tPosX-draggedWidth, tPosY-(targetHeight/2));
				}
				
				target.getStone().setRightNeighbour(draggedStone.getStone());
			}
			checkIntersection(draggedStone, false);		// Ueberschneidungen neu berechnen, um Grafikfehler zu vermeiden
		}
		else
			textOut("Steine sind leider nicht kompatibel");
	}
}