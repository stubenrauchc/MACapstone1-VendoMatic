package com.techelevator;

import com.techelevator.view.Menu;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        Menu menu = new Menu(System.in, System.out);
        VendingMachineCLI cli = new VendingMachineCLI(menu);
        System.out.println("\nWelcome to Moe and Chris's VendoTroMatic!");
        cli.run();
    }
}