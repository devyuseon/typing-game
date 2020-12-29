import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public class GameFrame extends JFrame {
	
	private Player player = new Player();
	private int score = 0;
	
	private LoginPanel loginPanel = new LoginPanel();
	private ScorePanel scorePanel = new ScorePanel();
	private GamePanel gamePanel = new GamePanel();
	
	// ��� ����
	private Music loginBGM = new Music("loginBGM.mp3",true);
	private Music basicBGM = new Music("basicBGM.mp3",false);
	
	public GameFrame() {
		setTitle("Ÿ���� ����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setContentPane(loginPanel); // LoginPanel�� ����Ʈ������ ����
		
		loginBGM.start(); // �α��������� �������
		
		setLocationRelativeTo(null); // ��ǻ�� ȭ�� �߾ӿ� ������ �����ϵ���
		setVisible(true);
	}
	
	public class LoginPanel extends JPanel{
		
		// LoginPage
		// �׷��� ����� �ѹ��� �����ϵ��� �ϱ� ����
		private Box langBox = Box.createHorizontalBox();
		private Box levelBox = Box.createHorizontalBox();
		private Box nameBox = Box.createHorizontalBox();
		
		private JLabel mainTitle = new JLabel("Typing Game");
		private JLabel langLabel = new JLabel("���   ");
		private JLabel lvLabel = new JLabel("����   ");
		private String [] level = {"Lv.1", "Lv.2", "Lv.3", "Lv.4", "Lv.5"};
		private JComboBox<String> lvCombo = new JComboBox<String>(level); // ���� ���� �޺��ڽ�
		private JRadioButton [] radio = new JRadioButton [2];// ��� �����ϴ� JRadioButton
		private ButtonGroup g = new ButtonGroup();
		private String [] langType = {"ko","en"};
		private JLabel name = new JLabel("�̸�   "); 
		private JTextField inputName = new JTextField(30); // �÷��̾� �̸� �Է�ĭ
		private JButton gameStartBtn = new JButton("���ӽ���");
		private JButton rankViewBtn = new JButton("��ŷ����");
		private JButton editKoWordBtn = new JButton("�ѱ۴ܾ�����");
		private JButton editEnWordBtn = new JButton("����ܾ�����");
		
		// Ranking Page
		private JLabel rankTitle = new JLabel("Top 10");
		private ImageIcon home = new ImageIcon("homeImage.png");
		private JButton goHome = new JButton(home);
		private JLabel modeTitle = new JLabel();
		
		private String line;
		private String []splitLine = new String[2];
		private String []rankText = new String[10];
		private JLabel []rankLabel = new JLabel[10];
		private JLabel []scoreText = new JLabel[10];
		
		@Override
		public void paintComponent(Graphics g) { // ����̹��� ����
			super.paintComponent(g);
			ImageIcon icon = new ImageIcon("loginBack.jpg");
			g.drawImage(icon.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
			setOpaque(false);
		}
		
		public LoginPanel() {
			
			this.setLayout(null); // ���ϴ� ��ǥ�� ������Ʈ ���� ����
			
			mainTitle.setFont(new Font("Goudy Stout",1,40));
			mainTitle.setBounds(130, 60, 800, 40);
			
			langLabel.setFont(new Font("���ʷҵ���",1,15));
			langBox.add(langLabel);
			langBox.setBounds(280, 150, 200, 30);

			lvLabel.setFont(new Font("���ʷҵ���",1,15));
			lvCombo.setFont(new Font("���ʷҵ���",1,15));
			levelBox.add(lvLabel);
			levelBox.add(lvCombo);
			levelBox.setBounds(280, 200, 200, 30);
			
			for (int i=0; i<radio.length; i++) {
				radio[i] = new JRadioButton(langType[i]);
				g.add(radio[i]);
				langBox.add(radio[i]);
				radio[i].setFont(new Font("���ʷҵ���",1,20));
				radio[i].setOpaque(false);
			}
			
			radio[1].setSelected(true); // �⺻���� en�����ϰ���
			
			name.setFont(new Font("���ʷҵ���",1,15));
			inputName.setFont(new Font("���ʷҵ���",1,15));
			nameBox.add(name);
			nameBox.add(inputName);
			nameBox.setBounds(280, 250, 200, 30);
		
			gameStartBtn.setFont(new Font("���ʷҵ���",1,15));
			gameStartBtn.setBounds(280, 300, 200, 30);
			gameStartBtn.setBorderPainted(false);	
			
			rankViewBtn.setFont(new Font("���ʷҵ���",1,15));
			rankViewBtn.setBounds(280, 350, 200, 30);
			rankViewBtn.setBorderPainted(false);
			
			editKoWordBtn.setFont(new Font("���ʷҵ���",1,10));
			editKoWordBtn.setBounds(280, 400, 100, 30);
			editKoWordBtn.setBorderPainted(false);
			
			editEnWordBtn.setFont(new Font("���ʷҵ���",1,10));
			editEnWordBtn.setBounds(380, 400, 100, 30);
			editEnWordBtn.setBorderPainted(false);
			
			add(mainTitle);
			add(langBox);
			add(levelBox);
			add(nameBox);
			add(gameStartBtn);
			add(rankViewBtn);
			add(editKoWordBtn);
			add(editEnWordBtn);
			
			// ���� ���� ��ư
			gameStartBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// ��ư Ŭ���� ȿ���� ��� false�� �ѹ��� ����Ѵ�.
					Music btnClickedBGM = new Music("btnClicked.mp3",false);
					btnClickedBGM.start();
					
					// ������ radioButton�� index����
					int selectedIndex;
					if(radio[0].isSelected()) selectedIndex = 0;
					else selectedIndex = 1;
					
					// Player��ü ����
					player = new Player(inputName.getText(),
							lvCombo.getSelectedIndex()+1, score, radio[selectedIndex].getText());
					player.setName(inputName.getText());
					player.setLevel(lvCombo.getSelectedIndex()+1);
					player.setLanguage(langType[selectedIndex]);
					
					// gamePanel����
					gamePanel = new GamePanel(scorePanel, player);
					
					// LoginPanel�� ��� ��Ҹ� �Ⱥ��̵��� ����
					setLoginPageHidden();
					
					// ������ �г��� ���̾ƿ� ����
					getContentPane().setLayout(new BorderLayout());
					splitPane(); // JsplitPane�� �����Ͽ� ContentPane�� CENTER�� ����
					makeInfoPanel(player);
					setResizable(false); // ������ ���� ���ϵ���
					loginBGM.close(); // loginBGM ����
					repaint();
					
					gamePanel.gameStart(player);
				}
			}); // end of ActionListener
			
			rankViewBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// ��ư Ŭ���� ȿ���� ���
					Music btnClickedBGM = new Music("btnClicked.mp3",false);
					btnClickedBGM.start();
					
					setLoginPageHidden();
					
					int selectedIndex;
					if(radio[0].isSelected()) selectedIndex = 0;
					else selectedIndex = 1;
					
					// �ʿ��� Player���� ����
					player = new Player(inputName.getText(),
							lvCombo.getSelectedIndex()+1, score, radio[selectedIndex].getText());					
					player.setLevel(lvCombo.getSelectedIndex()+1);
					player.setLanguage(langType[selectedIndex]);
					
					rankTitle.setFont(new Font("Goudy Stout",1,40));
					rankTitle.setBounds(270, 60, 800, 40);
					
					setModeTitle(player);
					modeTitle.setFont(new Font("Goudy Stout",1,20));
					modeTitle.setBounds(260, 120, 400, 20);
					
					goHome.setBounds(680,100, home.getIconWidth(), home.getIconHeight());
					
					// �̹����� ���̰�
					goHome.setBorderPainted(false);
					goHome.setFocusPainted(false);
					goHome.setContentAreaFilled(false);
					
					// ������������ Sorting�� ������� �ҷ���
					String fileName = "sorted" + player.getLanguage()
					+ player.getLevel()+".txt";
					
					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(
								new FileInputStream(fileName), "MS949"));
						
						int i=0;
						while (i<10) {
							line = in.readLine();
							if(line == null) break; // ��ŷ�� 10������ ���� ���� ��
							splitLine = line.trim().split(",");
							// data[0]�� name, data[1]�� score
							rankText[i] = Integer.toString(i+1) + "     " + splitLine[0];
							rankLabel[i] = new JLabel(rankText[i]);
							rankLabel[i].setFont(new Font("���ʷҵ���",1,15));
							rankLabel[i].setBounds(300, 150+i*22, 700, 20);
							
							scoreText[i] = new JLabel(splitLine[1]);
							scoreText[i].setFont(new Font("���ʷҵ���",1,15));
							scoreText[i].setBounds(500, 150+i*22, 700, 20);
							add(rankLabel[i]);
							add(scoreText[i]);
							
							i++;
						}
							
					} catch (IOException e1) {
						System.out.println("�ش� ��ŷ���� ����");
					} finally {
						add(rankTitle);
						add(modeTitle);
						add(goHome);
						
						// �ٽ� LoginPanel
						goHome.addActionListener(new ActionListener() { 
							@Override
							public void actionPerformed(ActionEvent e) {
								// ��ư Ŭ���� ȿ���� ���
								Music btnClickedBGM = new Music("btnClicked.mp3",false);
								btnClickedBGM.start();
								
								setRankPageHidden();							
								setLoginPageVisible();
							
							}
						});
					}
				}
			}); // end of ActionListener
			
			editEnWordBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					editWord("en.txt");
				}
				
			});
			
			editKoWordBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					editWord("ko.txt");
				}
				
			});
			
		
	} // end of LoginPanel()
		
		public void setLoginPageHidden() {
			mainTitle.setVisible(false);
			langBox.setVisible(false);
			levelBox.setVisible(false);
			nameBox.setVisible(false);
			gameStartBtn.setVisible(false);
			rankViewBtn.setVisible(false);
			editKoWordBtn.setVisible(false);
			editEnWordBtn.setVisible(false);
		}
		
		public void setLoginPageVisible() {
			mainTitle.setVisible(true);
			langBox.setVisible(true);
			levelBox.setVisible(true);
			nameBox.setVisible(true);
			gameStartBtn.setVisible(true);
			rankViewBtn.setVisible(true);
			editKoWordBtn.setVisible(true);
			editEnWordBtn.setVisible(true);
		}
		
		public void setRankPageHidden() {
			rankTitle.setVisible(false);
			modeTitle.setVisible(false);
			goHome.setVisible(false);
			
			for(int i=0; i < rankLabel.length; i++) {
				rankLabel[i].setVisible(false);
				scoreText[i].setVisible(false);
			}
		}
			
		public void setModeTitle(Player player) {
			modeTitle = new JLabel(player.getLanguage() + " Mode Lv." + player.getLevel());
		}
	
	protected void editWord(String fileName) {
		JOptionPane edit = new JOptionPane();
		String str = edit.showInputDialog("�߰��� �ܾ �Է��ϼ���!");
		String word = str.trim(); // Ȥ�ø� ���� ����
		try {
			FileWriter out = new FileWriter(fileName,true);
			out.write("\n" + word);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}

	private void splitPane() {
		JSplitPane hPane = new JSplitPane();
		getContentPane().add(hPane, BorderLayout.CENTER); // CENTER�� ����
		hPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT); // �������� �ɰ�
		hPane.setDividerLocation(600);
		hPane.setEnabled(false); // Ȱ��ȭ ���ƹ���(�������̰�)
		hPane.setLeftComponent(gamePanel);
		hPane.setRightComponent(scorePanel);
	}
	
	public void makeInfoPanel(Player player) {
		
		getContentPane().add(new UserInfoPanel(player), BorderLayout.NORTH);
	}
	
	public class UserInfoPanel extends JPanel{
		// ���� �÷��� �� ��ܿ� �÷��̾� ������ ǥ��
		public UserInfoPanel(Player player) {
			int level;
			String userName;
			String lang;
			level = player.getLevel();
			userName = player.getName();
			lang = player.getLanguage();
			
			this.setLayout(new FlowLayout());
			
			JLabel name = new JLabel("�÷��̾�:");
			JLabel userNameInfo = new JLabel("");
			userNameInfo.setText(userName + "  / ");
			JLabel levelInfo = new JLabel("");
			levelInfo.setText("Lv." + Integer.toString(level));
			JLabel langInfo = new JLabel("");
			langInfo.setText(" / " + lang);
			
			name.setFont(new Font("���ʷҵ���",Font.BOLD,12));
			userNameInfo.setFont(new Font("���ʷҵ���",Font.BOLD,12));
			levelInfo.setFont(new Font("���ʷҵ���",Font.BOLD,12));
			langInfo.setFont(new Font("���ʷҵ���",Font.BOLD,12));
			
			add(name); 
			add(userNameInfo);
			add(levelInfo);
			add(langInfo);
		}
	
		
	public void stopBasicBGM() {
		basicBGM.close();
	}	
	}
	} // end of Class LoginPanel
	
}
