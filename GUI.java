import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class GUI extends JFrame implements ActionListener{
	
	private static final int WIDTH = 730;
	private static final int HEIGHT = 400;
	
	private JTextArea chatArea;
	private JTextField chatField;
	private ImageIcon[] icons;
	private JLabel[] dices;
	private JButton[] fixbtn;
	private JButton roll;
	
	public GUI()
	{
		setTitle("TEST");
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		
		JLabel name1 = new JLabel("어디까지긴닉네임가능?");
		name1.setBounds(500, 10, 100, 20);
		JLabel name2 = new JLabel("어디까지긴닉네임가능?");
		name2.setBounds(600, 10, 100, 20);
		add(name1);
		add(name2);
		
		icons = new ImageIcon[7];
		for (int i = 0; i < 7; i++)
			icons[i] = new ImageIcon(new ImageIcon(this.getClass().getResource("/Images/" + i + ".png")).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
		
		dices = new JLabel[10];
		fixbtn = new JButton[10];
		for (int i = 0; i < 10; i++)
		{
			dices[i] = new JLabel();
			if (i < 5)
			{
				dices[i].setIcon(icons[0]);
				fixbtn[i] = new JButton("▼");
				dices[i].setBounds(30 + 60 * i, 40, 50, 50);
				fixbtn[i].setBounds(30 + 60 * i, 100, 50, 20);
			}
			else
			{
				dices[i].setIcon(icons[i - 4]);
				fixbtn[i] = new JButton("▲");
				dices[i].setBounds(30 + 60 * (i - 5), 160, 50, 50);
				fixbtn[i].setBounds(30 + 60 * (i - 5), 220, 50, 20);
			}
			add(dices[i]);
			fixbtn[i].setActionCommand("fix" + i);
			fixbtn[i].addActionListener(this);
			add(fixbtn[i]);
		}
		
		roll = new JButton("Roll");
		roll.setBounds(330, 100, 80, 80);
		roll.addActionListener(this);
		add(roll);
		
		JLabel[] row = new JLabel[15];
		row[0] = new JLabel("Aces");
		row[1] = new JLabel("Deuces");
		row[2] = new JLabel("Threes");
		row[3] = new JLabel("Fours");
		row[4] = new JLabel("Fives");
		row[5] = new JLabel("Sixes");
		row[6] = new JLabel("SubTotal");
		row[7] = new JLabel("Bonus");
		row[8] = new JLabel("Choice");
		row[9] = new JLabel("4 of a kind");
		row[10] = new JLabel("Full House");
		row[11] = new JLabel("S. Straight");
		row[12] = new JLabel("L. Straight");
		row[13] = new JLabel("Yacht");
		row[14] = new JLabel("Total");
		for (int i = 0; i < 15; i++)
			row[i].setBounds(430, 35 + 20 * i, 100, 20);
		
		JLabel[] user1row = new JLabel[15];
		user1row[0] = new JLabel("0");
		user1row[1] = new JLabel("0");
		user1row[2] = new JLabel("0");
		user1row[3] = new JLabel("0");
		user1row[4] = new JLabel("0");
		user1row[5] = new JLabel("0");
		user1row[6] = new JLabel("0");
		user1row[7] = new JLabel("0");
		user1row[8] = new JLabel("0");
		user1row[9] = new JLabel("0");
		user1row[10] = new JLabel("0");
		user1row[11] = new JLabel("0");
		user1row[12] = new JLabel("0");
		user1row[13] = new JLabel("0");
		user1row[14] = new JLabel("0");
		for (int i = 0; i < 15; i++)
			user1row[i].setBounds(535, 35 + 20 * i, 100, 20);
		
		JLabel[] user2row = new JLabel[15];
		user2row[0] = new JLabel("0");
		user2row[1] = new JLabel("0");
		user2row[2] = new JLabel("0");
		user2row[3] = new JLabel("0");
		user2row[4] = new JLabel("0");
		user2row[5] = new JLabel("0");
		user2row[6] = new JLabel("0");
		user2row[7] = new JLabel("0");
		user2row[8] = new JLabel("0");
		user2row[9] = new JLabel("0");
		user2row[10] = new JLabel("0");
		user2row[11] = new JLabel("0");
		user2row[12] = new JLabel("0");
		user2row[13] = new JLabel("0");
		user2row[14] = new JLabel("0");
		for (int i = 0; i < 15; i++)
		{
			user2row[i].setBounds(635, 35 + 20 * i, 100, 20);
			add(user2row[i]);
		}
		
		for (int i = 0; i < 15; i++)
		{
			add(row[i]);
			add(user1row[i]);
			add(user2row[i]);
		}
		
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane scrollPane1 = new JScrollPane(chatArea);
		scrollPane1.setBounds(15, 260, 405, 70);
		
		chatField = new JTextField("",30);
		chatField.setBounds(15, 330, 405, 20);
		
		add(scrollPane1);
		add(chatField);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(""))
		{
		}
	}
	
	public static void main(String argrs[])
	{
		new GUI();
	}
}
