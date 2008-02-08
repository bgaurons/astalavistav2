/**
 * 
 */
package mapLearner;

import service.*;
import motorDriver.*;
import sonarSensor.*;
import coreProcessor.*;

/**
 * @author Cindy
 * 
 */

/*
 * The initial area of the floor is known and the area is divided into a grid.
 * The number of cells in the Grid and the size of a single cell has to be
 * decided. For testing, I have hardcoded a 3*4 sized grid.
 */

public class MapLearnerService extends Service {
	// Number of Rows
	static int n = 3;
	// Number of Columns
	static int m = 4;

	/*
	 * Stacks to maintain a history of cells needed to backtrack
	 */
	// Stack for the X-coordinate
	int topX = 0;
	int[] stackX = new int[n * m];
	// Stack for the Y-coordinate
	int topY = 0;
	int[] stackY = new int[m * n];

	// Declare a Grid of size n * m and allocate memory for the Grid
	int[][] grid = new int[n][m];
	
	private SonarSensorService sonarSensorService;
	private MotorDriverService motorDriverService;
	public CoreProcessor coreProcessor;

	public MapLearnerService(CoreProcessor coreProcessor, SonarSensorService sonarSensorService, MotorDriverService motorDriverService) {
		// Initializing the Grid
		// Initially, '2' is placed in all the cells of the Grid
		// This denotes that the cell is untraversed
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				grid[i][j] = 2;
			}
		}

		// Initializing the Stack for X-Coordinates of the Cell
		for (int i = 0; i < n * m; i++) {
			stackX[i] = -1;
		}

		// Initializing the Stack for Y-Coordinates of the cell
		for (int i = 0; i < m * n; i++) {
			stackY[i] = -1;
		}
		
		this.sonarSensorService = sonarSensorService;
		this.motorDriverService = motorDriverService;
		this.coreProcessor = coreProcessor;
	}

	public void run() {
		// Printing the Grid
		System.out.print("\nInitialized Map...Before the Learning Phase\n");
		printMap();

		// Make a call to the Recursive Function to Learn the Floor Plan
		System.out.print("\n.....Learning the Floor Plan\n");

		// Make a call to the Recursive Function to Learn the Floor Plan
		System.out.print("\n.....Learning the Floor Plan\n");

		// Initially, the Robot is placed in the cell 0,0 facing the "NORTH"
		// direction
		int x = 0;
		int y = 0;
		String direction = "NORTH";

		traverseArea(x, y, direction);

		/*
		 * Some of the cells with obstacles are unreachable as these are
		 * surrounded by other cells with obstacles. Such unreachable cells
		 * cannot be traversed by the recursive floor learning algorithm. Such
		 * cells have to be marked as occupied after the recursive floor
		 * learning algorithm is complete. Mark such cells as 'Occupied' by
		 * placing a '1'
		 */
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (grid[i][j] == 2) {
					grid[i][j] = 0;
				}
			}
		}

		// The Learning Phase is complete. Print the learnt floor map.
		System.out.print("\n.....Learning Phase Complete...\n");
		System.out.print("\nPrinting the learnt map\n");
		printMap();
	}

	public void Stop() {

	}

	/*
	 * Recursive Function to Learn the Floor Plan
	 */
	public void traverseArea(int x, int y, String direction) {
		// Temporary Variables
		int scanX, scanY;

		/*
		 * The Robot is currently placed in the cell (x,y) facing the direction
		 * 'direction'
		 */

		// Mark the Current Cell as Unoccupied by placing a '1'.
		grid[x][y] = 1;
		System.out.print("\nCurrent cell of the Robot....." + x + " , " + y
				+ "\n");
		System.out.print("Robot is facing..." + direction + "\n");
		System.out.print("Cell " + x + ", " + y + " is marked as unoccupied\n");

		// Printing the intermediate Floor plan
		System.out.print("\nIntermediate Floor Plan...\n");
		printMap();

		/*
		 * Note: - We consider the cell as invalid if it lies outside the cell
		 * boundary
		 */

		/** ******************WEST DIRECTION *********************** */
		// If cell is valid and untraversed (A cell is untraversed if
		// it is marked with '2')
		if ((y - 1) >= 0 && grid[x][y - 1] == 2) {
			// If the Robot is currently not facing WEST,
			// then orient the Robot in the WEST direction
			if (direction != "WEST") {
				turnRobot(direction, "WEST");
				direction = "WEST";
				System.out.print("\nRobot has now turned to face " + direction
						+ "...\n");
			}

			// Check for Obstacles in the WEST direction
			scanX = x;
			scanY = y - 1;
			System.out.print("\nScanning cell....." + scanX + " , " + scanY
					+ "\n");
			// No obstacles are found in this cell
			if (!detectObstacles(x, y - 1)) {
				System.out.print("No Obstacle is found in cell " + scanX + ", "
						+ scanY);
				// Remember this cell by placing the coordinate values on the
				// stack
				pushStackX(x);
				pushStackY(y);
				// Move the Robot to this cell
				moveRobot(x, y - 1);
				System.out.print("\nRobot moves to cell ....." + scanX + " , "
						+ scanY + "\n");
				System.out.print("Robot is facing..." + direction + "\n");
				// Make a call to the recursive floor learning algorithm to
				// continue the learning process
				traverseArea(x, y - 1, direction);
			}
			// An obstacle is found in this cell
			else {
				System.out.print("An Obstacle is found in cell " + scanX + ", "
						+ scanY + "...Mark the cell as Occupied\n");
				// Mark this cell as occupied by placing a '0'
				grid[x][y - 1] = 0;
				// Printing the intermediate floor plan
				System.out.print("\nIntermediate Floor Plan...\n");
				printMap();
			}
		}

		/** ******************NORTH DIRECTION *********************** */
		// If cell is valid and untraversed (A cell is untraversed if
		// it is marked with '2')
		if ((x + 1) < n && grid[x + 1][y] == 2) {
			// If the Robot is currently not facing NORTH, then orient
			// the Robot in the NORTH direction
			if (direction != "NORTH") {
				turnRobot(direction, "NORTH");
				direction = "NORTH";
				System.out.print("\nRobot has now turned to face " + direction
						+ "...\n");
			}

			// Check for Obstacles in the NORTH direction
			scanX = x + 1;
			scanY = y;
			System.out.print("\nScanning cell....." + scanX + " , " + scanY
					+ "\n");
			// No obstacles are found in this cell
			if (!detectObstacles(x + 1, y)) {
				System.out.print("No Obstacle is found in cell " + scanX + ", "
						+ scanY);
				// Remember this cell by placing the coordinate values on the
				// stack
				pushStackX(x);
				pushStackY(y);
				// Move the Robot to this cell
				moveRobot(x + 1, y);
				System.out.print("\nRobot moves to cell ....." + scanX + " , "
						+ scanY + "\n");
				System.out.print("Robot is facing..." + direction + "\n");
				// Make a call to the recursive floor learning algorithm to
				// continue the learning process
				traverseArea(x + 1, y, direction);
			}
			// An obstacle is found in this cell
			else {
				System.out.print("An Obstacle is found in cell " + scanX + ", "
						+ scanY + "....Mark the cell as Occupied\n");
				// Mark this cell as occupied by placing a '0'
				grid[x + 1][y] = 0;
				// Printing the intermediate floor plan
				System.out.print("\nIntermediate Floor Plan...\n");
				printMap();
			}
		}

		/** ******************EAST DIRECTION *********************** */
		// If cell is valid and untraversed (A cell is untraversed if
		// it is marked with '2')
		if ((y + 1) < m && grid[x][y + 1] == 2) {
			// If the Robot is currently not facing EAST, then orient
			// the Robot in the EAST direction
			if (direction != "EAST") {
				turnRobot(direction, "EAST");
				direction = "EAST";
				System.out.print("\nRobot has now turned to face " + direction
						+ "...\n");
			}

			// Check for obstacles in the EAST direction
			scanX = x;
			scanY = y + 1;
			System.out.print("\nScanning cell....." + scanX + " , " + scanY
					+ "\n");
			// No obstacles are found in this cell
			if (!detectObstacles(x, y + 1)) {
				System.out.print("No Obstacle is found in cell " + scanX + ", "
						+ scanY);
				// Remember this cell by placing the coordinate values on the
				// stack
				pushStackX(x);
				pushStackY(y);
				// Move the Robot to this cell
				moveRobot(x, y + 1);
				System.out.print("\nRobot moves to cell ....." + scanX + " , "
						+ scanY + "\n");
				System.out.print("Robot is facing..." + direction + "\n");
				// Make a call to the recursive floor learning algorithm to
				// continue the learning process
				traverseArea(x, y + 1, direction);
			}
			// An obstacle is found in this cell
			else {
				System.out.print("An Obstacle is found in cell " + scanX + ", "
						+ scanY + "....Mark the cell as Occupied\n");
				// Mark this cell as occupied by placing a '0'
				grid[x][y + 1] = 0;
				// Printing the intermediate floor plan
				System.out.print("\nIntermediate Floor Plan...\n");
				printMap();
			}
		}

		/** ******************SOUTH DIRECTION *********************** */
		// If cell is valid and untraversed (A cell is untraversed if
		// it is marked with '2')
		if ((x - 1) >= 0 && grid[x - 1][y] == 2) {
			// If the Robot is currently not facing SOUTH, then orient
			// the Robot in the SOUTH direction
			if (direction != "SOUTH") {
				turnRobot(direction, "SOUTH");
				direction = "SOUTH";
				System.out.print("\nRobot has now turned to face " + direction
						+ "...\n");
			}

			// Check for obstacles in the SOUTH direction
			int temp2 = x - 1;
			scanX = x - 1;
			scanY = y;
			System.out.print("\nScanning cell....." + scanX + " , " + scanY
					+ "\n");
			// No obstacles are found in this cell
			if (!detectObstacles(x - 1, y)) {
				System.out.print("No Obstacle is found in cell " + scanX + ", "
						+ scanY);
				// Remember this cell by placing the coordinate values on the
				// stack
				pushStackX(x);
				pushStackY(y);
				// Move the Robot to this cell
				moveRobot(x - 1, y);
				System.out.print("\nRobot moves to cell ....." + scanX + " , "
						+ scanY + "\n");
				System.out.print("Robot is facing..." + direction + "\n");
				// Make a call to the recursive floor learning algorithm to
				// continue the learning process
				traverseArea(x - 1, y, direction);
			}
			// An obstacle is found in this cell
			else {
				System.out.print("An Obstacle is found in cell " + scanX + ", "
						+ scanY + "....Mark the cell as Occupied\n");
				// Mark this cell as occupied by placing a '0'
				grid[x - 1][y] = 0;
				// Printing the intermediate floor plan
				System.out.print("\nIntermediate Floor Plan...\n");
				printMap();
			}
		}

		// Making the Robot physically backtrack in accordance with
		// the backtracking of the recursion using the stacks StackX and StackY
		if (topX != 0 && topY != 0) {
			System.out.print("\nBacktracking....\n");
			int backTrackX = popStackX();
			int backTrackY = popStackY();
			String nextDirection = calculateNextDirection(x, y, backTrackX,
					backTrackY);

			// Now the Robot has to backtrack. Turn the Robot in the appropriate
			// direction if it is not oriented correctly
			if (direction != nextDirection) {
				turnRobot(direction, nextDirection);
				direction = nextDirection;
				System.out.print("\nRobot has now turned to face " + direction
						+ "...\n");
			}

			// Move the robot to a previous cell (backtrack)
			moveRobot(backTrackX, backTrackY);
			System.out.print("\nRobot moves to cell ....." + backTrackX + " , "
					+ backTrackY + "\n");
			System.out.print("Robot is facing..." + direction + "\n");
		}
	}

	public void turnRobot(String currentDirection, String nextDirection) {

		int degree = 0;

		if ((currentDirection == "NORTH" && nextDirection == "EAST")
				|| (currentDirection == "EAST" && nextDirection == "SOUTH")
				|| (currentDirection == "SOUTH" && nextDirection == "WEST")
				|| (currentDirection == "WEST" && nextDirection == "NORTH")) {
			degree = 90;
		}

		if ((currentDirection == "NORTH" && nextDirection == "WEST")
				|| (currentDirection == "WEST" && nextDirection == "SOUTH")
				|| (currentDirection == "SOUTH" && nextDirection == "EAST")
				|| (currentDirection == "EAST" && nextDirection == "NORTH")) {
			degree = -90;
		}

		if ((currentDirection == "NORTH" && nextDirection == "SOUTH")
				|| (currentDirection == "EAST" && nextDirection == "WEST")
				|| (currentDirection == "WEST" && nextDirection == "EAST")
				|| (currentDirection == "SOUTH" && nextDirection == "NORTH")) {
			degree = 180;
		}

		//!!!!!!!
		/*
		 * COMMUNICATE WITH THE MOTOR FUNCTION SO THAT THE ROBOT CAN TURN IN
		 * ACCORDANCE WITH THE DEGREE MENTIONED. USE AN EVENT DRIVEN MODEL TO
		 * ACCOMPLISH THIS TASK
		 */
		//this.motorDriverService.setTurn(degree);
	}

	public boolean detectObstacles(int x, int y) {
		/*
		 * Currently, I have hard coded few cells to have obstacles..
		 * Accordingly, this function will return a boolean value This is just
		 * for the purpose of testing...In reality,
		 * 
		 * THIS FUNCTION WILL COMMUNICATE WITH THE SONAR SENSORS TO DETECT FOR
		 * ANY OBSTACLES IN THE CELL(X,Y)...
		 * 
		 * When this function is called the robot is oriented in the right
		 * direction and ready to scan for obstaclces.
		 * 
		 * NOTE: USE AN EVENT DRIVEN MODEL TO ACCOMPLISH THIS TASK
		 */
		
		//!!!!!!!
		if (this.sonarSensorService.DistanceValue(false, 0) <= 100)
			return true;
		else
			return false;
	}

	/*
	 * Move the Robot to the required cell
	 */
	public void moveRobot(int x, int y) {
		/*
		 * THIS FUNCTION WILL COMMUNICATE WITH THE MOTOR FUNCTION TO PHYSICALLY
		 * MOVE THE ROBOT TO THIS CELL
		 * 
		 * The Robot shall be facing in the right direction and will be ready to
		 * move.
		 * 
		 * NOTE: USE AN EVENT DRIVEN MODEL TO ACCOMPLISH THIS TASK
		 */
		//this.motorDriverService.setForward(0);
	}

	/*
	 * Push the X-coordinate of a cell in the stack... This stack is used to
	 * physically backtrack the robot
	 */
	public void pushStackX(int x) {
		stackX[topX] = x;
		topX++;
	}

	/*
	 * Push the Y-coordinate of a cell in the Stack.... This stack is used to
	 * physically backtrack the Robot
	 */
	public void pushStackY(int y) {
		stackY[topY] = y;
		topY++;
	}

	/*
	 * Pop X-coordinate of a cell from the stack
	 */
	public int popStackX() {
		topX--;
		int x = stackX[topX];
		stackX[topX] = -1;
		return x;
	}

	/*
	 * Pop Y-coordinate of a cell from the stack
	 */
	public int popStackY() {
		topY--;
		int y = stackY[topY];
		stackY[topY] = -1;
		return y;
	}

	/*
	 * The coordinates of the current cell of the robot and the coordinates of
	 * the cell where the robot has to move next, are passed to this function.
	 * Based on this information, calculate the direction that the robot has to
	 * face so that the robot can be oriented (turned) in the correct direction
	 * in order to move to this cell.
	 */
	public String calculateNextDirection(int currentX, int currentY,
			int nextX, int nextY) {
		String nextDirection = "";

		// Next Direction is West
		if ((currentX == nextX) && (currentY > nextY)) {
			nextDirection = "WEST";
		}

		// Next Direction is North
		if ((currentX < nextX) && (currentY == nextY)) {
			nextDirection = "NORTH";
		}

		// Next Direction is East
		if ((currentX == nextX) && (currentY < nextY)) {
			nextDirection = "EAST";
		}

		// Next Direction is South
		if ((currentX > nextX) && (currentY == nextY)) {
			nextDirection = "SOUTH";
		}
		return nextDirection;
	}

	/*
	 * Printing the Floor Map
	 */
	public void printMap() {
		for (int i = n - 1; i >= 0; i--) {
			for (int j = 0; j < m; j++) {
				System.out.print(grid[i][j] + "\t");
			}
			System.out.print("\n");
		}
	}

}
