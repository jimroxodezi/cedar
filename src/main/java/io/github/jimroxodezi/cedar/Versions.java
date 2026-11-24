package io.github.jimroxodezi.cedar;


public record Versions(String compilePlugin, String surefirePlugin, String junit) {

    /** Offline fallback */
    public static Versions bundled() {
        return new Versions("3.14.0", "3.5.2", "5.11.4");
    }
}