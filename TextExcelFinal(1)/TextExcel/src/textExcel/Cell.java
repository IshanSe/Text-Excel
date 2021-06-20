package textExcel;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cell {
	String cellData;
	Type cellType;

	public enum Type {
		NULL, REAL, STRING, DATE;
	}

	public Cell() {

		cellData = "";
		cellType = Type.NULL;// STRING, REAL, DATE
	}

	/**
	 * Returns the text to be displayed inside this cell.
	 *
	 * @return A string, exactly 10 characters long, representing the contents
	 *         of this cell
	 */

	public String abbreviatedCellText() {


		String temp = cellData.replaceAll("^\"|\"$", "") + Spreadsheet.displayPads;
		return temp.substring(0, Spreadsheet.cellWindowSize);
	}

	/**
	 * Returns the full, unabbreviated contents of this cell. If the cell
	 * contains a formula, the formula is shown unevaluated. If the cell
	 * contains a string, the string is shown in quotes.
	 * 
	 * The result is not required to be 10 characters long. If longer, it is not
	 * truncated; if shorter, it is not padded.
	 * 
	 * @return The full contents of this cell
	 */
	public String fullCellText() {
		return cellData;
	}

	public void clearCell() {
		cellData = "";
		cellType = Type.NULL;
	}

	public void setCell(String s) {
		cellData = s;
		cellType = testCellTypeNeeded(s);
		
	}
	public static Type testCellTypeNeeded(String s) {
		Type type = Type.NULL;
		String[] formula;
		formula = s.split("[ ]+");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = dateFormat.parse(s, new ParsePosition(0));
		if (date != null) {
			type = Type.DATE;
		} else {
			
			// if(formula[0] == "(" && formula[formula.length-1] == ")"){
			if (formula[0].equals( "(")) {
				if (formula[formula.length - 1].equals( ")")) {
					type = Type.REAL;
				} else {
					System.out.println("ERROR: Invalid Expression.\n");
				}
				
			} else {
				type = Type.STRING;
			}
			try { // check if it is actually a Double Number
				double number = Double.parseDouble(s);
				if (number != Double.NaN) {
					type = Type.REAL;
				}
			} catch (NumberFormatException ex) {
				// ignore exception
			}
		}
		return type;
	} 
}
