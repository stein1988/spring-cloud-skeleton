# Java Null Safe 空安全使用规范

# 1\. 总则

## 1\.1 规范目的

为统一Java项目空安全编码标准，从源头杜绝空指针异常（NullPointerException，NPE），降低线上故障风险，提升代码可读性、可维护性和团队协作效率，结合JSpecify官方标准、Spring生态实践及行业最佳实践，制定本规范。本规范适用于所有Java后端项目，覆盖Controller、Service、DAO、DTO等全层级代码，所有开发、测试、重构工作均需严格遵循。

## 1\.2 核心原则

- 标准化：优先采用JSpecify官方空安全规范（1\.0\.0版本及以上），统一注解使用，摒弃多套空安全方案混用的情况，对齐Java生态空安全标准。

- 分层负责：编译期静态约束与运行时业务校验分离，各司其职，互不替代；内部代码防NPE，外部入参保合法。

- 简洁高效：减少冗余注解和无效判空，利用工具（IDE、编译器插件）实现自动化校验，降低开发成本。

- 兼容适配：兼顾新项目标准化与旧项目平滑迁移，支持与Optional、Spring生态组件无缝配合，保障代码兼容性。

## 1\.3 适用范围

本规范适用于所有Java后端项目（Spring Boot、Spring Cloud等），涵盖以下场景：DTO/VO接口入参、Controller接口、Service业务逻辑、DAO数据访问、工具类、实体类、泛型/集合操作等；涉及空安全相关注解、Optional使用、编译期校验配置等所有相关操作。

# 2\. 核心注解规范（重点）

空安全注解分为两类：编译期静态空安全注解（JSpecify）和运行时业务校验注解（Jakarta Validation），二者互补使用，禁止混淆或替代。

## 2\.1 JSpecify 静态空安全注解（编译期约束）

采用JSpecify官方注解（org\.jspecify\.annotations），为Java生态统一空安全标准，支持IDE实时提示、编译期强校验，无运行时性能消耗，适用于内部代码空安全约束，Spring 7及以上版本已全面拥抱该标准，替代原有Spring空安全注解。

### 2\.1\.1 核心注解及用法

- @NullMarked：包级/类级注解，标记当前包或类下所有类型使用位置（方法参数、返回值、成员变量、泛型、集合元素等）默认非空（NonNull），无需手动添加@NonNull注解。
使用场景：所有业务包（com\.xxx\.biz、com\.xxx\.service等）的package\-info\.java中添加，子包需单独添加（包无继承关系，不递归生效）；也可用于单个类（不推荐，优先包级统一配置）。
标准写法：
// package\-info\.java
@NullMarked
package com\.xxx\.service;
import org\.jspecify\.annotations\.NullMarked;

- @Nullable：仅用于显式标记可空的元素（参数、返回值、成员变量等），与@NullMarked配合使用，是唯一标记可空的注解。
使用场景：确实可能为null的元素，如“查询可能无结果的返回值”“非必传参数”“可空的成员变量”；泛型、数组元素的可空标注（如List\&lt;@Nullable String\&gt;）。
禁止场景：无需为非空元素添加@NonNull注解（@NullMarked已默认非空）；禁止用于局部变量（不受@NullMarked控制，也无需显式标注）。

- @NonNull：仅用于局部场景（如泛型中需显式强调非空的位置），禁止在@NullMarked标记的包/类中手动为参数、返回值、成员变量添加（冗余且无意义），仅在未使用@NullMarked的场景下临时使用。

### 2\.1\.2 覆盖范围

@NullMarked标记后，默认非空的元素包括：方法参数、方法返回值、类成员变量（字段）、泛型参数、集合元素，等价于Spring旧注解@NonNullApi \+ @NonNullFields的组合效果，无需重复添加旧注解（Spring 7已弃用旧注解，建议逐步迁移）。

### 2\.1\.3 依赖配置

需引入JSpecify依赖（无其他依赖，纯注解包），Maven/Gradle配置如下：

#### Maven

