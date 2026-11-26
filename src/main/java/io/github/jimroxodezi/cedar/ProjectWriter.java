package io.github.jimroxodezi.cedar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ProjectWriter {

    public void write(Path root, List<GeneratedFile> files) throws IOException {
        Path base = root.toAbsolutePath().normalize();
        for (GeneratedFile f : files) {
            Path target = base.resolve(f.path()).normalize();
            if (!target.startsWith(base)) {
                throw new IOException("Refusing to write outside the project: " + target);
            }
            Files.createDirectories(target.getParent());
            Files.writeString(target, f.content());
        }
    }
}