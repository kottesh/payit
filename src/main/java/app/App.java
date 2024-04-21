package app;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

enum Service {
    UPI,
    NETBANKING,
    CREDIT,
    DEBIT
}

// Account Type
enum AccountType {
    SAVINGS,
    CURRENT
}

class Logger {
    public static void Log(String message, String type) {
        if (type.equalsIgnoreCase("error")) {
            System.err.println("[ERROR]: " + message);
        } else {
            System.out.println("[INFO]: " + message);
        }
    }
}

class Bank {
    private String name;
    private String Id; 

    HashMap<Long, Account> accounts; // to store the list of accounts in the bank 

    Bank(
        String name
    ) {
        this.name = name;
        this.Id = String.valueOf(Math.abs((new Random()).nextLong()) % 1000000000000L);  // 12-digit Bank ID
        this.accounts = new HashMap<>();
    }

    public void createAccount(String acHolderName, BigDecimal openingBalance, AccountType accountType) {
        Long acNo = Math.abs(((new Random()).nextLong()) % 100000000L); // 8-digit Account Number
        this.accounts.put(acNo, new Account(acNo, acHolderName, openingBalance, accountType));
    }

    public void removeAccount(Long acNo) {
        this.accounts.remove(acNo);
    }

    // add service to the account 
    public void addService(Long acNo, Service type) {
        if (accounts.containsKey(acNo)) {
            ArrayList<Service> services = accounts.get(acNo).getServices();
            if (!services.contains(type)) {
                services.add(type);
                Logger.Log("Service("+ type +") added for AC: " + acNo, "info");
            } else {
                Logger.Log("Service("+ type +") is already available for AC: " + acNo, "info");
            }
        } else {
            Logger.Log("Account (" + acNo + ") NOT FOUND!", "error");
        }
    }

    public void removeService(Long acNo, Service type) {
        if (accounts.containsKey(acNo)) {
            ArrayList<Service> services = accounts.get(acNo).getServices();
            if (services.contains(type)) {
                services.remove(type);
                Logger.Log("Service("+ type +") removed for AC: " + acNo, "info");
            } else {
                // don't do anything
                //Logger.Log("Service("+ type +") is not available for AC: " + acNo, "info");
            }
        } else {
            Logger.Log("Account (" + acNo + ") NOT FOUND!", "error");
        }
    }

    public Account getAccount(Long acNo) {
        return accounts.get(acNo); 
    } 

    /**
     *  displays information such bank name, id, and 
     *  accounts associated with the bank
     */
    public void getBankInfos() {
        System.out.println("Bank Name: " + name);
        System.out.println("Bank ID  :" + Id);
        System.out.println(accounts);
    }
}

/*
 * By default every account will have the Debit Card
 */

class Account {
    private Long acNo;
    private String acHolderName;
    private BigDecimal currentBalance; 
    private ArrayList<Service> services;
    private AccountType accountType;

    Account(
        Long acNo, String acHolderName, BigDecimal openingBalance, AccountType accountType
    ) {
        this.acNo = acNo;
        this.acHolderName = acHolderName;
        this.accountType = accountType; 
        this.currentBalance = openingBalance; 
        this.services = new ArrayList<>();

        services.add(Service.DEBIT); // DEFAULT: Debit Card.
    } 

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.currentBalance = this.currentBalance.add(amount);
        } else {
            Logger.Log("Invalid Amount: " + amount, "error");
        }
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            if (amount.compareTo(currentBalance) > 0) {
                this.currentBalance = this.currentBalance.subtract(amount);
            } else {
                Logger.Log("Insufficent Balance: " + amount, "info");
            }
        } else  {
            Logger.Log("Invalid Amount: " + amount, "error");
        }
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public Long getAcNo() {
        return acNo;
    } 

    public AccountType getAcType() {
        return accountType;
    }

    // Override toString() to provide a formatted string representation for direct printing of Account objects
    // By default using println shows the class name and its hashcode. 
    @Override
    public String toString() {
        return "Account{" +
                "acNo=" + acNo +
                ", acHolderName='" + acHolderName + '\'' +
                ", currentBalance=" + currentBalance +
                ", services=" + services +
                ", accountType=" + accountType +
                '}';
    }
}

public class App  {
    public static void main(String[] args) {
        ArrayList<Bank> banks = new ArrayList<>();

        banks.add(new Bank("Bank 1"));
        banks.add(new Bank("Bank 2"));
        banks.add(new Bank("Bank 3"));
        banks.add(new Bank("Bank 4"));


        banks.get(0).createAccount("customer 1", new BigDecimal("1200"), AccountType.SAVINGS);
        banks.get(0).createAccount("customer 2", new BigDecimal("1400"), AccountType.CURRENT);
        banks.get(2).createAccount("customer 3", new BigDecimal("1600"), AccountType.SAVINGS);
        banks.get(3).createAccount("customer 4", new BigDecimal("1800"), AccountType.CURRENT);

        for (Bank bank:banks) {
            bank.getBankInfos();
        }
    }
}
