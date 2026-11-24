package io.github.jimroxodezi.cedar;

public record ProjectSpec(ProjectType type, String artifact, String groupId,
    String pkg, String javaRelease) { }