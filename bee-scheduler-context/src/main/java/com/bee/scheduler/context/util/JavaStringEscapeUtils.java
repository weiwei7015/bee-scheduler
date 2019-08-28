package com.bee.scheduler.context.util;

import org.apache.commons.text.translate.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JavaStringEscapeUtils {
    private static final CharSequenceTranslator ESCAPE_JAVA;

    static {
        final Map<CharSequence, CharSequence> escapeJavaMap = new HashMap<>();
        escapeJavaMap.put("\"", "\\\"");
        escapeJavaMap.put("\\", "\\\\");
        ESCAPE_JAVA = new AggregateTranslator(
                new LookupTranslator(Collections.unmodifiableMap(escapeJavaMap)),
                new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE)
        );
    }

    private static final CharSequenceTranslator UNESCAPE_JAVA;

    static {
        final Map<CharSequence, CharSequence> unescapeJavaMap = new HashMap<>();
        unescapeJavaMap.put("\\\\", "\\");
        unescapeJavaMap.put("\\\"", "\"");
        unescapeJavaMap.put("\\'", "'");
        unescapeJavaMap.put("\\", "");
        UNESCAPE_JAVA = new AggregateTranslator(
                new OctalUnescaper(),     // .between('\1', '\377'),
                new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_UNESCAPE),
                new LookupTranslator(Collections.unmodifiableMap(unescapeJavaMap))
        );
    }

    public static String escape(final String input) {
        return ESCAPE_JAVA.translate(input);
    }

    public static String unescape(final String input) {
        return UNESCAPE_JAVA.translate(input);
    }
}
