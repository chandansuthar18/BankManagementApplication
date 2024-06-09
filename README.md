Banking Application
This is a simple JavaFX banking application that allows users to check their balance, deposit money, withdraw money, and transfer money between accounts.

Features
Create Account :Create the New Accoun
Check Balance: View the balance of your account.
Deposit Money: Deposit money into your account.
Withdraw Money: Withdraw money from your account.
Transfer Money: Transfer money from your account to another account.
User-Friendly Messages: Provides clear feedback for operations and handles insufficient balance scenarios gracefully.
Prerequisites
Java Development Kit (JDK) 8 or higher
MySQL Database
Database Setup
Install MySQL: Ensure you have MySQL installed on your machine.

Create Database and Tables:

Create a database named banking_app_system.

Create the accounts table:
Create the transactions table:
Create the user table:

Configuration
Database Connection: Ensure the DatabaseConnection class has the correct MySQL URL, user, and password.

How to Run
Compile the Program:
Navigate to your project directory and compile the Java files:
javac -cp "path/to/mysql-connector-java.jar" *.java
Run the Program:
Run the main class:
java -cp ".;path/to/mysql-connector-java.jar" Main

Notes
Ensure your MySQL service is running and accessible.
Handle the necessary exception handling and validations for a robust application.
License
This project is licensed under the Chandan License.


