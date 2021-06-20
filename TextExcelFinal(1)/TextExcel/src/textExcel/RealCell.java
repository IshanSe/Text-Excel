package textExcel;
import java.util.*;

public class RealCell extends Cell {
	static Spreadsheet sheet;
	double[] formula;
	// All supported operators (in precedence order)
		private final static String[] ALL_OPERATORS = { "AVG", "SUM", "(", "*", "/", "-", "+" };

	public RealCell(Spreadsheet sheet) {
		this.sheet = sheet;
	}

	public String abbreviatedCellText() {
		String temp;
		try {  //evaluate expressions
			ArrayList<String> expressionTokens = Token.breakIntoTokens(cellData.toUpperCase());
			ArrayList<String> remainingOperators = allOperators();
			temp = evaluate(remainingOperators, expressionTokens);
			temp += Spreadsheet.displayPads;
			return temp.substring(0, Spreadsheet.cellWindowSize);

		} catch (StackOverflowError S) {
			System.out.println("ERROR: Invalid command.\n");
		} catch (Exception ex) {

			// do nothing, we are going to process the cell as a string or a
			// number
		}
		try {
			double number = Double.parseDouble(cellData);
			if (number != Double.NaN) { // If string s has a number, display
										// real number
				temp = Double.toString(number) + Spreadsheet.displayPads;
			} else {
				temp = cellData; // take the string as it is
			}
		} catch (NumberFormatException ex) {
			temp = cellData; // take the string as it is
		}
		temp += Spreadsheet.displayPads;
		return temp.substring(0, Spreadsheet.cellWindowSize);
	}

	/**
	 * @return A new list that initially contains all supported operators in
	 *         precedence order.
	 */
	public static ArrayList<String> allOperators() {
		return new ArrayList<String>(Arrays.asList(ALL_OPERATORS));
	}

	/**
	 * Recursively evaluates an arithmetic expression.
	 * 
	 * @param remainingOperators
	 *            Operators that are yet to be evaluated within this expression
	 *            (in precedence order). When this list is empty, evaluation is
	 *            complete.
	 * @param expressionTokens
	 *            Non-empty ordered list of sub-expressions that make up the
	 *            overall expression.
	 * @return The result of evaluating the entire expression.
	 */
	public static String evaluate(ArrayList<String> remainingOperators, ArrayList<String> expressionTokens)
			throws IllegalArgumentException {
		String result = null;
		ArrayList<String> operators;

		if (remainingOperators.size() == 0) {
			if (expressionTokens.size() == 1) {
				String s = expressionTokens.remove(0);
				return Double.toString(Double.parseDouble(s));
			} else {
				throw new IllegalArgumentException();
			}
		}
		try {
			String op = remainingOperators.remove(0);
			int index = expressionTokens.indexOf(op);
			if (index == -1) {
				result = evaluate(remainingOperators, expressionTokens);
			} else if(op.equals("AVG")){
				String arg = expressionTokens.remove(index + 1);
				String operator = expressionTokens.remove(index);
				result = AVG (arg);
				expressionTokens.add(index - 1, result);
			} else if(op.equals("SUM")){
				String arg = expressionTokens.remove(index + 1);
				String operator = expressionTokens.remove(index);
				result = SUM (arg);
				expressionTokens.add(index - 1, result);
			}
			else if (op.equals("(")) {
				expressionTokens.remove(index);
				result = evaluateSubExpression(index, expressionTokens);
				expressionTokens.add(index, result);
				operators = allOperators();
				result = evaluate(operators, expressionTokens);
			} else {
				String arg2 = expressionTokens.remove(index + 1);
				String operator = expressionTokens.remove(index);
				String arg1 = expressionTokens.remove(index - 1);
				result = applyOperator(arg1, operator, arg2);
				expressionTokens.add(index - 1, result);
				operators = allOperators();
				result = evaluate(operators, expressionTokens);
			}
		} catch (Exception e) {
			System.out.println("ERROR: Bad Expression \n");
		}

		/**
		 * TODO: Implement this method using recursion.
		 *
		 * Base case: remainingOperators list is empty.
		 *
		 * Recursive case: Find the first occurrence of the first remaining
		 * operator in the expression. o If not found: Remove the operator from
		 * its list - the problem just got smaller! o If found (+, -, * or /):
		 * Remove the two operands, evaluate the expression and replace the
		 * operator with the result - the problem just got smaller! o If found
		 * (parentheses): Remove all tokens up until the closing parentheses,
		 * evaluate the sub-expression formed by these tokens and replace the
		 * opening parentheses with the result - the problem just got smaller!
		 */

		return result;
	}

