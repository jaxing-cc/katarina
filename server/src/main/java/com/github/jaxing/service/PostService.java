package com.github.jaxing.service;

import com.github.jaxing.common.dto.post.PostSaveDTO;
import com.github.jaxing.common.dto.post.PostUpdateDTO;
import io.vertx.core.Future;

/**
 * @author cjxin
 * @date 2023/07/18
 */
public interface PostService {

    /**
     * 保存
     *
     * @param uid 用户id
     */
    Future<String> save(PostSaveDTO saveDTO, String uid);


    /**
     * 更新
     */
    Future<Void> update(PostUpdateDTO updateDTO);

}
