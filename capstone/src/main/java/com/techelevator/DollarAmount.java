package com.techelevator;
//tracks users balances
public class DollarAmount {
//defines base user balance
    public static final DollarAmount ZERO_DOLLARS = new DollarAmount(0);

    private int totalAmountInCents;

    public DollarAmount(int totalAmountInCents) {
        this.totalAmountInCents = totalAmountInCents;
    }
//conversion from dollar to cents and vice versa
    public int getCents() {
        return (int) (totalAmountInCents % 100);
    }

    public int getDollars() {
        return totalAmountInCents / 100;
    }

    //comparison methods
    public boolean isGreaterThan(DollarAmount amountToCompare) {
        return this.totalAmountInCents > amountToCompare.totalAmountInCents;
    }

    public boolean isGreaterThanOrEqualTo(DollarAmount amountToCompare) {
        return this.totalAmountInCents >= amountToCompare.totalAmountInCents;
    }

    public boolean isLessThan(DollarAmount amountToCompare) {
        return this.totalAmountInCents < amountToCompare.totalAmountInCents;
    }

    public boolean isLessThanOrEqualTo(DollarAmount amountToCompare) {
        return this.totalAmountInCents <= amountToCompare.totalAmountInCents;
    }

    public boolean isNegative() {
        return totalAmountInCents < 0;
    }

    public DollarAmount minus(DollarAmount amountToSubtract) {
        return new DollarAmount(this.totalAmountInCents - amountToSubtract.totalAmountInCents);
    }

    public DollarAmount plus(DollarAmount amountToAdd) {
        return new DollarAmount(this.totalAmountInCents + amountToAdd.totalAmountInCents);
    }

    public int compareTo(DollarAmount amountToCompare) {
        if (this.isGreaterThan(amountToCompare)) {
            return 1;
        } else if (this.isLessThan(amountToCompare)) {
            return -1;
        } else {
            return 0;
        }
    }

    public int getTotalAmountInCents() {
        return totalAmountInCents;
    }


    //below tracks user balance, formats the money, and calculates coin return
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DollarAmount) {
            return this.totalAmountInCents == ((DollarAmount) obj).totalAmountInCents;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return totalAmountInCents;
    }

    @Override
    public String toString() {
        //formats value down to 2 decimal places
        return "$" + getDollars() + "." + String.format("%02d", getCents());
    }

    //determines which coins to stock up on for return starting with largest denomination
    public void returnChange(){
        int totalCents = this.getTotalAmountInCents();
        int totalNickels = 0;
        int totalDimes = 0;
        int totalQuarters = 0;
        while (totalCents >0){
            if (totalCents >= 25){
                totalQuarters ++;
                totalCents-= 25;
            }else if (totalCents >= 10){
                totalDimes++;
                totalCents-= 10;
            }else if (totalCents >= 5){
                totalNickels++;
                totalCents-= 5;
            }
        }
        System.out.println("Change returned: " + totalQuarters + " quarters, " + totalDimes + " dimes, " + totalNickels + " nickels, " );
    }

    //determines if user input money value is valid
    public static boolean isValidDollar(String dollarToCheck) {
        return dollarToCheck.equals("1") || dollarToCheck.equals("2") || dollarToCheck.equals("5")
                || dollarToCheck.equals("10") || dollarToCheck.equals("20");
    }
}