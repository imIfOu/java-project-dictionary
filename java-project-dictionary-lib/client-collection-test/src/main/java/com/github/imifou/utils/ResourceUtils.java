package com.github.imifou.utils;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.Charset.defaultCharset;

public final class ResourceUtils {

    public static String loadResourceToString(String resourcePath) {
        try {
            InputStream inputStream = ResourceUtils.class.getClassLoader().getResourceAsStream(resourcePath);
            return inputStream != null ? new String(inputStream.readAllBytes(), defaultCharset()) : null;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
