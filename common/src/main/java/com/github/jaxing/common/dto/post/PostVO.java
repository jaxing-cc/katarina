package com.github.jaxing.common.dto.post;

import com.github.jaxing.common.domain.Client;
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
     * 用户id
     */
    private String uid;

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

    /**
     * 点赞数
     */
    private Integer thumbupCount;

    /**
     * 是否点赞过
     */
    private boolean thumbuped;

    public PostVO(JsonObject jsonObject) {
        if (jsonObject == null){
            return;
        }
        this.id = jsonObject.getString("id");
        this.title = jsonObject.getString("title");
        this.content = jsonObject.getString("content");
        this.state = jsonObject.getInteger("state");
        this.images = jsonObject.getJsonArray("images").stream().toArray(String[]::new);
        this.createTime = jsonObject.getLong("createTime");
        this.updateTime = jsonObject.getLong("updateTime");
        this.uid = jsonObject.getString("uid");
        this.user = new UserInfo();
        this.user.setId(this.uid);
        JsonObject user = jsonObject.getJsonObject("user");
        if (user != null) {
            this.user.setName(user.getString("name"));
            this.user.setUsername(user.getString("username"));
            this.user.setAvatar(user.getString("avatar"));
            this.user.setGender(user.getInteger("gender"));
            this.user.setOnline(Client.CLIENT_POOL.containsKey(this.uid));
        }
    }
}
