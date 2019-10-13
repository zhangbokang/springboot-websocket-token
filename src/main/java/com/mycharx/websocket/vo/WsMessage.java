package com.mycharx.websocket.vo;

import lombok.*;

/**
 * The type Ws message.
 *
 * @author 张卜亢
 * @date 2019.10.13 22:44:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsMessage {
    /**
     * 消息接收人，对应认证用户Principal.name（全局唯一）
     */
    private String toName;
    /**
     * 消息发送人，对应认证用户Principal.name（全局唯一）
     */
    private String fromName;
    /**
     * 消息内容
     */
    private Object content;
}

