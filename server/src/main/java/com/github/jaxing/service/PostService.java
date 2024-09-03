package com.github.jaxing.service;

import com.github.jaxing.common.dto.post.PostSaveDTO;
import com.github.jaxing.common.dto.post.PostUpdateDTO;
import com.github.jaxing.common.dto.post.PostVO;
import io.vertx.core.Future;

import java.util.List;

/**
 * @author cjxin
 * @date 2023/07/18
 */
public interface PostService {

    /**
     * 查询动态详情
     *
     * @return 动态
     */
    Future<PostVO> findById(String id, String uid);

    /**
     * 搜索
     *  @param uid
     * @param value 字符串
     * @param follow
     * @param page  页码
     */
    Future<List<PostVO>> search(String uid, String value, Boolean follow, Integer page);

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
