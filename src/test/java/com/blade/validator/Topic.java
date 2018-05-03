package com.blade.validator;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author biezhi
 * @date 2018/4/21
 */
@Data
public class Topic {

    private String        tid;
    private String        title;
    private String        content;
    private Long          nodeId;
    private String        email;
    private String        url;
    private Integer       range;
    private LocalDateTime created;

}
