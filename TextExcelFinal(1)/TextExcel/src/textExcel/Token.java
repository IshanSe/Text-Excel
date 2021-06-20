package textExcel;

import java.util.ArrayList;
import java.util.Arrays;

public class Token {
	public enum type {
		CELLID, CONSTANT, OPERATOR, UNDEFINED
	};

	private static String[] op = { "(", ")", "AVG", "SUM", "/", "*", "+", "-", "%", "|"};
	//String tokenval = ""; // if operator, val is any of /, *, +, - , %, |, (, )
	type tokenType = type.UNDEFINED; // 1: CellId, 2: Constant, 3: Operator



	
	/**
	 * @param expression
	 *            An expression to split (at whitespace) into individual tokens.
	 * @return An ordered list of individual tokens that together form the
	 *         provided expression.
	 */
	public static ArrayList<String> breakIntoTokens(String expression) {
		String[] parse;
		expression.replaceAll("^\\s*", ""); // ignore all preceding blanks
		expression.replaceAll("\\s*$", ""); // ignore all trailing blanks
		parse = expression.split("[ ]+"); // split the expression ignoring
											// repeating spaces
		return new ArrayList<String>(Arrays.asList(parse)); // converts the
															// array to an
															// Arraylist before
															// returning
	}


	public static boolean isOperator(String s) {
		for (int i = 0; i < op.length; i++) {
			if (s.equals(op[i])) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNumber(String s) {
		try {
			Double.valueOf(s);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	public static boolean isCellName(String s) {
		if (Spreadsheet.cellNameToLoc(s) == null) {
			return false;
		}
		return true;
	}
	
	public static type getType(String s) {
		type tokenType = type.UNDEFINED;
		if (isOperator(s)) {
			tokenType = type.OPERATOR;
		}
		else if (isNumber(s)) {
			tokenType = type.CONSTANT;
		}
		else if (isCellName(s)) {
			tokenType = type.CELLID;
		} 
		return tokenType;

	}
}
