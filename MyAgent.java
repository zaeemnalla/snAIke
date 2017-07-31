import java.io.*;
import java.util.*;
import za.ac.wits.snake.DevelopmentAgent;

public class MyAgent extends DevelopmentAgent  {

	public static void main(String[] args) {
		MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
	}
	
	@Override
    public void run() {
        try ( BufferedReader input = new BufferedReader(new InputStreamReader(System.in)) ) {
        	
// READING
        	
// (start game)
        	
        	// Setting up the play area            
        	String[] setup = input.readLine().split(" ");
    		int n = Integer.parseInt(setup[0]), cols = Integer.parseInt(setup[1]), rows = Integer.parseInt(setup[2]);		
    		int[][] board = new int[rows][cols];
    		/* Blue Apple	0 (set to 0 because I'm treating the apples as open spaces that can be explored)
	 		   Red Apple	0
	 		   Zombies		5,6,7
	 		   Snakes		1,2,3,4 */
    		
    		int myMaxLength = 0;
    		
// (during game)    
    		
        	while (true){
        		
        		// resetting the board
        		for (int i=0; i<rows; i++)
        			for (int j=0; j<cols; j++)
        				board[i][j] = 0;
        		
        		// Apples 
        		String[] apple = input.readLine().split(" ");
        		if (apple[0].contains("Game")){
        			System.out.println("log " + Integer.toString(myMaxLength));
                    break;
        		}
        		Cell blueApple = new Cell( Integer.parseInt(apple[0]) , Integer.parseInt(apple[1]) );
        		if (blueApple.row!=-1)
        			board[blueApple.row][blueApple.col] = 0;
        		apple = input.readLine().split(" ");
        		Cell redApple = new Cell( Integer.parseInt(apple[0]) , Integer.parseInt(apple[1]) );
        		board[redApple.row][redApple.col] = 0;
        		
        		// Zombies
        		Cell[] zombieHeads = new Cell[3];
        		Cell [] zombieKink = new Cell[3];
        		for (int i=0; i<3; i++){
        			String zombieLine = input.readLine();
        			board = drawSnake(zombieLine, i+5, board);
        			String[] zombieSplit = zombieLine.split(" ");
					zombieHeads[i] = new Cell( Integer.parseInt(zombieSplit[0].split(",")[0]), Integer.parseInt(zombieSplit[0].split(",")[1]) );
					zombieKink[i] = new Cell( Integer.parseInt(zombieSplit[1].split(",")[0]), Integer.parseInt(zombieSplit[1].split(",")[1]) );
        		}
        		
        		int meNum = Integer.parseInt(input.readLine());
        		
        		// Snake
        		LinkedList<Cell> snakeHeads = new LinkedList<Cell>();
        		Cell start = new Cell();
        		for (int i=0; i<n; i++){
        			String snake = input.readLine();
        			if (snake.contains("alive")){
        				int spaces = 0, index = 0;
        				for (int j=7; j<snake.length(); j++){
        					if ( snake.charAt(j) == ' ' )
        						spaces = spaces +1;
        					if (spaces == 2){
        						index = j+1;
        						break;
        					}
        				}
        				board = drawSnake(snake.substring(index), i+1, board);
        				if (i!=meNum){
	        				String[] snakeSplit = snake.substring(index).split(" ");
	        				snakeHeads.add( new Cell( Integer.parseInt(snakeSplit[0].split(",")[0]), Integer.parseInt(snakeSplit[0].split(",")[1]) ) );
        				}
        				if (i == meNum){
        					int length = Integer.parseInt(snake.split(" ")[1]);
        					if (length>=myMaxLength)
        						myMaxLength = length;
        					String[] meSnake = snake.substring(index).split(" ");
        					start = new Cell( Integer.parseInt(meSnake[0].split(",")[0]), Integer.parseInt(meSnake[0].split(",")[1]) );
        				}
        			}
        		}        		        	
        		
        		for (Cell z : zombieHeads){
        			z.setNeighbours(board, rows, cols);
        			for (Cell f: z.neighbours)        				
        				board[f.row][f.col] = 8;
        		}
        		        		
        		for (Cell s : snakeHeads){
        			s.setNeighbours(board, rows, cols);
        			for (Cell g : s.neighbours) 
        				board[g.row][g.col] = 8;
        		}
        		board[blueApple.row][blueApple.col] = 0;
        		board[redApple.row][redApple.col] = 0;
        		
        		int[][] boardcpy = new int[rows][cols];
        		for (int i=0; i<rows; i++)
        			for (int j=0; j<cols; j++)
        				boardcpy[i][j] = board[i][j];

// CALCULATING	
        		
        		Stack<Cell> path = BFS(board, rows, cols, start, redApple, blueApple, zombieHeads);		    
	        	// make one move in that path
	        	if ( !path.isEmpty() ){
	        		Cell next = path.peek();	        		
					printmove(next, start);
	        	}
	        	else{
	        		for (Cell s : snakeHeads){
	        			s.neighbours.clear();
	        			s.setNeighbours(boardcpy, rows, cols);
	        			for (Cell z : s.neighbours)
	        				if (boardcpy[z.row][z.col]==8)
	        					boardcpy[z.row][z.col]=0;
	        		}
	        		start.neighbours.clear();
	        		start.setNeighbours(boardcpy, rows, cols);
	        		if (!start.neighbours.isEmpty())
	        			printmove(start.neighbours.get(0), start);
		        	else
		        		System.out.println(5);
	        	}
        
        	}
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	static int[][] drawSnake(String snake, int number, int[][] area){
		String[] coords = snake.split(" ");
		for (int i = 0; i < coords.length-1; i++) {
			area = drawLine(area, coords[i], coords[i+1], number);
		}
		return area;
	}
	
	static int[][] drawLine(int[][] area, String pointa, String pointb, int number){
		int maxRow, minRow, maxCol, minCol;
		String a[] = pointa.split(","), b[] = pointb.split(",");
		
		// get max and min of x
		if (Integer.parseInt(a[0]) >= Integer.parseInt(b[0])){
			maxCol = Integer.parseInt(a[0]);
			minCol = Integer.parseInt(b[0]);
		}
		else{
			maxCol = Integer.parseInt(b[0]);
			minCol = Integer.parseInt(a[0]);
		}
		
		// get max and min of y
		if (Integer.parseInt(a[1]) >= Integer.parseInt(b[1])){
			maxRow = Integer.parseInt(a[1]);
			minRow = Integer.parseInt(b[1]);
		}
		else{
			maxRow = Integer.parseInt(b[1]);
			minRow = Integer.parseInt(a[1]);
		}
		
		// fill up spaces in play area
		for (int i = minRow; i <= maxRow; i++)
			for (int j = minCol; j <= maxCol; j++)
				area[i][j] = number;
		
		return area;
	}

	static Stack<Cell> BFS(int[][] board, int rows, int cols, Cell start, Cell goala, Cell goalb, Cell[] zombieHeads){
		
		Queue<Cell> q = new LinkedList<Cell>();
		Stack<Cell>s = new Stack<Cell>();
		Cell[][] parent = new Cell[rows][cols];
		int[][] distance = new int[rows][cols];
		Cell curr = new Cell();
		
		distance[start.row][start.col] = 0;
		q.add(start);
		while( (!q.isEmpty()) || (board[goala.row][goala.col]==0) || (board[goalb.row][goalb.col]==0) ){
			
			if (q.isEmpty())
				return s;
			
			curr = q.remove();
			curr.setNeighbours(board, rows, cols);
			for (Cell node : curr.neighbours){
					board[node.row][node.col] = 8;
					parent[node.row][node.col] = curr;
					distance[node.row][node.col] = distance[curr.row][curr.col] + 1;
					q.add(node);
			}
			
		} // end while
		
		if ( (board[goala.row][goala.col]==0) && (board[goalb.row][goalb.col]==0) ){
			return s;
		}
		else{
			
			Boolean isGoalA = false, isGoalB = false;
			int count = 0;
			
			for (Cell z : zombieHeads){
				for (Cell f : z.neighbours){
					if (f.row==goala.row && f.col==goala.col){
						isGoalA = true;
						break;
					}
					else if (f.row==goalb.row && f.col==goalb.col){
						isGoalB = true;
						break;
					}
				}
				if (isGoalA || isGoalB)
					break;
			}
			
			if (isGoalA && !isGoalB){
				curr = goalb;
				count = distance[goalb.row][goalb.col];
			}
			else if (!isGoalA && isGoalB){
				curr = goala;
				count = distance[goala.row][goala.col];
			}
			else if (!isGoalA && !isGoalB){
				if ( distance[goala.row][goala.col] < distance[goalb.row][goalb.col] ){
					curr = goala;
					count = distance[goala.row][goala.col];
				}
				else{
					curr = goalb;
					count = distance[goalb.row][goalb.col];
				}
			}
			
			for (int i=1; i<count; i++){
				s.push(curr);
				curr = parent[curr.row][curr.col];
			}
			s.push(curr);
			return s;
		} // end if
		
	} //end function
	
	void printmove(Cell go, Cell start){
		int move = 0;
		if (go.row>start.row)
			move = 1;
		else if (go.row<start.row)
			move = 0;
		else if (go.col<start.col)
			move = 2;
		else if (go.col>start.col)
			move = 3;
		System.out.println(move);
	}
	
}

class Cell {
	
	int row, col;
	Vector<Cell> neighbours = new Vector<Cell>();
	
	public Cell(){
		
	}
	
	public Cell(int col, int row){
		this.col = col;
		this.row = row;
	}
	
	void setNeighbours(int[][] board, int rows, int cols){
		
		// cell above
		Cell above = new Cell();
		above.row = row-1;
		above.col = col;
		validateCell(above, board, rows, cols);
		
		// cell right
		Cell right = new Cell();
		right.col = col+1;
		right.row = row;
		validateCell(right, board, rows, cols);
				
		// cell below
		Cell below = new Cell();
		below.row = row+1;
		below.col = col;
		validateCell(below, board, rows, cols);
				
		// cell left
		Cell left = new Cell();
		left.col = col-1;
		left.row = row;
		validateCell(left, board, rows, cols);
	
	}
	
	void validateCell(Cell tmp, int[][] board, int rows, int cols){
		if (tmp.col>=0 && tmp.col<cols && tmp.row>=0 && tmp.row<rows)
			if (board[tmp.row][tmp.col]==0)
				neighbours.add(tmp);
	}
	
}

















