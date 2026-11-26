// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Jimrox Odezi

package io.github.jimroxodezi.cedar;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TemplateEngine {
    /** PLACEHOLDER is a regex defining placeholders in template files in this format: {{abcd}} */
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    /**
    * 
    * @param template   the original template content containing placeholders loaded as String.
    * @param values
    * @return
 */
    public String render(String template, Map<String,String> values) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String value = values.get(matcher.group(1));
            if (value == null) {
                throw new IllegalArgumentException("Missing template value: " + matcher.group(1));
            }
            matcher.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    /**
    * 
    * @param resourcePath
    * @return resource content as String
 */
    public static String load(String resourcePath) {
        // Path or File will fail here. this is a resource packages inside the jar.
        try (InputStream in = TemplateEngine.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource: " + resourcePath);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}