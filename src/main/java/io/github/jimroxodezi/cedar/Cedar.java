package io.github.jimroxodezi.cedar;

import java.util.Scanner;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "cedar", mixinStandardHelpOptions = true, version = "cedar 0.1.0",
        description = "scaffold an empty Maven Java project.")
public class Cedar implements Callable<Integer> {

    @Parameters(index = "0", arity = "0..1", description = "Artifact ID (also the folder name)")
    private String artifactId;

    @Option(names = {"-g", "--group"}, description = "the group ID of the artefact. eg com.example")
    private String groupId;

    @Option(names = {"-j", "--java"}, description = "the java version. eg 25")
    private String javaRelease;
    
    public static void main(String[] args) {
        System.exit(new CommandLine(new Cedar()).execute(args));
    }

    static String ask(Scanner in, String label, String def) {
        System.out.print(label + " [" + def + "]: ");
        String line = in.nextLine().trim();
        return line.isEmpty() ? def : line;
    }
    
    @Override
    public Integer call() throws Exception {
        Scanner input = new Scanner(System.in);
        if (artifactId == null) artifactId = ask(input, "Artifact ID", "myapp");
        if (groupId == null) groupId = ask(input, "Group ID", "com.example");
        if (javaRelease == null) javaRelease = ask(input, "Java Release", "25");
        return 1;
    }
}