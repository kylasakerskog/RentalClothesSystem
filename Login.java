import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;

public class Login {
    JFrame frame;
    JPanel panel;
    JLabel nameLabel;
    JLabel passLabel;
    JTextField userName;
    JPasswordField pass;
    JLabel error;
    JLabel user;
    JLabel password;
    JButton createAccount;
    JButton button;

    public static void main(String[] args) {
        Login login = new Login();
        login.frame.setVisible(true);
    }

    class CreateAccountButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            SignUp signUp = new SignUp();
            signUp.setVisible(true);
        }
    }

    class LoginButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = userName.getText();
            char[] inputPass = pass.getPassword();
            String inputPassStr = new String(inputPass);
            Connection connection = null;

            try {
                connection = DriverManager.getConnection("jdbc:sqlite:system.sqlite");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                ResultSet rs = statement.executeQuery("select * from users");

                while (rs.next()) {
                    if (rs.getString("user_name").equals(name)) {
                        if (rs.getString("password").equals(inputPassStr)) {
                            MyRentedPage myRentedPage = new MyRentedPage(rs.getInt("id"));
                            myRentedPage.setVisible(true);
                            frame.setVisible(false);
                            break;
                        } else {
                            error.setText("NOT FOUND");
                            error.setForeground(Color.RED);
                        }
                    } else {
                        error.setText("NOT FOUND");
                        error.setForeground(Color.RED);
                    }
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (SQLException ex) {
                    System.err.println(ex);
                }
            }

        }
    }

    Login() {
        frame = new JFrame("Login");
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nameLabel = new JLabel("User");
        passLabel = new JLabel("Password");
        error = new JLabel();
        userName = new JTextField();
        pass = new JPasswordField();
        createAccount = new JButton("Sign up");
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 20, 60));
        panel.setLayout(new GridLayout(0, 1));
        button = new JButton("Login");
        LoginButtonAction loginButtonListener = new LoginButtonAction();
        button.addActionListener(loginButtonListener);
        CreateAccountButtonAction createAccountButtonListener = new CreateAccountButtonAction();
        createAccount.addActionListener(createAccountButtonListener);
        panel.add(nameLabel);
        panel.add(userName);
        panel.add(passLabel);
        panel.add(pass);
        panel.add(button);
        panel.add(error);
        panel.add(createAccount);
        Container contents = panel;
        frame.getContentPane().add(contents, BorderLayout.CENTER);
    }
}
