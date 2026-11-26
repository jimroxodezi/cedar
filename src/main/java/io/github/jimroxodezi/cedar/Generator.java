package io.github.jimroxodezi.cedar;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Generator {
    private final TemplateEngine templateEngine = new TemplateEngine();

    public List<GeneratedFile> generate(ProjectSpec spec, Versions versions) {
        Map<String,String> values = Map.of(
            "group", spec.groupId(),
            "artifact", spec.artifact(),
            "package", spec.pkg(),
            "javaRelease", spec.javaRelease(),
            "conpilerPluginVersion", versions.compilePlugin(),
            "surefirePluginversion", versions.surefirePlugin(),
            "junitVersion", versions.junit()
        );

        String pkgPath = spec.pkg().replace('.', '/');
        boolean app = spec.type() == ProjectType.APP;
        String folder = app ? "app" : "library";
        String main = app ? "App" : "Library";

        List<GeneratedFile> files = new ArrayList<>();
        files.add(file("pom.xml", "templates/common/pom.xml", values));
        files.add(file(".gitignore", "/templates/common/gitignore.tpl", values));
        files.add(file("src/main/java/" + pkgPath + "/" + main + ".java",
                        "/templates/" + folder + "/" + main + ".java.tpl", values));
        files.add(file("src/test/java/" + pkgPath + "/" + main + "Test.java",
                        "/templates/" + folder + "/" + main + "Test.java.tpl", values));
        return files;

        
    }

    private GeneratedFile file(String path, String template, Map<String, String> values) {
        return new GeneratedFile(Path.of(path), templateEngine.render(TemplateEngine.load(template), values));
    }
}