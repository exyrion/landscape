package landscape;

import java.util.ArrayList;


public class Printer {
	
	// print grid
	public static void printGrid(int[][] grid) {		
		System.out.printf("%-4s", "");
		for (int i = 0; i < grid[0].length; i++) {
		    System.out.printf("%-4d", i);
		}
		System.out.println();
		for (int i = 0; i < grid.length; i++) {
		    System.out.printf("%-4d", i);
		    for (int j = 0; j < grid[0].length; j++) {
		        System.out.printf("%-4d", grid[i][j]);
		    }
		    System.out.println();
		}
	}
	
	// print initial values
	public static void printInitialVals(){
		System.out.println("Row size: " + Landscape.rowSize + ", Column size: " + Landscape.colSize);
		System.out.println("Water level W: " + Landscape.w);
		System.out.println("Gradient threshold G: " + Landscape.g);
		System.out.print("Point A: "); Landscape.a.printPoint();
		System.out.print("Point B: "); Landscape.b.printPoint();
	}
	
	// print all the lakes
	public static void printLakes(ArrayList<Landscape.Lake> lakes){
		System.out.println("All the lakes: ");
		for(int i = 0; i < lakes.size(); i++){
			int lakeNum = i+1;
			System.out.println("Lake #" + lakeNum);
			lakes.get(i).printLake();
		}
	}
}
