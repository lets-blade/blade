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

import com.blade.kit.PatternKit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Validators
 *
 * @author biezhi
 * @date 2018/4/21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validators {

    private static final Map<String, String> I18N_MAP = new HashMap<>();

    private static String i18nPrefix = "EN_";

    static {
        I18N_MAP.put("CN_NOT_NULL", "不允许为 NULL");
        I18N_MAP.put("CN_NOT_EMPTY", "不允许为空");
        I18N_MAP.put("CN_MORE_THAN", "必须大于等于 %s 个字符");
        I18N_MAP.put("CN_LESS_THAN", "必须小于等于 %s 个字符");
        I18N_MAP.put("CN_CONTAINS", "必须包含 %s 字符");
        I18N_MAP.put("CN_LOWER_THAN", "必须小于 %s 个字符");
        I18N_MAP.put("CN_GREATER_THAN", "必须大于 %s 个字符");
        I18N_MAP.put("CN_IS_EMAIL", "不是一个合法的邮箱");
        I18N_MAP.put("CN_IS_URL", "不是一个合法的URL");

        I18N_MAP.put("EN_NOT_NULL", "must not be null.");
        I18N_MAP.put("EN_NOT_EMPTY", "must not be empty.");
        I18N_MAP.put("EN_MORE_THAN", "must have more than %s chars.");
        I18N_MAP.put("EN_LESS_THAN", "must have less than %s chars.");
        I18N_MAP.put("EN_CONTAINS", "must contain %s");
        I18N_MAP.put("EN_LOWER_THAN", "must be lower than %s.");
        I18N_MAP.put("EN_GREATER_THAN", "must be greater than %s.");
        I18N_MAP.put("EN_IS_EMAIL", "must be a email.");
        I18N_MAP.put("EN_IS_URL", "must be a url.");
    }

    public static void useChinese() {
        i18nPrefix = "CN_";
    }

    public static <T> Validation<T> notNull() {
        return notNull(I18N_MAP.get(i18nPrefix + "NOT_NULL"));
    }

    public static <T> Validation<T> notNull(String msg) {
        return SimpleValidation.from(Objects::nonNull, msg);
    }

    public static Validation<String> notEmpty() {
        return notEmpty(I18N_MAP.get(i18nPrefix + "NOT_EMPTY"));
    }

    public static Validation<String> notEmpty(String msg) {
        return SimpleValidation.from(s -> null != s && !s.isEmpty(), msg);
    }

    public static Validation<String> moreThan(int size) {
        return notEmpty().and(moreThan(size, I18N_MAP.get(i18nPrefix + "MORE_THAN")));
    }

    public static Validation<String> moreThan(int size, String msg) {
        return notEmpty().and(SimpleValidation.from((s) -> s.length() >= size, format(msg, size)));
    }

    public static Validation<String> lessThan(int size) {
        return lessThan(size, I18N_MAP.get(i18nPrefix + "LESS_THAN"));
    }

    public static Validation<String> lessThan(int size, String msg) {
        return notEmpty().and(SimpleValidation.from((s) -> s.length() <= size, format(msg, size)));
    }

    public static Validation<String> between(int minSize, int maxSize) {
        return moreThan(minSize).and(lessThan(maxSize));
    }

    public static Validation<String> contains(String c) {
        return contains(c, I18N_MAP.get(i18nPrefix + "CONTAINS"));
    }

    public static Validation<String> contains(String c, String msg) {
        return notEmpty().and(SimpleValidation.from((s) -> s.contains(c), format(msg, c)));
    }

    public static Validation<Integer> lowerThan(int max) {
        return lowerThan(max, I18N_MAP.get(i18nPrefix + "LOWER_THAN"));
    }

    public static Validation<Integer> lowerThan(int max, String msg) {
        return SimpleValidation.from((i) -> i < max, format(msg, max));
    }

    public static Validation<Integer> greaterThan(int min) {
        return greaterThan(min, I18N_MAP.get(i18nPrefix + "GREATER_THAN"));
    }

    public static Validation<Integer> greaterThan(int min, String msg) {
        return SimpleValidation.from((i) -> i > min, format(msg, min));
    }

    public static Validation<Integer> range(int min, int max) {
        return greaterThan(min).and(lowerThan(max));
    }

    public static Validation<String> isEmail() {
        return isEmail(I18N_MAP.get(i18nPrefix + "IS_EMAIL"));
    }

    public static Validation<String> isEmail(String msg) {
        return notEmpty().and(SimpleValidation.from(PatternKit::isEmail, msg));
    }

    public static Validation<String> isURL() {
        return isURL(I18N_MAP.get(i18nPrefix + "IS_URL"));
    }

    public static Validation<String> isURL(String msg) {
        return notEmpty().and(SimpleValidation.from(PatternKit::isURL, msg));
    }

}
