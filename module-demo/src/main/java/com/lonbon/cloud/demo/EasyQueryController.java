package com.lonbon.cloud.demo;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.solon.annotation.Db;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;

import java.util.List;

@Controller
public class EasyQueryController {

    @Db("db_master")//注意这边使用sql-solon-plugin包下的Db注解
    private EasyEntityQuery easyEntityQuery;

    @Mapping("/query")
    public List<Topic> query(@Param(defaultValue = "world") String name) {
        return easyEntityQuery.queryable(Topic.class).toList();
    }
}
