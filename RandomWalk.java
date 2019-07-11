import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;

/**
 * 
 * @author Eric
 *
 */

public class RandomWalk {
	private int gridSize; // number of dots along one side of grid
	private long seed; // random generator seed
	private Random rand; // for determining east or south
	private boolean done; // for determining if walk is done
	private int x;
	private int y;
	private int stepSize;
	private Point start;
	private Point nextPoint;
	private ArrayList<Point> path;

	/**
	 * Constructs RandomWalk, starting path at (0,0) without seed
	 * 
	 * @param aGridSize
	 */
	public RandomWalk(int aGridSize) {
		gridSize = aGridSize;
		stepSize = 1;
		x = 0;
		y = 0;
		done = false;
		start = new Point(x, y);
		path = new ArrayList<Point>();
		path.add(start);
		rand = new Random();
	}

	/**
	 * Constructs RandomWalk, starting path at (0,0) with a seed
	 * 
	 * @param aGridSize
	 * @param aSeed
	 */
	public RandomWalk(int aGridSize, long aSeed) {
		gridSize = aGridSize;
		seed = aSeed;
		stepSize = 1;
		x = 0;
		y = 0;
		done = false;
		start = new Point(x, y);
		path = new ArrayList<Point>();
		path.add(start);
		rand = new Random(seed);
	}

	/**
	 * Makes walk go one more step, adding point to ArrayList
	 */
	public void step() {

		if (x < gridSize - 1 && y < gridSize - 1) {
			if (rand.nextDouble() <= .5) {
				this.nextPoint = new Point(x + stepSize, y);
				x += stepSize;
				path.add(this.nextPoint);

			} else {
				this.nextPoint = new Point(x, y + stepSize);
				y += stepSize;
				path.add(this.nextPoint);

			}
		} else if (x == gridSize - 1 && y < gridSize - 1) {
			this.nextPoint = new Point(x, y + stepSize);
			y += stepSize;
			path.add(this.nextPoint);
		} else if (y == gridSize - 1 && x < gridSize - 1) {
			this.nextPoint = new Point(x + stepSize, y);
			x += stepSize;
			path.add(this.nextPoint);
		} else if (x == gridSize - 1 && y == gridSize - 1) {
			done = true;
		}

	}

	/**
	 * Creates entire walk using step()
	 */
	public void createWalk() {

		while (done == false) {
			step();

		}

	}

	/**
	 * Boolean, true if the random walk is complete
	 * 
	 * @return done
	 */
	public boolean isDone() {

		return this.done;
	}

	/**
	 * Gets array of points in path
	 * 
	 * @return path
	 */
	public ArrayList<Point> getPath() {

		return this.path;
	}

	public String toString() {
		// Returns the path as a nicely formatted string as shown below:
		// [0,0] [0,1] [0,2] [1,2] [2,2] [3,2] [4,2] [4,3] [4,4]

		String str = "";
		for (Point p : path) {
			str = str + ("[" + p.x + "," + p.y + "] ");

		}
		return str;

	}
}
