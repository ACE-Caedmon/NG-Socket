package com.ace.ng.examples.server;

import com.ace.ng.boot.Extension;

/**
 * Created by Administrator on 2014/6/9.
 */
public class TestExtension extends Extension {
    @Override
    protected void init() {
        regiterCmd(Handler0001.class);
    }
}
