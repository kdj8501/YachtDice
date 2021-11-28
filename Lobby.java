import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Lobby extends JFrame implements ActionListener{
	
	private PrintWriter pw;
	private static final int WIDTH = 450;
	private static final int HEIGHT = 400;
	private static final String SPLITER = "#!#";
	
	private JList<Object> serverList;
	private JTextArea userList;
	private JTextArea chatArea;
	private JTextField chatField;
	private JButton refresh;
	private JButton join;
	private JButton create;
	
	private ArrayList<String> rooms;
	
	public Lobby(Socket socket, PrintWriter pw)
	{
		this.pw = pw;
		setTitle("Yacht Lobby");
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		
		rooms = new ArrayList<String>();
		serverList = new JList<Object>();
		serverList.setListData(rooms.toArray());
		JScrollPane scrollPane1 = new JScrollPane(serverList);
		scrollPane1.setBounds(15, 55, 270, 195);
		
		refresh = new JButton("Refresh List");
		refresh.addActionListener(this);
		refresh.setBounds(15, 20, 270, 30);
		
		join = new JButton("Join Room");
		join.addActionListener(this);
		join.setBounds(300, 20, 120, 30);
		
		create = new JButton("Create Room");
		create.addActionListener(this);
		create.setBounds(300, 55, 120, 30);
		
		JLabel userListText = new JLabel("Lobby Users");
		userListText.setBounds(300, 90, 100, 20);
		
		userList = new JTextArea();
		userList.setEditable(false);
		JScrollPane scrollPane2 = new JScrollPane(userList);
		scrollPane2.setBounds(300, 110, 120, 140);
		
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane scrollPane3 = new JScrollPane(chatArea);
		scrollPane3.setBounds(15, 260, 405, 70);
		
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
                System.exit(0);
            }
        });
		
		add(create);
		add(refresh);
		add(join);
		add(userListText);
		add(scrollPane1);
		add(scrollPane2);
		add(scrollPane3);
		add(chatField);
		
		setVisible(true);
		
		new ChatClientReceiveThread(socket).start();
		
		doMessage("request:rooms");
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("Refresh List"))
		{
			doMessage("request:rooms");
		}
		else if (actionCmd.equals("Create Room"))
		{
			new CreateRoom();
		}
		else if (actionCmd.equals("Join Room"))
		{
			if (serverList.getSelectedIndex() == -1)
				JOptionPane.showMessageDialog(null, "접속할 방을 선택해주세요.", "Error", JOptionPane.INFORMATION_MESSAGE);
			else
			{
				doMessage("tojoin:" + serverList.getSelectedIndex());
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
	
	private class CreateRoom extends JFrame implements ActionListener
	{	
		private JTextField textField;
		private JButton ok;
		
		CreateRoom()
		{
			setTitle("Create Room");
			setSize(280, 100);
			setLocationRelativeTo(null);
			setResizable(false);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setLayout(null);
			
			textField = new JTextField("", 170);
			textField.setBounds(10, 20, 170, 20);
			
			ok = new JButton("OK");
			ok.setBounds(190, 10, 60, 40);
			ok.addActionListener(this);
			
			add(textField);
			add(ok);
			
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			String actionCmd = e.getActionCommand();
			if (actionCmd.equals("OK"))
			{
				if (textField.getText().isEmpty() || textField.getText().isBlank())
					JOptionPane.showMessageDialog(null, "방 이름을 입력해주세요.", "Error", JOptionPane.INFORMATION_MESSAGE);
				else
				{
			        doMessage("create:" + textField.getText());
			        dispose();
				}
			}
		}
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
	            	else if (msg.split(":")[0].equals("message"))
	            	{
	            		chatArea.append(msg.substring(msg.indexOf(":") + 1));
		            	chatArea.append("\n");
		            	chatArea.setCaretPosition(chatArea.getDocument().getLength());
	            	}
	            	else if (msg.split(":")[0].equals("users"))
	            	{
	            		String str = msg.split(":")[1].replace(SPLITER, "\n");
	            		userList.setText(str);
	            	}
	            	else if (msg.split(":")[0].equals("join"))
	            	{
	            		if (msg.split(":")[1].equals("error"))
	            		{
	            			JOptionPane.showMessageDialog(null, "접속할 수 없습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            			doMessage("request:rooms");
	            		}
	            		else if (msg.split(":")[1].equals("full"))
	            		{
	            			JOptionPane.showMessageDialog(null, "방의 인원이 가득 찼습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            			doMessage("request:rooms");
	            		}
	            		else if (msg.split(":")[1].equals("started"))
	            		{
	            			JOptionPane.showMessageDialog(null, "이미 시작한 방입니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
	            			doMessage("request:rooms");
	            		}
	            		else
	            		{
		            		new Room(socket, pw, msg.split(":")[1]);
		            		dispose();
		            		break;
	            		}
	            	}
	            	else if (msg.split(":")[0].equals("rooms"))
	            	{
		            	rooms.clear();
		            	if (!"rooms:".equals(msg))
		            	{
			            	for (String x : msg.split(":")[1].split(SPLITER))
			            		rooms.add(x);
		            	}
		            	serverList.setListData(rooms.toArray());
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