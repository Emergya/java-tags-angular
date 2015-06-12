package com.emergya.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 * Utility methods for the analyzer package.
 *
 * All methods in this class are thread safe.
 *
 */
public abstract class Utils {

    public static final Collator COLLATOR_ES = Collator.getInstance(new Locale("es", "ES"));

    private static final String UTF_8 = "UTF-8";
    public static final Charset UTF8_CHARSET = Charset.forName(UTF_8);
    public static final Charset LATIN1_CHARSET = Charset.forName("ISO-8859-1");
    public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private static final String LINE = System.getProperties().getProperty("line.separator");

    private static final Pattern CHARSET_PATTERN = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

    public static String encodeURL(final String url) {
        try {
            return URLEncoder.encode(url, UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String readContentAsString(final InputStream is, final String charsetName, final String initDelimiter,
            final String endDelimiter) throws IOException {
        final int initDelimiterLength = StringUtils.isEmpty(initDelimiter) ? 0 : initDelimiter.length();
        final boolean hasEndDelimiter = !StringUtils.isEmpty(endDelimiter);
        final BufferedReader br = new BufferedReader(new InputStreamReader(is, charsetName));
        final StringBuilder sb = new StringBuilder();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (initDelimiterLength == 0) {
                sb.append(line);
            } else {
                if (sb.length() > 0) {
                    sb.append(line);
                } else {
                    final int initIndex = line.indexOf(initDelimiter);
                    if (initIndex > -1 && line.length() > initDelimiterLength) {
                        sb.append(line.substring(initIndex + initDelimiterLength));
                    }
                }
            }
            if (sb.length() > 0) {
                sb.append(LINE);
            }
            if (hasEndDelimiter && sb.length() > 0) {
                final int endIndex = sb.indexOf(endDelimiter);
                if (endIndex > 0) {
                    sb.substring(0, endIndex);
                    break;
                }
            }
        }
        br.close();
        return sb.toString();
    }

    public static String charsetNameFromContentType(final String contentType) {
        final Matcher m = CHARSET_PATTERN.matcher(contentType);
        if (m.find()) {
            final String cs = m.group(1).trim().toUpperCase();
            return cs;
        }
        // return DEFAULT_CHARSET.name();
        return LATIN1_CHARSET.name();
    }

    public static byte[] readClassPathResource(String path) throws IOException {
        // The user has no image, we need to load the 'no image' image.
        final ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            throw new IllegalArgumentException("The supplied path is not a valid classpath pointing to a file!");
        }

        final byte[] data = new byte[(int) resource.contentLength()];
        resource.getInputStream().read(data);
        return data;
    }

    public static String readClassPathResourceAsString(String path) throws IOException {
        final ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            throw new IllegalArgumentException("The supplied path is not a valid classpath pointing to a file!");
        }

        Scanner scanner = new Scanner(resource.getInputStream()).useDelimiter("\\A");
        return scanner.next();
    }

    public static String getFieldName(Method method, String providedName) {
        if (!StringUtils.isEmpty(providedName)) {
            return providedName;
        }

        String methodName = method.getName();
        if (methodName.startsWith("get")) {
            methodName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            methodName = methodName.substring(2);
        } else {
            throw new IllegalArgumentException("Not supported accesor prefix in method " + methodName);
        }

        providedName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
        return providedName;
    }

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }
}
