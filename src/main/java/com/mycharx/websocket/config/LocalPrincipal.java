package com.mycharx.websocket.config;

import java.security.Principal;

/**
 * 用来区分用户，这里传入的name就是该用户的唯一标识
 *
 * @author 张卜亢
 * @date 2019.09.22 00:16:02
 */
public class LocalPrincipal implements Principal {

    /**
     * The Name.
     */
    private String name;

    @Override
    public String getName() {
        return name;
    }


    /**
     * Instantiates a new Local principal.
     *
     * @param name the name
     */
    public LocalPrincipal(String name) {
        this.name = name;
    }

    /**
     * Instantiates a new Local principal.
     */
    public LocalPrincipal() {
    }
}
