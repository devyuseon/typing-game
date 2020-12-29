import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

public class GamePanel extends JPanel {
	
	private final int MAX_WORDS = 100;
	
	private Player player = new Player();
	
	private JTextField input = new JTextField(30);
	private Vector<JLabel>targetVector = new Vector<JLabel>(); // targetLabel을 담는 targetVector
	
	// 색
	public Color skyBlue = new Color(219, 239, 255);
	public Color lightBlue = new Color(94, 177, 255);
	
	private ScorePanel scorePanel = null;
	private GameGroundPanel gameGroundPanel = new GameGroundPanel();
	private InputPanel inputPanel = new InputPanel();
	
	private TextSource textSource = new TextSource(); // 단어 벡터 생성
	
	// 배경 음악
	private Music basicBGM = new Music("basicBGM.mp3",true);
	
	// 단어를 생성하는 스레드
	private GenerateWordThread generateWordThread = new GenerateWordThread(targetVector, player);
	// 단어를 떨어뜨리는 스레드
	private DropWordThread dropWordThread = new DropWordThread(targetVector,player);
	// 땅에 닿은 단어 감지하는 스레드
	private DetectBottomThread detectBottomThread = new DetectBottomThread(targetVector);
	
	// 레벨에 따른 난이도 조절
	private int [] generationSpeed = {4000,3000,2000,1000,800};
	private int [] droppingSpeed = {400,300,200,80,40};
	
	public GamePanel() {
	}
	
