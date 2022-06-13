package com.techelevator;

import com.techelevator.view.Menu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VendingMachineCLI {
	// added additional menu options and logging system

	private static final String HIDDEN_TECH_MENU_PRINT_SALES_REPORT = " ";
	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";

	private static final String[] MAIN_MENU_OPTIONS = { HIDDEN_TECH_MENU_PRINT_SALES_REPORT,
			MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT };
	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";
	private static final String[] PURCHASE_MENU_OPTIONS = { HIDDEN_TECH_MENU_PRINT_SALES_REPORT,
			PURCHASE_MENU_OPTION_FEED_MONEY, PURCHASE_MENU_OPTION_SELECT_PRODUCT,
			PURCHASE_MENU_OPTION_FINISH_TRANSACTION };


	//defined classes and files as variables
	private Menu menu;
	private Inventory inventory;
	private DollarAmount currentBalance;
	private File transactionLogFile;
	private File salesReportFile;

	//instantiated the Menu
	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
		this.inventory = new Inventory();
		this.currentBalance = DollarAmount.ZERO_DOLLARS;
		this.transactionLogFile = new File("TransactionLog.txt");
		this.salesReportFile = new File("SalesReportLog.csv");
	}

	//runs Inventory class to scan and parse the vendingmachine.csv while also defining how much of each product
	//and runs menu options from user input
	public void run() throws InterruptedException {
		inventory.stockInventory();

		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(HIDDEN_TECH_MENU_PRINT_SALES_REPORT)) {
				File technicianSalesReport = generateTechnicianSalesReport();
				inventory.printSalesReport(technicianSalesReport);
				System.out.println("Sales Report Generated");

			} else if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				// display vending machine items
				inventory.displayInventory();
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// do purchase
				purchaseMainMenu();
			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)){
				System.out.println("Restocking the VendoTroMatic, goodbye!");
				System.exit(0);
			}
		}
	}


	//runs purchase menu and tracks users money balance while logging each monetary transaction
	public void purchaseMainMenu() throws InterruptedException {
		while (true) {
			System.out.println();
			System.out.println("Current Balance: " + currentBalance);
			String choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);

			if (choice.equals(HIDDEN_TECH_MENU_PRINT_SALES_REPORT)) {
				File technicianSalesReport = generateTechnicianSalesReport();
				inventory.printSalesReport(technicianSalesReport);
				System.out.println("Sales Report Generated");
			} else if (choice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
				int moneyToFeed = menu.getDollarAmountFromUser();
				DollarAmount updatedBalance = new DollarAmount(moneyToFeed * 100);
				currentBalance = (currentBalance.plus(updatedBalance));
				try {
					logDeposit(updatedBalance);

				} catch (IOException ex) {
					System.out.println(ex.getMessage());
					System.exit(1);
				}

			} else if (choice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
				String productSelection = "";
				Object userChoice = menu.getSlotKey(choice);

				if (userChoice != null) {
					productSelection = (String) userChoice;
					productSelection = productSelection.toUpperCase();

					if (!inventory.selectedProductInStock(productSelection)) {
						System.out.println("Those are out of stock. Try again.");

					} else {

						DollarAmount newBalance = inventory.selectProductBySlot(productSelection, currentBalance);
						try {
							logPurchase(productSelection, newBalance);

						} catch (IOException ex) {
							System.out.println(ex.getMessage());
							System.exit(1);
						}
						currentBalance = newBalance;
					}
				} else {

					System.out.println("Not a valid option");

				}

			} else if (choice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
				DollarAmount changeToReturn = currentBalance;
				try {
					logFinishTransaction();

				} catch (IOException ex) {
					System.out.println(ex.getMessage());
					System.exit(1);
				}
				changeToReturn.returnChange();

				currentBalance = DollarAmount.ZERO_DOLLARS;
				break;
			}
		}
	}


	//log systems below: Deposits, Purchases, and Withdrawals(transaction completions) then hidden sales report
	public void logDeposit(DollarAmount updatedBalance) throws IOException {

		try (PrintWriter depositWriter = new PrintWriter(new FileOutputStream(transactionLogFile, true))) {
			LocalDateTime now = LocalDateTime.now();
			String date = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
			String time = now.format(DateTimeFormatter.ISO_LOCAL_TIME);
			String timeWithOutSeconds = time.substring(0, time.length() - 4);

			String endingBalance = currentBalance.toString();
			String newBalance = updatedBalance.toString();
			String formattedLogLine = String.format("%1$-15s %2$-11s %3$-15s %4$-20s %5$-10s %6$-12s %7$-10s", date,
					timeWithOutSeconds, "Deposit", " ", " ", newBalance, endingBalance);
			depositWriter.println();
			depositWriter.println(formattedLogLine);
			depositWriter.flush();

		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}

	}

	public void logPurchase(String slotKey, DollarAmount postPurchaseBalance) throws IOException {
		try (PrintWriter purchaseWriter = new PrintWriter(new FileOutputStream(transactionLogFile, true))) {

			LocalDateTime now = LocalDateTime.now();
			String date = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
			String time = now.format(DateTimeFormatter.ISO_LOCAL_TIME);
			String timeWithOutSeconds = time.substring(0, time.length() - 4);
			String tenderedBalance = currentBalance.toString();

			Product itemName = inventory.getProductBySlotKey(slotKey);
			String purchasedProductName = itemName.getName();
			DollarAmount purchasedProductPrice = itemName.getPrice();
			String formattedLogLine = String.format("%1$-15s %2$-11s %3$-15s %4$-20s %5$-10s %6$-12s %7$-10s", date,
					timeWithOutSeconds, "Purchase", purchasedProductName, purchasedProductPrice, tenderedBalance,
					postPurchaseBalance);

			purchaseWriter.println();
			purchaseWriter.println(formattedLogLine);
			purchaseWriter.flush();

		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}

	}

	public void logFinishTransaction() throws IOException {

		try (PrintWriter depositWriter = new PrintWriter(new FileOutputStream(transactionLogFile, true))) {

			LocalDateTime now = LocalDateTime.now();
			String date = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
			String time = now.format(DateTimeFormatter.ISO_LOCAL_TIME);
			String timeWithOutSeconds = time.substring(0, time.length() - 4);

			String changeToReturn = currentBalance.toString();

			String formattedLogLine = String.format("%1$-15s %2$-11s %3$-15s %4$-20s %5$-10s %6$-12s %7$-10s", date,
					timeWithOutSeconds, "Withdrawal", " ", " ", changeToReturn, "$0.00 ");

			depositWriter.println();
			depositWriter.println(formattedLogLine);
			depositWriter.flush();

		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}

	public File generateTechnicianSalesReport() {
		File technicianSalesReport = null;
		try (PrintWriter depositWriter = new PrintWriter(new FileOutputStream(salesReportFile, true))) {
			LocalDateTime now = LocalDateTime.now();
			String date = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
			String time = now.format(DateTimeFormatter.ISO_LOCAL_TIME);
			String timeWithOutSeconds = time.substring(0, time.length() - 4);

			String nameOfTechnicianSalesReport = "SalesReportLog.csv";
			depositWriter.println();
			depositWriter.println("SalesReportLog-" + date + "-" + timeWithOutSeconds);
			depositWriter.flush();
			technicianSalesReport = new File(nameOfTechnicianSalesReport);

		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}

		return technicianSalesReport;
	}
}