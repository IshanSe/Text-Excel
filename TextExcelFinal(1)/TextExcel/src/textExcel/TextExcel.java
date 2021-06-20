package textExcel;
// Update this file with your own code.

import java.util.Scanner;

public class TextExcel {

	public static void main(String[] args) {
		Spreadsheet sheet = new Spreadsheet(); // Keep this as the first
												// statement in main
		Scanner console = new Scanner(System.in);
		String input = console.nextLine();
		while (!input.equals("quit")) {
			System.out.println(sheet.processCommand(input));
			input = console.nextLine();
		}
		System.out.println("Goodbye bruh");
		console.close();

	}
}
