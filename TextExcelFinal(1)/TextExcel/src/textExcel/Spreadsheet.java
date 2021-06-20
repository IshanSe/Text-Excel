package textExcel;

import java.util.*;

import textExcel.Cell.Type;

import java.lang.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class Spreadsheet {
	static String header = "";
	static String[] colName = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" };
	static String separator = "|";
	static int cellWindowSize = 10;
	static String displayPads = "          "; // cellWindowSize number of spaces for padding
	private int rows = 20;
	private int columns = 12;
	String eol = "\n" ; 

	public Cell[][] spread;

	public Spreadsheet() {
		spread = new Cell[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				spread[i][j] = new Cell();
			}
		}
	}

	/**
	 * Evaluates the given command, updating the spreadsheet if appropriate.
	 * 
	 * @param command
	 *            The text of the command to process
	 * @return The text displayed as a result of executing the command, if any
	 */
	public String processCommand(String incommand) {
		String command ;
		command = incommand.trim().replaceAll("\\s+", " ");
		if (command.equals("")) {
			return "";
		}
		String[] params = command.split("[ ]+", 3);
		if (params.length == 1) {
			if (params[0].toUpperCase().equals("CLEAR")) {// clear sheet command
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < columns; j++) {
						spread[i][j].clearCell();
					}
				}
			} else // <cell> --- cell inspection command
			{
				Location loc = cellNameToLoc(params[0]);
				if (loc == null) {
					System.out.println("ERROR: Invalid command.\n");
					return ""; // invalid cell inspection command exception
				}
				Cell cell = getCell(loc);
				return cell.fullCellText(); // no need to return the spreadsheet
			}
		} else {
			if (params.length == 2) { // clear <cell> command
				if (params[0].toUpperCase().equals("CLEAR")) {// clear cell
																// location
																// command
					Location loc = cellNameToLoc(params[1]);
					if (loc == null) {
						System.out.println("ERROR: Invalid command.\n");
						return ""; // invalid clear command exception
					}
					Cell cell = getCell(loc);
					cell.clearCell();
				} else {
					System.out.println("ERROR: Invalid command.\n");
					return ""; // invalid set cell command
				}
			} else { // <cell> = <string> --- assignment command
				Location loc = cellNameToLoc(params[0]);
				if (loc == null) {
					System.out.println("ERROR: Invalid command.\n");
					return ""; // invalid set cell command
				}
				Cell cell = getCell(loc);
				if (params[1].equals("=") && cell != null) { // verify assignment sign
					cell = getCorrectTypeCell(params[2]);
					if (cell == null) { // verify assignment sign
						System.out.println("ERROR: Invalid command.\n");
						return "" ;
					}
					spread[loc.getRow()][loc.getCol()] = cell;
					cell.setCell(params[2]); // assigns the cell text
				} else {
					System.out.println("ERROR: Invalid command.\n");
					return ""; // invalid set cell command
				}
			}
		}
		return getGridText();
	}

	public static Location cellNameToLoc(String cellName) {
		int colNumber;
		int rowNumber;
		char c = Character.toUpperCase(cellName.charAt(0));
		if (c<'A' || c > 'L')return null;
		colNumber = Character.toUpperCase(cellName.charAt(0)) - 'A';
		if (cellName.length() == 2) {
			rowNumber = Character.toUpperCase(cellName.charAt(1)) - '1';
		} else if (cellName.length() == 3) {
			rowNumber = (Character.toUpperCase(cellName.charAt(1)) - '0') * 10;
			rowNumber += ((Character.toUpperCase(cellName.charAt(2)) - '0') - 1);
		} else
			return null;

		return new Location(rowNumber, colNumber);
	}

	/**
	 * Returns the number of rows in the spreadsheet.
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Returns the number of columns in the spreadsheet.
	 */
	public int getCols() {
		return columns;
	}

	/**
	 * Returns the spreadsheet cell at the given location.
	 */
	public Cell getCell(Location loc) {
		int r = loc.getRow();
		int c = loc.getCol();
		if (r <0 || r > rows || c < 0 || c > columns){
			System.out.println("ERROR: Invalid Cell.\n");
			return null; // invalid set cell command
		}
		return spread[r][c];
	}

	/**
	 * Returns the full display of the current spreadsheet.
	 */
	public String getGridText() {
		String result = "";
		result += getHeader() + eol;
		for (int i = 0; i < getRows(); i++) {
			result += displayRow(i) + eol;
		}
		return result;
	}

	public String getHeader() {// compose the header display containing the
								// column names

		if (header == "") {
			header = "   |";
			for (int i = 0; i < colName.length; i++) {
				header += (colName[i] + displayPads).substring(0, cellWindowSize) + separator;
			}
		}
		return header;
	}

	public String displayRow(int rowNum) {// compose a line text to display a
		// given row number
		String lineText = "";
		lineText = displayRowNum(rowNum + 1) + separator;
		for (int i = 0; i < getCols(); i++) {
			Cell c = spread[rowNum][i];
			if (c.cellType.equals(Type.REAL)) {
				lineText += ((RealCell) c).abbreviatedCellText();
			} else if (c.cellType.equals(Type.DATE)) {
				lineText += ((DateCell) c).abbreviatedCellText();
			} else
				lineText += c.abbreviatedCellText();
			lineText += separator;
		}
		return lineText;
	}

	public String displayRowNum(int rowNum) {// convert rowNum to display a
												// padded String
		String text = "";
		text = Integer.toString(rowNum) + displayPads;
		return text.substring(0, 3);
	}
	
	public Cell getCorrectTypeCell(String s) {
		Cell c = null;
		Type type = Cell.testCellTypeNeeded(s);
		if(type == Type.DATE){
			c = new DateCell();
		} else if(type == Type.REAL){
			c = new RealCell(this);
		}else if(type == Type.STRING){
			c = new TextCell();
		}
		
		return c;
	}
	
}
