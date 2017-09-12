package com.blade.kit.json;

import lombok.Builder;
import lombok.Data;

/**
 * @author biezhi
 * @date 2017/9/12
 */
@Data
@Builder
public class SerializeMapping {

    @Builder.Default
    private String datePatten     = "yyyy-MM-dd";
    @Builder.Default
    private int    bigDecimalKeep = 2;

    private static final SerializeMapping instance = SerializeMapping.builder().build();

    public static SerializeMapping defaultMapping() {
        return instance;
    }

}
