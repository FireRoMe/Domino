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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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

/**
 * Das Hauptfenster des Spiels 
 */
public class MainWindow
{
	/** Die paintingComponent rendert jeden Frame */
	private PaintingComponent paintingComponent = new PaintingComponent();
	/** Eine Liste der Bilder der Dominosteine, die gerendert werden sollen */
	private ArrayList<RenderImage> renderedImages = new ArrayList<RenderImage>();
	/** Das Fenster selbst */
	private JFrame frame;
	private MouseClickMotionListener mouseHandler;
	private ButtonListener buttonListener;
	private JLabel lbl_mouseX = new JLabel("Position X: 0");
	private JLabel lbl_mouseY = new JLabel("Position Y: 0");
	private JLabel lbl_player1Points = new JLabel("Spieler 1: 0");
	private JLabel lbl_player2Points = new JLabel("Spieler 2: 0");
	private JLabel lbl_splash1 = new JLabel();
	private JLabel lbl_splash2 = new JLabel();
	private JButton btn_drawTalon = new JButton("Stein ziehen");
	/** Zeigt die Punkte des Spiels an */
	private PointsLabel lbl_points = new PointsLabel("Punkte: 0");
	private DominoLabel targetLabel = null;
	private ArrayList<DominoLabel> dLabels = new ArrayList<DominoLabel>();
	private ArrayList<DominoLabel> handLabels = new ArrayList<DominoLabel>();
	private JPanel graphicsPane = new JPanel();
	private ScrollPane scrollPane = new ScrollPane();
	private JPanel handPane = new JPanel();
	private JPanel contentPane;
	
