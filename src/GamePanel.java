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
	private Vector<JLabel>targetVector = new Vector<JLabel>(); // targetLabel�� ��� targetVector
	
	// ��
	public Color skyBlue = new Color(219, 239, 255);
	public Color lightBlue = new Color(94, 177, 255);
	
	private ScorePanel scorePanel = null;
	private GameGroundPanel gameGroundPanel = new GameGroundPanel();
	private InputPanel inputPanel = new InputPanel();
	
	private TextSource textSource = new TextSource(); // �ܾ� ���� ����
	
	// ��� ����
	private Music basicBGM = new Music("basicBGM.mp3",true);
	
	// �ܾ �����ϴ� ������
	private GenerateWordThread generateWordThread = new GenerateWordThread(targetVector, player);
	// �ܾ ����߸��� ������
	private DropWordThread dropWordThread = new DropWordThread(targetVector,player);
	// ���� ���� �ܾ� �����ϴ� ������
	private DetectBottomThread detectBottomThread = new DetectBottomThread(targetVector);
	
	// ������ ���� ���̵� ����
	private int [] generationSpeed = {4000,3000,2000,1000,800};
	private int [] droppingSpeed = {400,300,200,80,40};
	
	public GamePanel() {
	}
	
	public GamePanel(ScorePanel scorePanel, Player player) {
		// ����صд�.
		this.scorePanel = scorePanel;
		this.player = player;
		
		// ������ ������ �θ���
		generateWordThread = new GenerateWordThread(targetVector, player);
		dropWordThread = new DropWordThread(targetVector,player);
		textSource = new TextSource(player.getLanguage()); // �ܾ� ���� ����

		//���̾ƿ� ����
		setLayout(new BorderLayout());
		add(gameGroundPanel, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);
		
		// �Է¶�
		input.setHorizontalAlignment(JTextField.CENTER); // input JTextField �������
		input.setFont(new Font("Aharoni", Font.PLAIN, 20));
		
		// textfield���� enter ������ �����
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized(targetVector) {
					JTextField t = (JTextField)(e.getSource());
					String inWord = t.getText(); // ����ڰ� �Է��� �ܾ�
					for (int i=0; i < targetVector.size(); i++) {
						String text = targetVector.get(i).getText();
						if(text.equals(inWord)) { // �ܾ���߱� ����
						
							System.out.println(inWord + " ����"); // �ֿܼ��� Ȯ�� ����
							// ���� ����
							scorePanel.increase(player);
							// scorePaenl repaint
							scorePanel.repaintScore();
							gameGroundPanel.remove(targetVector.get(i)); // �гο��� �� ����
							targetVector.remove(i); // targetVector���� ����
							t.setText(null); // input ����
							// �ܾ� Ʋ�������� ���ǵ� ����
							if (droppingSpeed[player.getLevel()-1] > 2)
								droppingSpeed[player.getLevel()-1]--;
							if (generationSpeed[player.getLevel()-1] > 20)
								generationSpeed[player.getLevel()-1] -= 10;
							break;
						}
						// ���� ���������ҿ����� ��ġ�ϴ� �ܾ� ��ã��
						if((i == (targetVector.size() - 1)) && !targetVector.get(i).getText().equals(inWord)) {
							System.out.println(inWord + "Ʋ��");
							// ���� ����
							scorePanel.decrease(player);
							scorePanel.repaintScore();
							t.setText(null);
						}
						t.requestFocus(); // ���� ģ �Ŀ��� textField�� focus����
					} // end of for
				}
			} // end of actionPerformed()
		});
	}

	class GameGroundPanel extends JPanel{ // �ܾ�����°� ����̹���
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
			// �ܾ �������̷� �����;���.
			setLayout(null);
		}
	}
	
	class InputPanel extends JPanel{ // �ܾ� �Է��ϴ°�
		public InputPanel() {
			setLayout(new FlowLayout());
			this.setBackground(lightBlue);
			add(input);
		}
	}
	
	public void gameStart(Player player) {
		this.player = player;

		basicBGM.start(); // BGM���
		// �ܾ���� ����
		generateWordThread.start();
		// �ܾ� ����߸��� ����
		dropWordThread.start();
		// ���� ���� �ܾ� ���� ����
		detectBottomThread.start();
	}
	
	public void gameOver() { // ��������
		// �ܾ���� �ߴ�
		generateWordThread.interrupt();
		// �ܾ� ����߸��� �ߴ�
		dropWordThread.interrupt();
		// ���� ���� �ܾ� ���� �ߴ�
		detectBottomThread.interrupt();
	}
	
	// �ܾ� �����ϴ� ������
	public class GenerateWordThread extends Thread{
		
		private Vector<JLabel>targetVector = null;
		private Player player = null;
		
		// �ܾ� ������ Label����, �����ϴ� �޼ҵ�
		synchronized void generateWord(Player player) {
			JLabel targetLabel = new JLabel("");
			// �ܾ� �� �� ����
			String newWord = textSource.get(player.getLanguage());
			targetLabel.setText(newWord);
			
			// targetLabel ���
			targetLabel.setHorizontalAlignment(JLabel.CENTER); // JLabel �������
			targetLabel.setSize(200, 40);
			if(player.getLanguage()=="ko") {
				targetLabel.setFont(new Font("���ʷҵ���",1,21));
			}
			else targetLabel.setFont(new Font("Dialog", 1, 21));
			targetLabel.setForeground(Color.WHITE);
			
			// x��ǥ ���� ����
			int startX = (int) (Math.random()*gameGroundPanel.getWidth());
			while(true) {
				if ((startX + targetLabel.getWidth()) > gameGroundPanel.getWidth()) 
					startX = (int) (Math.random()*gameGroundPanel.getWidth());
				else
					break;
			}
			
			targetLabel.setLocation(startX,0);
			
			targetLabel.setOpaque(false); // ��� �����ϰ�
			targetVector.addElement(targetLabel); // targetVector�� ������ newWord �߰�
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
	
	// �ܾ� �Ʒ��� ������ ������
	public class DropWordThread extends Thread{
		
		private Vector<JLabel>targetVector = null;
		private Player player = null;
		
		public DropWordThread(Vector<JLabel>targetVector, Player player) {
			this.targetVector = targetVector;
			this.player = player;
		}
		
		// y��ǥ ������ �ܾ� ������ ����
		synchronized void dropWord(Player player) {
			for (int i=0; i<targetVector.size(); i++) {
				int x = targetVector.get(i).getX();
				int y = targetVector.get(i).getY();
				targetVector.get(i).setLocation(x, y+5);
				gameGroundPanel.repaint();
			} // end of for
		}
		
		// targetVector�� ����ִ� ��� JLabel���� y��ǥ ����
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
						// �ٴڿ� ���� �ܾ� ���ϱ� ����
						int y = ((JLabel)targetVector.get(i)).getY();
						if (y > gameGroundPanel.getHeight()-20) {
							System.out.println(targetVector.get(i).getText() + " ������");
							
							// true���� ��ȯ�Ǹ� ������ �����Ѵ�.
							boolean isGameOver =scorePanel.decreaseLife(player, basicBGM);
							if(isGameOver == true) { // ��罺���� ����
								gameOver();
							}
							
							// ������ ������� ���� ��� �гο��� �� ���� ���� ��ӵ�
							gameGroundPanel.remove(targetVector.get(i)); // �гο��� �� ����
							targetVector.remove(i); // targetVector���� ����
						}
					}
				} catch (InterruptedException e) {
					return;
				}
			} // end of while
		} // end of run()
	}// end of Thread
		
	}

