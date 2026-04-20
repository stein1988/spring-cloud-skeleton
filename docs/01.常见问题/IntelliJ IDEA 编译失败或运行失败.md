# IntelliJ IDEA 编译失败或运行失败

## 问题描述

同时使用 IntelliJ IDEA 和 Trae（VSCode 内核）时，IDEA 编译失败，提示 "AccessDeniedException" 或文件被占用无法写入。

## 原因分析

Trae（VSCode 内核）默认会对项目目录进行文件监视、后台索引和自动保存，这些操作会持有编译目录（target/）的文件句柄，导致 Maven 编译时无法独占写入。

## 解决方案

在项目根目录 `d:\Java\spring-cloud-skeleton\.vscode\settings.json` 中添加以下配置：

```json
{
  // 核心：排除编译产物目录，彻底释放文件锁
  "files.watcherExclude": {
    "**/target/**": true,
    "**/.idea/**": true,
    "**/node_modules/**": true,
    "**/.git/**": true,
    "**/build/**": true,
    "**/dist/**": true
  },
  // 加快搜索，减少后台遍历
  "search.exclude": {
    "**/target": true,
    "**/.idea": true,
    "**/node_modules": true
  },
  // 关闭自动保存，避免保存时触发文件占用
  "files.autoSave": "off",
  // 可选：关闭符号链接跟踪，降低IO
  "search.followSymlinks": false,
  // 关闭后台编译（关键）
  "java.compile.nullAnalysis.mode": "disabled",
  // 关闭 pom.xml 自动同步（关键）
  "java.configuration.updateBuildConfiguration": "disabled",
  // 关闭 VSCode Java 自动构建
  "java.autobuild.enabled": false
}
```

## 配置说明

| 配置项 | 作用 |
|--------|------|
| `files.watcherExclude` | 告诉 VSCode 内核不要监听 target/ 等目录，不再持有文件句柄，解决 AccessDeniedException |
| `files.autoSave` | 关闭自动保存，避免保存时锁定文件与 Maven 清理/覆盖冲突 |
| `java.autobuild.enabled` | 关闭 VSCode 后台 Java 编译，避免并发冲突 |

## 补充措施

1. **关闭 IDEA 自动构建**：
   - `File → Settings → Build, Execution, Deployment → Compiler`
   - 取消勾选 "Build project automatically"

2. **清理缓存并重启**：
   - IDEA：`File → Invalidate Caches... → Invalidate and Restart`
   - Trae：关闭后重新打开项目

3. **命令行编译绕过 IDE 冲突**：
   ```bash
   mvn clean compile -Dmaven.test.skip=true
   ```

4. **禁用不必要的扩展**：
   - 如 Live Server、GitLens 等，减少后台进程

## 相关文档

- [IntelliJ IDEA 与 Trae 冲突 - 豆包](https://www.doubao.com/thread/w008fa09b3bef0b43)