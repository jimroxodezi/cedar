package io.github.jimroxodezi.cedar;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Command(name = "cedar", mixinStandardHelpOptions = true, version = "cedar 0.1.0",
         description = "Generate a ready-to-build Maven project, like `gradle init`.")
public class Cedar implements Callable<Integer> {

    @Parameters(index = "0", arity = "0..1", description = "Artifact ID (also the default folder name)")
    String artifact;

    @Option(names = {"-t", "--type"},
            description = "Project type: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE})")
    ProjectType type = ProjectType.APP;

    @Option(names = {"-g", "--group"}, description = "Group ID, e.g. com.example")
    String group;

    @Option(names = {"-p", "--package"}, description = "Java package (default: the group ID)")
    String pkg;

    @Option(names = {"-j", "--java"}, description = "Java release, e.g. 25")
    String java;

    @Option(names = "--into", description = "Directory to generate into (default: ./<artifact>)")
    Path into;

    @Option(names = "--overwrite", description = "Allow generating into a non-empty directory")
    boolean overwrite;

    @Option(names = "--dry-run", description = "Print the files that would be created, write nothing")
    boolean dryRun;

    @Option(names = {"-y", "--yes"}, description = "Accept defaults instead of asking questions")
    boolean yes;

    public static void main(String[] args) {
        int exit = new CommandLine(new Cedar())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
        System.exit(exit);
    }

    @Override
    public Integer call() {
        try {
            Prompter prompter = new Prompter(yes);
            String artifactId = artifact != null ? artifact : prompter.ask("Artifact ID", "myapp");
            String groupId = group != null ? group : prompter.ask("Group ID", "com.example");
            String release = java != null ? java : prompter.ask("Java release", defaultJavaRelease());
            String packageName = pkg != null ? pkg : groupId;

            List<String> errors = Validator.validate(artifactId, groupId, packageName, release);
            if (!errors.isEmpty()) {
                errors.forEach(e -> System.err.println("error: " + e));
                return 1;
            }

            Path root = into != null ? into : Path.of(artifactId);
            if (!overwrite && isNonEmptyDirectory(root)) {
                System.err.println("error: " + root + " is not empty (use --overwrite to continue)");
                return 1;
            }

            ProjectSpec spec = new ProjectSpec(type, artifactId, groupId, packageName, release);
            List<GeneratedFile> files = new Generator().generate(spec, Versions.bundled());

            if (dryRun) {
                files.forEach(f -> System.out.println(root.resolve(f.path())));
                return 0;
            }

            new ProjectWriter().write(root, files);
            System.out.println("Created " + root.toAbsolutePath());
            System.out.println("Next: cd " + root + " && mvn verify");
            if (type == ProjectType.APP) {
                System.out.println("Run:  java -cp target/classes " + packageName + ".App");
            }
            return 0;
        } catch (IllegalStateException e) {
            System.err.println("error: " + e.getMessage());
            return 2;
        } catch (IOException e) {
            System.err.println("error: " + e.getMessage());
            return 2;
        }
    }

    /** Placeholder: this is the JVM running Cedar, not necessarily the JDK Maven uses. */
    static String defaultJavaRelease() {
        return String.valueOf(Runtime.version().feature());
    }

    private static boolean isNonEmptyDirectory(Path p) throws IOException {
        if (!Files.exists(p)) return false;
        if (!Files.isDirectory(p)) return true;
        try (Stream<Path> entries = Files.list(p)) {
            return entries.findAny().isPresent();
        }
    }
}