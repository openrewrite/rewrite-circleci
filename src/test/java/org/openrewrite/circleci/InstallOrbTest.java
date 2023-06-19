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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.nio.file.Path;

import static org.openrewrite.yaml.Assertions.yaml;

class InstallOrbTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new InstallOrb("java", "circleci/openjdk:jdk"));
    }

    @DocumentExample
    @Test
    void installNewOrb(@TempDir Path tempDir) {
        rewriteRun(
          yaml(
            """
              version: 2.1
              orbs:
                node: circleci/node@1.0
              """,
            """
                version: 2.1
                orbs:
                  node: circleci/node@1.0
                  java: circleci/openjdk:jdk
              """,
            spec -> spec.path(".circleci/config.yml"))
        );
    }

    @Test
    void installFirstOrb(@TempDir Path tempDir) {
        rewriteRun(
          yaml("""
                  version: 2.1
              """,
            """
                  version: 2.1
                  orbs:
                    java: circleci/openjdk:jdk
              """,
            spec -> spec.path(".circleci/config.yml")
          )
        );
    }
}
