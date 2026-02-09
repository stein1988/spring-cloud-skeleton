package com.lonbon.cloud.demo;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.demo.proxy.TopicProxy;
import lombok.Data;

@Data
@Table("t_topic")
@EntityProxy
public class Topic implements ProxyEntityAvailable<Topic, TopicProxy> {

    @Column(primaryKey = true)
    private String id;
    private Integer stars;
    private String title;
}
