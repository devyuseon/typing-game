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
	
	// 배경 음악
	private Music loginBGM = new Music("loginBGM.mp3",true);
	private Music basicBGM = new Music("basicBGM.mp3",false);
	
	public GameFrame() {
		setTitle("타이핑 게임");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setContentPane(loginPanel); // LoginPanel을 컨텐트팬으로 설정
		
		loginBGM.start(); // 로그인페이지 배경음악
		
		setLocationRelativeTo(null); // 컴퓨터 화면 중앙에 프레임 생성하도록
		setVisible(true);
	}
	
	public class LoginPanel extends JPanel{
		
		// LoginPage
		// 그룹을 만들어 한번에 부착하도록 하기 위함
		private Box langBox = Box.createHorizontalBox();
		private Box levelBox = Box.createHorizontalBox();
		private Box nameBox = Box.createHorizontalBox();
		
		private JLabel mainTitle = new JLabel("Typing Game");
		private JLabel langLabel = new JLabel("언어   ");
		private JLabel lvLabel = new JLabel("레벨   ");
		private String [] level = {"Lv.1", "Lv.2", "Lv.3", "Lv.4", "Lv.5"};
		private JComboBox<String> lvCombo = new JComboBox<String>(level); // 레벨 고르는 콤보박스
		private JRadioButton [] radio = new JRadioButton [2];// 언어 선택하는 JRadioButton
		private ButtonGroup g = new ButtonGroup();
		private String [] langType = {"ko","en"};
		private JLabel name = new JLabel("이름   "); 
		private JTextField inputName = new JTextField(30); // 플레이어 이름 입력칸
		private JButton gameStartBtn = new JButton("게임시작");
		private JButton rankViewBtn = new JButton("랭킹보기");
		private JButton editKoWordBtn = new JButton("한글단어편집");
		private JButton editEnWordBtn = new JButton("영어단어편집");
		
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
		public void paintComponent(Graphics g) { // 배경이미지 설정
			super.paintComponent(g);
			ImageIcon icon = new ImageIcon("loginBack.jpg");
			g.drawImage(icon.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
			setOpaque(false);
		}
		
		public LoginPanel() {
			
			this.setLayout(null); // 원하는 좌표에 컴포넌트 부착 위함
			
			mainTitle.setFont(new Font("Goudy Stout",1,40));
			mainTitle.setBounds(130, 60, 800, 40);
			
			langLabel.setFont(new Font("함초롬돋움",1,15));
			langBox.add(langLabel);
			langBox.setBounds(280, 150, 200, 30);

			lvLabel.setFont(new Font("함초롬돋움",1,15));
			lvCombo.setFont(new Font("함초롬돋움",1,15));
			levelBox.add(lvLabel);
			levelBox.add(lvCombo);
			levelBox.setBounds(280, 200, 200, 30);
			
			for (int i=0; i<radio.length; i++) {
				radio[i] = new JRadioButton(langType[i]);
				g.add(radio[i]);
				langBox.add(radio[i]);
				radio[i].setFont(new Font("함초롬돋움",1,20));
				radio[i].setOpaque(false);
			}
			
			radio[1].setSelected(true); // 기본으로 en선택하게함
			
			name.setFont(new Font("함초롬돋움",1,15));
			inputName.setFont(new Font("함초롬돋움",1,15));
			nameBox.add(name);
			nameBox.add(inputName);
			nameBox.setBounds(280, 250, 200, 30);
		
			gameStartBtn.setFont(new Font("함초롬돋움",1,15));
			gameStartBtn.setBounds(280, 300, 200, 30);
			gameStartBtn.setBorderPainted(false);	
			
			rankViewBtn.setFont(new Font("함초롬돋움",1,15));
			rankViewBtn.setBounds(280, 350, 200, 30);
			rankViewBtn.setBorderPainted(false);
			
			editKoWordBtn.setFont(new Font("함초롬돋움",1,10));
			editKoWordBtn.setBounds(280, 400, 100, 30);
			editKoWordBtn.setBorderPainted(false);
			
			editEnWordBtn.setFont(new Font("함초롬돋움",1,10));
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
			
			// 게임 시작 버튼
			gameStartBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 버튼 클릭시 효과음 재생 false면 한번만 재생한다.
					Music btnClickedBGM = new Music("btnClicked.mp3",false);
					btnClickedBGM.start();
					
					// 선택한 radioButton의 index저장
					int selectedIndex;
					if(radio[0].isSelected()) selectedIndex = 0;
					else selectedIndex = 1;
					
					// Player객체 설정
					player = new Player(inputName.getText(),
							lvCombo.getSelectedIndex()+1, score, radio[selectedIndex].getText());
					player.setName(inputName.getText());
					player.setLevel(lvCombo.getSelectedIndex()+1);
					player.setLanguage(langType[selectedIndex]);
					
					// gamePanel생성
					gamePanel = new GamePanel(scorePanel, player);
					
					// LoginPanel의 모든 요소를 안보이도록 설정
					setLoginPageHidden();
					
					// 부착할 패널의 레이아웃 설정
					getContentPane().setLayout(new BorderLayout());
					splitPane(); // JsplitPane을 생성하여 ContentPane의 CENTER에 부착
					makeInfoPanel(player);
					setResizable(false); // 사이즈 조정 못하도록
					loginBGM.close(); // loginBGM 종료
					repaint();
					
					gamePanel.gameStart(player);
				}
			}); // end of ActionListener
			
			rankViewBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 버튼 클릭시 효과음 재생
					Music btnClickedBGM = new Music("btnClicked.mp3",false);
					btnClickedBGM.start();
					
					setLoginPageHidden();
					
					int selectedIndex;
					if(radio[0].isSelected()) selectedIndex = 0;
					else selectedIndex = 1;
					
					// 필요한 Player정보 저장
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
					
					// 이미지만 보이게
					goHome.setBorderPainted(false);
					goHome.setFocusPainted(false);
					goHome.setContentAreaFilled(false);
					
					// 내림차순으로 Sorting한 기록파일 불러옴
					String fileName = "sorted" + player.getLanguage()
					+ player.getLevel()+".txt";
					
					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(
								new FileInputStream(fileName), "MS949"));
						
						int i=0;
						while (i<10) {
							line = in.readLine();
							if(line == null) break; // 랭킹이 10위까지 있지 않을 때
							splitLine = line.trim().split(",");
							// data[0]은 name, data[1]은 score
							rankText[i] = Integer.toString(i+1) + "     " + splitLine[0];
							rankLabel[i] = new JLabel(rankText[i]);
							rankLabel[i].setFont(new Font("함초롬돋움",1,15));
							rankLabel[i].setBounds(300, 150+i*22, 700, 20);
							
							scoreText[i] = new JLabel(splitLine[1]);
							scoreText[i].setFont(new Font("함초롬돋움",1,15));
							scoreText[i].setBounds(500, 150+i*22, 700, 20);
							add(rankLabel[i]);
							add(scoreText[i]);
							
							i++;
						}
							
					} catch (IOException e1) {
						System.out.println("해당 랭킹파일 없음");
					} finally {
						add(rankTitle);
						add(modeTitle);
						add(goHome);
						
						// 다시 LoginPanel
						goHome.addActionListener(new ActionListener() { 
							@Override
							public void actionPerformed(ActionEvent e) {
								// 버튼 클릭시 효과음 재생
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
		String str = edit.showInputDialog("추가할 단어를 입력하세요!");
		String word = str.trim(); // 혹시모를 공백 제거
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
		getContentPane().add(hPane, BorderLayout.CENTER); // CENTER에 부착
		hPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT); // 수평으로 쪼갬
		hPane.setDividerLocation(600);
		hPane.setEnabled(false); // 활성화 막아버림(못움직이게)
		hPane.setLeftComponent(gamePanel);
		hPane.setRightComponent(scorePanel);
	}
	
	public void makeInfoPanel(Player player) {
		
		getContentPane().add(new UserInfoPanel(player), BorderLayout.NORTH);
	}
	
	public class UserInfoPanel extends JPanel{
		// 게임 플레이 중 상단에 플레이어 정보를 표시
		public UserInfoPanel(Player player) {
			int level;
			String userName;
			String lang;
			level = player.getLevel();
			userName = player.getName();
			lang = player.getLanguage();
			
			this.setLayout(new FlowLayout());
			
			JLabel name = new JLabel("플레이어:");
			JLabel userNameInfo = new JLabel("");
			userNameInfo.setText(userName + "  / ");
			JLabel levelInfo = new JLabel("");
			levelInfo.setText("Lv." + Integer.toString(level));
			JLabel langInfo = new JLabel("");
			langInfo.setText(" / " + lang);
			
			name.setFont(new Font("함초롬돋움",Font.BOLD,12));
			userNameInfo.setFont(new Font("함초롬돋움",Font.BOLD,12));
			levelInfo.setFont(new Font("함초롬돋움",Font.BOLD,12));
			langInfo.setFont(new Font("함초롬돋움",Font.BOLD,12));
			
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
