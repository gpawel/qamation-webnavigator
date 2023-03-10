package org.qamation.mouse;

import org.openqa.selenium.Point;
import org.qamation.commons.utils.RegExpUtils;

public class MouseActionPatterns {
    public static final String MOUSE_ACTION_REGEXP = "<@([!\\?_~\\^]{1,})+.*>";//"<@([!]{1,2}|[\\?_~\\^]).*>";
    public static final String MOUSE_ACTION_COORDINATES_REGEXP = "<@.*\\[(\\d{1,},\\d{1,})\\]>";
    public static final String MOUSE_ACTION_BYSTRING_REGEXP = "<@.*\\{(.*)\\}>";


    public static String[] extractMouseAction(String navigationString) {
        return getAllFindings(navigationString, MOUSE_ACTION_REGEXP);
    }

    public static String[] extractBYString(String mouseNavigation) {
        return getAllFindings(mouseNavigation, MOUSE_ACTION_BYSTRING_REGEXP);
    }

    public static String[] extractCoordinates(String navigationString) {
        return getAllFindings(navigationString, MOUSE_ACTION_COORDINATES_REGEXP);
    }

    public static Point extractPoint(String mouseActionLine) {
        String[] coordinates = extractCoordinates(mouseActionLine);
        String[] point = coordinates[1].split(",");
        int x = Integer.parseInt(point[0]);
        int y = Integer.parseInt(point[1]);
        return new Point(x, y);
    }

    public static boolean isMouseAction(String str) {
        str = str.toLowerCase();
        if (str.startsWith("<@")) return true;
        if (
            str.contains("click")
            || str.contains("move")
            || str.contains("drag")
            || str.contains("release")
            ) return true;
        return false;
    }

    public static boolean hasMouseBYString(String input) {
        return doesMatch(input, MOUSE_ACTION_BYSTRING_REGEXP);
    }

    public static boolean hasMouseCoordinates(String input) {
        return doesMatch(input, MOUSE_ACTION_COORDINATES_REGEXP);
    }

    private static boolean doesMatch(String input, String regExp) {
        RegExpUtils utils = new RegExpUtils(input, regExp);
        return utils.isInputMatches();
    }

    private static String[] getAllFindings(String input, String regExp) {
        RegExpUtils utils = new RegExpUtils(input, regExp);
        return utils.getAllFindings();
    }
}
