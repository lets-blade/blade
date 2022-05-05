package com.hellokaton.blade.security.limit;

import lombok.ToString;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class LimitExpression {

    private static final Pattern EXP_PATTERN = Pattern.compile("^(\\d+)/(\\d*)([s\\|m\\|h])((/warmup)?)$");
    private static final Map<String, BigDecimal> TIME_TO_SECONDS = new HashMap<>(3);

    static {
        TIME_TO_SECONDS.put("s", BigDecimal.ONE);
        TIME_TO_SECONDS.put("m", BigDecimal.valueOf(60L));
        TIME_TO_SECONDS.put("h", BigDecimal.valueOf(3600L));
    }

    @ToString
    static class Limiter {
        double permitsPerSecond;
        long warmupPeriod;
    }

    public static Limiter match(String expression) {
        Matcher matcher = EXP_PATTERN.matcher(expression);
        if (!matcher.matches()) {
            return null;
        }
        int count = Integer.parseInt(matcher.group(1));
        String period = matcher.group(2);
        String unit = matcher.group(3);
        String mode = matcher.group(4);
        if (!TIME_TO_SECONDS.containsKey(unit)) {
            return null;
        }
        if (null == period || period.isEmpty()) {
            period = "1";
        }

        Limiter limiter = new Limiter();
        if (null != mode && !mode.isEmpty()) {
            limiter.permitsPerSecond = count;
            limiter.warmupPeriod = TIME_TO_SECONDS.get(unit).multiply(new BigDecimal(period)).longValue();
        } else {
            limiter.permitsPerSecond = new BigDecimal(count).divide(TIME_TO_SECONDS.get(unit), RoundingMode.HALF_DOWN).doubleValue();
        }
        return limiter;
    }

    public static void main(String[] args) {
        System.out.println(LimitExpression.match("5/1s/warmup"));
        System.out.println(LimitExpression.match("5/3s"));
    }

}
