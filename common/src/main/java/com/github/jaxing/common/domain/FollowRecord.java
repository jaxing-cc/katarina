package com.github.jaxing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author cjxin
 * @date 2023/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowRecord implements Serializable {

    private String followerUid;

    private String targetUid;

    private Long createTime;
}
