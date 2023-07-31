package com.github.jaxing.service;

import com.github.jaxing.common.domain.UserInfo;
import com.github.jaxing.common.dto.RegisterRequestDTO;
import io.vertx.core.Future;


/**
 * @author cjxin
 * @date 2023/07/18
 */
public interface UserService {

    /**
     * 数据初始化
     */
    void init();

    /**
     * 注册
     */
    Future<Void> register(RegisterRequestDTO registerRequestDTO);

    /**
     * 查询用户信息
     *
     * @param uid 用户id
     * @return 用户信息
     */
    Future<UserInfo> findById(String uid);
}
