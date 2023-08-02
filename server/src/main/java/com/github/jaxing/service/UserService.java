package com.github.jaxing.service;

import com.github.jaxing.common.domain.UserInfo;
import com.github.jaxing.common.dto.RegisterRequestDTO;
import io.vertx.core.Future;

import java.util.List;


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
     * id查询用户信息
     *
     * @param uid 用户id
     * @return 用户信息
     */
    Future<UserInfo> findById(String uid);

    /**
     * 搜索用户
     *
     * @param key 用户名/名字
     * @return 用户信息
     */
    Future<List<UserInfo>> searchUser(String key);
}
