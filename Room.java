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
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Room extends JFrame implements ActionListener {

	private PrintWriter pw;
	private static final int WIDTH = 500;
	private static final int HEIGHT = 400;
	private static final String TOP_TITLE = "Yacht Dice Online";
	private static final String SPLITER = "#!#";
	
	private JTextArea chatArea;
	private JTextField chatField;
	private ImageIcon[] icons;
	private JLabel[] dices;
	private int[] diceVal;
	private JButton[] fixbtn;
	private JButton roll;
	private JButton gameBtn;
	private JLabel turn;
	private JLabel rollCount;
	private ArrayList<userSet> users;
	
	public class userSet {
		JLabel nameLabel;
		JLabel[] labels;
		JButton[] buttons;
		score scores;
		
		public userSet(String name) {
			nameLabel = new JLabel(name);
			labels = new JLabel[3];
			for (int i = 0; i < 3; i++)
				labels[i] = new JLabel("0");
			buttons = new JButton[12];
			for (int i = 0; i < 12; i++)
				buttons[i] = new JButton("-");
			scores = new score(name);
		}
	}
	
	public Room(Socket socket, PrintWriter pw) {
		this.pw = pw;
		setTitle(TOP_TITLE);
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
		chatField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                char keyCode = e.getKeyChar();
                if (keyCode == KeyEvent.VK_ENTER && !chatField.getText().isEmpty())
                    sendMessage();
            }
        });
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	doMessage("quit");
            }
        });
		
		add(scrollPane1);
		add(chatField);
		
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
		for (int i = 0; i < 15; i++) {
			row[i].setBounds(430, 35 + 20 * i, 100, 20);
			add(row[i]);
		}
		
		icons = new ImageIcon[7];
		for (int i = 0; i < 7; i++)
			icons[i] = new ImageIcon(new ImageIcon(this.getClass().getResource("/Images/" + i + ".png")).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
		
		dices = new JLabel[10];
		diceVal = new int[5];
		fixbtn = new JButton[10];
		for (int i = 0; i < 10; i++) {
			dices[i] = new JLabel();
			if (i < 5) {
				dices[i].setIcon(icons[0]);
				diceVal[i] = 0;
				fixbtn[i] = new JButton("▼");
				dices[i].setBounds(30 + 60 * i, 40, 50, 50);
				fixbtn[i].setBounds(30 + 60 * i, 100, 50, 20);
			}
			else {
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
		
		rollCount = new JLabel("RollCount: 0");
		rollCount.setBounds(330, 180, 80, 20);
		add(rollCount);
		
		turn = new JLabel("Ready");
		turn.setBounds(30, 125, 200, 20);
		add(turn);
		
		gameBtn = new JButton("Start");
		gameBtn.setBounds(330, 60, 80, 30);
		gameBtn.addActionListener(this);
		gameBtn.setActionCommand("gameBtn");
		add(gameBtn);
		
		users = new ArrayList<userSet>();
		setVisible(true);
		new ChatClientReceiveThread(socket, this).start();
	}
	
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("Roll"))
			doMessage("btn:roll");
		else if (actionCmd.equals("gameBtn")) {
			if (gameBtn.getText().equals("Start"))
				doMessage("command:start");
			else
				doMessage("command:stop");
		}
		else {
			for (int i = 0; i < 10; i++) {
				if (actionCmd.equals("fix/" + i)) {
					doMessage("fixbtn:" + i);
					return;
				}
			}
			for (int i = 0; i < users.size(); i++) {
				for (int j = 0; j < 12; j++) {
					if (actionCmd.equals("btn/" + i + "," + j)) {
						int tmp = j;
						if (tmp > 5)
							tmp += 2;
						doMessage("btn:" + i + SPLITER + tmp);
						return;
					}
				}
			}
		}
	}
	
	private void sendMessage() {
        String message = chatField.getText();
        doMessage("message:" + message );
        chatField.setText("");
        chatField.requestFocus();
    }
	
	private void doMessage(String data) {
		pw.println(data);
		pw.flush();
	}
	
	private class ChatClientReceiveThread extends Thread {
	    Socket socket = null;
	    Room room;
	    ChatClientReceiveThread(Socket socket, Room room) {
	    	this.socket = socket;
	    	this.room = room;
	    }
	    public void run() {
	        try {
	        	BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
	            while(true) {
	            	String msg = br.readLine();
	            	if (msg == null) break;
	            	else if (msg.split(":")[0].equals("message")) {
	            		if (!chatArea.getText().isEmpty())
	            			chatArea.append("\n");
	            		chatArea.append(msg.substring(msg.indexOf(":") + 1));
		            	chatArea.setCaretPosition(chatArea.getDocument().getLength());
	            	}
	            	else if (msg.split(":")[0].equals("users")) {
	            		String[] tokens = msg.split(":")[1].split(SPLITER);
	            		for (userSet x : users) {
	            			room.remove(x.nameLabel);
	            			for (int i = 0; i < 3; i++)
	            				room.remove(x.labels[i]);
	            			for (int i = 0; i < 12; i++)
	            				room.remove(x.buttons[i]);
	            		}
	            		users.clear();
	            		for (int i = 0; i < tokens.length; i++)
	            			users.add(new userSet(tokens[i]));
	            		
	            		for (userSet x : users) {
	            			int idx = users.indexOf(x);
	            			x.nameLabel.setBounds(500 + idx * 100, 10, 100, 20);
	            			room.add(x.nameLabel);
	            			x.labels[0].setText(Integer.toString(x.scores.getSubTotal()));
	            			x.labels[1].setText(Integer.toString(x.scores.getBonus()));
	            			x.labels[2].setText(Integer.toString(x.scores.getTotal()));
	            			x.labels[0].setBounds(535 + 100 * idx, 155, 50, 20);
	            			x.labels[1].setBounds(535 + 100 * idx, 175, 50, 20);
	            			x.labels[2].setBounds(535 + 100 * idx, 315, 50, 20);
	            			for (int i = 0; i < 3; i++)
	            				room.add(x.labels[i]);
	            			for (int i = 0; i < 12; i++) {
	            				int tmp = i;
	            				if (tmp > 5)
	            					tmp += 2;
	            				if (x.scores.getScore(tmp) == -1)
	            					x.buttons[i].setText("-");
	            				else
	            					x.buttons[i].setText(Integer.toString(x.scores.getScore(tmp)));
	            				if (i < 6)
	            					x.buttons[i].setBounds(515 + 100 * idx, 35 + 20 * i, 50, 20);
	            				else
	            					x.buttons[i].setBounds(515 + 100 * idx, 75 + 20 * i, 50, 20);
	            				x.buttons[i].setActionCommand("btn/" + idx + "," + i);
	            				x.buttons[i].addActionListener(room);
	            				room.add(x.buttons[i]);
	            			}
	            		}
	            		room.setSize(WIDTH + 100 * tokens.length, HEIGHT);
	            	}
	            	else if (msg.split(":")[0].equals("game")) {
	            		if (msg.split(":")[1].equals("reset")) {
	            			gameBtn.setText("Start");
	            			rollCount.setText("RollCount");
	            			turn.setText("Ready");
	            			for (int i = 0; i < 10; i++)
	            				dices[i].setIcon(icons[0]);
	            		}
	            		else if (msg.split(":")[1].equals("start")) {
	            			gameBtn.setText("Stop");
	            			turn.setText(users.get(0).scores.getName() + "'s Turn");
	            			rollCount.setText("RollCount: 0");
	            		}
	            		else {
	            			String tmp[] = msg.split(":")[1].split(SPLITER);
	                        for (int i = 0; i < 10; i++)
	                            	dices[i].setIcon(icons[Integer.parseInt(tmp[i])]);
	                        for (int i = 10; i < users.size() * 15 + 10; i++)
	                            users.get((i - 10) / 15).scores.setScore((i - 10) % 15, Integer.parseInt(tmp[i]));
	                        rollCount.setText("RollCount: " + tmp[users.size() * 15 + 10]);
	                        turn.setText(users.get(Integer.parseInt(tmp[users.size() * 15 + 11])).scores.getName() + "'s Turn");
	                        for (userSet x : users) {
		            			x.labels[0].setText(Integer.toString(x.scores.getSubTotal()));
		            			x.labels[1].setText(Integer.toString(x.scores.getBonus()));
		            			x.labels[2].setText(Integer.toString(x.scores.getTotal()));
		            			for (int i = 0; i < 12; i++) {
		            				int tmpval = i;
		            				if (tmpval > 5)
		            					tmpval += 2;
		            				if (x.scores.getScore(tmpval) == -1)
		            					x.buttons[i].setText("-");
		            				else
		            					x.buttons[i].setText(Integer.toString(x.scores.getScore(tmpval)));
		            			}
		            		}
	            		}
	            	}
	            	else if (msg.split(":")[0].equals("error")) {
	            		if (msg.split(":")[1].equals("turn"))
	            			JOptionPane.showMessageDialog(null, "당신의 턴이 아닙니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("auth"))
	            			JOptionPane.showMessageDialog(null, "당신은 방장(1픽)이 아닙니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("start"))
	            			JOptionPane.showMessageDialog(null, "이미 시작한 게임입니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("notstart"))
	            			JOptionPane.showMessageDialog(null, "아직 게임이 시작되지 않았습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("cannot"))
	            			JOptionPane.showMessageDialog(null, "현재 사용할 수 없는 버튼입니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("fullroll"))
	            			JOptionPane.showMessageDialog(null, "더이상 주사위를 굴릴 수 없습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            		else if (msg.split(":")[1].equals("notyet"))
	            			JOptionPane.showMessageDialog(null, "두 명 이상부터 시작할 수 있습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            	}
	            	else if (msg.equals("chact:1")) {
	            		dispose();
	            		new Lobby(socket, pw);
	            		break;
	            	}
	            }
	        }
	        catch (IOException e) {
	            JOptionPane.showMessageDialog(null, "서버와의 연결이 종료되었습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            dispose();
	        }
	    }
	}

}