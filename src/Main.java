import java.sql.*;
import java.util.Scanner;


public class Main{
    private static final String url = "jdbc:mysql://localhost:3306/lenden";
    private static final String username = "root";    private static final String password = "dipak";

    public static void main(String[] args) {

//        Load drivers
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        // make Connection with Driver Manager

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            String credit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

            PreparedStatement debitpreparedStatement = connection.prepareStatement(debit_query);
            PreparedStatement creditpreparedStatement = connection.prepareStatement(credit_query);

            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Sender Account Number - : ");
            int fromAccountNumber = sc.nextInt();

            System.out.print("Enter Receiver Account Number + : ");
            int ToAccountNumber = sc.nextInt();
            System.out.print("Enter Amount in INR : ");
            double amount = sc.nextDouble();



            debitpreparedStatement.setDouble(1,amount);
            debitpreparedStatement.setInt(2,fromAccountNumber);
            creditpreparedStatement.setDouble(1,amount);
            creditpreparedStatement.setInt(2,ToAccountNumber);

            debitpreparedStatement.executeUpdate();
            creditpreparedStatement.executeUpdate();

            if (isSufficient(connection, fromAccountNumber, amount)) {
                connection.commit();
                System.out.println("Transaction successful ");
            }else {
                connection.rollback();
                System.out.println("Transaction Fail, Insufficient Balance");
            }
            debitpreparedStatement.close();
            creditpreparedStatement.close();
            connection.close();
            sc.close();


        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }


    static boolean isSufficient(Connection connection, int account_number, double amount) {

        try {
            String query = "SELECT balance FROM accounts WHERE account_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double current_balance = resultSet.getDouble("balance");
                if (amount > current_balance) {
                    return false;
                } else {
                    return true;
                }
            }
                resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}


// Project Completed