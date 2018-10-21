import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.text.*;

public class MyRentedPage extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int arrears = 2000;
	private JLabel user;
	private JTextArea detailLabel;
	private JList clothesList;
	private JButton detail;
	private JButton rent;
	private JButton reserved;
	private JButton giveBack;
	private DefaultListModel clothesListModel;
	private JButton logout;

	class LogoutButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Login login = new Login();
			login.frame.setVisible(true);
			setVisible(false);
		}
	}

	class RentButtonAction implements ActionListener {
		int userId;

		RentButtonAction(int inputUserId) {
			super();
			userId = inputUserId;
		}

		public void actionPerformed(ActionEvent e) {
			Rental app = new Rental(userId);
			setVisible(false);
		}
	}

	class ReservedButtonAction implements ActionListener {
		int userId;

		ReservedButtonAction(int inputUserId) {
			super();
			userId = inputUserId;
		}

		public void actionPerformed(ActionEvent e) {
			MyReservedPage reserved = new MyReservedPage(userId);
			reserved.setVisible(true);
			setVisible(false);
		}
	}

	class DetailButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int selected = clothesList.getSelectedIndex();
			if (selected == -1) {
				detailLabel.setText("");
				JOptionPane.showMessageDialog(clothesList, "None selected!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String chose = clothesList.getSelectedValue().toString();
			String[] data = chose.split(" ");
			Connection connection = null;
			try {
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
				sb.append("RentalDeadline: " + rs.getString("return_deadline") + "\n");
				detailLabel.setText(new String(sb));
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

	class ReturnButtonAction implements ActionListener {
		int userId;
		String clothesId;

		ReturnButtonAction(int inputUserId) {
			super();
			userId = inputUserId;
		}

		public void actionPerformed(ActionEvent e) {
			String[] data;
			int selected = clothesList.getSelectedIndex();
			if (selected == -1) {
				JOptionPane.showMessageDialog(clothesList, "None selected!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				String chose = clothesList.getSelectedValue().toString();
				data = chose.split(" ");
				clothesListModel.remove(selected);
			}

			Connection connection = null;
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:system.sqlite");
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30); 

				String sql = "select * from reservations where clothes_id = " + data[0];
				ResultSet rs = statement.executeQuery(sql);
				int next_user_id = 0;
				if (rs.isBeforeFirst()) {
					next_user_id = rs.getInt(1);
					detailLabel.setText("");

					sql = "delete from reservations where user_id = " + next_user_id + " and clothes_id = " + data[0];
					statement.executeUpdate(sql);

					sql = "select * from clothes where id = " + data[0];
					rs = statement.executeQuery(sql);
					sql = "update clothes set rental_user_id = " + next_user_id + " where id = " + data[0];
					statement.executeUpdate(sql);

					sql = "select return_deadline from clothes where id = " + data[0];
					rs = statement.executeQuery(sql);

					String due = rs.getString("return_deadline");
					String[] ymd = due.split("/");

					Calendar rd = Calendar.getInstance();
					
					rd.set(Integer.parseInt(ymd[0]),Integer.parseInt(ymd[1])-1,Integer.parseInt(ymd[2]),0,0,0);

					Calendar now = Calendar.getInstance();
					
					long diffTime = now.getTimeInMillis() - rd.getTimeInMillis();
					int diffDays = (int)(diffTime / (1000 * 60 * 60 * 24));

					sql = "select period from clothes where id = " + data[0];
					rs = statement.executeQuery(sql);
					now.add(Calendar.DAY_OF_MONTH, rs.getInt(1));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					sql = "update clothes set return_deadline = '" + sdf.format(now.getTime()) + "' where id = " + data[0];
					statement.executeUpdate(sql);

					if(diffTime > 0){
						JFrame frame = new JFrame();
						JLabel label = new JLabel("Pay " + arrears * (diffDays + 1) + "yen");
						label.setForeground(Color.RED);
						JOptionPane.showMessageDialog(frame, label, "Dialog", JOptionPane.PLAIN_MESSAGE);
						return;
					}
				}
				else{
					sql = "update clothes set rental_user_id = 0 where id = " + data[0];
					statement.executeUpdate(sql);
					sql = "update clothes set return_deadline = '0' where id = " + data[0];
					statement.executeUpdate(sql);
				}
				detailLabel.setText("");
				JFrame frame = new JFrame();
				JLabel label = new JLabel("Returned!");
				label.setForeground(Color.RED);
				JOptionPane.showMessageDialog(frame, label, "Dialog", JOptionPane.PLAIN_MESSAGE);
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (connection != null)
						connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	MyRentedPage(int userId) {
		setTitle("My Rental Page");
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
			sql = "select * from clothes where rental_user_id = " + userId;
			rs = statement.executeQuery(sql);
			while (rs.next()) {
				clothesListModel.addElement(rs.getString("id") + " " + rs.getString("name"));
			}

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {

				System.err.println(ex);
			}
		}

		user = new JLabel(userName + "'s Rental Page");
		detail = new JButton("Detail");
		rent = new JButton("Let's Rental");
		logout = new JButton("Logout");
		reserved = new JButton("My Reserved Page");
		giveBack = new JButton("Return");

		detailLabel = new JTextArea();
		detailLabel.setEditable(false);
		JScrollPane sp = new JScrollPane();

		sp.getViewport().setView(clothesList);
		sp.setPreferredSize(new Dimension(200, 80));

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JPanel startPanel = new JPanel();
		startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.X_AXIS));
		JPanel endPanel = new JPanel();
		endPanel.setLayout(new BoxLayout(endPanel, BoxLayout.X_AXIS));
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));

		RentButtonAction RentButtonListener = new RentButtonAction(userId);
		rent.addActionListener(RentButtonListener);
		ReservedButtonAction ReservedButtonListener = new ReservedButtonAction(userId);
		reserved.addActionListener(ReservedButtonListener);
		DetailButtonAction DetailButtonListener = new DetailButtonAction();
		detail.addActionListener(DetailButtonListener);
		ReturnButtonAction ReturnButtonListener = new ReturnButtonAction(userId);
		giveBack.addActionListener(ReturnButtonListener);
		LogoutButtonAction LogoutButtonListener = new LogoutButtonAction();
		logout.addActionListener(LogoutButtonListener);

		startPanel.add(user);
		startPanel.add(reserved);
		startPanel.add(logout);
		centerPanel.add(sp);
		centerPanel.add(detailLabel);
		endPanel.add(detail);
		endPanel.add(rent);
		endPanel.add(giveBack);
		panel.add(startPanel);
		panel.add(centerPanel);
		panel.add(endPanel);
		Container pane = getContentPane();
		pane.add(panel);
	}
}
