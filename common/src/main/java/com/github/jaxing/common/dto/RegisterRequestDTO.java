package com.github.jaxing.common.dto;

import lombok.Data;

/**
 * @author cjxin
 * @date 2023/07/18
 */
@Data
public class RegisterRequestDTO {

    private String username;

    private String password;

    private String name;

    private Integer gender;
}
