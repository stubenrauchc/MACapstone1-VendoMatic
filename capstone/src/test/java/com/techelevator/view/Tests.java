package com.techelevator.view;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.techelevator.DollarAmount;
import com.techelevator.Inventory;
import com.techelevator.Product;
import com.techelevator.VendingMachineCLI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.techelevator.Inventory.getInventoryFile;

public class Tests {

	private ByteArrayOutputStream output;

	private Menu getMenuForTestingWithUserInput(String userInput) {
		ByteArrayInputStream input = new ByteArrayInputStream(String.valueOf(userInput).getBytes());
		return new Menu(input, output);
	}

	private Menu getMenuForTesting() {
		return getMenuForTestingWithUserInput("1\n");
	}

	@Before
	public void setup() {
		output = new ByteArrayOutputStream();
	}

	@Test
	public void displayMenuOptionsPromptUser() throws InterruptedException {
		Object[] options = new Object[] {"Hidden", "3", "Blind", "Mice" };
		Menu menu = getMenuForTesting();

		menu.getChoiceFromOptions(options);

		String expected = "\r\n"+
				"1) "+ options[1].toString()+"\r\n" +
				"2) "+ options[2].toString()+"\r\n" +
				"3) "+ options[3].toString()+"\r\n" +
				"\nPlease choose an option >>> ";
		Assert.assertEquals(expected, output.toString());
	}

	@Test
	public void returnUserChoice() throws InterruptedException {
		Integer expected = 456;
		Integer[] options = new Integer[] {123, expected, 789};
		Menu menu = getMenuForTestingWithUserInput("1\n");

		Integer result = (Integer)menu.getChoiceFromOptions(options);

		Assert.assertEquals(expected, result);
	}

	@Test
	public void reprintMenuIfInvalidChoice() throws InterruptedException {
		Object[] options = new Object[] {"Hidden", "Larry", "Curly", "Moe" };
		Menu menu = getMenuForTestingWithUserInput("5\n2");

		menu.getChoiceFromOptions(options);

		String menuDisplay = "\r\n"+
				"1) "+options[1].toString()+"\r\n" +
				"2) "+options[2].toString()+"\r\n" +
				"3) "+options[3].toString()+"\r\n\n" +
				"Please choose an option >>> ";

		String expected = menuDisplay +
				"\n*** 5 is not a valid option ***\n\r\n" +
				menuDisplay;

		Assert.assertEquals(expected, output.toString());
	}

	@Test
	public void reprintMenuIfChoiceBelowLowestOption() throws InterruptedException {
		Object[] options = new Object[] {"Hidden", "Larry", "Curly", "Moe" };
		Menu menu = getMenuForTestingWithUserInput("-1\n2\n");

		menu.getChoiceFromOptions(options);

		String menuDisplay = "\r\n"+
				"1) "+options[1].toString()+"\r\n" +
				"2) "+options[2].toString()+"\r\n" +
				"3) "+options[3].toString()+"\r\n\n" +
				"Please choose an option >>> ";

		String expected = menuDisplay +
				"\n*** -1 is not a valid option ***\n\r\n" +
				menuDisplay;

		Assert.assertEquals(expected, output.toString());
	}

	@Test
	public void reprintMenuIfUserInputsNonInt() throws InterruptedException {
		Object[] options = new Object[] {"Hidden", "Larry", "Curly", "Moe" };
		Menu menu = getMenuForTestingWithUserInput("Mickey Mouse\n1\n");

		menu.getChoiceFromOptions(options);

		String menuDisplay = "\r\n"+
				"1) "+options[1].toString()+"\r\n" +
				"2) "+options[2].toString()+"\r\n" +
				"3) "+options[3].toString()+"\r\n\n" +
				"Please choose an option >>> ";

		String expected = menuDisplay +
				"\n*** Mickey Mouse is not a valid option ***\n\r\n" +
				menuDisplay;

		Assert.assertEquals(expected, output.toString());
	}

	//inventory tests

	@Test
	public void getInventoryFileTest()  throws IOException {
		Inventory inv = new Inventory();
		String result = String.valueOf(getInventoryFile());
		String expected = "vendingmachine.csv";
		Assert.assertEquals(expected, result);
	}














}