	public GamePanel(ScorePanel scorePanel, Player player) {
		// 기억해둔다.
		this.scorePanel = scorePanel;
		this.player = player;
		
		// 스레드 생성자 부르기
		generateWordThread = new GenerateWordThread(targetVector, player);
		dropWordThread = new DropWordThread(targetVector,player);
		textSource = new TextSource(player.getLanguage()); // 단어 벡터 생성

		//레이아웃 설정
		setLayout(new BorderLayout());
		add(gameGroundPanel, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);
		
		// 입력란
		input.setHorizontalAlignment(JTextField.CENTER); // input JTextField 가운데정렬
		input.setFont(new Font("Aharoni", Font.PLAIN, 20));
		
		// textfield에서 enter 누르면 실행됨
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized(targetVector) {
					JTextField t = (JTextField)(e.getSource());
					String inWord = t.getText(); // 사용자가 입력한 단어
					for (int i=0; i < targetVector.size(); i++) {
						String text = targetVector.get(i).getText();
						if(text.equals(inWord)) { // 단어맞추기 성공
						
							System.out.println(inWord + " 맞춤"); // 콘솔에서 확인 위함
							// 점수 증가
							scorePanel.increase(player);
							// scorePaenl repaint
							scorePanel.repaintScore();
							gameGroundPanel.remove(targetVector.get(i)); // 패널에서 라벨 떼기
							targetVector.remove(i); // targetVector에서 삭제
							t.setText(null); // input 비우기
							// 단어 틀릴때마다 스피드 증가
							if (droppingSpeed[player.getLevel()-1] > 2)
								droppingSpeed[player.getLevel()-1]--;
							if (generationSpeed[player.getLevel()-1] > 20)
								generationSpeed[player.getLevel()-1] -= 10;
							break;
						}
						// 벡터 마지막원소에서도 일치하는 단어 못찾음
						if((i == (targetVector.size() - 1)) && !targetVector.get(i).getText().equals(inWord)) {
							System.out.println(inWord + "틀림");
							// 점수 감소
							scorePanel.decrease(player);
							scorePanel.repaintScore();
							t.setText(null);
						}
						t.requestFocus(); // 엔터 친 후에도 textField에 focus유지
					} // end of for
				}
			} // end of actionPerformed()
		});
	}

	class GameGroundPanel extends JPanel{ // 단어내려오는곳 배경이미지
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			ImageIcon icon = new ImageIcon("gamePanelBack.jpg");
			g.drawImage(icon.getImage(), 0, 0, gameGroundPanel.getWidth(),
					gameGroundPanel.getHeight(), gameGroundPanel);
			setOpaque(false);
		}
		public GameGroundPanel() {
			this.setBackground(skyBlue);
			// 단어가 마구잡이로 내려와야함.
			setLayout(null);
		}
	}
	
	class InputPanel extends JPanel{ // 단어 입력하는곳
		public InputPanel() {
			setLayout(new FlowLayout());
			this.setBackground(lightBlue);
			add(input);
		}
	}
	
	public void gameStart(Player player) {
		this.player = player;

		basicBGM.start(); // BGM재생
		// 단어생성 시작
		generateWordThread.start();
		// 단어 떨어뜨리기 시작
		dropWordThread.start();
		// 땅에 닿은 단어 감지 시작
		detectBottomThread.start();
	}
	
	public void gameOver() { // 게임종료
		// 단어생성 중단
		generateWordThread.interrupt();
		// 단어 떨어뜨리기 중단
		dropWordThread.interrupt();
		// 땅에 닿은 단어 감지 중단
		detectBottomThread.interrupt();
	}
	
	// 단어 생성하는 스레드
	public class GenerateWordThread extends Thread{
		
		private Vector<JLabel>targetVector = null;
		private Player player = null;
		
		// 단어 가져와 Label설정, 부착하는 메소드
		synchronized void generateWord(Player player) {
			JLabel targetLabel = new JLabel("");
			// 단어 한 개 선택
			String newWord = textSource.get(player.getLanguage());
			targetLabel.setText(newWord);
			
			// targetLabel 모양
			targetLabel.setHorizontalAlignment(JLabel.CENTER); // JLabel 가운데정렬
			targetLabel.setSize(200, 40);
			if(player.getLanguage()=="ko") {
				targetLabel.setFont(new Font("함초롬돋움",1,21));
			}
			else targetLabel.setFont(new Font("Dialog", 1, 21));
			targetLabel.setForeground(Color.WHITE);
			
			// x좌표 랜덤 설정
			int startX = (int) (Math.random()*gameGroundPanel.getWidth());
			while(true) {
				if ((startX + targetLabel.getWidth()) > gameGroundPanel.getWidth()) 
					startX = (int) (Math.random()*gameGroundPanel.getWidth());
				else
					break;
			}
			
			targetLabel.setLocation(startX,0);
			
			targetLabel.setOpaque(false); // 배경 투명하게
			targetVector.addElement(targetLabel); // targetVector에 생성한 newWord 추가
			gameGroundPanel.add(targetLabel);
		}
		
		public GenerateWordThread(Vector<JLabel>targetVector, Player player) {
			this.targetVector = targetVector;
			this.player = player;
		}
		
		@Override
		public void run() {
			while(true) {
				int generationTime = generationSpeed[player.getLevel()-1];
				generateWord(player);
				gameGroundPanel.repaint();
				try {
					sleep(generationTime);
				} catch (InterruptedException e) {
					return;
				}
			} // end of while
		} // end of run()
	} // end of GenerateWordThread
	
	// 단어 아래로 내리는 스레드
	public class DropWordThread extends Thread{
		
		private Vector<JLabel>targetVector = null;
		private Player player = null;
		
		public DropWordThread(Vector<JLabel>targetVector, Player player) {
			this.targetVector = targetVector;
			this.player = player;
		}
		
		// y좌표 증가해 단어 밑으로 내림
		synchronized void dropWord(Player player) {
			for (int i=0; i<targetVector.size(); i++) {
				int x = targetVector.get(i).getX();
				int y = targetVector.get(i).getY();
				targetVector.get(i).setLocation(x, y+5);
				gameGroundPanel.repaint();
			} // end of for
		}
		
		// targetVector에 들어있는 모든 JLabel들의 y좌표 증가
		@Override
		public void run() {
			 while (true){
				 int dropTime = droppingSpeed[player.getLevel()-1];
				 dropWord(player);
				 gameGroundPanel.repaint();
				 try {
					 sleep(dropTime);
					} catch (InterruptedException e) {
						return;
					}
			} // end of while
		} // end of run()
	} // end of DropWordThread
	
	public class DetectBottomThread extends Thread {
		
		private Vector<JLabel>targetVector = null;
		
		public DetectBottomThread(Vector<JLabel>targetVector) {
			this.targetVector = targetVector;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					sleep(1);
					for(int i=0; i<targetVector.size(); i++) {
						// 바닥에 닿은 단어 구하기 위함
						int y = ((JLabel)targetVector.get(i)).getY();
						if (y > gameGroundPanel.getHeight()-20) {
							System.out.println(targetVector.get(i).getText() + " 떨어짐");
							
							// true값이 반환되면 게임을 종료한다.
							boolean isGameOver =scorePanel.decreaseLife(player, basicBGM);
							if(isGameOver == true) { // 모든스레드 종료
								gameOver();
							}
							
							// 게임이 종료되지 않을 경우 패널에서 라벨 제거 게임 계속됨
							gameGroundPanel.remove(targetVector.get(i)); // 패널에서 라벨 떼기
							targetVector.remove(i); // targetVector에서 삭제
						}
					}
				} catch (InterruptedException e) {
					return;
				}
			} // end of while
		} // end of run()
	}// end of Thread
		
	}

