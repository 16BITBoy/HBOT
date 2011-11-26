package hbot;

import java.util.StringTokenizer;

public class MessageUtils {
	public static String[] clean(String msg) {
		StringTokenizer tokens = new StringTokenizer(msg);
		String[] splitMsg = new String[tokens.countTokens()];
		int i = 0;
		while (tokens.hasMoreTokens()) {
			splitMsg[i] = tokens.nextToken();
		}
		return splitMsg;
	}
}
