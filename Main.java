package BANKINGAPPLICATIONByCK.src;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Select an operation:");
                System.out.println("1. Open Account");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline character

                switch (choice) {
                    case 1:
                        openAccount(scanner);
                        break;
                    case 2:
                        login(scanner);
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void openAccount(Scanner scanner) throws SQLException {
        System.out.println("Enter your Account holder name:");
        String accountHolderName = scanner.nextLine();
        System.out.println("Enter initial deposit amount:");
        double initialDeposit = scanner.nextDouble();
        scanner.nextLine();

        String accountNumber = AccountService.createAccount(accountHolderName, initialDeposit);
        System.out.println("Account created successfully. Your account number is: " + accountNumber);
    }

    private static void login(Scanner scanner) throws SQLException {
        System.out.println("Enter your Account holder name:");
        String accountHolderName = scanner.nextLine();
        String accountNumber = AccountService.getAccountNumberByName(accountHolderName);

        if (accountNumber == null) {
            System.out.println("Account not found for the given name.");
            return;
        }

        while (true) {
            System.out.println("Select an operation:");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            Account account;
            double amount;

            switch (choice) {
                case 1:
                    account = AccountService.checkBalance(accountNumber);
                    System.out.println("Balance: " + account.getBalance() + " Account holder: " + account.getAccountHolderName());
                    break;
                case 2:
                    System.out.println("Enter amount you want to deposit:");
                    amount = scanner.nextDouble();
                    scanner.nextLine(); // consume the newline character
                    AccountService.deposit(accountNumber, amount);
                    account = AccountService.checkBalance(accountNumber);
                    System.out.println("Deposited " + amount + " to account " + accountNumber);
                    System.out.println("Your new Balance: " + account.getBalance() + " Account holder: " + account.getAccountHolderName());
                    break;
                case 3:
                    System.out.println("Enter amount you want to withdraw:");
                    amount = scanner.nextDouble();
                    scanner.nextLine(); // consume the newline character
                    account = AccountService.checkBalance(accountNumber);
                    if (account.getBalance() < amount || account.getBalance() == 0.0) {
                        System.out.println("Insufficient balance. Your balance is " + account.getBalance());
                    } else {
                        AccountService.withdraw(accountNumber, amount);
                        account = AccountService.checkBalance(accountNumber);
                        System.out.println("Withdrew " + amount + " from account " + accountNumber);
                        System.out.println("Your new Balance: " + account.getBalance() + " Account holder: " + account.getAccountHolderName());
                    }
                    break;
                case 4:
                    System.out.println("Enter the account number to transfer to:");
                    String toAccountNumber = scanner.next();
                    System.out.println("Enter amount you want to transfer:");
                    amount = scanner.nextDouble();
                    scanner.nextLine(); // consume the newline character
                    account = AccountService.checkBalance(accountNumber);
                    if (account.getBalance() < amount || account.getBalance() == 0.0) {
                        System.out.println("Insufficient balance. Your balance is " + account.getBalance());
                    } else {
                        AccountService.transfer(accountNumber, toAccountNumber, amount);
                        account = AccountService.checkBalance(accountNumber);
                        System.out.println("Transferred " + amount + " from account " + accountNumber + " to account " + toAccountNumber);
                        System.out.println("Your new Balance: " + account.getBalance() + " Account holder: " + account.getAccountHolderName());
                    }
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Not Valid Choice. Please try again.");
                    break;
            }
        }
    }
}
