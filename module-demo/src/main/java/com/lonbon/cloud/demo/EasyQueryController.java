package com.lonbon.cloud.demo;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EasyQueryController {

    private final EasyEntityQuery easyEntityQuery;

    @GetMapping("/query")
    public List<Topic> query(@RequestParam(defaultValue = "world") String name) {
        return easyEntityQuery.queryable(Topic.class).toList();
    }
}
