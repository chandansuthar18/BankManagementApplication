package BANKINGAPPLICATIONByCK.src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountService {

    public static Account checkBalance(String accountNumber) throws SQLException {
        String query = "SELECT account_holder_name, balance FROM accounts WHERE account_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String accountHolderName = resultSet.getString("account_holder_name");
                double balance = resultSet.getDouble("balance");
                return new Account(accountNumber, accountHolderName, balance);
            } else {
                throw new SQLException("Account not found.");
            }
        }
    }

    public static void deposit(String accountNumber, double amount) throws SQLException {
        String updateBalanceQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String insertTransactionQuery = "INSERT INTO transactions (account_number, amount, transaction_type) VALUES (?, ?, 'DEPOSIT')";

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery);
                 PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

                connection.setAutoCommit(false);

                updateBalanceStmt.setDouble(1, amount);
                updateBalanceStmt.setString(2, accountNumber);
                updateBalanceStmt.executeUpdate();

                insertTransactionStmt.setString(1, accountNumber);
                insertTransactionStmt.setDouble(2, amount);
                insertTransactionStmt.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();
                }
                throw new SQLException("Failed to deposit money.", e);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static void withdraw(String accountNumber, double amount) throws SQLException {
        String checkBalanceQuery = "SELECT balance FROM accounts WHERE account_number = ?";
        String updateBalanceQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String insertTransactionQuery = "INSERT INTO transactions (account_number, amount, transaction_type) VALUES (?, ?, 'WITHDRAWAL')";

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement checkBalanceStmt = connection.prepareStatement(checkBalanceQuery);
                 PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery);
                 PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

                connection.setAutoCommit(false);

                checkBalanceStmt.setString(1, accountNumber);
                ResultSet resultSet = checkBalanceStmt.executeQuery();
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    if (balance < amount) {
                        throw new SQLException("Insufficient balance.");
                    }
                } else {
                    throw new SQLException("Account not found.");
                }

                updateBalanceStmt.setDouble(1, amount);
                updateBalanceStmt.setString(2, accountNumber);
                updateBalanceStmt.executeUpdate();

                insertTransactionStmt.setString(1, accountNumber);
                insertTransactionStmt.setDouble(2, amount);
                insertTransactionStmt.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();
                }
                throw new SQLException("Failed to withdraw money.", e);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static void transfer(String fromAccount, String toAccount, double amount) throws SQLException {
        String checkBalanceQuery = "SELECT balance FROM accounts WHERE account_number = ?";
        String updateFromBalanceQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String updateToBalanceQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String insertTransactionQuery = "INSERT INTO transactions (account_number, amount, transaction_type) VALUES (?, ?, 'TRANSFER')";

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement checkBalanceStmt = connection.prepareStatement(checkBalanceQuery);
                 PreparedStatement updateFromBalanceStmt = connection.prepareStatement(updateFromBalanceQuery);
                 PreparedStatement updateToBalanceStmt = connection.prepareStatement(updateToBalanceQuery);
                 PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

                connection.setAutoCommit(false);

                checkBalanceStmt.setString(1, fromAccount);
                ResultSet resultSet = checkBalanceStmt.executeQuery();
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    if (balance < amount) {
                        throw new SQLException("Insufficient balance.");
                    }
                } else {
                    throw new SQLException("Source account not found.");
                }

                updateFromBalanceStmt.setDouble(1, amount);
                updateFromBalanceStmt.setString(2, fromAccount);
                updateFromBalanceStmt.executeUpdate();

                updateToBalanceStmt.setDouble(1, amount);
                updateToBalanceStmt.setString(2, toAccount);
                updateToBalanceStmt.executeUpdate();

                insertTransactionStmt.setString(1, fromAccount);
                insertTransactionStmt.setDouble(2, amount);
                insertTransactionStmt.executeUpdate();

                insertTransactionStmt.setString(1, toAccount);
                insertTransactionStmt.setDouble(2, amount);
                insertTransactionStmt.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();
                }
                throw new SQLException("Failed to transfer money.", e);
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static String createAccount(String accountHolderName, double initialDeposit) throws SQLException {
        String insertAccountQuery = "INSERT INTO accounts (account_number, account_holder_name, balance) VALUES (?, ?, ?)";
        String getAccountNumberQuery = "SELECT account_number FROM accounts WHERE account_holder_name = ?";

        String accountNumber = UUID.randomUUID().toString().substring(0, 20);  // Generate a unique account number

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement insertAccountStmt = connection.prepareStatement(insertAccountQuery);
             PreparedStatement getAccountNumberStmt = connection.prepareStatement(getAccountNumberQuery)) {

            insertAccountStmt.setString(1, accountNumber);
            insertAccountStmt.setString(2, accountHolderName);
            insertAccountStmt.setDouble(3, initialDeposit);
            insertAccountStmt.executeUpdate();

            getAccountNumberStmt.setString(1, accountHolderName);
            ResultSet resultSet = getAccountNumberStmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("account_number");
            } else {
                throw new SQLException("Failed to retrieve account number.");
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to create account.", e);
        }
    }

    // New method to fetch account number by account holder's name
    public static String getAccountNumberByName(String accountHolderName) throws SQLException {
        String query = "SELECT account_number FROM accounts WHERE account_holder_name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountHolderName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("account_number");
            } else {
                return null;
            }
        }
    }
}
