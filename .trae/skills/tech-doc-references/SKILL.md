---
name: "tech-doc-references"
description: "为技术文档生成参考链接。当编写需要在末尾添加有效、非虚构参考链接的技术文档时调用。"
---

# 技术文档参考链接

此技能用于为技术文档生成有效的参考链接，确保链接真实存在且格式正确。

## 使用指南

### 何时使用
- 当编写技术文档时
- 当需要在文档末尾添加参考链接时
- 当希望确保参考链接有效且非虚构时

### 如何使用
1. 在技术文档末尾调用此技能
2. 提供您正在记录的主题或技术
3. 技能将以指定格式生成参考链接

## 格式要求

参考链接应遵循以下格式：

```markdown
## 8. 参考链接

- [链接标题](https://example.com)
- [另一个链接标题](https://another-example.com)
```

## 示例

对于 Easy-Query TypeHandler 和 ValueConverter 的文档：

```markdown
## 8. 参考链接

- [easy-query 官方文档|高级功能|自定义TypeHandler](https://www.easy-query.com/easy-query-doc/adv/type-handler.html)
- [easy-query 官方文档|高级功能|Java对象数据库值转换](https://www.easy-query.com/easy-query-doc/adv/value-converter.html)
- [GitHub Issue: 建议在数据写入过程中，添加@Column中typeHandler参数的支持](https://github.com/dromara/easy-query/issues/462)
- [DeepWiki: JdbcTypeHandler 与 ValueAutoConverter 的使用](https://deepwiki.com/search/jdbctypehandler-valueautoconve_1b5bbaf2-c7d4-48ee-bf90-359e1c98adf7)
```

## 重要注意事项

- 所有链接必须有效且可访问
- 链接应与文档主题相关
- 不要使用虚构或不存在的链接
- 在包含链接之前验证每个链接
- 使用描述性链接标题，清晰地表明链接包含的内容