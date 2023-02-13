package org.qamation.navigator;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.qamation.keyboard.KeyboardEmulator;
import org.qamation.mouse.MouseActionPatterns;
import org.qamation.mouse.MouseEmulator;
import org.qamation.commons.utils.StringUtils;
import org.qamation.commons.webdriver.IsPageReadyUtils;

import java.util.HashMap;
import java.util.Set;

public class WebPageNavigator {
	private final String SUBTOKENS_DELIMETER = " ";	
	private final String SPECIAL_KEY_ENDS_WITH = "}";
	private static final HashMap<String,String> SPECIAL_KEY_STARTS_MAP;
	static {
		SPECIAL_KEY_STARTS_MAP = new HashMap<String,String>();
		SPECIAL_KEY_STARTS_MAP.put("{","{");
		SPECIAL_KEY_STARTS_MAP.put("+{","{SHIFT+");
		SPECIAL_KEY_STARTS_MAP.put("^{","{CONTROL+");
		SPECIAL_KEY_STARTS_MAP.put("%{","{ALT+");
	}
	private KeyboardEmulator keyboard;
	private WebDriver driver;
	private String delimeter=null;


	public WebPageNavigator(WebDriver driver) {
		this.driver = driver;
		this.keyboard = new KeyboardEmulator(this.driver);
	}

	public WebPageNavigator(WebDriver driver,String delim) {
        this(driver);
	    this.delimeter = delim;
    }

	public void processNavigationSequence(String[] tokens) {
		for (String t : tokens) {
			String[] subTokens= processToken(t);
		}
	}

	public void processNavigationString(String navigation, String delim) {
		NavigationString navStr = new NavigationString(navigation,delim);
		String[] seq = navStr.getNavigationSequence();
		processNavigationSequence(seq);
	}

	public void processNavigationString(String navigation) {
	    if (delimeter == null) throw new RuntimeException("Delimeter was not set for this instance of WebPageNavigator.\nUse processNavigationString(String navigation, String delim, IsReady page)\nor constructor WebPageNavigator(WebDriver driver,String delim, IsReady isReady)");
	    processNavigationString(navigation,this.delimeter);
    }


	private void pressEnterIfLastSubTokenIsNotSpecialKey(String lastSubToken) {
		if (isSpecialKey(lastSubToken)) return;
		if (isMouseAction(lastSubToken)) return;
		else {
			keyboard.sendSpecialKeys("ENTER");
			IsPageReadyUtils.isPageChangeStopped(this.driver);
		}
	}

	private boolean isMouseAction(String t) {
		return MouseActionPatterns.isMouseAction(t);
	}

	private boolean isSpecialKey(String token) {
		Set<String> specialKeysSet = SPECIAL_KEY_STARTS_MAP.keySet();
		for (String s : specialKeysSet ) {
			if (token.startsWith(s) && token.endsWith(SPECIAL_KEY_ENDS_WITH)) return true;
		}
		return false;
	}
	
	private String[] processToken(String token) {
		String[] subTokens;
		boolean doNotSplit = isMouseAction(token);
		if (doNotSplit) {
			subTokens = new String[] {token};
		}
		else {
			subTokens = splitTokenBy(SUBTOKENS_DELIMETER, token);
		}
		processSubTokens(subTokens);
		return subTokens;
	}

	private String[] splitTokenBy(String delimeter, String token) {
		return token.split(delimeter);
	}
	
	private void processSubTokens(String[] subTokens) {
		for (String st : subTokens) {
			if (isMouseAction(st)) {
				processMouseAction(st);
				continue;
			}
			else if (isSpecialKey(st)) {
				processSubTokenAsSpecialKey(st);
				continue;
			}
			keyboard.sendKeys(StringUtils.convertCharSequenceToArray(st));
		}
	}

	private void processMouseAction(String st) {
		MouseEmulator mouseEmulator = new MouseEmulator(driver);
		Action action = mouseEmulator.getAction(st);
		action.perform();
		IsPageReadyUtils.isPageChangeStopped(driver);
	}

	private void processSubTokenAsSpecialKey(String specialKey) {
		String convertedSpecialKey = rewriteSpecialKey(specialKey);
		String s = StringUtils.extractContentFromCurlyBruckets(convertedSpecialKey);
		keyboard.sendSpecialKeys(s);
		if (specialKey.equalsIgnoreCase("{TAB}")
				|| specialKey.equalsIgnoreCase("{DELETE}")
				|| specialKey.equalsIgnoreCase("{SPACE}")
				) return;
		IsPageReadyUtils.isPageChangeStopped(driver);
	}

	private String rewriteSpecialKey(String specialKey) {
		Set<String> specialKeysSet = SPECIAL_KEY_STARTS_MAP.keySet();
		for (String s : specialKeysSet) {
			if (specialKey.startsWith(s)) return specialKey.replace(s, SPECIAL_KEY_STARTS_MAP.get(s));
		}
		throw new RuntimeException(specialKey+" is not supported");
	}
	
}