	/**
	 * Evaluates a simple arithmetic expression.
	 * 
	 * @param leftHandOperand
	 *            The left-hand operand (a number).
	 * @param operator
	 *            The operator to apply.
	 * @param rightHandOperand
	 *            The right-hand operand (a number).
	 * @return The result (a number).
	 */
	private static String applyOperator(String leftHandOperand, String operator, String rightHandOperand)
			throws IllegalArgumentException {
		
		Location l;
		double result = 0;
		double op1 = 0;
		double op2 = 0;
		String n;

		n = leftHandOperand;
		l = Spreadsheet.cellNameToLoc(leftHandOperand);
		if (l != null) { // it is a cell number
			Cell c = sheet.getCell(l);
			if (c instanceof RealCell) {
				RealCell r = (RealCell) c;
				n = r.abbreviatedCellText();
			}
		}
		n = getNumber(n);
		if (n != null) {
			op1 = Double.valueOf(n);
		} else {
			throw new IllegalArgumentException();
		}

		n = rightHandOperand;
		l = Spreadsheet.cellNameToLoc(rightHandOperand);
		if (l != null) { // it is a cell number
			Cell c = sheet.getCell(l);
			if (c instanceof RealCell) {
				RealCell r = (RealCell) c;
				n = r.abbreviatedCellText();
			}
		}
		n = getNumber(n);
		if (n != null) {
			op2 = Double.valueOf(n);
		} else {
			throw new IllegalArgumentException();
		}

		if (operator.equals("*")) {
			result = op1 * op2;
		}
		if (operator.equals("/")) {
			result = op1 / op2;
		}
		if (operator.equals("-")) {
			result = op1 - op2;
		}
		if (operator.equals("+")) {
			result = op1 + op2;
		}

		return Double.toString(result);
	}

	/**
	 * Given the position of a "(" token in a list of tokens, removes subsequent
	 * tokens until the corresponding ")" has been removed.
	 * 
	 * @param position
	 *            The position of a "(" token.
	 * @param remainingTokens
	 *            All tokens in the expression.
	 * @return The result of evaluating the sub-expression denoted by the
	 *         removed tokens.
	 */
	private static String evaluateSubExpression(int position, ArrayList<String> allTokens)
			throws IllegalArgumentException {
		ArrayList<String> subExprList = new ArrayList<String>();
		ArrayList<String> aoperators = allOperators();
		String res;
		String tkn;

		int i = position;
		tkn = allTokens.get(i);
		while (!tkn.equals(")")) {
			allTokens.remove(i);
			if (tkn.equals("(")) {
				res = evaluateSubExpression(position, allTokens);
				subExprList.add(res);
			} else {
				subExprList.add(tkn);
			}
			tkn = allTokens.get(i);
			// i++;
		}
		if (tkn.equals(")")) {
			res = evaluate(aoperators, subExprList);
			allTokens.remove(i);
			return res;
		}

		/**
		 * TODO: Implement this method.
		 */

		return "I don't know";
	}
	
	public static String getNumber(String s) {
		try {
			double number = Double.parseDouble(s);
			if (number != Double.NaN) { // If string s has a number, return as
										// real number
				return (Double.toString(number));
			} else {
				return null; // not a number
			}
		} catch (NumberFormatException ex) {
			return null; // not a number
		}

	}
	
	public static ArrayList<String> getRangeList(String cellRange) {	
		Location l1, l2;
		String n = null;
		int si, sj, fi, fj;
		ArrayList<String> list = new ArrayList<String>();
		String[] cells = cellRange.split("-");
		if(cells.length !=2){
			throw new IllegalArgumentException();
		}
		l1 = Spreadsheet.cellNameToLoc(cells[0]);
		if(l1 ==null){  //invalid cell name
			throw new IllegalArgumentException();
		}
		si = l1.getRow();
		sj = l1.getCol();
		l2 = Spreadsheet.cellNameToLoc(cells[1]);
		if(l2 ==null){
			throw new IllegalArgumentException();
		}
		fi = l2.getRow();
		fj = l2.getCol();
		for (int i = si; i <= fi; i++) {
			for (int j = sj; j <= fj; j++) {
				Cell c = sheet.getCell(new Location(i, j));
				n = c.abbreviatedCellText();
				list.add(n);
			}
		}
		return list;
	}
	
	public static String AVG(String s) {
		double sum = 0;
		int count = 0;
		ArrayList<String> list = getRangeList(s);
		for (String element : list) {
			sum += Double.valueOf(element);
			count += 1;
		}
		return Double.toString(sum / count);
	}
	
	public static String SUM(String s) {
		double sum = 0;
		ArrayList<String> list = getRangeList(s);
		for (String element : list) {
			sum += Double.valueOf(element);
		}
		return Double.toString(sum);
	}

	
}