package com.github.jaxing.common.domain;

import lombok.Data;

@Data
public class ThumbupRecord {

    private String id;

    private String uid;

    private String targetId;

    private String targetType;

    private Long createTime;

}
