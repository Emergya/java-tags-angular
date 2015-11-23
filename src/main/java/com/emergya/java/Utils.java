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
    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    public static final Charset LATIN1_CHARSET = Charset.forName("ISO-8859-1");
    public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private static final String UTF_8 = "UTF-8";

    private static final String LINE = System.getProperties().getProperty("line.separator");

    private static final Pattern CHARSET_PATTERN = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

    /**
     * Encodes an URL.
     *
     * @param url the url to be encoded.
     * @return The encoded url.
     */
    public static String encodeURL(final String url) {
        try {
            return URLEncoder.encode(url, UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Reads an InputStream's content as a string.
     *
     * @param is The input stream to be read.
     * @param charsetName The charset the contents of the InputStream are in.
     * @param initDelimiter If not null, used to mark the start of the content to be read.
     * @param endDelimiter If not null, used to trim the end of the read content.
     * @return The read content.
     * @throws IOException If there is an error reading the file at some point.
     */
    public static String readContentAsString(final InputStream is, final String charsetName, final String initDelimiter,
            final String endDelimiter) throws IOException {
        final int initDelimiterLength = StringUtils.isEmpty(initDelimiter) ? 0 : initDelimiter.length();
        final boolean hasEndDelimiter = !StringUtils.isEmpty(endDelimiter);
        final BufferedReader br = new BufferedReader(new InputStreamReader(is, charsetName));
        final StringBuilder sb = new StringBuilder();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (initDelimiterLength == 0) {
                sb.append(line);
            } else if (sb.length() > 0) {
                sb.append(line);
            } else {
                final int initIndex = line.indexOf(initDelimiter);
                if (initIndex > -1 && line.length() > initDelimiterLength) {
                    sb.append(line.substring(initIndex + initDelimiterLength));
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

    /**
     * Returns a charset name from a content type.
     *
     * @param contentType The content type to be parsed.
     * @return The charset's name, or latin1 ("ISO-8859-1") if no charset info is included in the contentType argument.
     */
    public static String charsetNameFromContentType(final String contentType) {
        final Matcher m = CHARSET_PATTERN.matcher(contentType);
        if (m.find()) {
            final String cs = m.group(1).trim().toUpperCase(Locale.UK);
            return cs;
        }
        // return DEFAULT_CHARSET.name();
        return LATIN1_CHARSET.name();
    }

    /**
     * Reads a classpath resource as a byte array (binary read).
     *
     * @param path the classpath resources' path.
     * @return The read content.
     * @throws IOException If there is a read error during access to the resource.
     */
    public static byte[] readClassPathResource(String path) throws IOException {
        // The user has no image, we need to load the 'no image' image.
        final ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            throw new IllegalArgumentException("The supplied path is not a valid classpath pointing to a file!");
        }

        final byte[] data = new byte[(int) resource.contentLength()];
        if (resource.getInputStream().read(data) != resource.contentLength()) {
            throw new RuntimeException("Error reading resource file " + path);
        }
        return data;
    }

    /**
     * Reads a classpath resource as a String (text read).
     *
     * @param path the classpath resources' path.
     * @return The read content.
     */
    public static String readClassPathResourceAsString(String path) {
        final ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            throw new IllegalArgumentException("The supplied path is not a valid classpath pointing to a file!");
        }

        Scanner scanner;
        try {
            scanner = new Scanner(resource.getInputStream(), "UTF-8").useDelimiter("\\A");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return scanner.next();
    }

    /**
     * Returns the name for a field given an accesor method name.
     *
     * @param method The method's name.
     * @return The field's name.
     */
    public static String getFieldName(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("get")) {
            methodName = methodName.substring("get".length());
        } else if (methodName.startsWith("is")) {
            methodName = methodName.substring("is".length());
        } else {
            throw new IllegalArgumentException("Not supported accesor prefix in method " + methodName);
        }

        methodName = methodName.substring(0, 1).toLowerCase(Locale.UK) + methodName.substring(1);
        return methodName;
    }

    /**
     * Returns if the string is empty.
     *
     * @deprecated Just use StringUtils.isEmpty.
     * @param str The string to be checked.
     * @return true if the string is empty.
     */
    @Deprecated()
    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }
}
