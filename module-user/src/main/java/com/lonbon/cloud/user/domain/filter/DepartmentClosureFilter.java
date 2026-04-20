package com.lonbon.cloud.user.domain.filter;

import com.easy.query.core.annotation.Nullable;
import com.easy.query.core.basic.extension.navigate.NavigateBuilder;
import com.easy.query.core.basic.extension.navigate.NavigateExtraFilterStrategy;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.parser.core.base.WherePredicate;
import org.springframework.stereotype.Component;

/**
 * 部门闭包查询过滤器
 */
@Component
public class DepartmentClosureFilter implements NavigateExtraFilterStrategy {

    @Override
    public @Nullable SQLActionExpression1<WherePredicate<?>> getPredicateFilterExpression(NavigateBuilder builder) {
        return o->o.gt("distance", 0);
    }

    /**
     * 过滤中间表常用于多对多
     */
    @Override
    public @Nullable SQLActionExpression1<WherePredicate<?>> getPredicateMappingClassFilterExpression(NavigateBuilder builder) {
        return null;
    }
}
