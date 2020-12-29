import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ScorePanel extends JPanel {
	private Player player = new Player();
	private GamePanel gamePanel = new GamePanel();
	private int score = 0;
	private int life = 5; // ����
	private JLabel textLabel = new JLabel("����");
	private JLabel scoreLabel = new JLabel(Integer.toString(score));
	private JLabel [] lifeLabel = new JLabel[life];
	private JLabel warningLabel = new JLabel("<html>��Ʈ�� ��� ��������<br>�ء� Game Over! �ء�</html>");
	private Color skyBlue = new Color(153, 214, 255);
	
	private Music basicBGM = new Music("basicBGM.mp3",true);
	private Music warningBGM = new Music("warningBGM.mp3",true);

	public ScorePanel() {
		setBackground(skyBlue);
		setLayout(null);
		
		textLabel.setFont(new Font("���ʷҵ���",1,15));
		textLabel.setSize(50,20);
		textLabel.setLocation(20,200);
		add(textLabel);
		
		scoreLabel.setFont(new Font("���ʷҵ���",1,15));
		scoreLabel.setSize(100,20);
		scoreLabel.setLocation(100,200);
		add(scoreLabel);
		
		ImageIcon heart = new ImageIcon("heart.png");
		
		for (int i=0; i<life; i++) {
			lifeLabel[i] = new JLabel(heart);
			lifeLabel[i].setSize(heart.getIconWidth(),heart.getIconHeight());
			lifeLabel[i].setLocation(30*i+20,50);
			add(lifeLabel[i]);
		}
		
		warningLabel.setFont(new Font("���ʷҵ���",1,15));
		warningLabel.setSize(200,50);
		warningLabel.setLocation(20,70);
		add(warningLabel);

	}
	
	synchronized void increase(Player player) {
		score += 10;
		Music successBGM = new Music("successBGM.mp3",false);
		successBGM.start();
		System.out.print("���� "+ score + "�� ����  ");
		player.setScore(score);
		scoreLabel.setText(Integer.toString(score));
		System.out.println("����" + score + "�� ��ħ");
		scoreLabel.getParent().repaint();
	}
	
	synchronized void decrease(Player player) {
		score -= 10;
		Music failBGM = new Music("failBGM.MP3",false);
		failBGM.start();
		System.out.print("���� "+ score + "�� ����  ");
		player.setScore(score);
		scoreLabel.setText(Integer.toString(score));
		scoreLabel.getParent().repaint();
	}
	
	public void repaintScore() {
		scoreLabel.getParent().repaint();
	}
	
	public void initPlayerInfo(String name, int level, int score, String language) {
		player = new Player(name, level, score, language);

	}
	
	synchronized boolean decreaseLife(Player player, Music basicBGM) {
		life--;
		boolean isTrue = false;
		
		switch(life) {
		case 4: // �� �� �� �� �� 
			lifeLabel[4].setVisible(false);
			break;
		case 3: // �� �� �� �� �� 
			lifeLabel[0].setVisible(false);
			break;
		case 2: // �� �� �� �� �� 
			lifeLabel[3].setVisible(false);
			basicBGM.close();
			warningBGM.start();
			break;
		case 1: // �� �� �� �� �� 
			lifeLabel[1].setVisible(false);
			break;
		case 0: // �� �� �� �� �� 
			lifeLabel[2].setVisible(false);
			// ���� Panel�Ⱥ��̰�
			warningLabel.setText("GAME OVER");
			warningLabel.setLocation(70,70);
			warningBGM.close();
			Music gameoverBGM = new Music("gameoverBGM.MP3",false);
			gameoverBGM.start();
			
			// ���� ���� �� ���� ����
			Player p = new Player(player.getName(), player.getLevel(),
					player.getScore(), player.getLanguage());
			p.storeInfo();
			
			// �����Ұ����� ����� JOptionPane
			String [] answer = {"��", "�ٽý���"};
			int choice = JOptionPane.showOptionDialog(gamePanel, player.getName() + "��(��) " + player.getScore() + "�� �Դϴ�.\n������ �����Ͻðڽ��ϱ�?",
					"���� ����", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, answer, null);
			
			if(choice == 0) { // "��" ����. â �ݴ´�
				System.exit(JFrame.EXIT_ON_CLOSE);
			}
			else if(choice == 1) { // "�ٽý���" ����. ���� ������ �ݰ� �� ������ ����
				// ������ �����ϰ�  �ٽ� ����...
				GameFrame f = new GameFrame();
				isTrue = true;
			}
			
			break;
		}
		return isTrue;
	}
}
