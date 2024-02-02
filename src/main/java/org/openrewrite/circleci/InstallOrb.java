/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.circleci;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.yaml.*;
import org.openrewrite.yaml.tree.Yaml;

import java.time.Duration;

@Value
@EqualsAndHashCode(callSuper = false)
public class InstallOrb extends Recipe {
    @Option(displayName = "Orb key",
            description = "The orb key to be followed by an orb slug identifying a specific orb version.",
            example = "kube")
    String orbKey;

    @Option(displayName = "Slug",
            description = "A specific orb to install, in the form `<namespace>/<orb-name>@1.2.3`.",
            example = "circleci/kubernetes@0.11.0")
    String slug;

    @Override
    public String getDisplayName() {
        return "Install an orb";
    }

    @Override
    public String getDescription() {
        return "Install a CircleCI [orb](https://circleci.com/docs/2.0/orb-intro/) if it is not already installed.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        JsonPathMatcher orbs = new JsonPathMatcher("$.orbs");
        return Preconditions.check(new FindSourceFiles(".circleci/config.yml"), new YamlIsoVisitor<ExecutionContext>() {
            @Override
            public Yaml.Document visitDocument(Yaml.Document document, ExecutionContext ctx) {
                Yaml.Document d = super.visitDocument(document, ctx);
                if (!orbs.find(getCursor()).isPresent() || Boolean.TRUE.equals(getCursor().getMessage("INSERT_ORB"))) {
                    doAfterVisit(new MergeYamlVisitor<>(document.getBlock(), "" +
                            "orbs:\n" +
                            "  " + orbKey + ": " + slug,
                            false, null));
                }
                return d;
            }

            @Override
            public Yaml.Mapping visitMapping(Yaml.Mapping mapping, ExecutionContext ctx) {
                if (orbs.matches(getCursor().getParentOrThrow())) {
                    for (Yaml.Mapping.Entry entry : mapping.getEntries()) {
                        if (entry.getValue() instanceof Yaml.Scalar) {
                            String existingSlug = ((Yaml.Scalar) entry.getValue()).getValue();
                            if (slug.split("@")[0].equals(existingSlug.split("@")[0])) {
                                return mapping;
                            }
                        }
                    }

                    getCursor().putMessageOnFirstEnclosing(Yaml.Document.class, "INSERT_ORB", true);
                }

                return super.visitMapping(mapping, ctx);
            }
        });
    }
}
