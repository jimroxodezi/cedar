package io.github.jimroxodezi.cedar;

import java.nio.file.Files;
import java.nio.file.Path;
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

    static final String POM = """
               <project xmlns="http://maven.apache.org/POM/4.0.0">
                 <modelVersion>4.0.0</modelVersion>
                 <groupId>%s</groupId>
                 <artifactId>%s</artifactId>
                 <version>1.0</version>
                 <properties>
                   <maven.compiler.release>%s</maven.compiler.release>
                   <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                 </properties>
                 <build>
                   <plugins>
                     <plugin>
                       <groupId>org.apache.maven.plugins</groupId>
                       <artifactId>maven-compiler-plugin</artifactId>
                       <version>3.14.0</version>
                     </plugin>
                   </plugins>
                 </build>
               </project>
               """;

    static final String APP = """
                package %s;
    
                public class App {
                    public static void main(String[] args) {
                        System.out.println("hello from %s");
                    }
                }
                """;
    
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

        if (!groupId.matches("[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*")) {
            System.err.println("Invalid group ID! (Use a lowercase package name): " + groupId);
            return 1;
        }

        Path root = Path.of(artifactId);
        if (Files.exists(root)) {
            System.err.println("Already exists: " + root);
            return 1;
        }

        Path packageDir = root.resolve("src/main/java").resolve(groupId.replace(".", "/"));
        Files.createDirectories(packageDir);
        Files.createDirectories(root.resolve("src/test/java"));

        Files.writeString(root.resolve("pom.xml"), POM.formatted(groupId, artifactId, javaRelease));
        Files.writeString(packageDir.resolve("App.java"), APP.formatted(groupId, artifactId));
        Files.writeString(root.resolve(".gitignore"), "target/\n");

        System.out.println("Created " + root.toAbsolutePath());
        System.out.println("Next: cd " + artifactId + " && mvn compile && java -cp target/classes " + groupId + ".App");
        return 0;
    }
}