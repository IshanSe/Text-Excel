package textExcel;

public class Location
{
	private int rowNum;
	private int colNum;
	public Location(int row, int col) {
		rowNum = row;
		colNum = col;
	}
	
	public int getRow() {
		return rowNum;
	}
	
	public int getCol() {
		return colNum;
	}
}
