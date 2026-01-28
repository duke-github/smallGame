/*
 * ioGame
 * Copyright (C) 2021 - 2023  渔民小镇 （262610965@qq.com、luoyizhu@gmail.com） . All Rights Reserved.
 * # iohao.com . 渔民小镇
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ccw;

import com.iohao.game.action.skeleton.core.doc.IoGameDocumentHelper;
import com.iohao.game.action.skeleton.ext.spring.ActionFactoryBeanForSpring;
import com.iohao.game.external.core.netty.simple.NettySimpleHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;


@SpringBootApplication
public class GameApplication {

    public static void main(String[] args) {

        SpringApplication.run(GameApplication.class, args);
        // 游戏对外服端口
        int port = 10100;

        // 游戏逻辑服
        var demoLogicServer = new DemoLogicServer();

        // 启动 对外服、网关服、逻辑服; 并生成游戏业务文档
        // 这三部分在一个进程中相互使用内存通信
        NettySimpleHelper.run(port, List.of(demoLogicServer));

        // 生成对接文档
        extractedDoc();
    }

    private static void extractedDoc() {
        // 添加枚举错误码 class，用于生成错误码相关信息
        IoGameDocumentHelper.addErrorCodeClass(GameCode.class);
        // 生成文档
        IoGameDocumentHelper.generateDocument();
    }

    @Bean
    public ActionFactoryBeanForSpring actionFactoryBean() {
        // 将业务框架交给 spring 管理
        return ActionFactoryBeanForSpring.me();
    }
}
