import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Player {
	private String name;
	private int level;
	private int score;
	private String language;
	
	public Player() {
		
	}
	
	public Player (String name, int level, int score, String language) {
		this.name = name;
		this.level = level;
		this.score = score;
		this.language = language;
	}
	
	public String getName() {
		return name;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setName(String name) {
		this.name = name;
	}	
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public void storeInfo() {
		try {
			String fileName = language + level + ".txt";
			FileWriter fw = new FileWriter(fileName,true);
			String data = name + "," + score + "\n";
			fw.write(data);
			fw.close();
			sortInfo(fileName);
			
		} // end of try
		catch(IOException e) {
			System.out.println(e);
		} // end of catch
	}
	
	public void sortInfo(String fileName) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "MS949"));
			FileWriter out = new FileWriter("sorted"+fileName,false); // ���
			
			ArrayList<String> list = new ArrayList<String>();
			String line;
			String []data = new String[2];
			
			// �ؽ��ʿ� ������ ����
			Map<String,Integer> temp = new HashMap<>();
			while ((line = in.readLine()) != null) {
				data = line.trim().split(",");
				// data[0]�� name, data[1]�� score
				temp.put(data[0], Integer.parseInt(data[1]));
			}
			
			// ����Ʈ ����
			List<Entry<String, Integer>> rank 
			= new ArrayList<Entry<String, Integer>>(temp.entrySet());
			
			// Comparator�� ����
			Collections.sort(rank, new Comparator<Entry<String, Integer>>() {
				// compare�� ���� ��
				public int compare(Entry<String, Integer> obj1, 
						Entry<String, Integer> obj2)
				{
					// ���� �������� ����
					return obj2.getValue().compareTo(obj1.getValue());
				}
			});
			
			// sorted+fileName�� ���ĵ� ������ ����
			for(Entry<String, Integer> entry : rank) {
				String rankData;
				rankData = entry.getKey() + "," + Integer.toString(entry.getValue()) + "\n";
				out.write(rankData);
				out.flush();
			}
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		} 
	}
	
}
