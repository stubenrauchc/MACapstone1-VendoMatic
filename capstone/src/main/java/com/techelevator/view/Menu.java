package com.techelevator.view;

import com.techelevator.DollarAmount;
import com.techelevator.Inventory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

//user input recording and menu displaying

public class Menu {

	private PrintWriter out;
	private Scanner in;

	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}

	//General purpose object for displaying menu and using users choice/input
	public Object getChoiceFromOptions(Object[] options) throws InterruptedException {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}


	//gets users choice/input
	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > -1 && selectedOption <= options.length) {
				choice = options[selectedOption];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since
			// choice will be null
		}
		if (choice == null) {
			out.println("\n*** " + userInput + " is not a valid option ***\n");
		}
		return choice;
	}
	//creates the list of menu options
	private void displayMenuOptions(Object[] options) throws InterruptedException {
		out.println();

		//Chris's favorite piece of magic of the month
		for (int i = 0; i < 50; i++){
			Thread.sleep(8);
			System.out.print("-");
		}
		System.out.println("");

		for (int i = 1; i < options.length; i++) {
			int optionNum = i;
			String menuoptions = (optionNum + ") " + options[i] + "\n");
			Thread.sleep(125);

			//remove 'System.' from line below for proper testing
			System.out.print(menuoptions);
			Thread.sleep(125);
		}
		for (int j = 0; j < 50; j++){
			Thread.sleep(8);
			System.out.print("-");
		}

		out.print("\nPlease choose an option >>> ");
		out.flush();

	}
		//records users inputted money
	public Integer getDollarAmountFromUser() {
		Object choice = null;

		while (choice == null) {
			out.println("Please insert $1, $2, $5, $10, or $20");
			out.println("ex $1 = 1, ex $2 = 2, ex $5 = 5, ex $10 = 10, or ex $20 = 20");
			out.flush();
			String userInput = in.nextLine();
			try {
				if (DollarAmount.isValidDollar(userInput)) {
					Integer feedMoneyInput = Integer.parseInt(userInput);
					choice = feedMoneyInput;
				} else {
					out.println("\n*** " + userInput + " is not a valid option ***\n");
					out.flush();
				}

			} catch (NumberFormatException e) {
				// eat the exception, an error message will be displayed below
				// since
				// choice will be null
			}
		}

		return (Integer) choice;
	}
	//records users slot choice for product
	public String getSlotKey(String slotKeyChoice) {
		Object choice = null;

		while (choice == null) {
			out.println("Please enter product slot");
			out.flush();
			String userInput = in.nextLine();
			try {
				if (Inventory.isValidProductSlotKey(userInput)) {
					choice = userInput;
				} else if (choice == null) {
					out.println("\n*** " + userInput + " is not a valid option ***\n");
					out.flush();
				}
			} catch (NumberFormatException e) {
				// eat the exception, an error message will be displayed below
				// since
				// choice will be null
			}

		}
		return (String) choice;
	}

}