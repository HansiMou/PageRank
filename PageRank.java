import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

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

	static HashMap<String, Double> base = new HashMap<String, Double>();
	static double sum = 0;
	static int N;
	static double F;
	static double[][] weight;
	static double epsilon;
	static HashMap<String, Integer> match = new HashMap<String, Integer>();
	static HashMap<String, Double> score = new HashMap<String, Double>();
	static HashMap<String, Double> newscore = new HashMap<String, Double>();
	static String path;

	/**
	 * Description:
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		parseCommand(args);
		
		// read in all files and get the base score
		ReadAllFile(path);
		
		// get the sum of the base score
		getSum();
		
		// test();
		N = base.size();
		epsilon = 0.01 / N;
		weight = new double[N][N];
		
		// use hashmap to map filename to the index of weight array
		DoTheMatch();
		
		ComputeWeight("example");
		// test2();
		Initialize();
		ComputeScore();
		test3();
	}
	public static void parseCommand(String[] args) {
		for (int i = 0; i < args.length;) {
			if (args[i].equals("-docs")) {
				path = args[i + 1];
				i = i + 2;
			}
			if (args[i].equals("-f")) {
				F = Double.parseDouble(args[i + 1]);
				i = i + 2;
			}
		}
	}
	/**
	 * Description:
	 */
	private static void test3() {
		// TODO Auto-generated method stub
		ArrayList<Entry<String, Double>> arrayList = new ArrayList<Map.Entry<String, Double>>(
				score.entrySet());
		Collections.sort(arrayList,
				new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> map1,
							Map.Entry<String, Double> map2) {
						return ((map2.getValue() - map1.getValue() == 0) ? 0
								: (map2.getValue() - map1.getValue() > 0) ? 1
										: -1);
					}
				});
		for (Entry<String, Double> entry : arrayList) {
			System.out.println(entry.getKey() + "\t\t" + entry.getValue());
		}
	}

	/**
	 * Description:
	 * 
	 * @param f
	 */
	private static void ComputeScore() {
		// TODO Auto-generated method stub
		boolean changed = false;
		do {
			changed = false;
			for (Map.Entry<String, Double> entry : base.entrySet()) {
				double res = 0;
				for (Map.Entry<String, Double> entry2 : score.entrySet()) {
					res += weight[match.get(entry.getKey())][match.get(entry2
							.getKey())] * entry2.getValue();
				}
				res = res * F + (1 - F) * entry.getValue();
				newscore.put(entry.getKey(), res);
				if (Math.abs(res - score.get(entry.getKey())) > epsilon)
					changed = true;
			}
			for (Map.Entry<String, Double> entry : base.entrySet()) {
				double n = newscore.get(entry.getKey());
				score.put(entry.getKey(), n);
			}
		} while (changed);
	}

	/**
	 * Description:
	 */
	private static void Initialize() {
		// TODO Auto-generated method stub
		for (Map.Entry<String, Integer> entry : match.entrySet()) {
			double v = base.get(entry.getKey()) / sum;
			base.put(entry.getKey(), v);
			score.put(entry.getKey(), v);
		}
	}

	/**
	 * Description:
	 */
	private static void test2() {
		// TODO Auto-generated method stub
		for (Map.Entry<String, Integer> row : match.entrySet()) {
			for (Map.Entry<String, Integer> col : match.entrySet()) {
				if (weight[col.getValue()][row.getValue()] > 0)
					System.out.println(row.getKey() + "->" + col.getKey()
							+ "\t" + weight[col.getValue()][row.getValue()]);
			}
		}
	}

	/**
	 * Description:
	 * 
	 * @param string
	 */
	private static void ComputeWeight(String path) {
		// TODO Auto-generated method stub
		File f = null;
		f = new File(path);
		File[] files = f.listFiles();
		for (File file : files) {
			String lcPage = readFileContent(file).toLowerCase();

			Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false);
			tidy.setOnlyErrors(false);
			tidy.setEncloseText(true);
			tidy.setDropFontTags(true);
			InputStream is = new ByteArrayInputStream(lcPage.getBytes());
			org.w3c.dom.Document root = tidy.parseDOM(is, null);
			Element rawDoc = root.getDocumentElement();
			// h1
			NodeList children = rawDoc.getElementsByTagName("h1");
			for (int i = 0; i < children.getLength(); i++) {
				NodeList nl = children.item(i).getChildNodes();
				for (int ii = 0; ii < nl.getLength(); ii++) {
					Node child = nl.item(ii);
					switch (child.getNodeType()) {
					case Node.ELEMENT_NODE:
						String href = ((Element) child).getAttribute("href")
								.toLowerCase();
						if (href == null || href.length() == 0) {
						} else {
							double tt = weight[match.get(href)][match.get(file
									.getName().toLowerCase())];
							if (tt < 1) {
								weight[match.get(href)][match.get(file
										.getName().toLowerCase())] = 1;
							}
						}
						break;
					}
				}
			}
			// h2
			children = rawDoc.getElementsByTagName("h2");
			for (int i = 0; i < children.getLength(); i++) {
				NodeList nl = children.item(i).getChildNodes();
				for (int ii = 0; ii < nl.getLength(); ii++) {
					Node child = nl.item(ii);
					switch (child.getNodeType()) {
					case Node.ELEMENT_NODE:
						String href = ((Element) child).getAttribute("href")
								.toLowerCase();
						if (href == null || href.length() == 0) {
						} else {
							double tt = weight[match.get(href)][match.get(file
									.getName().toLowerCase())];
							if (tt < 1) {
								weight[match.get(href)][match.get(file
										.getName().toLowerCase())] = 1;
							}
						}
						break;
					}
				}
			}

			// h3
			children = rawDoc.getElementsByTagName("h3");
			for (int i = 0; i < children.getLength(); i++) {
				NodeList nl = children.item(i).getChildNodes();
				for (int ii = 0; ii < nl.getLength(); ii++) {
					Node child = nl.item(ii);
					switch (child.getNodeType()) {
					case Node.ELEMENT_NODE:
						String href = ((Element) child).getAttribute("href")
								.toLowerCase();
						if (href == null || href.length() == 0) {
						} else {
							double tt = weight[match.get(href)][match.get(file
									.getName().toLowerCase())];
							if (tt < 1) {
								weight[match.get(href)][match.get(file
										.getName().toLowerCase())] = 1;
							}
						}
						break;
					}
				}
			}
			// h4
			children = rawDoc.getElementsByTagName("h");
			for (int i = 0; i < children.getLength(); i++) {
				NodeList nl = children.item(i).getChildNodes();
				for (int ii = 0; ii < nl.getLength(); ii++) {
					Node child = nl.item(ii);
					switch (child.getNodeType()) {
					case Node.ELEMENT_NODE:
						String href = ((Element) child).getAttribute("href")
								.toLowerCase();
						if (href == null || href.length() == 0) {
						} else {
							double tt = weight[match.get(href)][match.get(file
									.getName().toLowerCase())];
							if (tt < 1) {
								weight[match.get(href)][match.get(file
										.getName().toLowerCase())] = 1;
							}
						}
						break;
					}
				}
			}
			// b
			children = rawDoc.getElementsByTagName("b");
			for (int i = 0; i < children.getLength(); i++) {
				NodeList nl = children.item(i).getChildNodes();
				for (int ii = 0; ii < nl.getLength(); ii++) {
					Node child = nl.item(ii);
					switch (child.getNodeType()) {
					case Node.ELEMENT_NODE:
						String href = ((Element) child).getAttribute("href")
								.toLowerCase();
						if (href == null || href.length() == 0) {
						} else {
							if (match.containsKey(href)) {
								double tt = weight[match.get(href)][match
										.get(file.getName().toLowerCase())];
								if (tt < 1) {
									weight[match.get(href)][match.get(file
											.getName().toLowerCase())] = 1;
								}
							}
						}
						break;
					}
				}
			}
			// em
			children = rawDoc.getElementsByTagName("bem");
			for (int i = 0; i < children.getLength(); i++) {
				NodeList nl = children.item(i).getChildNodes();
				for (int ii = 0; ii < nl.getLength(); ii++) {
					Node child = nl.item(ii);
					switch (child.getNodeType()) {
					case Node.ELEMENT_NODE:
						String href = ((Element) child).getAttribute("href")
								.toLowerCase();
						if (href == null || href.length() == 0) {
						} else {
							double tt = weight[match.get(href)][match.get(file
									.getName().toLowerCase())];
							if (tt < 1) {
								weight[match.get(href)][match.get(file
										.getName().toLowerCase())] = 1;
							}
						}
						break;
					}
				}
			}

			int index = 0; // position in page
			int iEndAngle, ihref, iURL, iCloseQuote, iHatchMark, iEnd;
			while ((index = lcPage.indexOf("<a", index)) != -1) {
				iEndAngle = lcPage.indexOf(">", index);
				ihref = lcPage.indexOf("href", index);
				if (ihref != -1) {
					iURL = lcPage.indexOf("\"", ihref) + 1;
					if ((iURL != -1) && (iEndAngle != -1) && (iURL < iEndAngle)) {
						iCloseQuote = lcPage.indexOf("\"", iURL);
						iHatchMark = lcPage.indexOf("#", iURL);
						if ((iCloseQuote != -1) && (iCloseQuote < iEndAngle)) {
							iEnd = iCloseQuote;
							if ((iHatchMark != -1)
									&& (iHatchMark < iCloseQuote))
								iEnd = iHatchMark;
							String newUrlString = lcPage.substring(iURL, iEnd);
							if (!match.containsKey(newUrlString)) {
							} else {
								double tt = weight[match.get(newUrlString)][match
										.get(file.getName().toLowerCase())];
								weight[match.get(newUrlString)][match.get(file
										.getName().toLowerCase())] = tt + 1;
							}
						}
					}
				}
				index = iEndAngle;
			}
			int col = match.get(file.getName().toLowerCase());
			int tsum = 0;
			for (int i = 0; i < N; i++) {
				tsum += weight[i][col];
			}
			for (int i = 0; i < N; i++) {
				weight[i][col] = tsum == 0 ? 1 / N : weight[i][col] / tsum;
			}
		}
	}

	private static String readFileContent(File file) {
		BufferedReader bf;
		String content = "";
		StringBuilder sb = new StringBuilder();
		try {
			bf = new BufferedReader(new FileReader(file));
			while (content != null) {
				content = bf.readLine();
				if (content == null) {
					break;
				}
				sb.append(content.trim());
			}
			bf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * Description:
	 */
	private static void DoTheMatch() {
		// TODO Auto-generated method stub
		int i = 0;
		for (Map.Entry<String, Double> entry : base.entrySet()) {
			match.put(entry.getKey(), i++);
		}
	}

	/**
	 * Description:
	 */
	private static void getSum() {
		// TODO Auto-generated method stub
		for (Map.Entry<String, Double> entry : base.entrySet()) {
			sum += entry.getValue();
		}
	}

	/**
	 * Description:
	 */
	private static void test() {
		// TODO Auto-generated method stub
		for (Map.Entry<String, Double> entry : base.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue() / sum);
		}
	}

	private static void ReadAllFile(String filePath) {
		File f = null;
		f = new File(filePath);
		File[] files = f.listFiles();
		for (File file : files) {
			base.put(file.getName().toLowerCase(), Math.log(wordCount(file))
					/ Math.log(2.0));
		}
	}

	public static double wordCount(File file) {
		try {
			Scanner sc;
			sc = new Scanner(new FileInputStream(file));
			double count = 0;
			while (sc.hasNext()) {
				sc.next();
				count++;
			}
			return count;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
