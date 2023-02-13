package org.qamation.keyboard;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.qamation.locator.LocatorFactory;


import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class KeyboardEmulator {

	private static final long KEY_PRESS_PAUSE_MIL_SEC = 20;
	private WebDriver driver;
	//private org.openqa.selenium.interactions.Keyboard keyboard;
	private org.openqa.selenium.interactions.Actions actions;
	private List<Field> keyboardKeys;
	
	public KeyboardEmulator(WebDriver driver) {
		this.driver = driver;
		this.actions = new Actions(driver);//getKeyboardFromWebDriver();
		populateKeyboardKeys();
	}

	private void populateKeyboardKeys() {
		keyboardKeys = new ArrayList<Field>();
		Field[] declairedFields = Keys.class.getDeclaredFields();
		for (Field field : declairedFields) {
			if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
				keyboardKeys.add(field);
			}
		}
	}

	public void pressKey(CharSequence key) {
		actions.keyDown(key);

	}

	public void releaseKey(CharSequence key) {
		actions.keyUp(key);

	}

	public void sendKeys(CharSequence... keys) {
		actions.sendKeys(keys).perform();
		/*
		String[] sequence = StringUtils.convertCharSequenceToArray(keys);
		for (int i=0; i < sequence.length; i++) {
			actions.sendKeys(sequence[i]);
			pause(KEY_PRESS_PAUSE_MIL_SEC);
		}
		 */
	}
	
	public void sendKeysAt(String location, CharSequence... keys) {
		By locator = createLocator(location);
		WebElement el = findElement(locator);		
		typeKeysAt(el,keys);
	}

	private void typeKeysAt(WebElement el, CharSequence[] keys) {
		actions.sendKeys(el, keys).perform();
		/*
		String[] sequence = StringUtils.convertCharSequenceToArray(keys);
		for (int i=0; i < sequence.length; i++) {
			el.sendKeys(sequence[i]);
			pause(KEY_PRESS_PAUSE_MIL_SEC);
		}
		 */
	}

	private By createLocator(String location) {
		LocatorFactory lu = new LocatorFactory();
		By locator = lu.getLocator(location);
		return locator;
	}
	
	private WebElement findElement(By locator) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		return driver.findElement(locator);
	}
	
	private void pause(long pauseLength) {
		try {
			Thread.sleep(pauseLength);
		} catch (Exception e) {
			throw new RuntimeException("Unable to pause because:\n", e);
		}
	}

	public void sendSpecialKeys(String keys) {
		String[] sequence = keys.split(" ");
		for (int i = 0; i < sequence.length; i++) {
			String s = sequence[i];
			//if (processKeyCombination(s)) continue;
			if (processKeyChord(s)) continue;
			Keys key = convertToKeys(s);
			actions.sendKeys(key);
			//pause(KEY_PRESS_PAUSE_MIL_SEC);
		}
		actions.perform();
	}

	/*
	private boolean processKeyCombination(String s) {
		if (s.contains("+")) {
			String[] substrings = s.split("\\+");
			if (substrings.length < 2) return false; // like SHIFT+
			Actions action = new Actions(driver);
			action = pressModifiers(substrings, action);
			action = sendSpecialKey(substrings,action);
			action.build().perform();
			action = releaseModifiers(substrings,action);
			action.build().perform();
			return true;
		}
		return false;
	}
*/
	private boolean processKeyChord(String s) {
		if (s.contains("+")) {
			String[] substrings = s.split("\\+");
			if (substrings.length < 2) return false; // like SHIFT+
			//String keysToSend=Keys.chord(convertToKeys(substrings[0]),convertToCharSeq(substrings[1]));
			driver.switchTo().activeElement();
			Keys modifier = convertToKeys(substrings[0]);
			Keys key = convertToKeys(substrings[1]);
			//action.sendKeys(Keys.chord(convertToKeys(substrings[0]),convertToCharSeq(substrings[1])));
			//action.build();
			pressKey(modifier);
			this.actions.sendKeys(key);
			releaseKey(modifier);
			//action.keyDown(modifier).sendKeys(key).keyUp(modifier).perform();
			return true;
		}
		return false;
	}

	private Actions sendSpecialKey(String[] substrings, Actions action) {
		int lastElement = substrings.length-1;
		CharSequence chars = convertToCharSeq(substrings[lastElement]);
		action.sendKeys(chars);
		return action;
	}

	private CharSequence convertToCharSeq(String string) {
		try {
			for (Field field : keyboardKeys) {
				if (field.getName().equalsIgnoreCase(string)) {
					return (CharSequence) field.get(null);
				}
			}
			return Keys.NULL;
		} catch (Exception e) {
			throw new RuntimeException("Unable convert input string into a Key.", e);
		}
	}

	private Keys convertToKeys(String string) {
		try {
			for (Field field : keyboardKeys) {
				if (field.getName().equalsIgnoreCase(string)) {
					return (Keys) field.get(null);
				}
			}
			return Keys.NULL;
		} catch (Exception e) {
			throw new RuntimeException("Unable convert input string into a Key.", e);
		}
	}

}
