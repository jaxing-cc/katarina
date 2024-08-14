package com.github.jaxing.common.dto.post;

import com.github.jaxing.common.domain.UserInfo;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVO {
    /**
     * 文章id
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片
     */
    private String[] images;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 用户信息
     */
    private UserInfo user;

    public PostVO(JsonObject jsonObject) {
        this.id = jsonObject.getString("id");
        this.title = jsonObject.getString("title");
        this.content = jsonObject.getString("content");
        this.state = jsonObject.getInteger("state");
        this.images = jsonObject.getJsonArray("images").stream().toArray(String[]::new);
        this.createTime = jsonObject.getLong("createTime");
        this.updateTime = jsonObject.getLong("updateTime");
        this.user = new UserInfo();
        this.user.setId(jsonObject.getString("uid"));
        this.user.setName(jsonObject.getJsonObject("user").getString("name"));
        this.user.setUsername(jsonObject.getJsonObject("user").getString("username"));
        this.user.setAvatar(jsonObject.getJsonObject("user").getString("avatar"));
        this.user.setGender(jsonObject.getJsonObject("user").getInteger("gender"));
    }
}
