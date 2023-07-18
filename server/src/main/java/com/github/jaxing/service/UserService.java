package com.github.jaxing.service;

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
}