```xml
<dependency>
    <groupId>org.jspecify</groupId>
    <artifactId>jspecify</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Gradle

```groovy
dependencies {
    implementation 'org.jspecify:jspecify:1.0.0'
}
```

## 2\.2 Jakarta Validation 运行时校验注解（业务校验）

采用jakarta\.validation\.constraints\.NotNull，属于JSR380校验规范，需配合Hibernate Validator实现运行时校验，仅用于接口入参、DTO/VO字段、数据库实体字段的业务合法性校验，不负责编译期空安全和NPE防护。

### 2\.2\.1 核心注解及用法

- @NotNull：标记字段/参数不可为null，仅在运行时校验，无编译期提示，需配合@Valid（Spring MVC）触发校验，校验失败抛ConstraintViolationException或MethodArgumentNotValidException，用于给前端返回明确的业务错误提示。
使用场景：DTO/VO接口入参、数据库实体必填字段（如用户ID、用户名）。
标准写法：
public class UserDTO \{
    @NotNull\(message = \&\#34;用户ID不能为空\&\#34;\)
    private Long userId;
\}

- 补充说明：该注解仅校验“是否为null”，不校验空字符串（\&\#34;\&\#34;）、空集合（如new ArrayList\&lt;\&gt;\(\)），若需校验非空且非空白，需配合@NotBlank（字符串）、@NotEmpty（集合）使用。

### 2\.2\.2 依赖配置（如需使用）

```xml
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.0.2</version>
</dependency>
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>8.0.1.Final</version>
</dependency>
```

## 2\.3 注解使用禁忌

- 禁止在Service、工具类等内部代码中使用jakarta\.validation\.NotNull（无法触发校验，纯属冗余）。

- 禁止用JSpecify注解（@NullMarked、@Nullable）替代Jakarta Validation注解做接口入参业务校验（无错误提示文案，无法拦截前端非法入参）。

- 禁止混用不同框架的空安全注解（如JetBrains的@NotNull、Spring旧注解@NonNullApi，兼容性差，优先统一为JSpecify）。

- 禁止使用javax\.validation\.constraints\.NotNull（已被jakarta\.validation\.constraints\.NotNull替代，属于旧规范，不推荐使用）。

# 3\. 各层级代码空安全规范

## 3\.1 DTO/VO层（接口入参/出参）

- 入参DTO：使用jakarta\.validation\.NotNull（及@NotBlank、@NotEmpty）做运行时业务校验，配合@Valid触发校验，明确错误提示文案。

- 出参VO：使用JSpecify注解，@NullMarked包级标记，可空字段显式添加@Nullable，禁止返回null（可空字段用@Nullable标注，集合类返回空集合而非null）。

- 禁止在DTO/VO中使用Optional作为字段（Optional不适合作为传输对象，序列化支持弱，语义模糊）。

## 3\.2 Controller层

- 接口入参：DTO参数添加@Valid注解，触发Jakarta Validation校验；路径参数、请求参数默认非空（@NullMarked包级标记），可空参数显式添加@Nullable。

- 接口返回值：默认非空，可空返回值（如查询无结果）用@Nullable标注，或返回Optional封装（推荐后者，更清晰表达业务语义）。

- 标准写法：
@RestController
@NullMarked
@RequestMapping\(\&\#34;/user\&\#34;\)
public class UserController \{
    @PostMapping\(\&\#34;/add\&\#34;\)
    public Result\&lt;Void\&gt; add\(@Valid @RequestBody UserDTO userDTO\) \{
        // 业务逻辑
    \}
    @GetMapping\(\&\#34;/get/\{id\}\&\#34;\)
    public Optional\&lt;UserVO\&gt; getById\(@PathVariable String id\) \{
        // 业务逻辑
    \}
\}

## 3\.3 Service层（核心业务逻辑）

- 包级统一添加@NullMarked（package\-info\.java），所有方法参数、返回值、成员变量默认非空，可空元素仅添加@Nullable。

- 方法参数：禁止传入null（编译期校验拦截），无需手动判空（@NullMarked保障）；可空参数（@Nullable标注）必须在方法内判空后使用，避免NPE。

- 返回值：禁止返回null（非空返回值）；可能无结果的返回值（如查询业务），用Optional封装（优先）或@Nullable标注，强制调用方处理可空场景。

- 成员变量：默认非空，必须在构造方法、初始化方法中赋值（或通过依赖注入），禁止未初始化的非空字段。

- 标准写法：
@Service
public class UserService \{
    // 默认非空，依赖注入赋值
 private final UserRepository userRepository;
    // 构造方法注入
 public UserService\(UserRepository userRepository\) \{
        this\.userRepository = userRepository;
    \}
    // 参数默认非空，返回值可空（用Optional封装）
    public Optional\&lt;User\&gt; getById\(String id\) \{
        return userRepository\.selectById\(id\);
    \}
    // 可空参数，必须判空
    public void log\(@Nullable String msg\) \{
        if \(msg \!= null\) \{
            System\.out\.println\(msg\);
        \}
    \}
\}

## 3\.4 Repository层（数据访问）

- 包级添加@NullMarked，方法参数默认非空（如查询ID），禁止传入null。

- 查询方法：可能无结果的返回值，用Optional封装（如selectById、selectOne）；批量查询返回空集合而非null。

- 标准写法：
public interface UserRepository \{
    Optional\&lt;User\&gt; selectById\(String id\);
    List\&lt;User\&gt; selectList\(@Nullable String name\);
\}

- 包级添加@NullMarked，方法参数默认非空（如查询ID），禁止传入null。

- 查询方法：可能无结果的返回值，用Optional封装（如selectById、selectOne）；批量查询返回空集合而非null。

- 标准写法：
@Repository
@NullMarked
public interface UserDAO \{
    Optional\&lt;User\&gt; selectById\(String id\);
    List\&lt;User\&gt; selectList\(@Nullable String name\);
\}

## 3\.5 工具类

- 包级添加@NullMarked，方法参数、返回值默认非空，可空参数显式标注@Nullable并判空。

- 禁止工具类方法返回null，集合返回空集合，对象返回Optional（可空场景）。

- 禁止在工具类中使用jakarta\.validation\.NotNull注解（无校验触发场景）。

# 4\. 构建工具编译期校验配置

为强化编译期空安全校验，需配置对应构建工具插件，实现“编码时IDE提示、编译时报错”，从源头拦截NPE风险；核心依赖ErrorProne（谷歌编译检查工具）\+ NullAway（空安全专门检查器），Spring Gradle插件底层已自动集成，Maven需手动配置。

## 4\.1 Gradle项目（推荐）

使用Spring官方插件io\.spring\.nullability，自动配置ErrorProne、NullAway，无需手动配置编译器参数。

```groovy
plugins {
    id 'java'
    id 'io.spring.nullability' version '0.0.11' // 最新稳定版
}

