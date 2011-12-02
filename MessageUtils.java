package hbot;

import java.util.StringTokenizer;

public class MessageUtils
{
	//Separa un mensaje en palabras, quitando los espacios
	public static String[] clean(String msg)
	{
		StringTokenizer tokens = new StringTokenizer(msg);
		String[] splitMsg = new String[tokens.countTokens()];

		for(int i=0;tokens.hasMoreTokens();i++)
		{
			splitMsg[i] = tokens.nextToken();
		}
		
		return splitMsg;
	}
}
