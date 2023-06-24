package com.github.jaxing.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @JsonProperty("_id")
    private String id;

    private String RoleKey;

    private String roleName;

    private String roleDesc;
}
