package com.github.jaxing.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Data
public class UserInfo implements Serializable {

    @JsonProperty("_id")
    private String id;

    private String name;

    private String username;

    private String password;

    private String avatar;

    private String email;

    private Integer gender;

    private List<String> roles;

    private boolean online;

    private Boolean disable = Boolean.FALSE;
}
