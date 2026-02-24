package dev.afonso.galleryac.util;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorize(String text) {
        if (text == null) return "";

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            StringBuilder magic = new StringBuilder("§x");
            for (char c : hexCode.toCharArray()) {
                magic.append('§').append(c);
            }
            matcher.appendReplacement(buffer, magic.toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static String strip(String text) {
        if (text == null) return "";
        return text.replaceAll("&#[0-9A-Fa-f]{6}", "")
                   .replaceAll("&[0-9a-fk-or]", "");
    }
}