package com.github.jaxing.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfflineMessageCountVO {
    private String userId;

    private Integer count;
}
