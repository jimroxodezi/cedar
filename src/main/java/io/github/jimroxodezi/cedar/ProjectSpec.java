// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Jimrox Odezi

package io.github.jimroxodezi.cedar;

public record ProjectSpec(ProjectType type, String artifact, String groupId,
    String pkg, String javaRelease) { }