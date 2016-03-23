import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Hansi Mou
 * @date Mar 22, 2016
 * @version 1.0
 */

/**
 * @author Hansi Mou
 *
 *         Mar 22, 2016
 */
public class PageRank {

	/**
	 * Description:
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static int wordCount(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		int res = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String s[] = tempString.split("\\p{Punct}");
				res += s.length;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return res;
	}
}
