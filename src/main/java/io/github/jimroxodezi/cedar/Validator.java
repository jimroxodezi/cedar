package io.github.jimroxodezi.cedar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class Validator {

    private static final Pattern PACKAGE = Pattern.compile("[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*");
    private static final Pattern GROUP = Pattern.compile("[A-Za-z0-9_][A-Za-z0-9_.-]*");
    private static final Pattern ARTIFACT = Pattern.compile("[A-Za-z0-9][A-Za-z0-9_.-]*");

    private static final Set<String> KEYWORDS = Set.of(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "void", "volatile", "while", "true", "false",
            "null", "_");

    private Validator() { }

    public static List<String> validate(String artifact, String group, String pkg, String release) {
        List<String> errors = new ArrayList<>();
        if (!ARTIFACT.matcher(artifact).matches()) {
            errors.add("Invalid artifact ID '" + artifact + "' (letters, digits, '.', '_' and '-' only)");
        }
        if (!GROUP.matcher(group).matches()) {
            errors.add("Invalid group ID '" + group + "'");
        }
        if (!PACKAGE.matcher(pkg).matches()) {
            errors.add("Invalid package '" + pkg + "' (lowercase letters, digits, '_' and dots; use --package to set it)");
        } else {
            for (String part : pkg.split("\\.")) {
                if (KEYWORDS.contains(part)) {
                    errors.add("Package segment '" + part + "' is a Java keyword");
                }
            }
        }
        if (!release.matches("\\d{1,3}") || Integer.parseInt(release) < 8) {
            errors.add("Java release must be a number of 8 or more, got '" + release + "'");
        }
        return errors;
    }
}