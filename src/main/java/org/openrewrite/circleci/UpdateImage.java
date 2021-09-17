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
import lombok.Getter;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.yaml.ChangeValue;

@EqualsAndHashCode(callSuper = false)
@Getter
public class UpdateImage extends Recipe {
    @Option(displayName = "Image",
            description = "Image to use.",
            example = "circleci/openjdk:jdk")
    private final String image;

    public UpdateImage(String image) {
        this.image = image;
        doNext(new ChangeValue("$.jobs.build.machine.image",
                image,
                ".circleci/config.yml")
        );
    }

    @Override
    public String getDisplayName() {
        return "Update CircleCI image";
    }

    @Override
    public String getDescription() {
        return "See the list of [pre-built CircleCI images](https://circleci.com/docs/2.0/circleci-images/).";
    }
}
