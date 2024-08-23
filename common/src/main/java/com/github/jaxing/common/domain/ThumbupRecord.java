package com.github.jaxing.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ThumbupRecord {

    @JsonProperty("_id")
    private String id;

    private String uid;

    private String targetId;

    private String targetType;

    private Long createTime;

}
