/*
	Author: Justin Liang
	Description: ThoughtSpot take home assignment "MTS System"
*/

package landscape;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Landscape {
	
	// global variables
	static int rowSize, colSize; // row size and col size of grid
	static int w, g; // water level and gradient threshold
	static Point a, b; // points A and B for question 3
	static int[][] landscape; // 2d array of the whole landscape
	static int[][] visited; // helper 2d array to keep track of visited points for BFS, 0 = unvisited, 1 = visited
	static ArrayList<Lake> lakes = new ArrayList<Lake>(); // an arraylist of lakes, where each lake is just an arraylist of points
		
	// a point contains row and column indexes, z value, and a parent pointer for tracing shortest path
	public static class Point{
		int row, col, elev;
		Point parent = null;
		// Point constructors
		public Point(int r, int c, int z){
			row = r; col = c; elev = z;
		}
		public Point(int r, int c, int z, Point p){
			row = r; col = c; elev = z; parent = p;
		}
		// prints the point's values
		public void printPoint(){
			System.out.println("(" + row + ", " + col + "), z=" + elev);
		}
	}
	
	// a lake contains an ArrayList of points
	public static class Lake{
		ArrayList<Point> lake = new ArrayList<Point>();
		int SA = 0, vol = 0;
		// adds a point to the lake
		public void addPoint(Point p){
			lake.add(p);
		}
		// computes surface area of lake
		public int computeSA(){
			SA = lake.size();
			return SA;
		}
		// computes volume of lake
		public int computeVol(){
			for(int i = 0; i < lake.size(); i++){
				vol += w - lake.get(i).elev; // volume = w - z
			}
			return vol;
		}
		// prints all the points in the lake
		public void printLake(){
			for(int i = 0; i < lake.size(); i++){
				lake.get(i).printPoint();
			}
		}
	}
	
	// method to find all the lakes in the landscape
	public static void findLakes(){
		for(int i = 0; i < rowSize; i++){
			for(int j = 0; j < colSize; j++){
				if(visited[i][j] == 0 && landscape[i][j] < w){ // if the current point is unvisited and contains water, perform a BFS on it
					Lake lake = BFS(i, j);
					lakes.add(lake);
				}
				else{
					visited[i][j] = 1;
				}
			}
		}
	}
	
	// performs a breadth first search to find all adjacent points that are filled with water and unvisited
	// returns a Lake
	public static Lake BFS(int i, int j){
		Lake lake = new Lake(); // create the lake
		Queue<Point> q = new LinkedList<Point>(); // create queue for BFS
		Point p = new Point(i, j, landscape[i][j]); // create point
		visited[i][j] = 1; // mark point as visited
		lake.addPoint(p); // add point to lake
		q.offer(p); // enqueue the point		
		
		// while the queue is not empty
		while(!q.isEmpty()){
			p = q.poll(); // dequeue the point			
			// check points for 3 conditions: (1) not out of bounds (2) not visited (3) below water level			
			//check point above p
			if((p.row-1 < rowSize) && (visited[p.row-1][p.col] == 0) && (landscape[p.row-1][p.col] < w)){ 
				Point newP = new Point(p.row-1, p.col, landscape[p.row-1][p.col]); // create the new point
				lake.addPoint(newP); // add the new point to the lake
				visited[newP.row][newP.col] = 1; // mark the new point as visited
				q.offer(newP); // enqueue the new point
			}
			// check point below p
			if((p.row+1 < rowSize) && (visited[p.row+1][p.col] == 0) && (landscape[p.row+1][p.col] < w)){ 
				Point newP = new Point(p.row+1, p.col, landscape[p.row+1][p.col]); // create the new point
				lake.addPoint(newP); // add the new point to the lake
				visited[newP.row][newP.col] = 1; // mark the new point as visited
				q.offer(newP); // enqueue the new point
			}
			// check point left of p
			if((p.col-1 < colSize) && (visited[p.row][p.col-1] == 0) && (landscape[p.row][p.col-1] < w)){ 
				Point newP = new Point(p.row, p.col-1, landscape[p.row][p.col-1]);
				lake.addPoint(newP);
				visited[newP.row][newP.col] = 1;
				q.offer(newP);
			}
			// check point right of p
			if((p.col+1 < colSize) && (visited[p.row][p.col+1] == 0) && (landscape[p.row][p.col+1] < w)){ 
				Point newP = new Point(p.row, p.col+1, landscape[p.row][p.col+1]);
				lake.addPoint(newP);
				visited[newP.row][newP.col] = 1;
				q.offer(newP);
			}
		}
		lake.SA = lake.computeSA(); // compute the current lake's surface area
		lake.vol = lake.computeVol(); // compute the current lake's volume
		return lake;
	}
	
	// finds lake with largest surface area
	public static Lake findLargestSALake(){
		if(lakes.size() == 0) return null; // if there's no lakes, return null
		int largestSA = 0;
		Lake largestSALake = lakes.get(0);
		for(int i = 0; i < lakes.size(); i++){
			if(lakes.get(i).SA > largestSA){
				largestSA = lakes.get(i).SA;
				largestSALake = lakes.get(i);
			}
		}
		return largestSALake;
	}

	// finds lake with largest volume
	public static Lake findLargestVolLake(){
		if(lakes.size() == 0) return null; // if there's no lakes, return null
		int largestVol = 0;
		Lake largestVolLake = lakes.get(0);
		for(int i = 0; i < lakes.size(); i++){
			if(lakes.get(i).vol > largestVol){
				largestVol = lakes.get(i).vol;
				largestVolLake = lakes.get(i);
			}
		}
		return largestVolLake;
	}
	
	// finds the shortest motorable path between point A and point B using BFS
	public static void findPath(){
		// check if the start and end points are above water
		if(a.elev < w || b.elev < w){
			System.out.println("Cannot find path, one of these points is underwater!");
			return;
		}
		// clear visited grid for reuse
		for(int i = 0; i < rowSize; i++){
			for(int j = 0; j < colSize; j++){
				visited[i][j] = 0;
			}
		}		

		ArrayList<Point> path = new ArrayList<Point>(); // create an ArrayList for the reverse path
		Queue<Point> q = new LinkedList<Point>(); // create queue for BFS
		Point p = a; // point p is starting point
		visited[p.row][p.col] = 1; // mark point as visited
		q.offer(p); // enqueue the point

		// while the queue is not empty
		while(!q.isEmpty()){
			p = q.poll(); // dequeue point p
			// point p is point b, exit
			if(p.row == b.row && p.col == b.col){
				break;
			}
			// check points for 4 conditions: (1) not out of bounds (2) not visited (3) equal to or above water level (4) gradient less than threshold g
			// check point above p
			if((p.row-1 >= 0) && (visited[p.row-1][p.col] == 0) && 
					(landscape[p.row-1][p.col] >= w) && Math.abs(p.elev - landscape[p.row-1][p.col]) < g){ 
				Point newP = new Point(p.row-1, p.col, landscape[p.row-1][p.col], p); // create the point, this time keep track of parent pointer
				visited[newP.row][newP.col] = 1; // mark point as visited
				q.offer(newP); // enqueue point
			}
			// check point below p
			if((p.row+1 < rowSize) && (visited[p.row+1][p.col] == 0) && 
					(landscape[p.row+1][p.col] >= w) && Math.abs(p.elev - landscape[p.row+1][p.col]) < g){ 
				Point newP = new Point(p.row+1, p.col, landscape[p.row+1][p.col], p);
				visited[newP.row][newP.col] = 1;
				q.offer(newP);
			}
			// check point left of p
			if((p.col-1 >= 0) && (visited[p.row][p.col-1] == 0) && 
					(landscape[p.row][p.col-1] >= w) && Math.abs(p.elev - landscape[p.row][p.col-1]) < g){ 
				Point newP = new Point(p.row, p.col-1, landscape[p.row][p.col-1], p);
				visited[newP.row][newP.col] = 1;
				q.offer(newP);
			}
			// check point right of p
			if((p.col+1 < colSize) && (visited[p.row][p.col+1] == 0) && 
					(landscape[p.row][p.col+1] >= w) && Math.abs(p.elev - landscape[p.row][p.col+1]) < g){ 
				Point newP = new Point(p.row, p.col+1, landscape[p.row][p.col+1], p);
				visited[newP.row][newP.col] = 1;
				q.offer(newP);
			}
		}

		// No path
		if(p.row != b.row || p.col != b.col){
			System.out.println("There is no path from A to B");
		}
		// There is a path
		else{
			// follow each point's parent pointer until parent is null to retrace the path backwards
			while(p != null){ 
				path.add(p); // add each point to ArrayList path
				p = p.parent;
			}
			System.out.println("This is the shortest motorable path with length " + path.size() + ":");
			for(int i = path.size()-1; i >= 0; i--){ // iterate through ArrayList path backwards to reconstruct proper path
				path.get(i).printPoint();
			}
		}
	}
	
	// parses test file and populates variables
	public static void main(String[] args) throws FileNotFoundException{		
		
		Scanner scanner = new Scanner(new File("test2.txt"));		
		// initialize row size and column size
		rowSize = scanner.nextInt(); colSize = scanner.nextInt();		
		// initialize water level and gradient threshold
		w = scanner.nextInt(); g = scanner.nextInt();		
		// initialize point A and point B for question 3
		int aRow = scanner.nextInt(); int aCol = scanner.nextInt();
		int bRow = scanner.nextInt(); int bCol = scanner.nextInt();
		a = new Point(aRow, aCol, 0); // temporarily assigns A's elevation to 0
		b = new Point(bRow, bCol, 0); // temporarily assigns B's elevation to 0		
		// initialize landscape and visited
		landscape = new int[rowSize][colSize];
		visited = new int[rowSize][colSize];
		for(int i = 0; i < rowSize; i++){
			for(int j = 0; j < colSize; j++){
				landscape[i][j] = scanner.nextInt();
				visited[i][j] = 0;
			}
		}
		a.elev = landscape[aRow][aCol];	// reassign A's elevation after parsing grid
		b.elev = landscape[bRow][bCol]; // reassign B's elevation after parsing grid
		scanner.close();		

//		Printer.printGrid(landscape); // prints the grid		
		Printer.printInitialVals(); // print all initial values			
		// find and print all the lakes
		findLakes();		
		Printer.printLakes(lakes); 		
		// find and print lake with largest surface area
		Lake lake = findLargestSALake();
		System.out.println("This lake has the largest surface area of size " + lake.SA + ":");
		lake.printLake();		
		// find and print lake with largest volume
		lake = findLargestVolLake();
		System.out.println("This lake has the largest volume of size " + lake.vol + ":");
		lake.printLake();		
		// print the motorable path if possible		
		findPath();
	}
}