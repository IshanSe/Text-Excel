package textExcel;

import java.util.*;
import java.text.*;

public class DateCell extends Cell {
	public String abbreviatedCellText() {
		String temp;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = dateFormat.parse(cellData, new ParsePosition(0));
			if (date != null) {
				temp = dateFormat.format(date);
			} else {
				temp = cellData;
			}
		} catch (Exception ex) {
			temp = cellData; // take the string as it is
		}
		temp += Spreadsheet.displayPads;
		return temp.substring(0, Spreadsheet.cellWindowSize);
	}
}