	/**
	 * Initialisiert alle Komponenten
	 * @param allStones
	 * @param mouseHandler
	 * @param buttonListener
	 */
	public void initializeWindow(Stone[] allStones,
				MouseClickMotionListener mouseHandler, ButtonListener buttonListener)
	{
		this.mouseHandler = mouseHandler;
		this.buttonListener = buttonListener;
		frame = new JFrame("Domino");
		contentPane = (JPanel) frame.getContentPane();

		scrollPane.setBounds(-2, 734, 1600, 141);
		
		JLabel lbl_help1 = new JLabel("Rechte Maustaste "
				+ "gedrueckt halten, um das Spielfeld zu verschieben");
		JLabel lbl_help2 = new JLabel("Mit linker Maustaste "
				+ "auf einen Stein im unteren Feld klicken, um ihn zu legen");
		
		//Initialisierung aller Kompontenten
		contentPane.setLayout(null);
		contentPane.setBounds(0, 0, 1600, 900);
		contentPane.add(lbl_mouseX);
		contentPane.add(lbl_mouseY);
		contentPane.add(lbl_points);
		contentPane.add(lbl_splash1);
		contentPane.add(lbl_splash2);
		contentPane.add(lbl_player1Points);
		contentPane.add(lbl_player2Points);
		contentPane.add(btn_drawTalon);
		contentPane.add(lbl_help1);
		contentPane.add(lbl_help2);
		contentPane.add(scrollPane);
		contentPane.add(graphicsPane);

		scrollPane.add(handPane);
		
		lbl_help1.setBounds(600, 5, 400, 20);
		lbl_help1.setHorizontalAlignment(JLabel.CENTER);
		lbl_help2.setBounds(575, 25, 450, 20);
		lbl_help2.setHorizontalAlignment(JLabel.CENTER);
		
		lbl_splash1.setBounds(0, (contentPane.getHeight()/2) - 200, contentPane.getWidth(), 100);
		lbl_splash1.setHorizontalAlignment(JLabel.CENTER);
		lbl_splash1.setFont(lbl_splash1.getFont().deriveFont(100.0f));

		lbl_splash2.setBounds(0, (contentPane.getHeight()/2) -100, contentPane.getWidth(), 150);
		lbl_splash2.setHorizontalAlignment(JLabel.CENTER);
		lbl_splash2.setFont(lbl_splash1.getFont().deriveFont(100.0f));

		lbl_mouseX.setBounds(0, 0, 100, 20);
		lbl_mouseY.setBounds(0, 20, 100, 20);

		lbl_points.setBounds(contentPane.getWidth() - 89, 13, 70, 21);
		lbl_points.setIcon(new ImageIcon("ImageSrc/BG_Points.png"));
		lbl_points.setIconTextGap(-65);
		
		lbl_player1Points.setBounds(contentPane.getWidth() - 93, 45, 105, 20);
		lbl_player2Points.setBounds(contentPane.getWidth() - 93, 65, 105, 20);
		
		btn_drawTalon.setBounds((contentPane.getWidth()/2) - 77, scrollPane.getY() - 40, 155, 40);
		btn_drawTalon.addActionListener(buttonListener);
		
		// FlowLayout fuer die ScrollPane, damit die Steine nebeneinander angeordnet werden
		FlowLayout flow = new FlowLayout();
		flow.setVgap(10);
		flow.setHgap(15);
		
		// Dem JPanel fuer die Hand einen Namen geben fuer spaetere Pruefung
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
		
		lbl_points.setIcon(new ImageIcon("ImageSrc/BG_Points.png"));
			
		graphicsPane.add(paintingComponent);
			
		paintingComponent.setSize(new Dimension(graphicsPane.getWidth(), graphicsPane.getHeight()));
			
		/*
		 * Fenster anhand Bildschirmaufloesung zentriert ausrichten
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
	
	/**
	 * Zeichnet Ueberschneidungen farbig auf das Spielfeld 
	 */
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
			super.paintComponent(g);	// Bevor gezeichnet wird, wird die Zeichenflaeche geleert.
			
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
				
				rects[i] = new Rectangle((int)x, (int)y, (int)w, (int)h);
				
				Path2D.Double path = new Path2D.Double();
				path.append(rects[i], false);
				
				g2d.setColor(Color.GREEN);
				g2d.draw(shapes[i]);
				
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
	
	/**
	 * Schreibt den uebergebenen Text auf die Konsole
	 * @param s - Der Text der ausgegeben werden soll
	 */
	public void textOut(String s)
	{
		System.out.println(s);
	}
	
	/**
	 * Zeigt die Mausposition im Fenster an
	 * @param x - Mausposition X
	 * @param y - Mausposition Y
	 */
	public void showMousePosition(int x, int y)
	{
		lbl_mouseX.setText("Position X: " + x);
		lbl_mouseY.setText("Position Y: " + y);
		
		lbl_mouseX.setSize(lbl_mouseX.getText().length() * 6, 20);
		lbl_mouseY.setSize(lbl_mouseY.getText().length() * 6, 20);
	}
	
	/**
	 * Aktualisiert die Punkteanzeige
	 * @param firstStone - true wenn der erste Stein gelegt wird
	 */
	public void updatePoints(boolean firstStone)
	{
		int points = DominoRules.calculatePoints(lbl_points.getPoints()
									, lbl_points.getDoublePoints(), firstStone);
		lbl_points.setText("Punkte: " + points);
	}
	
	/**
	 * Fuegt auf der handPane einen Stein zur Hand des Spielers hinzu
	 * @param s - Der Dominostein, der hinzugefuegt werden soll
	 * @param firstMove - Gibt an, ob es der erste Spielzug der Runde ist
	 */
	public void addDominoeToHand(Stone s, boolean firstMove)
	{
		boolean abort = false;
		
		// Hiermit wird sichergestellt, dass nicht versehentlich 
		// zwei identische Steine auf der Hand liegen koennen
		for (DominoLabel h: handLabels)
		{
			if (h.getStone() == s)
				abort = true;
		}
		
		// wenn der Stein noch nicht auf der Hand ist
		if (!abort)
		{
			/* wenn der Stein noch nicht gedreht ist,
			aber gedreht werden muss wird er hier gedreht */
			if (s.isDoublestone() && firstMove)
				s.rotateImage(90);
			
			// Fuegt den Stein zu Liste der angezeigten Steine hinzu
			handLabels.add(new DominoLabel(s));
			DominoLabel d = handLabels.get(handLabels.size() -1);
			
			textOut("HandLabels Groesse: " + handLabels.size());
				
			// fuegt dem Stein den MouseListener hinzu
			d.addMouseListener(mouseHandler);
			d.addMouseMotionListener(mouseHandler);
			
			// fuegt den Stein zur Anzeige der handPane hinzu
			handPane.add(d);
			
			// Grafik aktualisieren
			updatePanels();
		}
	}
	
	/**
	 * Prueft ueberlappende Steine auf Ueberschneidungen
	 * @param draggedStone - der Stein, der gelegt werden soll
	 * @param released - true, wenn die Maustaste losgelassen wurde
	 * @param edgePoints - die Kantenpunkte
	 * @param doublePoints - haelt die Positionen der Doppelsteine
	 * @return true - wenn es Ueberschneidungen gibt <br>
	 * false - wenn nicht
	 */
	public boolean checkIntersection(DominoLabel draggedStone,
						boolean released, int[] edgePoints, boolean[] doublePoints)
	{
		ArrayList<Shape> intersections = new ArrayList<Shape>();
		ArrayList<Boolean> intersectionColors = new ArrayList<Boolean>();
		
		int i = 0;
		int lastIndex = dLabels.size() - 1;
		
		for (DominoLabel d: dLabels)
		{
			if (d == null || (i+1) > lastIndex)
				break;
			else
			{
				/* prueft fuer alle gelegten Steine,
				ob es Ueberschneidungen gibt*/ 
				for (int j = i+1; j < dLabels.size(); j++) 
				{
					if (dLabels.get(i).getBounds().intersects
												(dLabels.get(j).getBounds()))
					{
						intersections.add(dLabels.get(i).getBounds().intersection
													(dLabels.get(j).getBounds()));
						
						if (dLabels.get(j) == draggedStone)
						{
							targetLabel = dLabels.get(i);
							intersectionColors.add(DominoRules.checkCompatibility
													(dLabels.get(j), targetLabel));
						}
						else if (dLabels.get(i) == draggedStone)
						{
							targetLabel = dLabels.get(j);
							intersectionColors.add(DominoRules.checkCompatibility
													(dLabels.get(i), targetLabel));
						}
						else
							intersectionColors.add(DominoRules.checkCompatibility
													(dLabels.get(i), dLabels.get(j)));
					}
					/* wenn der nächste Wert der ArrayList leer ist 
					soll es keinen weiteren Durchlauf geben */
					if ((j+1) > lastIndex)
						break;
				}
			}
			i++;
		}
		
		// wenn es mindestens eine Uberschneidung zwischen 2 Steinen gibt
		if (!intersections.isEmpty())
		{
			/* hier werden die Ueberschneidungen zum zeichnen an 
			die paintingComponent uebergeben */
			paintingComponent.setIntersectionShapes(intersections.toArray(), 
											intersectionColors.toArray());
			
			/* wenn es mehrere Ueberschneidungen gibt, kann auch 
			kein Stein angelegt worden sein */
			if (intersections.size() > 1)
				return false;
			
			// wenn die Maustaste losgelassen wurde
			if (released == true)
			{
				if (intersectionColors.get(0))
				{
					// wenn der Stein horizontal angelegt werden kann
					if(!DominoRules.checkIfVertical(targetLabel))
						return moveStoneHorizontal(draggedStone, targetLabel,
								intersectionColors, edgePoints, doublePoints);
					else
						return moveStoneVertical(draggedStone, targetLabel,
								intersectionColors, edgePoints, doublePoints);
				}
			}
			
			return false;
		}
		// wenn es keine Ueberschneidungen gibt
		else
		{
			targetLabel = null;
			paintingComponent.setIntersectionShapes(null, null);
			return false;
		}
	}
	
	/**
	 * Gibt den Stein zurueck, mit dem es aktuell
	 * eine Ueberschneidung gibt
	 * @return
	 */
	public DominoLabel getCurrentTarget()
	{
		return targetLabel;
	}
	
	/**
	 * Legt einen Stein horizontal an
	 * @param draggedStone - der Stein, der gelegt werden soll
	 * @param target - der Stein, an den angelegt werden soll
	 * @param intersectionColors - Auswertung der Ueberschneidungen
	 * @param edgePoints - die Punkte der offenen Kanten
	 * @param doublePoints - weiß ob Punkte doppelt gezaehlt werden sollen
	 * @return true - wenn der Stein angelegt wurde<br>
	 * false - wenn nicht angelegt wurde
	 */
	private boolean moveStoneHorizontal(DominoLabel draggedStone, DominoLabel target, 
			ArrayList<Boolean> intersectionColors, int[] edgePoints, boolean[] doublePoints)
	{

		boolean snapRight = DominoRules.snapRight(draggedStone, target);
		boolean noNeighbours = DominoRules.checkNeighboursHorizontal(draggedStone, target, snapRight);
		
		// wenn die beiden Steine kompatibel sind
		if (intersectionColors.get(0) == true && draggedStone.isDraggable() && noNeighbours)
		{
			// Steinpositionen und Groessen holen
			int tPosX = target.getLocation().x;
			int tPosY = target.getLocation().y;
			int draggedWidth = draggedStone.getWidth();
			int draggedHeight = draggedStone.getHeight();
			int targetWidth = target.getWidth();
			int targetHeight = target.getHeight();
			
			// wenn der Stein rechts angelegt werden soll
			if (snapRight)
			{
				// wenn der Stein kein Doppelstein ist
				if (!draggedStone.getStone().isDoublestone())
				{
					// wenn der Zielstein kein Doppelstein ist
					if (!target.getStone().isDoublestone())
						// Stein positionieren
						draggedStone.setLocation(tPosX+draggedWidth, tPosY);
					else
						draggedStone.setLocation(tPosX+(draggedWidth/2),
														tPosY+(draggedHeight/2));
				}
				// wenn der Stein ein Doppelstein ist
				else
				{
					draggedStone.setLocation(tPosX+targetWidth, tPosY-(targetHeight/2));
				}
				
				// Nachbarn festlegen
				target.getStone().setRightNeighbour(draggedStone.getStone());
				draggedStone.getStone().setLeftNeighbour(target.getStone());
				
				// Punkte berechnen
				DominoRules.calculatePointsRight(draggedStone, target, edgePoints, doublePoints);
			}
			// wenn links angelegt werden soll
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
			// beide Steine nicht verschiebbar machen
			draggedStone.setNotDraggable();
			target.setNotDraggable();
			// Ueberschneidungen neu berechnen, um Grafikfehler zu vermeiden
			checkIntersection(draggedStone, false, edgePoints, doublePoints);		
			
			// Punkte im Fenster anzeigen
			lbl_points.setPoints(edgePoints);
			lbl_points.setDoublePoints(doublePoints);
			updatePoints(false);
			
			return true;
		}
		// wenn nicht angelegt werden konnte 
		else
		{
			textOut("Die Steine sind leider nicht kompatibel");
			return false;
		}
	}

	/**
	 * Legt einen Stein horizontal an
	 * @param draggedStone - der Stein, der gelegt werden soll
	 * @param target - der Stein, an den angelegt werden soll
	 * @param intersectionColors - Auswertung der Ueberschneidungen
	 * @param edgePoints - die Punkte der offenen Kanten
	 * @param doublePoints - weiß ob Punkte doppelt gezaehlt werden sollen
	 * @return true - wenn der Stein angelegt wurde<br>
	 * false - wenn nicht angelegt wurde
	 */
	private boolean moveStoneVertical(DominoLabel draggedStone, DominoLabel target, 
			ArrayList<Boolean> intersectionColors, int[] edgePoints, boolean[] doublePoints)
	{
		// Kommentare aehnlich moveStoneVertical
		
		boolean snapTop = DominoRules.snapTop(draggedStone, target);
		boolean noNeighbours = DominoRules.checkNeighboursVertical(draggedStone, target, snapTop);
		
		if (intersectionColors.get(0) == true && draggedStone.isDraggable() && noNeighbours)
		{
			int tPosX = target.getLocation().x;
			int tPosY = target.getLocation().y;
			int draggedWidth = draggedStone.getWidth();
			int draggedHeight = draggedStone.getHeight();
			int targetWidth = target.getWidth();
			int targetHeight = target.getHeight();
			
			if (snapTop)
			{
				if (!draggedStone.getStone().isDoublestone())
				{
					if(target.getStone().isSpinner() || !target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX, tPosY-targetHeight);
					else
					{
						System.err.println("Normalen Stein vertikal oben an Doppelstein angelegt");
						draggedStone.setLocation(tPosX+(draggedWidth/2), tPosY-draggedHeight);
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
						draggedStone.setLocation(tPosX+(draggedWidth/2), tPosY+targetHeight);
					}
				}
				else
				{
					System.err.println("Doppelstein unten angelegt");
					draggedStone.setLocation(tPosX-(targetWidth/2), tPosY+targetHeight);
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
			
			return true;
		}
		else
		{
			textOut("Die Steine sind leider nicht kompatibel");
			return false;
		}
	}
	
	/**
	 * Aktualisiert die JPanels
	 */
	public void updatePanels()
	{
		contentPane.updateUI();
		graphicsPane.updateUI();
		handPane.updateUI();
	}
	
	/**
	 * Gibt die Position des Hauptfensters auf dem Bildschirm zurueck
	 * @return - Die absoluten Koordinaten des Ursprungs des Hauptfensters
	 */
	public Point getFrameCoordinates()
	{
		return frame.getLocationOnScreen();
	}
	
	/**
	 * Legt einen Stein von der Hand aufs Spielfeld
	 * @param clickedStone - der angeklickte Stein
	 * @param offsetX - das X-Offset des Spielfeldes
	 * @param offsetY - das Y-Offset des Spielfeldes
	 * @param playedDominoes - die Anzahl der gelegten Steine
	 */
	public void dropFromHand(DominoLabel clickedStone, int offsetX,
									int offsetY, int playedDominoes)
	{
		Stone s = clickedStone.getStone();
		Player p = s.getPlayer();
		textOut("Stein: " + s);
		// den Stein von der Hand des Spielers loeschen
		p.deleteStone(s);
		
		// den Stein vom JPanel Hand loeschen
		handPane.remove(clickedStone);
		handLabels.remove(clickedStone);
		
		// den Stein dem Spielfeld hinzufuegen
		dLabels.add(clickedStone);
		graphicsPane.add(clickedStone, 0);
		
		/*wenn bereits Steine gelegt wurden,
		wird der Stein etwas ueber der Hand dargestellt */
		if (playedDominoes != 0)
			clickedStone.setLocation(offsetX + clickedStone.getX(),
						offsetY + 680 - clickedStone.getHeight());
		/* wenn noch keine Steine gelegt wurden, wird der Stein
		mittig auf dem Spielfeld dargestellt*/
		else
		{	
			int halfSizeX = frame.getWidth()/2;
			int halfSizeY = scrollPane.getY()/2;
			int halfStoneWidth = clickedStone.getWidth()/2;
			int halfStoneHeight = clickedStone.getHeight()/2;
			
			clickedStone.setLocation(offsetX + halfSizeX - halfStoneWidth, offsetY + halfSizeY - halfStoneHeight);
		}
		
		updatePanels();
	}
	
	/**
	 * Loescht auf dem GUI alle Steine von der Hand
	 */
	public void clearHand()
	{
		handPane.removeAll();	// Alle Labels vom JPanel "Hand" leoschen
		handLabels.clear();		// Alle Labels aus der Hand-ArrayList loeschen
		
		updatePanels();		// Grafik auffrischen, um Aenderungen wirksam zu machen
	}

	/**
	 * Uebergibt beim ersten Zug Referenzen von edgePoints
	 * und doublePoints zur Punkteberechnung und -darstellung
	 * an das PunkteLabel. Danach ist keine weitere Uebergabe dieser
	 * Variablen mehr nötig, die diese noch mit der Hauptspielklasse
	 * referenziert sind
	 * @param edgePoints - haelt die Punktzahlen der offenen Kanten
	 * @param doublePoints - speichert, an welcher offenen Kante ein Doppelstein liegt
	 */
	public void firstPoints(int[] edgePoints, boolean[] doublePoints)
	{
		lbl_points.setPoints(edgePoints);
		lbl_points.setDoublePoints(doublePoints);
	}
	
	/**
	 * Kann je nach Uebergabeparameter den Button aktiv/inaktiv
	 * schalten und den Text des Buttons aendern 
	 * @param isActive - <i>true</i> - Button aktiv, 
	 * <i>false</i> - Button inaktiv
	 * @param text - Text, der auf dem Button dargestellt wird
	 * (Standardtext, wenn <b>""</b>)
	 */
	public void updateButton(boolean isActive, String text)
	{
		if (text != "")
			btn_drawTalon.setText(text);
		
		btn_drawTalon.setEnabled(isActive);
		btn_drawTalon.setFocusPainted(isActive);
	}

	/**
	 * Aktualisiert das Punktelabel nach dem Zug eines Spielers mit
	 * dessen aktuellem Punktestand
	 * @param player - Der Spieler, dessen Punkte aktualisiert werden sollen
	 * @param playerIndex - Der Spielerindex des Spielers
	 */
	public void updatePlayerPoints(Player player, int playerIndex)
	{
		if (playerIndex == 0)
			lbl_player1Points.setText("Spieler 1: " + player.getPoints());
		else
			lbl_player2Points.setText("Spieler 2: " + player.getPoints());
	}
	
	/**
	 * Stellt den Pfeil neben der Punktzahl des aktuellen Spielers dar
	 * @param playerIndex - Index des aktuellen Spielers
	 * @param playedDominoes - Die Anzahl der bereits gelegten Steine
	 */
	public void updatePlayerArrow(int playerIndex, int playedDominoes)
	{
		// Variable fuer das Icon erzeugen
		ImageIcon arrow = null;
		// Positionen der Labels holen
		int x1 = lbl_player1Points.getLocation().x;
		int y1 = lbl_player1Points.getLocation().y;
		int x2 = lbl_player2Points.getLocation().x;
		int y2 = lbl_player2Points.getLocation().y;
		
		// das Bild laden
		try
		{
			arrow = new ImageIcon("ImageSrc/Arrow.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// wenn Spieler 1 der aktive Spieler ist
		if (playerIndex == 0)
		{
			// wenn das Label kein Icon hat
			if (lbl_player1Points.getIcon() == null)
			{
				// Icon setzen und Position anpassen, da das
				// Label durch das Icon nach rechts geschoben wird
				lbl_player1Points.setIcon(arrow);
				lbl_player1Points.setLocation(x1 - 24, y1);
				// das Icon aus dem Label des andern Spielers entfernen
				lbl_player2Points.setIcon(null);
				
				// wenn bereits Steine gelegt worden sind, muss das Label
				// des anderen Spielers zurueck nach rechts geschoben werden
				if (playedDominoes != 0)
					lbl_player2Points.setLocation(x2 + 24, y2);
			}
		}
		// wenn Spieler 2 der aktive Spieler ist
		else
		{
			if (lbl_player2Points.getIcon() == null)
			{
				lbl_player2Points.setIcon(arrow);
				lbl_player2Points.setLocation(x2 - 24, y2);
				lbl_player1Points.setIcon(null);
				
				if (playedDominoes != 0)
					lbl_player1Points.setLocation(x1 + 24, y1);
			}
		}
	}
	
	/**
	 * Setzt alle relaventen Variablen im Fenster zurueck,
	 * bevor die Runde neu gestartet wird
	 */
	public void resetWindow()
	{
		// Punktelabel zuruecksetzen
		lbl_points.setText("Punkte: 0");
		
		btn_drawTalon.setText("Stein ziehen");
		// Punktelabel der Spieler auf Ursprungskoordinaten zurueck schieben
		lbl_player1Points.setBounds(contentPane.getWidth() - 93, 45, 105, 20);
		lbl_player2Points.setBounds(contentPane.getWidth() - 93, 65, 105, 20);
		// Icon aus Spielerlabels entfernen
		lbl_player1Points.setIcon(null);
		lbl_player2Points.setIcon(null);
		// Alle DominoLabels aus der Steinliste loeschen
		dLabels.clear();
		clearHand();
		// Spielfeld wieder an die Ursprungsposition schieben
		graphicsPane.setLocation(-5500, -5500);
		// Alle Labels vom Spielfeld loeschen
		graphicsPane.removeAll();
		// paintingComponent wieder hinzufuegen
		graphicsPane.add(paintingComponent);
		
		updatePanels();		// Grafik auffrischen, um Aenderungen wirksam zu machen
	}
	
	/**
	 * Zeigt den Dominoschriftzug an der zu sehen ist, wenn das Programm startet 
	 */
	public void showStartSplash()
	{
		// Button inaktiv machen
		updateButton(false, "");
		
		int fontSize1 = 0;	// Anfangsschriftgroesse ist 0
		
		lbl_splash1.setVisible(true);	// Label sichtbar machen
		
		// Labeltext setzen
		lbl_splash1.setText("Let's play Domino");
		// Schriftgroesse des Labeltextes auf Anfangsschriftgroesse setzen
		lbl_splash1.setFont(lbl_splash1.getFont().deriveFont((float)fontSize1));
		
		// Schleife von 0 bis 255 (RGB- und Alpha-Werte)
		for (int i = 0; i <= 255; i++)
		{
			// Schriftfarbe des Labels anhand des Schleifenzaehlers aendern
			lbl_splash1.setForeground(new Color(255 - i,(i/2),(i/4),i));
				
			// Variable fuer Schriftgroesse erhoehen, bis maximal 100
			if (i % 3 == 0 && fontSize1 < 100)
				fontSize1++;
			
			// Schrift vergroessern
			lbl_splash1.setFont(lbl_splash1.getFont().deriveFont((float)fontSize1));
			
			delay(5);	// Verzoegerung vor dem naechsten Schleifendurchlauf
		}
		
		delay(1500);	// Zeit, die der Schriftzug in voller Groesse dargestellt wird
		
		// umgekehrte Schleife fuer verkleinerung der Schrift
		for (int i = 255; i >= 0; i--)
		{
			lbl_splash1.setForeground(new Color(255 - i,(i/2),(i/4),i));
			if (i % 3 == 0 && fontSize1 >= 0)
			{
				fontSize1--;
			}
				
			lbl_splash1.setFont(lbl_splash1.getFont().deriveFont((float)fontSize1));
			
			delay(1);	// laeuft schneller ab, da Verzoegerung geringer
		}
		// zum Schluss wird das Label ausgeblendet
		lbl_splash1.setVisible(false);
		
		delay(300);	// Verzoegerung, bevor die Methode verlassen wird
	}
	
	/**
	 * Stellt diverse Meldungen auf dem Bildschirm dar
	 * @param text1 - Der Text, der in dem oberen Label stehen soll
	 * @param text2 - Der Text, der in dem unteren Label stehen soll
	 * @param textSize - die Textgroesse
	 * @param delay - Die Zeit, die verstreichen soll, bevor die Meldung angezeigt wird (in Millisekunden)
	 * @param fadeOut - Gibt an, ob die dargestellte Meldung wieder ausgeblendet werden soll
	 */
	public void showGameInfo(final String text1, final String text2, 
			final float textSize, int delay, boolean fadeOut)
	{
		Timer t = new Timer(true);	// Der Timer fuer das ein- bzw. ausblenden
		
		// Die Aufgabe, die der Timer nach der Verzoegerung ausfuehren soll
		t.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				// Textgroesse setzen
				lbl_splash1.setFont(lbl_splash1.getFont().deriveFont(textSize));
				lbl_splash2.setFont(lbl_splash2.getFont().deriveFont(textSize));
				// Textfarbe setzen
				lbl_splash1.setForeground(Color.BLACK);
				lbl_splash2.setForeground(Color.BLACK);
				// Text setzen
				lbl_splash1.setText(text1);
				lbl_splash2.setText(text2);
				// Label anzeigen
				lbl_splash1.setVisible(true);
				lbl_splash2.setVisible(true);
			}
		}, delay);
		
		// wenn die Meldung ausgeblendet werden soll
		if (fadeOut)
		{
			t.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					// Label nach Verzoegerung wieder ausblenden
					lbl_splash1.setVisible(false);
					lbl_splash2.setVisible(false);
				}
			}, delay + 2000);
		}
	}
	
	/**
	 * Laesst den Thread fuer die angebene Zeit pausieren, um eine
	 * Verzoegerung zu erreichen (nur fuer den Splashscreen zu Beginn des Spiels)
	 * @param milliseconds - Die Zeit in Millisekunden, die der Thread schlafen soll
	 */
	public void delay(int milliseconds)
	{
		try
		{
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}