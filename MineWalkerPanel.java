import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MineWalker game
 * 
 * @author Eric
 *
 */
public class MineWalkerPanel extends JPanel implements ActionListener,
		ChangeListener {
	private int DELAY = 100, count = 0;
	private int gridDim, initGridDim = 10, percentMines, numOfMines, x, y,
			tempDangerCount, livesCount, score;
	private JPanel gridPanel, keyPanel, keyGrid, westOptionPanel,
			southOptionPanel;
	private JButton[][] grid;
	private JButton newGameButton, showMinesButton, showPathButton;
	private JTextArea textArea;
	private JSlider slider;
	private JScrollPane scroll;
	private TitledBorder textAreaTitle, percentMinesTitle, keyTitle;
	private JLabel zeroMines, oneMine, twoMines, threeMines, explodedMine,
			livesLabel, scoreLabel, yourPositionKey, home;
	private Hashtable<Integer, JLabel> labelTable;
	private Font font1;
	private boolean gameOver;
	private RandomWalk walk;
	private ArrayList<Point> path, mines, validMoves, craters;
	private Point yourPosition, tempP, prevPosition;
	private Random rand;
	private Color tempColor;

	public MineWalkerPanel() {
		init();
		startAnimation();
	}

	/**
	 * Create an animation thread that runs periodically
	 */
	private void startAnimation() {
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				repaint();
			}
		};
		new Timer(DELAY, taskPerformer).start();
	}

	/**
	 * Switches color of position JButton Text
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		count = (count + 1) % 10;
		if (count < 5) {
			grid[x][y].setForeground(Color.blue);
		} else {
			grid[x][y].setForeground(Color.white);
		}
	}

	/**
	 * Initial settings
	 */
	public void init() {
		this.setPreferredSize(new Dimension(1000, 700));
		this.setLayout(new BorderLayout());
		rand = new Random();
		font1 = new Font("Arial", 1, 11);
		yourPosition = new Point(x, y);
		prevPosition = new Point();
		validMoves = new ArrayList<Point>();
		craters = new ArrayList<Point>();
		gridDim = initGridDim;
		this.add(genGridPanel(), BorderLayout.CENTER);
		this.add(genWestOptionPanel(), BorderLayout.WEST);
		this.add(genSouthOptionPanel(), BorderLayout.SOUTH);
		this.add(genKeyPanel(), BorderLayout.EAST);
		showMinesButton.setEnabled(false);
		showPathButton.setEnabled(false);

	}

	/**
	 * generates the key panel
	 * 
	 * @return keyPanel
	 */
	private Component genKeyPanel() {
		keyPanel = new JPanel();
		keyPanel.setBackground(Color.lightGray);
		zeroMines = new JLabel("       0 Live Mines");
		zeroMines.setBackground(Color.green);
		zeroMines.setOpaque(true);
		oneMine = new JLabel("       1 Live Mine");
		oneMine.setBackground(Color.yellow);
		oneMine.setOpaque(true);
		twoMines = new JLabel("       2 Live Mines");
		twoMines.setBackground(Color.orange);
		twoMines.setOpaque(true);
		threeMines = new JLabel("       3 Live Mines");
		threeMines.setBackground(Color.red);
		threeMines.setOpaque(true);
		explodedMine = new JLabel("   EXPLODED MINE!");
		explodedMine.setForeground(Color.white);
		explodedMine.setBackground(Color.black);
		explodedMine.setOpaque(true);
		yourPositionKey = new JLabel("  X = Your Position");
		yourPositionKey.setForeground(Color.blue);
		home = new JLabel("             HOME");
		home.setForeground(Color.white);
		home.setBackground(Color.blue);
		home.setOpaque(true);
		keyGrid = new JPanel();
		keyGrid.setLayout(new GridLayout(7, 1));
		keyGrid.setPreferredSize(new Dimension(150, 200));
		keyGrid.add(zeroMines);
		keyGrid.add(oneMine);
		keyGrid.add(twoMines);
		keyGrid.add(threeMines);
		keyGrid.add(explodedMine);
		keyGrid.add(home);
		keyGrid.add(yourPositionKey);
		keyTitle = new TitledBorder("COLOR KEY");
		keyTitle.setTitleJustification(TitledBorder.CENTER);
		keyTitle.setTitleFont(font1);
		keyGrid.setBorder(keyTitle);
		keyPanel.add(keyGrid);
		keyPanel.setBorder(BorderFactory.createEmptyBorder(150, 20, 10, 5));
		return keyPanel;
	}

	/**
	 * generates west option panel with score and buttons to start game, show
	 * mines, show path
	 * 
	 * @return westOptionPanel
	 */
	private Component genWestOptionPanel() {
		westOptionPanel = new JPanel();
		westOptionPanel.setBackground(Color.lightGray);
		westOptionPanel.setLayout(new GridLayout(6, 1, 0, 50));
		westOptionPanel.setBorder(BorderFactory
				.createEmptyBorder(10, 5, 10, 30));
		newGameButton = new JButton("New Game");
		showMinesButton = new JButton("Show Mines");
		showPathButton = new JButton("Show Path");
		newGameButton.addActionListener(this);
		showMinesButton.addActionListener(this);
		showPathButton.addActionListener(this);
		livesLabel = new JLabel("Lives: " + livesCount);
		scoreLabel = new JLabel("Score: " + score);
		livesLabel.setFont(new Font("Arial", 1, 20));
		scoreLabel.setFont(new Font("Arial", 1, 20));
		westOptionPanel.add(newGameButton);
		westOptionPanel.add(showMinesButton);
		westOptionPanel.add(showPathButton);
		westOptionPanel.add(livesLabel);
		westOptionPanel.add(scoreLabel);
		return westOptionPanel;
	}

	/**
	 * generates south panel with difficulty setting slider and grid size
	 * textbox
	 * 
	 * @return southOptionPanel
	 */
	private Component genSouthOptionPanel() {
		southOptionPanel = new JPanel();
		southOptionPanel.setBackground(Color.lightGray);
		textArea = new JTextArea(1, 6);
		textArea.setEditable(true);
		textArea.setText(Integer.toString(initGridDim));
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		southOptionPanel.add(textArea);
		scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(100, 60));
		textAreaTitle = new TitledBorder("Grid Size");
		textAreaTitle.setTitleJustification(TitledBorder.CENTER);
		textAreaTitle.setTitleFont(font1);
		scroll.setBorder(textAreaTitle);
		southOptionPanel.add(scroll);
		// difficulty slider stuff
		slider = new JSlider(JSlider.HORIZONTAL, 10, 50, 25);
		slider.setName("Percent Mines");
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(2);
		slider.setPaintTicks(true);
		percentMinesTitle = new TitledBorder("Percent Mines");
		percentMinesTitle
				.setTitle("Percent Mines - " + getPercentMines() + "%");
		percentMinesTitle.setTitleJustification(TitledBorder.CENTER);
		percentMinesTitle.setTitleFont(font1);
		slider.setBorder(percentMinesTitle);
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(10, new JLabel("Easy"));
		labelTable.put(50, new JLabel("Hard"));
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
		slider.addChangeListener(this);
		southOptionPanel.add(slider);
		return southOptionPanel;
	}

	/**
	 * generates game grid
	 * 
	 * @return
	 */
	private Component genGridPanel() {
		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(gridDim, gridDim));
		gridPanel.setBorder(BorderFactory.createEtchedBorder(2, Color.red,
				Color.black));
		grid = new JButton[gridDim][gridDim];
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid.length; col++) {
				grid[col][row] = new JButton("");
				grid[col][row].addActionListener(this);
				gridPanel.add(grid[col][row]);
				grid[col][row].setBackground(Color.gray);
				// changes the margin so that ... doesn't appear so easily
				grid[col][row].setMargin(new Insets(0, 0, 0, 0));
			}
		}
		return gridPanel;
	}

	/**
	 * establishes a path to the corner
	 */
	private void makePath() {
		walk = new RandomWalk(gridDim);
		walk.createWalk();
		path = walk.getPath();
	}

	/**
	 * makes path visible
	 */
	private void showPath() {
		if (showPathButton.getText() == "Show Path") {
			showPathButton.setText("Hide Path");
			for (Point p : path) {
				grid[p.x][p.y].setText("O");
				grid[p.x][p.y].setForeground(Color.black);
			}
		} else if (showPathButton.getText() == "Hide Path") {
			showPathButton.setText("Show Path");
			for (Point p : path) {
				grid[p.x][p.y].setText("");
			}
		}
		grid[x][y].setText("X");
	}

	/**
	 * makes mines visible
	 */
	private void showMines() {
		if (showMinesButton.getText() == "Show Mines") {
			showMinesButton.setText("Hide Mines");
			for (Point p : mines) {
				grid[p.x][p.y].setText("!M!");
			}
		} else if (showMinesButton.getText() == "Hide Mines") {
			showMinesButton.setText("Show Mines");
			for (Point p : mines) {
				grid[p.x][p.y].setText("");
			}
		}

	}

	/**
	 * places mines around path
	 */
	private void setMines() {
		mines = new ArrayList<Point>();
		numOfMines = (int) ((gridDim * gridDim - path.size()) * percentMines
				* .01 + 0.5);
		while (mines.size() < numOfMines) {

			int tempX = rand.nextInt(gridDim);
			int tempY = rand.nextInt(gridDim);
			tempP = new Point(tempX, tempY);
			if (!path.contains(tempP) && !mines.contains(tempP)) {
				mines.add(tempP);
			}
			if (mines.size() == numOfMines) {
				break;

			}
		}
	}

	/**
	 * begins a new game, resetting the grid size and %mines to specified values
	 */
	private void startNewGame() {
		score = 500;
		craters.clear();
		livesCount = 5;
		showMinesButton.setEnabled(true);
		showPathButton.setEnabled(true);
		slider.setEnabled(false);
		textArea.setEnabled(false);
		showMinesButton.setText("Show Mines");
		showPathButton.setText("Show Path");
		livesLabel.setText("Lives: " + livesCount);
		scoreLabel.setText("Score: " + score);
		gameOver = false;
		getPercentMines();
		percentMinesTitle.setTitle("Percent Mines - " + percentMines + "%");
		try {
			gridDim = Integer.parseInt(textArea.getText());
			if (gridDim < 3 || gridDim > 50) {
				gridDim = initGridDim;
				textArea.setText(Integer.toString(initGridDim)
						+ " -Exceeded Range: [3,50]");
			}
		} catch (Exception ee) {
			gridDim = initGridDim;
			textArea.setText(Integer.toString(initGridDim)
					+ " -Error! Integers only. No Spaces.");
		}
		newGameButton.setText("Give Up");
		remove(gridPanel);
		genGridPanel();
		this.add(genGridPanel(), BorderLayout.CENTER);
		makePath();
		setMines();
		x = gridDim - 1;
		y = gridDim - 1;
		yourPosition.move(x, y);
		grid[x][y].setText("X");
		grid[x][y].setBackground(getColor());
		grid[x][y].setForeground(Color.blue);
		revalidate();

	}

	/**
	 * determines color of current position according to number of surrounding
	 * live mines
	 * 
	 * @return color
	 */
	private Color getColor() {

		for (Point p : getValidMoves()) {
			if (mines.contains(p)) {
				tempDangerCount++;
			}
		}

		if (livesCount <= 0) {
			tempDangerCount = -2;
		}

		switch (tempDangerCount) {
		case -2:
			tempColor = Color.black;
			grid[x][y].setBackground(tempColor);
			JOptionPane
					.showMessageDialog(this,
							"YOU'VE EXPLODED ALL YOUR LIVES AWAY! \n                         GAMEOVER");
			gameOver = true;
		case 0:
			tempColor = Color.green;
			break;
		case 1:
			tempColor = Color.yellow;
			break;
		case 2:
			tempColor = Color.orange;
			break;
		case 3:
			tempColor = Color.red;
			break;
		default:
			break;
		}
		tempDangerCount = 0;

		if (yourPosition.getX() == 0 && yourPosition.getY() == 0) {
			grid[x][y].setBackground(Color.blue);
			score += livesCount * 100;
			JOptionPane.showMessageDialog(this,
					"              YOU WON! \nLives Left: " + livesCount
							+ " (+" + livesCount * 100
							+ " BONUS!) \nFinal Score: " + score + " points!");
			scoreLabel.setText("Score: " + score);
			gameOver = true;
		}

		return tempColor;
	}

	/**
	 * retrieves the value of %mines specified by the user from the slider
	 * 
	 * @return percentMines
	 */
	private int getPercentMines() {
		percentMines = slider.getValue();
		return percentMines;
	}

	/**
	 * determines valid moves (stay within the grid, can't step on mines twice)
	 * 
	 * @return validMoves
	 */
	private ArrayList<Point> getValidMoves() {
		validMoves.clear();
		if (x + 1 < grid.length) {
			validMoves.add(new Point(x + 1, y));
		}
		if (x - 1 >= 0) {
			validMoves.add(new Point(x - 1, y));
		}
		if (y + 1 < grid.length) {
			validMoves.add(new Point(x, y + 1));
		}
		if (y - 1 >= 0) {
			validMoves.add(new Point(x, y - 1));
		}
		validMoves.removeAll(craters);
		return validMoves;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == newGameButton) {
			if (newGameButton.getText() == "New Game") {
				startNewGame();
			} else if (newGameButton.getText() == "Give Up") {
				newGameButton.setText("New Game");
				gameOver = true;
			}
		} else if (e.getSource() == showMinesButton && !gameOver) {
			showMines();
		} else if (e.getSource() == showPathButton) {
			showPath();
		}
		if (newGameButton.getText() == "Give Up") {
			for (Point p : getValidMoves()) {
				if (e.getSource() == grid[p.x][p.y]) {
					if (mines.contains(p) && !craters.contains(p)) {
						grid[p.x][p.y].setBackground(Color.black);
						craters.add(p);
						livesCount--;
						score -= 100;
						break;
					}
					prevPosition.move(x, y);
					grid[x][y].setText("");
					x = p.x;
					y = p.y;
					grid[x][y].setText("X");
					yourPosition.move(x, y);
					score -= 1;
				}
			}
			livesLabel.setText("Lives: " + livesCount);
			scoreLabel.setText("Score: " + score);
			grid[x][y].setBackground(getColor());
			grid[x][y].setForeground(Color.blue);

		}
		if (gameOver) {
			newGameButton.setText("New Game");
			slider.setEnabled(true);
			textArea.setEnabled(true);
			if (showMinesButton.getText() == "Show Mines"
					&& e.getSource() != showMinesButton
					&& e.getSource() != showPathButton) {
				showMines();
			}
			if (e.getSource() == showMinesButton) {
				showMines();
			}

		}

	}

	public void stateChanged(ChangeEvent e) {
		if (slider.getValueIsAdjusting()) {
			percentMinesTitle.setTitle("Percent Mines - " + getPercentMines()
					+ "%");
		}
	}

}
