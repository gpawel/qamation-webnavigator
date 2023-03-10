package org.qamation.navigator;

import org.qamation.commons.utils.StringUtils;


public class NavigationString {
	// http://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
	//private final String SPLIT = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

	// the following way to split navigation string
	// developed to consider mouse action
	// it based on the following article:
	// https://stackoverflow.com/questions/21105360/regex-find-comma-not-inside-quotes/21106122
	// changed to accomadate the symbol <.> single quotes used in xpath or css locators inside
	// mouse action
	private final String SPLIT_PREFIX = "(?!\\B'[^']*)";
	private final String SPLIT_SUFFIX = "(?![^']*'\\B)";

	// attampt to cover cases like 4"  private final String SPLIT = "(?=([^\"]*\"[^\"][\"\"]*\")*[^\"]*$)"; //",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	private String delimeter;
	private String navigation;
	private String splitRegEx;
	private String [] tokens;
	
	public NavigationString (String navSeq, String delim) {
		this.navigation = navSeq;
		this.delimeter = delim;
		//this.splitRegEx = delimeter+SPLIT;
		this.splitRegEx = SPLIT_PREFIX +delimeter+ SPLIT_SUFFIX;
		this.tokens = generateNavigationSequence();
	}
	
	private String[] generateNavigationSequence() {
		String[] tokens = navigation.split(splitRegEx);
		if (tokens.length==1 && tokens[0].isEmpty()) return new String[] {};
		for (int i=0; i < tokens.length; i++) {
			tokens[i] = StringUtils.extractContentFromQuotes(tokens[i]);
		}
		return tokens;
	}
	
	public String[] getNavigationSequence() {
		return tokens;
	}
	
	public String getRegExp() {
		return splitRegEx;
	}
	
}
