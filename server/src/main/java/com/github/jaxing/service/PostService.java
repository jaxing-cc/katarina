package com.github.jaxing.service;

import com.github.jaxing.common.dto.post.PostSaveDTO;
import com.github.jaxing.common.dto.post.PostUpdateDTO;
import com.github.jaxing.common.dto.post.PostVO;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * @author cjxin
 * @date 2023/07/18
 */
public interface PostService {

    /**
     * 搜索
     * @param value 字符串
     * @param page 页码
     */
    Future<List<PostVO>> search(String value, Integer page);

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
