import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;

public class SignUp extends JFrame{
	private Container pane;
	private JPanel panel;
	private JLabel nameLabel;
	private JLabel passLabel;
	private JLabel emailLabel;
	private JTextField text;
	private JPasswordField pass;
	private JTextField email;
	private JLabel error;
	private JButton button;
	
	class SignUpAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String name = text.getText();
			char[] passChar = pass.getPassword();
			String passStr = new String(passChar);
			String emAddress = email.getText();
			int id = 0;
			Connection connection = null;
			String sql = "insert into users (id, user_name, password, email) values (?, ?, ?, ?)";
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:system.sqlite");
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30);
				ResultSet rs = statement.executeQuery("select * from users");
				while (rs.next()) {
					id = rs.getInt("id");
					if (rs.getString("user_name").equals(name)) {
						error.setText("This name is used.");
						error.setForeground(Color.RED);
						return;
					}
				}
				PreparedStatement pstmt = connection.prepareStatement(sql);
				pstmt.setQueryTimeout(30); 
				pstmt.setInt(1, id+1);
				pstmt.setString(2, name);
				pstmt.setString(3, passStr);
				pstmt.setString(4, emAddress);
				pstmt.executeUpdate();
				JFrame frame = new JFrame();
				JLabel label = new JLabel("Registered!");
				label.setForeground(Color.RED);
				JOptionPane.showMessageDialog(frame, label);
				setVisible(false);
				Login login = new Login();
				login.frame.setVisible(true);
			}
			catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
			finally {
				try {
					if (connection != null)
						connection.close();
				}
				catch (SQLException ex) {
					System.err.println(ex);
				}
			}
			
		}
	}

	SignUp(){
		setTitle("Sign up");
		setSize(300, 300);
		setLocationRelativeTo(null);
		
		nameLabel = new JLabel("User:");
		passLabel = new JLabel("Password:");
		emailLabel = new JLabel("E-mail");
		error = new JLabel();
		text = new JTextField();
		pass = new JPasswordField();
		email = new JTextField();
		
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder( 30, 60, 20, 60 ));
		panel.setLayout(new GridLayout(0,1));
		button = new JButton("Sign up");
		
		SignUpAction signUpListener = new SignUpAction();
		button.addActionListener(signUpListener);

		panel.add(nameLabel);
		panel.add(text);
		panel.add(passLabel);
		panel.add(pass);
		panel.add(emailLabel);
		panel.add(email);
		panel.add(button);
		panel.add(error);
		
		pane = getContentPane();
		pane.add(panel);
	
	}
	
}
