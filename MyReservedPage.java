import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.text.*;

public class MyReservedPage extends JFrame{
	private JButton logout;
	private JLabel user;
	private JTextArea detailLabel;
	private JList clothesList;
	private JButton detail;
	private JButton reserve;
	private JButton rented;
	private JButton giveBack;
	private DefaultListModel clothesListModel;	

	class LogoutButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			Login login = new Login();
			login.frame.setVisible(true);
			setVisible(false);
		}
	}

	class ReserveButtonAction implements ActionListener{
		int userId;
		
		ReserveButtonAction(int inputUserId){
			super();
			userId = inputUserId;
		}
		public void actionPerformed(ActionEvent e){
			Reserve app = new Reserve(userId);
			setVisible(false);
		}
	}

	class RentedButtonAction implements ActionListener{
		int userId;
		
		RentedButtonAction(int inputUserId){
			super();
			userId = inputUserId;
		}
		public void actionPerformed(ActionEvent e){
			MyRentedPage rented  = new MyRentedPage(userId); 
			rented.setVisible(true);
			setVisible(false);
		}
	}
	
	class DetailButtonAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int selected = clothesList.getSelectedIndex();
			if(selected==-1){
				detailLabel.setText("");
				JOptionPane.showMessageDialog(clothesList, "None selected!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String chose = clothesList.getSelectedValue().toString();
			String[] data = chose.split(" ");
			Connection connection = null;
        try
        {

        connection = DriverManager.getConnection("jdbc:sqlite:system.sqlite");
        Statement statement = connection.createStatement();
		statement.setQueryTimeout(30);  
		String sql = "select * from clothes where id = " + data[0];
        ResultSet rs = statement.executeQuery(sql);
		StringBuffer sb = new StringBuffer();
		sb.append("ID: " + rs.getInt("id") + "\n"); 
		sb.append("Name: " + rs.getString("name") + "\n");
		sb.append("Gender: " + rs.getInt("gender") + "\n");
		sb.append("Genre: " + rs.getInt("genre") + "\n");
		sb.append("Height: " + rs.getInt("size") + "\n");
		sb.append("Color: " + rs.getString("color") + "\n");
		sb.append("Price: " + rs.getInt("price") + "\n");
		sb.append("Period: " + rs.getInt("period") + "\n");
		sb.append("RentalUserID: " + rs.getInt("rental_user_id") + "\n");
		detailLabel.setText(new String(sb));
        }
        catch(SQLException ex)
        {
          System.err.println(ex.getMessage());
        }
        finally
        {
          try
          {
            if(connection != null)
              connection.close();
          }
          catch(SQLException ex)
          {
            System.err.println(ex);
          }
        }
	}
}

	class CancelButtonAction implements ActionListener{
		int userId;
		String clothesId;
		CancelButtonAction(int inputUserId){
			super();
			userId = inputUserId;
		}
		public void actionPerformed(ActionEvent e){
			String[] data;
			int selected = clothesList.getSelectedIndex();
			if(selected == -1){
				JOptionPane.showMessageDialog(clothesList, "None selected!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else{
				String chose = clothesList.getSelectedValue().toString();
				data = chose.split(" ");
				clothesListModel.remove(selected);
			}
			
			Connection connection = null;
        try
        {
        connection = DriverManager.getConnection("jdbc:sqlite:system.sqlite");
        Statement statement = connection.createStatement();
		statement.setQueryTimeout(30);  
		String sql = "delete from reservations where clothes_id = " + data[0] + " and user_id = " + userId;
		statement.executeUpdate(sql);
		detailLabel.setText("");
		JFrame frame = new JFrame();
		JLabel label = new JLabel("Canceled!");
		label.setForeground(Color.RED);
		JOptionPane.showMessageDialog(frame, label, "Dialog", JOptionPane.PLAIN_MESSAGE);
        }
        catch(SQLException ex)
        {
          System.err.println(ex.getMessage());
        }
        finally
        {
          try
          {
            if(connection != null)
              connection.close();
          }
          catch(SQLException ex)
          {
            System.err.println(ex);
          }
        }
	}
}


	MyReservedPage(int userId){
		setTitle("My Reserved Page");
		setSize(520, 520);
		setLocationRelativeTo(null);
		clothesListModel = new DefaultListModel();
		clothesList = new JList(clothesListModel);
		Connection connection = null;
		String userName = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:system.sqlite");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); 
			String sql = "select * from users where id = " + userId;
			ResultSet rs = statement.executeQuery(sql);
			userName = rs.getString("user_name");
			sql = "select c.* from clothes c, reservations r where r.clothes_id = c.id and r.user_id = " + userId;
			rs = statement.executeQuery(sql);
			while (rs.next()) {
				clothesListModel.addElement(rs.getString("id") + " " + rs.getString("name"));
			}
		}
		catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		finally {
			try {
				if (connection != null){
					connection.close();
				}	
			}
			catch (SQLException ex) {

				System.err.println(ex);
			}
		}
		user = new JLabel(userName+"'s Reserved Page");
		detail = new JButton("Detail");
		reserve  = new JButton("Let's Reserve");
		rented = new JButton("My Rental Page");
		logout = new JButton("Logout");
		giveBack = new JButton("Cancel");
		detailLabel = new JTextArea();
		detailLabel.setEditable(false);
		JScrollPane sp = new JScrollPane();
		
	    sp.getViewport().setView(clothesList);
	    sp.setPreferredSize(new Dimension(200, 80));
	    
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder( 20, 60, 20, 60 ));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JPanel startPanel = new JPanel();
		startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.X_AXIS));
		JPanel endPanel = new JPanel();
		endPanel.setLayout(new BoxLayout(endPanel, BoxLayout.X_AXIS));
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));

		ReserveButtonAction ReserveButtonListener = new ReserveButtonAction(userId);
		reserve.addActionListener(ReserveButtonListener);
		RentedButtonAction RentedButtonListener = new RentedButtonAction(userId);
		rented.addActionListener(RentedButtonListener);
		DetailButtonAction DetailButtonListener = new DetailButtonAction();
		detail.addActionListener(DetailButtonListener);
		CancelButtonAction CancelButtonListener = new CancelButtonAction(userId);
		giveBack.addActionListener(CancelButtonListener);
		LogoutButtonAction LogoutButtonListener = new LogoutButtonAction();
		logout.addActionListener(LogoutButtonListener);
		
		startPanel.add(user);
		startPanel.add(rented);
		startPanel.add(logout);
		centerPanel.add(sp);
		centerPanel.add(detailLabel);
		endPanel.add(detail);
		endPanel.add(reserve);
		endPanel.add(giveBack);
		panel.add(startPanel);
		panel.add(centerPanel);
    	panel.add(endPanel);

		Container pane = getContentPane();
		pane.add(panel);
	}	
}
