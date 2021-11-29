import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Room extends JFrame implements ActionListener{

	private PrintWriter pw;
	private static final int WIDTH = 730;
	private static final int HEIGHT = 400;
	private static final String SPLITER = "#!#";
	
	private JTextArea chatArea;
	private JTextField chatField;
	private JLabel name1;
	private JLabel name2;
	private ImageIcon[] icons;
	private JLabel[] dices;
	private int[] diceVal;
	private JButton[] fixbtn;
	private JButton roll;
	private JLabel[] user1row;
	private JLabel[] user2row;
	private int[] user1val;
	private int[] user2val;
	private JButton[] user1btn;
	private JButton[] user2btn;
	private JLabel rollCount;
	
	public Room(Socket socket, PrintWriter pw, String title)
	{
		this.pw = pw;
		setTitle(title);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(null);
		
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane scrollPane1 = new JScrollPane(chatArea);
		scrollPane1.setBounds(15, 260, 405, 70);
		
		chatField = new JTextField("",30);
		chatField.setBounds(15, 330, 405, 20);
		chatField.addKeyListener(new KeyAdapter()
		{
            public void keyReleased(KeyEvent e)
            {
                char keyCode = e.getKeyChar();
                if (keyCode == KeyEvent.VK_ENTER && !chatField.getText().isEmpty())
                    sendMessage();
            }
        });
		
		addWindowListener(new WindowAdapter()
		{
            public void windowClosing(WindowEvent e)
            {
            	doMessage("quit");
            }
        });
		
		add(scrollPane1);
		add(chatField);
		
		name1 = new JLabel("-");
		name1.setBounds(500, 10, 100, 20);
		name2 = new JLabel("-");
		name2.setBounds(600, 10, 100, 20);
		add(name1);
		add(name2);
		
		icons = new ImageIcon[7];
		for (int i = 0; i < 7; i++)
			icons[i] = new ImageIcon(new ImageIcon(this.getClass().getResource("/Images/" + i + ".png")).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
		
		dices = new JLabel[10];
		diceVal = new int[5];
		fixbtn = new JButton[10];
		for (int i = 0; i < 10; i++)
		{
			dices[i] = new JLabel();
			if (i < 5)
			{
				dices[i].setIcon(icons[0]);
				diceVal[i] = 0;
				fixbtn[i] = new JButton("▼");
				dices[i].setBounds(30 + 60 * i, 40, 50, 50);
				fixbtn[i].setBounds(30 + 60 * i, 100, 50, 20);
			}
			else
			{
				dices[i].setIcon(icons[0]);
				fixbtn[i] = new JButton("▲");
				dices[i].setBounds(30 + 60 * (i - 5), 160, 50, 50);
				fixbtn[i].setBounds(30 + 60 * (i - 5), 220, 50, 20);
			}
			add(dices[i]);
			fixbtn[i].setActionCommand("fix/" + i);
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
		
		user1row = new JLabel[15];
		user1val = new int[15];
		user1btn = new JButton[15];
		for (int i = 0; i < 15; i++)
		{
			user1row[i] = new JLabel("0");
			user1row[i].setBounds(535, 35 + 20 * i, 50, 20);
			user1row[i].setVisible(false);
			user1val[i] = 0;
			user1btn[i] = new JButton("-");
			user1btn[i].setActionCommand("user1/" + i);
			user1btn[i].setBounds(515, 35 + 20 * i, 50, 20);
			user1btn[i].addActionListener(this);
			if (i == 6 || i == 7 || i == 14)
			{
				user1btn[i].setVisible(false);
				user1row[i].setVisible(true);
			}
		}
		
		user2row = new JLabel[15];
		user2val = new int[15];
		user2btn = new JButton[15];
		for (int i = 0; i < 15; i++)
		{
			user2row[i] = new JLabel("0");
			user2row[i].setBounds(635, 35 + 20 * i, 50, 20);
			user2row[i].setVisible(false);
			user2val[i] = 0;
			user2btn[i] = new JButton("-");
			user2btn[i].setActionCommand("user2/" + i);
			user2btn[i].setBounds(615, 35 + 20 * i, 50, 20);
			user2btn[i].addActionListener(this);
			if (i == 6 || i == 7 || i == 14)
			{
				user2btn[i].setVisible(false);
				user2row[i].setVisible(true);
			}
		}
		
		for (int i = 0; i < 15; i++)
		{
			add(row[i]);
			add(user1row[i]);
			add(user2row[i]);
			add(user1btn[i]);
			add(user2btn[i]);
		}
		
		rollCount = new JLabel("Roll: 0");
		rollCount.setBounds(350, 30, 80, 20);
		add(rollCount);
		
		setVisible(true);
		
		new ChatClientReceiveThread(socket).start();
		doMessage("request:rooms");
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("Roll"))
		{
			doMessage("btn:roll");
		}
		for (int i = 0; i < 15; i++)
		{
			if (actionCmd.equals("user1/" + i))
			{
				doMessage("btn:0" + SPLITER + i);
				break;
			}
			if (actionCmd.equals("user2/" + i))
			{
				doMessage("btn:1" + SPLITER + i);
				break;
			}
		}
		for (int i = 0; i < 10; i++)
		{
			if (actionCmd.equals("fix/" + i))
			{
				doMessage("fixbtn:" + i);
				break;
			}
		}
	}
	
	private void sendMessage()
	{
        String message = chatField.getText();
        String request;
        if (message.equals("/"))
        {
            chatArea.append("server:올바르지 않은 명령어 입니다.");
            chatArea.append("\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        }
        else
        {
            if (message.charAt(0) == '/')	
            	request = "command:" + message.substring(1);
            else
            	request = "message:" + message ;
            doMessage(request);
        }
        chatField.setText("");
        chatField.requestFocus();
    }
	
	private void doMessage(String data)
	{
		pw.println(data);
		pw.flush();
	}
	
	private class ChatClientReceiveThread extends Thread
	{
	    Socket socket = null;
	    ChatClientReceiveThread(Socket socket)
	    {
	    	this.socket = socket;
	    }
	    public void run()
	    {
	        try
	        {
	        	BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
	            while(true)
	            {
	            	String msg = br.readLine();
	            	if (msg == null) break;
	            	if (msg.split(":")[0].equals("message"))
	            	{
	            		chatArea.append(msg.substring(msg.indexOf(":") + 1));
		            	chatArea.append("\n");
		            	chatArea.setCaretPosition(chatArea.getDocument().getLength());
	            	}
	            	else if (msg.split(":")[0].equals("users"))
	            	{
	            		String[] tokens = msg.split(":")[1].split(SPLITER);
	            		if (tokens.length == 1)
	            		{
	            			name1.setText(tokens[0]);
	            			name2.setText("-");
	            		}
	            		else
	            		{
	            			name1.setText(tokens[0]);
	            			name2.setText(tokens[1]);
	            		}
	            	}
	            	else if (msg.split(":")[0].equals("game"))
	            	{
	            		if (msg.split(":")[1].equals("reset"))
	            		{
	            			for (int i = 0; i < 15; i++)
	            			{
	            				if (i != 6 && i != 7 && i != 14)
	            				{
	            					user1btn[i].setVisible(true);
	            					user2btn[i].setVisible(true);
	            					user1row[i].setVisible(false);
	            					user2row[i].setVisible(false);
	            				}
	            			}
	            		}
	            		else
	            		{
		            		String[] tokens = msg.split(":")[1].split(SPLITER);
		            		for (int i = 0; i < 10; i++)
		            			dices[i].setIcon(icons[Integer.parseInt(tokens[i])]);
		            		for (int i = 10; i < 25; i++)
		            		{
		            			user1row[i - 10].setText(tokens[i]);
		            		}
		            		for (int i = 25; i < 40; i++)
		            		{
	            				user2row[i - 25].setText(tokens[i]);
		            		}
		            		rollCount.setText(tokens[40]);
	            		}
	            	}
	            	else if (msg.split(":")[0].equals("visible"))
	            	{
	            		String[] tokens = msg.split(":")[1].split(SPLITER);
	            		int p = Integer.parseInt(tokens[0]);
	            		int n = Integer.parseInt(tokens[1]);
	            		if (p == 0)
	            		{
	            			user1btn[n].setVisible(false);
	            			user1row[n].setVisible(true);
	            		}
	            		else
	            		{
	            			user2btn[n].setVisible(false);
	            			user2row[n].setVisible(true);
	            		}
	            	}
	            	else if (msg.split(":")[0].equals("error"))
	            	{
	            		if (msg.split(":")[1].equals("turn"))
	            			JOptionPane.showMessageDialog(null, "당신의 턴이 아닙니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("auth"))
	            			JOptionPane.showMessageDialog(null, "당신은 방장이 아닙니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("start"))
	            			JOptionPane.showMessageDialog(null, "이미 시작한 게임입니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("notstart"))
	            			JOptionPane.showMessageDialog(null, "아직 게임이 시작되지 않았습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("cannot"))
	            			JOptionPane.showMessageDialog(null, "현재 사용할 수 없는 버튼입니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("fullroll"))
	            			JOptionPane.showMessageDialog(null, "더이상 주사위를 굴릴 수 없습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("notfull"))
	            			JOptionPane.showMessageDialog(null, "아직 시작할 수 없습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            	}
	            	else if (msg.split(":")[0].equals("join"))
	            	{
	            		dispose();
	            		new Lobby(socket, pw);
	            		break;
	            	}
	            }
	        }
	        catch (IOException e)
	        {
	            JOptionPane.showMessageDialog(null, "서버와의 연결이 종료되었습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            dispose();
	        }
	    }
	}

}