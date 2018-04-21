/**
 * Copyright (c) 2018, biezhi 王爵nice (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static java.lang.String.format;

/**
 * @author biezhi
 * @date 2018/4/21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validators {

    public static Validation<String> notNull = SimpleValidation.from(Objects::nonNull, "must not be null.");

    public static Validation<String> moreThan(int size) {
        return SimpleValidation.from((s) -> s.length() >= size, format("must have more than %s chars.", size));
    }

    public static Validation<String> lessThan(int size) {
        return SimpleValidation.from((s) -> s.length() <= size, format("must have less than %s chars.", size));
    }

    public static Validation<String> between(int minSize, int maxSize) {
        return moreThan(minSize).and(lessThan(maxSize));
    }

    public static Validation<String> contains(String c) {
        return SimpleValidation.from((s) -> s.contains(c), format("must contain %s", c));
    }

    public static Validation<Integer> lowerThan(int max) {
        return SimpleValidation.from((i) -> i < max, format("must be lower than %s.", max));
    }

    public static Validation<Integer> greaterThan(int min) {
        return SimpleValidation.from((i) -> i > min, format("must be greater than %s.", min));
    }

    public static Validation<Integer> intBetween(int min, int max) {
        return greaterThan(min).and(lowerThan(max));
    }

}
