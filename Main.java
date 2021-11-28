import javax.swing.JFrame;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class Main extends JFrame implements ActionListener{
	
	private static final int WIDTH = 450;
	private static final int HEIGHT = 300;
	private static final String TOP_TITLE = "Yacht Dice Online";
	private static final String MAIN_TITLE = "야추 온라인";
	private static final String SERVER_IP = "127.0.0.1";
	private static final int SERVER_PORT = 7777;
	
	private JTextField nickname;
	
	public Main()
	{
		setTitle(TOP_TITLE);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		
		JLabel title = new JLabel(MAIN_TITLE);
		JLabel nicktext = new JLabel("Nickname");
		nickname = new JTextField(20);
		JButton connect = new JButton("Connect");
		JButton exit = new JButton("Exit");
		Font f1 = new Font("맑은 고딕", Font.BOLD, 40);
		title.setFont(f1);
		connect.addActionListener(this);
		exit.addActionListener(this);
		add(title);
		add(nicktext);
		add(nickname);
		add(connect);
		add(exit);
		title.setBounds(100, 0, 500, 100);
		nicktext.setBounds(80, 100, 280, 20);
		nickname.setBounds(80, 120, 280, 20);
		connect.setBounds(80, 150, 280, 40);
		exit.setBounds(80, 200, 280, 40);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("Connect"))
		{
			if (nickname.getText().isEmpty() || nickname.getText().isBlank())
				JOptionPane.showMessageDialog(null, "닉네임을 입력해주세요.", "Error", JOptionPane.INFORMATION_MESSAGE);
			else
			{
				Socket socket = new Socket();
		        try
		        {
		            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
		            boolean able = false;
		            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
		            String request = nickname.getText() + "\n";
		            pw.println(request);
		            pw.flush();
		            try
			        {
			        	BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			            String msg = br.readLine();
			            if (msg.equals("Y"))
			            	able = true;
			        }
			        catch (IOException h)
			        {
			            JOptionPane.showMessageDialog(null, "서버 점검중 입니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
			            dispose();
			        }
		            if (able)
		            {
		            	new Lobby(socket, pw);
		            	pw.println("join");
		            	pw.flush();
		            	dispose();
		            }
		            else
		            	 JOptionPane.showMessageDialog(null, "현재 접속중인 닉네임입니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
		        }
		        catch (IOException g)
		        {
		        	JOptionPane.showMessageDialog(null, "서버 점검중 입니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
		        }
			}
		}
		else if (actionCmd.equals("Exit"))
		{
			dispose();
		}
	}
	
	public static void main(String argrs[])
	{
		new Main();
	}
}
