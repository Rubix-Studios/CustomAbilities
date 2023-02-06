package nl.rubixstudios.partneritems.util;

import java.util.stream.IntStream;

/**
 * @author Djorr
 * @created 23/11/2022 - 10:07
 * @project PartnerItems
 */
public class StringUtil {

    public static String center(String value, int maxLength) {
        StringBuilder builder = new StringBuilder(maxLength - value.length());
        IntStream.range(0, maxLength - value.length()).forEach(i -> builder.append(" "));

        builder.insert((builder.length() / 2) + 1, value);
        return builder.toString();
    }
}
