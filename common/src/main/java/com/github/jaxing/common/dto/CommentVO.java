package com.github.jaxing.common.dto;

import com.github.jaxing.common.domain.Client;
import com.github.jaxing.common.domain.Comment;
import com.github.jaxing.common.domain.UserInfo;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentVO extends Comment {

    private UserInfo user;

    private int childSize;

    private int thumbupCount;

    private boolean liked;

    public CommentVO(JsonObject jsonObject) {
        this.setId(jsonObject.getString("id"));
        this.setUid(jsonObject.getString("uid"));
        this.setReplyId(jsonObject.getString("replyId"));
        this.setContent(jsonObject.getString("content"));
        this.setDeleted(jsonObject.getBoolean("deleted"));
        this.setCreateTime(jsonObject.getLong("createTime"));
        this.user = new UserInfo();
        JsonObject user = jsonObject.getJsonObject("user");
        if (user != null) {
            this.user.setName(user.getString("name"));
            this.user.setUsername(user.getString("username"));
            this.user.setAvatar(user.getString("avatar"));
            this.user.setGender(user.getInteger("gender"));
            this.user.setOnline(Client.CLIENT_POOL.containsKey(this.getUid()));
        }
    }

}