// 可选：自定义校验配置
nullability {
    requireExplicitNullMarking = true // 强制所有包/类必须有@NullMarked/@NullUnmarked（推荐开启）
    errorProneVersion = '2.26.1'      // 自定义ErrorProne版本
    nullAwayVersion = '0.11.0'        // 自定义NullAway版本
}

// 启用测试代码空安全检查（可选）
tasks.named('compileTestJava') {
    nullability {
        checking = 'tests'
    }
}

// JSpecify依赖（必须）
dependencies {
    implementation 'org.jspecify:jspecify:1.0.0'
}
```

## 4\.2 Maven项目

io\.spring\.nullability插件不支持Maven，需手动配置ErrorProne \+ NullAway，实现与Gradle插件完全一致的校验效果；NullAway 0\.12\.3及以上版本需指定annotatedPackages或onlyNullMarked参数。

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.12.0</version>
            <configuration>
                <encoding>UTF-8</encoding>
                <source>17</source> <!-- 匹配项目Java版本 -->
                <target>17</target>
                <compilerId>javac-with-errorprone</compilerId>
                <compilerArgs>
                    <arg>-Xplugin:NullAway</arg>
                    <arg>-Xep:NullAway:ERROR&lt;/arg&gt; <!-- 校验失败直接编译报错（推荐） -->
                    <!-- 方式1：指定需要校验的包（多个包用逗号分隔） -->
                    <arg>-XepOpt:NullAway:AnnotatedPackages=com.xxx&lt;/arg&gt; <!-- 替换为项目实际包名 --><!-- 方式2：仅校验@NullMarked标记的包（NullAway 0.12.3+支持） -->
                    <!-- <arg>-XepOpt:NullAway:OnlyNullMarked=true</arg> -->
                </compilerArgs>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>com.google.errorprone</groupId>
                    <artifactId>error_prone_core</artifactId>
                    <version>2.26.1</version>
                </dependency>
                <dependency>
                    <groupId>com.uber.nullaway</groupId>
                    <artifactId>nullaway</artifactId>
                    <version>0.11.0</version>
                </dependency>
                <dependency>
                    <groupId>org.codehaus.plexus</groupId>
                    <artifactId>plexus-compiler-javac-errorprone</artifactId>
                    <version>2.13.0</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins&gt;
&lt;/build&gt;

<!-- JSpecify依赖（必须） -->
<dependencies>
    <dependency>
        <groupId>org.jspecify</groupId>
        <artifactId>jspecify</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## 4\.3 校验效果说明

配置完成后，以下场景会触发IDE警告/编译报错，强制修复：

- 给@NullMarked标记下的非空参数传null；

- 非空方法返回null；

- 非空成员变量未初始化；

- @Nullable标注的元素未判空即使用；

- 集合元素为null（未标注@Nullable时）。

# 5\. Optional 使用规范

Optional是Java 8引入的运行时空安全工具，与JSpecify注解为互补关系，而非替代关系；JSpecify负责编译期静态约束，Optional负责运行时显式处理可空场景，表达“可能无结果”的业务语义，避免NPE。

## 5\.1 核心使用场景（仅推荐2种）

- 方法返回值：表示“可能有值、可能无值”的业务场景（如根据ID查询用户、查询单个结果），替代返回null，强制调用方处理无值场景。

- 标准示例：
// 正确：查询可能无结果，返回Optional
Optional\&lt;User\&gt; findUser\(String id\);
// 错误：ID不可能为null，参数无需用Optional封装
Optional\&lt;User\&gt; findUser\(Optional\&lt;String\&gt; id\);

## 5\.2 禁止使用场景

- 禁止作为方法参数（迫使调用方包装参数，语义模糊，增加调用成本）；

- 禁止作为类成员变量（Optional不适合持久化和序列化，语义不清晰）；

- 禁止作为集合元素（如List\&lt;Optional\&lt;User\&gt;\&gt;，属于反模式，应过滤null后使用List\&lt;User\&gt;）；

- 禁止嵌套使用（如Optional\&lt;Optional\&lt;User\&gt;\&gt;，语义混乱，增加代码复杂度）。

## 5\.3 正确使用方式

- 创建Optional对象：优先使用Optional\.ofNullable\(\)（支持null），非空对象可用Optional\.of\(\)（传入null会抛NPE，需谨慎）；禁止直接赋值null给Optional变量（如Optional\&lt;User\&gt; user = null）。

- 处理Optional对象：优先使用ifPresent\(\)、orElse\(\)、orElseGet\(\)、map\(\)，禁止直接调用get\(\)（无值时抛NoSuchElementException，与NPE同样危险）；orElse\(\)立即求值，orElseGet\(\)延迟求值（推荐用于耗时操作）。

- 标准示例：
// 正确用法
Optional\&lt;User\&gt; userOpt = userService\.getById\(id\);
// 方式1：有值则执行逻辑
userOpt\.ifPresent\(user \-\&gt; System\.out\.println\(user\.getName\(\)\)\);
// 方式2：无值返回默认值（orElseGet延迟求值）
User user = userOpt\.orElseGet\(\(\) \-\&gt; new User\(\)\);
// 方式3：无值抛异常（明确业务异常场景）
User user = userOpt\.orElseThrow\(\(\) \-\&gt; new RuntimeException\(\&\#34;用户不存在\&\#34;\)\);

# 6\. JSpecify、Jakarta Validation、Optional 对比表

为清晰区分三者的定位与用法，避免混淆，特整理以下对比表，明确各工具的核心差异与适用场景：

|对比维度|JSpecify|Jakarta Validation|Optional|
|---|---|---|---|
|核心类型|编译期静态空安全注解|运行时业务校验注解|运行时空安全工具类|
|核心作用|定义类型空安全语义，约束代码编写，从编译期拦截NPE|校验接口入参/实体字段合法性，返回业务错误提示|显式表达“可能无值”的业务语义，强制调用方处理可空场景|
|生效时机|编码期（IDE提示）、编译期|运行时（接口调用/参数校验触发）|运行时（代码执行过程中）|
|适用场景|Service、Repository、工具类等内部代码，约束参数、返回值、成员变量|DTO/VO接口入参、数据库实体必填字段，业务校验|方法返回值（可能无结果的场景，如单条查询）|
|依赖要求|需引入jspecify依赖（纯注解包，无其他依赖）|需引入jakarta\.validation\-api \+ hibernate\-validator实现|JDK 8及以上自带，无需额外依赖|
|是否防NPE|是（编译期拦截，从源头杜绝）|否（仅校验参数非空，不控制代码逻辑NPE）|是（强制处理无值场景，避免忘记判空）|
|IDE提示|有（实时空安全警告，如传null给非空参数）|无（仅运行时报错，编码期无提示）|有（提示未处理无值场景，禁止直接调用get\(\)）|
|典型注解/用法|@NullMarked（包级默认非空）、@Nullable（显式可空）|@NotNull（非空校验）、@Valid（触发校验）|ofNullable\(\)、ifPresent\(\)、orElse\(\)、orElseThrow\(\)|
|核心特点|统一空安全标准，零运行时性能消耗，全局约束|专注业务校验，可自定义错误文案，适配接口场景|语义清晰，强制处理无值场景，避免冗余判空|

# 7\. 常见问题与避坑指南

## 6\.1 注解使用类问题

- 问题1：@NullMarked标记父包后，子包未添加，导致子包空安全校验失效。
解决方案：每个子包单独在package\-info\.java中添加@NullMarked（包无继承关系，不递归生效）；Java 9\+可通过模块级@NullMarked标记，覆盖所有子包。

- 问题2：混淆JSpecify @NonNull与jakarta\.validation\.NotNull，导致校验失效。
解决方案：牢记“编译期用JSpecify，运行时用Jakarta Validation”，各司其职，不混用。

- 问题3：在@NullMarked标记的包中，手动添加@NonNull注解，造成冗余。
解决方案：删除冗余@NonNull注解，@NullMarked已默认所有元素非空。

## 6\.2 编译期校验类问题

- 问题1：Maven配置后，编译未触发NullAway校验。
解决方案：检查compilerId是否为javac\-with\-errorprone，是否配置AnnotatedPackages/OnlyNullMarked参数，依赖版本是否兼容。

- 问题2：NullAway 0\.12\.3\+版本编译报错，提示缺少AnnotatedPackages或OnlyNullMarked参数。
解决方案：按规范添加其中一个参数（推荐AnnotatedPackages指定项目包名）。

## 6\.3 Optional使用类问题

- 问题1：返回Optional对象时，仍返回null（如return null; 而非return Optional\.empty\(\);）。
解决方案：所有返回Optional的方法，必须返回Optional\.of\(\)、Optional\.ofNullable\(\)或Optional\.empty\(\)，禁止返回null。

- 问题2：过度使用Optional，导致代码臃肿（如Optional\.ofNullable\(str\)\.ifPresent\(s \-\&gt; s\.length\(\)\)）。
解决方案：简单可空场景用JSpecify @Nullable标注，仅在“可能无结果”的返回值中使用Optional。

## 6\.4 旧项目迁移类问题

- 问题1：旧项目使用Spring旧注解（@NonNullApi、@NonNullFields），如何迁移到JSpecify。
解决方案：1\. 引入JSpecify依赖；2\. 将包级@NonNullApi \+ @NonNullFields替换为@NullMarked；3\. 逐步删除冗余的@NonNull注解；4\. 配置编译期校验插件，逐步修复校验报错。

- 问题2：旧项目大量使用手动判空，如何简化。
解决方案：通过@NullMarked统一非空约束，删除非必要的手动判空；可空场景用@Nullable标注并判空，或用Optional封装返回值。

# 7\. 旧项目迁移指南

## 7\.1 迁移原则

平滑迁移，不影响现有业务运行；优先统一注解规范，再配置编译期校验，最后逐步修复校验报错，可分模块、分批次迁移，降低迁移成本。

## 7\.2 迁移步骤

1. 引入JSpecify依赖，排除Spring旧空安全注解（如org\.springframework\.lang\.NonNullApi）。

2. 在核心业务包（service、dao、util）的package\-info\.java中添加@NullMarked，替换原有@NonNullApi \+ @NonNullFields。

3. 配置构建工具编译期校验（Gradle用io\.spring\.nullability，Maven配置ErrorProne \+ NullAway），暂时关闭“校验失败报错”（将\-Xep:NullAway:ERROR改为\-WARN），先收集所有校验问题。

4. 逐步修复校验警告：给可空元素添加@Nullable，补全非空字段初始化，删除冗余@NonNull注解，优化Optional使用。

5. 修复完成后，开启“校验失败报错”，强制后续开发遵循规范；逐步替换DTO层的javax\.validation注解为jakarta\.validation注解。

## 7\.3 迁移工具推荐

可使用Modernize OpenRewrite自动化重构工具，批量迁移空安全注解，减少手动操作成本；Spring Boot 4\.0提供空安全迁移插件，可自动扫描潜在空指针风险，生成迁移报告。

# 8\. 附则

- 本规范自发布之日起执行，所有新增代码必须严格遵循；旧代码逐步按规范迁移，未迁移部分需在备注中说明原因。

- 团队需定期开展空安全编码评审，重点检查注解使用、Optional使用、编译期校验配置，及时纠正不规范写法。

- 本规范将根据JSpecify版本更新、Spring生态升级及项目实际需求，定期修订完善。

- 参考标准：JSpecify 1\.0\.0官方规范、Spring Framework 7\.x空安全实践、NullAway官方配置指南、Jakarta Validation 3\.0规范。

> （注：文档部分内容可能由 AI 生成）
