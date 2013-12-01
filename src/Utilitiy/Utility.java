package Utilitiy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
	
	//Does the Multi-threading issue matter here? of will this be threaded as well?
	
	public static String getItemID(String uri) {
		Matcher matcher = Pattern.compile("(?<=Order/).*").matcher(uri);
		matcher.find();
		return matcher.group();
	}

	public static String getCategory(String uri) {
		String rv = "";
		if (uri.toUpperCase().contains("MEAT")) rv = "Meat";
		else if (uri.toUpperCase().contains("CEREAL")) rv = "Cereal";
		else if (uri.toUpperCase().contains("ICECREAM")) rv = "Ice Cream";
		else if (uri.toUpperCase().contains("CHEESE")) rv = "Cheese";
		return rv;
	}

}
