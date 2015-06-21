package view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.Player;
import data.Stone;
import control.DominoGame.ButtonListener;
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
	private ButtonListener buttonListener;
	private JLabel lbl_mouseX = new JLabel("Position X: 0");
	private JLabel lbl_mouseY = new JLabel("Position Y: 0");
	private JLabel lbl_player1Points = new JLabel("Spieler 1: 0");
	private JLabel lbl_player2Points = new JLabel("Spieler 2: 0");
	private JButton btn_drawTalon = new JButton("Stein ziehen");
	/** Zeigt die Punkte des Spiels an */
	private PointsLabel lbl_points = new PointsLabel("Punkte: 0");
	private ArrayList<DominoLabel> dLabels = new ArrayList<DominoLabel>();
	private ArrayList<DominoLabel> handLabels = new ArrayList<DominoLabel>();
	private JPanel graphicsPane = new JPanel();
	private JPanel handPane = new JPanel();
	private JPanel contentPane;
	
	public void initializeWindow(Stone[] allStones, MouseClickMotionListener mouseHandler, ButtonListener buttonListener)
	{
		this.mouseHandler = mouseHandler;
		this.buttonListener = buttonListener;
		frame = new JFrame("TestFenster");
		contentPane = (JPanel) frame.getContentPane();

		ScrollPane scrollbar = new ScrollPane();
		scrollbar.setBounds(-2, 740, 1600, 135);
		
		JLabel lbl_help1 = new JLabel("Rechte Maustaste gedrueckt halten, um das Spielfeld zu verschieben");
		JLabel lbl_help2 = new JLabel("Mit linker Maustaste auf eine freie Fl�che klicken, um einen Stein zu ziehen");
		
		contentPane.setLayout(null);
		contentPane.setBounds(0, 0, 1600, 900);
		contentPane.add(lbl_mouseX);
		contentPane.add(lbl_mouseY);
		contentPane.add(lbl_points);
		contentPane.add(lbl_player1Points);
		contentPane.add(lbl_player2Points);
		contentPane.add(btn_drawTalon);
		contentPane.add(lbl_help1);
		contentPane.add(lbl_help2);
		contentPane.add(scrollbar);
		contentPane.add(graphicsPane);
		
		scrollbar.add(handPane);
		
		lbl_help1.setBounds(440, 5, 400, 20);
		lbl_help1.setHorizontalAlignment(JLabel.CENTER);
		lbl_help2.setBounds(415, 25, 450, 20);
		lbl_help2.setHorizontalAlignment(JLabel.CENTER);
		
		lbl_mouseX.setBounds(0, 0, 100, 20);
		lbl_mouseY.setBounds(0, 20, 100, 20);
		lbl_points.setBounds(contentPane.getWidth() - 85, 1, 78, 21);
		lbl_points.setIcon(new ImageIcon("ImageSrc/BG_Points.png"));
		lbl_points.setIconTextGap(-65);
		
		lbl_player1Points.setBounds(contentPane.getWidth() - 90, 24, 78, 20);
		lbl_player2Points.setBounds(contentPane.getWidth() - 90, 44, 78, 20);
		
		btn_drawTalon.setBounds(contentPane.getWidth() - 162, scrollbar.getY() - 40, 155, 40);
		btn_drawTalon.addActionListener(buttonListener);
		
		FlowLayout flow = new FlowLayout();
		flow.setVgap(10);
		flow.setHgap(5);
		
		handPane.setName("Hand");
		handPane.setBackground(new Color(140,100,40));
		handPane.setBounds(0, 580, 1280, 120);
		handPane.setLayout(flow);
		handPane.addMouseListener(mouseHandler);
		handPane.addMouseMotionListener(mouseHandler);
		
		graphicsPane.setBackground(Color.LIGHT_GRAY);
		graphicsPane.setBounds(-5500, -5500, 10000, 10000);
		graphicsPane.setLayout(null);
		graphicsPane.addMouseListener(mouseHandler);
		graphicsPane.addMouseMotionListener(mouseHandler);
		
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
			
			addDominoe(allStones[25], 10, graphicsPane.getHeight() - (allStones[25].getIcon().getHeight(null)*2 + 45));
			textOut("dLabels Size: " + dLabels.size());
			addDominoe(allStones[26], dLabels.get(dLabels.size() - 1).getWidth() + 20, dLabels.get(dLabels.size() - 1).getY());
			textOut("dLabels Size: " + dLabels.size());
			addDominoe(allStones[27], dLabels.get(dLabels.size() - 1).getWidth() + 130, dLabels.get(dLabels.size() - 1).getY());
			
			graphicsPane.add(paintingComponent);
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
			
			paintingComponent.setSize(new Dimension(graphicsPane.getWidth(), graphicsPane.getHeight()));
			
//			prepareRender(allStones[0].getIcon(), 0, new Dimension(x+w, (int) (y+0.75*h)), new Dimension(w, h));
//			prepareRender(allStones[1].getIcon(), 59, new Dimension((int) (x+2*w-5), (int) (y+0.75*h + 10)), new Dimension(w, h));
//			prepareRender(allStones[2].getIcon(), 0, new Dimension((int) (x+3*w+2), (int) (y+0.75*h)), new Dimension(w, h));
//			prepareRender(allStones[3].getIcon(), 0, new Dimension((int) (x+4*w+3), (int) (y+0.75*h)), new Dimension(w, h));
			
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
		String os = p.getProperty("os.name");

		textOut("Aufloesung: " + (int) screen.getWidth() + "x" + (int) screen.getHeight());
		int x = (screen.width / 2) - (contentPane.getWidth() / 2);
		int y = (screen.height / 2) - (contentPane.getHeight() / 2);
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
	
	public int updatePoints(boolean firstStone)
	{
		int points = DominoRules.calculatePoints(lbl_points.getPoints(), lbl_points.getDoublePoints(), firstStone);
		lbl_points.setText("Punkte: " + points);
		
		return points;
	}
	
	/**
	 * Fuegt der grpahicsPane einen Stein an der Mausposition hinzu
	 * @param s - Der Dominostein, der hinzugefuegt werden soll
	 * @param p - Die aktuellen Koordinaten des Mauszeigers
	 */
	public void addDominoe(Stone s, Point p)
	{
		textOut("addDominoe wurde aufgerufen");
		
//		if (s.isDoublestone())
//			s.rotateImage(90);
		
		DominoLabel d = new DominoLabel(s);
		dLabels.add(d);
		d.setLocation(p);
		d.addMouseListener(mouseHandler);
		d.addMouseMotionListener(mouseHandler);
		
		graphicsPane.add(d, 0);
		
		checkIntersection(d, false, lbl_points.getPoints(), lbl_points.getDoublePoints());
		updatePanels();
	}
	
	/**
	 * Fuegt zu Debugzwecken einen Stein zur graphicsPane hinzu
	 * @param s - Der Dominostein, der hinzugefuegt werden soll
	 * @param x - X-Koordinate
	 * @param y - Y-Koordinate
	 */
	public void addDominoe(Stone s, int x, int y)
	{
		if (s.isDoublestone())
			s.rotateImage(90);
		
		dLabels.add(new DominoLabel(s));
		DominoLabel d = dLabels.get(dLabels.size()-1);
		d.setLocation(x, y);
		d.addMouseListener(mouseHandler);
		d.addMouseMotionListener(mouseHandler);
		
		graphicsPane.add(d, 0);
		
		checkIntersection(d, false, lbl_points.getPoints(), lbl_points.getDoublePoints());
		updatePanels();
	}
	
	/**
	 * Fuegt auf der handPane einen Stein zur Hand des Spielers hinzu
	 * @param s - Der Dominostein, der hinzugefuegt werden soll
	 * @param firstMove - Gibt an, ob es der erste Spielzug der Runde ist
	 */
	public void addDominoeToHand(Stone s, boolean firstMove)
	{
		if (s.isDoublestone() && firstMove)
			s.rotateImage(90);
		
		handLabels.add(new DominoLabel(s));
		DominoLabel d = handLabels.get(handLabels.size() -1);
		
//		textOut("HandLabels Groesse: " + handLabels.size());
			
		d.addMouseListener(mouseHandler);
		d.addMouseMotionListener(mouseHandler);
		
		handPane.add(d);
		
		updatePanels();
	}
	
	public DominoLabel checkIntersection(DominoLabel draggedStone, boolean released, int[] edgePoints, boolean[] doublePoints)
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
				if(!DominoRules.checkIfVertical(target))
					moveStoneHorizontal(draggedStone, target, intersectionColors, edgePoints, doublePoints);
				else
					moveStoneVertical(draggedStone, target, intersectionColors, edgePoints, doublePoints);
			}
			
			return target;
		}
		else
		{
			paintingComponent.setIntersectionShapes(null, null);
			return null;
		}
	}
	
	private void moveStoneHorizontal(DominoLabel draggedStone, DominoLabel target, ArrayList<Boolean> intersectionColors, int[] edgePoints, boolean[] doublePoints)
	{
		if (intersectionColors.get(0) == true && draggedStone.isDraggable())		// wenn die beiden Steine kompatibel sind
		{
			int tPosX = target.getLocation().x;
			int tPosY = target.getLocation().y;
			int draggedWidth = draggedStone.getWidth();
			int draggedHeight = draggedStone.getHeight();
			int targetWidth = target.getWidth();
			int targetHeight = target.getHeight();
			boolean snapRight = DominoRules.snapRight(draggedStone, target);
			
			if (snapRight)
			{
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
				
				DominoRules.calculatePointsRight(draggedStone, target, edgePoints, doublePoints);
			}
			else
			{
				if (!draggedStone.getStone().isDoublestone())
				{
					if (!target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX-draggedWidth, tPosY);
					else
						draggedStone.setLocation(tPosX-draggedWidth, tPosY+(draggedHeight/2));
				}
				else
				{
					draggedStone.setLocation(tPosX-draggedWidth, tPosY-(targetHeight/2));
				}
				
				target.getStone().setLeftNeighbour(draggedStone.getStone());
				draggedStone.getStone().setRightNeighbour(target.getStone());
				
				DominoRules.calculatePointsLeft(draggedStone, target, edgePoints, doublePoints);
				
			}
			draggedStone.setNotDraggable();
			target.setNotDraggable();
			checkIntersection(draggedStone, false, edgePoints, doublePoints);		// Ueberschneidungen neu berechnen, um Grafikfehler zu vermeiden
			
			lbl_points.setPoints(edgePoints);
			lbl_points.setDoublePoints(doublePoints);
			updatePoints(false);
		}
		else
			textOut("Steine sind leider nicht kompatibel");
	}

	private void moveStoneVertical(DominoLabel draggedStone, DominoLabel target, ArrayList<Boolean> intersectionColors, int[] edgePoints, boolean[] doublePoints)
	{
		if (intersectionColors.get(0) == true && draggedStone.isDraggable())
		{
			int tPosX = target.getLocation().x;
			int tPosY = target.getLocation().y;
			int draggedWidth = draggedStone.getWidth();
			int draggedHeight = draggedStone.getHeight();
			int targetWidth = target.getWidth();
			int targetHeight = target.getHeight();
			boolean snapTop = DominoRules.snapTop(draggedStone, target);
			
			if (snapTop)
			{
				if (!draggedStone.getStone().isDoublestone())
				{
					if(target.getStone().isSpinner() || !target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX, tPosY-targetHeight);
					else
					{
						System.err.println("Normalen Stein vertikal oben an Doppelstein angelegt");
						draggedStone.setLocation(tPosX+(draggedHeight/2), tPosY-draggedHeight*2);
					}
				}
				else
				{
					System.err.println("Doppelstein oben angelegt");
					draggedStone.setLocation(tPosX-(targetWidth/2), tPosY-targetWidth);
				}

				target.getStone().setTopNeighbour(draggedStone.getStone());
				draggedStone.getStone().setBottomNeighbour(target.getStone());
				
				DominoRules.calculatePointsTop(draggedStone, target, edgePoints, doublePoints);
				
			}
			else
			{
				textOut("Lege unten an");
				if (!draggedStone.getStone().isDoublestone())
				{
					if(target.getStone().isSpinner() || !target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX, tPosY+targetHeight);
					else
					{
						System.err.println("Normalen Stein vertikal unten an Doppelstein angelegt");
						draggedStone.setLocation(tPosX+(draggedHeight/2), tPosY+draggedHeight);
					}
				}
				else
				{
					System.err.println("Doppelstein unten angelegt");
					draggedStone.setLocation(tPosX-(targetWidth/2), tPosY+draggedWidth*2);
				}
				
				target.getStone().setBottomNeighbour(draggedStone.getStone());
				draggedStone.getStone().setTopNeighbour(target.getStone());
				
				DominoRules.calculatePointsBottom(draggedStone, target, edgePoints, doublePoints);
			}
			
			draggedStone.setNotDraggable();
			target.setNotDraggable();
			checkIntersection(draggedStone, false, edgePoints, doublePoints);
			
			lbl_points.setPoints(edgePoints);
			lbl_points.setDoublePoints(doublePoints);
			updatePoints(false);
		}
	}
	
	public void updatePanels()
	{
		contentPane.updateUI();
		graphicsPane.updateUI();
		handPane.updateUI();
	}
	
	public Point getFrameCoordinates()
	{
		return frame.getLocationOnScreen();
	}

	public void dropFromHand(DominoLabel clickedStone, int x, int y)
	{
		Stone s = clickedStone.getStone();
		Player p = s.getPlayer();
		
		p.deleteStone(s);
		
		handPane.remove(clickedStone);
		handLabels.remove(clickedStone);
		
		dLabels.add(clickedStone);
		graphicsPane.add(clickedStone, 0);
		
		clickedStone.setLocation(x + clickedStone.getX(), y + 450 - clickedStone.getHeight());
		
		updatePanels();
	}

	public void clearHand()
	{
		handPane.removeAll();
		textOut("Nach der Leerung: " + handPane.getComponentCount());
		handPane.validate();
		handPane.repaint();
		handLabels.clear();
		
		updatePanels();
	}

	public void firstPoints(int[] edgePoints, boolean[] doublePoints)
	{
		lbl_points.setPoints(edgePoints);
		lbl_points.setDoublePoints(doublePoints);
	}
	
	public void updateButton(boolean isActive)
	{
		btn_drawTalon.setEnabled(isActive);
		btn_drawTalon.setFocusPainted(isActive);
	}

	public void updatePlayerPoints(Player player, int playerIndex)
	{
		if (playerIndex == 0)
			lbl_player1Points.setText("Spieler 1: " + player.getPoints());
		else
			lbl_player2Points.setText("Spieler 2: " + player.getPoints());
	}
}