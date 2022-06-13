package com.techelevator;

import java.io.*;
import java.util.*;


public class Inventory {

    private final Map<String, Product> inventoryMap;

    public Inventory() {
        this.inventoryMap = new HashMap<>();
    }

    public Product getProductBySlotKey(String slotKey) {
        return inventoryMap.get(slotKey);
    }
    //scans inventory file and creates map of products
    public void stockInventory() {
        try {
            File inventoryInput = getInventoryFile();
            try (Scanner inventoryReader = new Scanner(inventoryInput)) {
                while (inventoryReader.hasNextLine()) {
                    String productInfoFromFile = inventoryReader.nextLine();
                    Product currentProduct = getAttributes(productInfoFromFile);
                    inventoryMap.put(currentProduct.getSlot(), currentProduct);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

    }

    public int getInventorySize() {
        return inventoryMap.size();
    }
    //creates vending item information (slot, product, price, quantity left, and type of product)
    public Product getAttributes(String inventoryFileLine) {
        String[] attributes = inventoryFileLine.split("\\|");
        double priceAsDouble = Double.parseDouble(attributes[2]);
        String productType = attributes[3];

        int price = (int) (priceAsDouble * 100);
        return new Product(attributes[0], attributes[1], new DollarAmount(price), 5, productType);

    }

    /**
     * File path verification
     * @return
     * @throws IOException
     */
    public static File getInventoryFile() throws IOException {
        File inventoryFile = new File("vendingmachine.csv");

        if (!inventoryFile.exists()) {
            throw new FileNotFoundException("Inventory file :" + inventoryFile + " does not exist!");
        }

        if (!inventoryFile.isFile()) {
            throw new IOException("Inventory file :" + inventoryFile + "exists, but is not a file!");
        }

        return inventoryFile;
    }
    //header for vending product information
    public void displayInventory() {
        String formattedLogLine = String.format("%1$-5s %2$-25s %3$-10s %4$-20s", "Slot", "Product", "Price",
                "Quantity");
        System.out.println(formattedLogLine);

        List<String> keys = new ArrayList<>(inventoryMap.keySet());
        Collections.sort(keys);

        for (String slot : keys) {
            Product currentProduct = inventoryMap.get(slot);
            System.out.println(currentProduct);
        }
    }
    //verifies user input for product selection and converts user input to variables for product selection
    public static boolean isValidProductSlotKey(String slotKey) {
        if (slotKey.length() < 2) {
            return false;
        }
        String[] slotKeySplit = slotKey.split("");
        boolean firstKeySpot = slotKeySplit[0].equalsIgnoreCase("A") || slotKeySplit[0].equalsIgnoreCase("B")
                || slotKeySplit[0].equalsIgnoreCase("C") || slotKeySplit[0].equalsIgnoreCase("D");
        boolean secondKeySpot = slotKeySplit[1].equals("1") || slotKeySplit[1].equals("2")
                || slotKeySplit[1].equals("3") || slotKeySplit[1].equals("4");

        return firstKeySpot && secondKeySpot;
    }
    //verifies user balance is enough to purchase product
    public DollarAmount selectProductBySlot(String productSelection, DollarAmount currentBalance) {
        Product selectedProduct = inventoryMap.get(productSelection);

        DollarAmount selectedProductPrice = selectedProduct.getPrice();
        if (currentBalance.isGreaterThanOrEqualTo(selectedProductPrice)) {
            selectedProduct.decreaseQuantity();
            System.out.println("Please enjoy your " + selectedProduct.getName() + "." + " There are "
                    + selectedProduct.getQuantity() + " left to purchase.");

            //print out the much glug crunch chew chew!
            String prodType = selectedProduct.getProdType();
            switch(prodType) {
                case "Chip" :
                    System.out.println("Crunch Crunch, Yum!");
                    break;
                case "Candy" :
                    System.out.println("Munch Munch, Yum!");
                    break;
                case "Drink" :
                    System.out.println("Glug Glug, Yum!");
                    break;
                case "Gum" :
                    System.out.println("Chew Chew, Yum!");
                    break;

                default :
                    return null;

            }

            return new DollarAmount(
                    (currentBalance.getTotalAmountInCents() - selectedProduct.getPrice().getTotalAmountInCents()));

        } else {
            System.out.println("Please insert additional money");
            return new DollarAmount(currentBalance.getTotalAmountInCents());
        }
    }
    //verifies that product is in stock
    public boolean selectedProductInStock(String slotKey) {
        Product selectedProduct = inventoryMap.get(slotKey);
        return selectedProduct.getQuantity() > 0;
    }
    //creates hidden sales report log
    public void printSalesReport(File technicianSalesReport) {
        try (PrintWriter technicianSalesReportWriter = new PrintWriter(
                new FileOutputStream(technicianSalesReport, true))) {

            String technicianSalesReportHeader = String.format("%1$-25s %2$-15s %3$-15s", "Product Name",
                    "Quantity Sold", "Gross Sales");
            technicianSalesReportWriter.println(technicianSalesReportHeader);

            DollarAmount totalGrossSales = DollarAmount.ZERO_DOLLARS;
            for (Product product : inventoryMap.values()) {

                String productName = product.getName();
                int productQuantitySold = 5 - product.getQuantity();
                DollarAmount productGrossSales = new DollarAmount(
                        product.getPrice().getTotalAmountInCents() * productQuantitySold);
                String formattedTechnicianSalesLine = String.format("%1$-25s %2$-15s %3$-15s", productName,
                        productQuantitySold, productGrossSales);

                technicianSalesReportWriter.println();
                technicianSalesReportWriter.println(formattedTechnicianSalesLine);
                technicianSalesReportWriter.flush();
                totalGrossSales = totalGrossSales.plus(productGrossSales);
            }

            technicianSalesReportWriter.println();
            technicianSalesReportWriter.println("Total Gross Sales : " + totalGrossSales);
            technicianSalesReportWriter.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

    }

}