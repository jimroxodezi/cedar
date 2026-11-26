package io.github.jimroxodezi.cedar;

import java.util.Scanner;

public final class Prompter {

    private final Scanner in = new Scanner(System.in);
    private final boolean acceptDefaults;

    public Prompter(boolean acceptDefaults) {
        this.acceptDefaults = acceptDefaults;
    }

    public String ask(String label, String defaultValue) {
        if (acceptDefaults) {
            return defaultValue;
        }
        System.out.print(label + " [" + defaultValue + "]: ");
        System.out.flush();
        if (!in.hasNextLine()) {
            System.out.println();
            throw new IllegalStateException(
                    "No input available for '" + label + "'. Pass it as a flag, or use --yes for defaults.");
        }
        String line = in.nextLine().trim();
        return line.isEmpty() ? defaultValue : line;
    }
}