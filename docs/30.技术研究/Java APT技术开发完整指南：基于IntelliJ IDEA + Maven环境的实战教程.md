# Java APT 技术开发完整指南：基于 IntelliJ IDEA + Maven 环境的实战教程

## 1. APT 技术背景与概念基础

### 1.1 什么是 APT 技术

\*\*APT（Annotation Processing Tool，注解处理工具）\*\* 是 Java 编译器的重要扩展机制，它允许开发者在编译阶段扫描、处理源代码中的注解，并基于这些注解生成额外的代码或资源文件。APT 技术在 Java 5.0 中首次引入，最初作为一个实验性工具，随后在 Java 6.0 中被直接集成到 javac 编译器中[(79)](https://docs.oracle.com/javase/jp/1.5.0/guide/apt/GettingStarted.html)。

APT 技术的核心价值在于**编译期代码生成**，它能够在不修改原有代码的情况下，通过注解驱动自动生成辅助代码。与传统的运行时反射机制相比，APT 生成的代码具有更高的性能和类型安全性，因为所有的代码生成和验证都在编译阶段完成，不会产生任何运行时开销[(2)](https://blog.51cto.com/u_12227/14368868)。

APT 技术的主要应用场景包括：



* **代码生成**：根据注解自动生成 getter/setter 方法、建造者模式代码、数据库映射代码等

* **编译时验证**：检查代码是否符合特定规范，如方法参数验证、类结构检查等

* **元数据处理**：收集和处理代码中的注解信息，生成文档或配置文件

* **框架集成**：支持依赖注入、ORM 映射、事件总线等框架的自动配置

### 1.2 javax.annotation.processing API 核心概念

javax.annotation.processing 包提供了开发注解处理器的核心 API，包含以下主要组件：

#### Processor 接口

**Processor 接口**是所有注解处理器的基础接口，定义了注解处理器的生命周期和核心方法[(7)](http://docs.oracle.com/en/java/javase/24/docs/api/java.compiler/javax/annotation/processing/package-summary.html)。该接口包含以下关键方法：



* **init(ProcessingEnvironment processingEnv)**：初始化方法，在处理器创建后立即调用，用于获取处理环境

* **process(Set\<? extends TypeElement> annotations, RoundEnvironment roundEnv)**：核心处理方法，用于处理注解

* **getSupportedAnnotationTypes()**：返回处理器支持的注解类型集合

* **getSupportedSourceVersion()**：返回处理器支持的 Java 版本

* **getSupportedOptions()**：返回处理器支持的选项集合

#### AbstractProcessor 抽象类

**AbstractProcessor 抽象类**是 Processor 接口的便利实现，为大多数具体处理器提供了基础实现[(33)](https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/annotation/processing/AbstractProcessor.html)。它简化了处理器的开发，提供了以下功能：



* 自动处理 @SupportedAnnotationTypes、@SupportedSourceVersion、@SupportedOptions 注解

* 提供了 getProcessingEnv () 方法获取处理环境

* 实现了默认的 getSupportedAnnotationTypes () 和 getSupportedSourceVersion () 方法

#### RoundEnvironment 接口

**RoundEnvironment 接口**提供了当前和前一轮注解处理的环境信息[(45)](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/RoundEnvironment.html)。它包含以下核心方法：



* **getElementsAnnotatedWith(Class\<? extends Annotation> a)**：获取被指定注解标注的所有元素

* **getElementsAnnotatedWith(TypeElement a)**：获取被指定类型元素标注的所有元素

* **getRootElements()**：获取当前轮次的根元素

* **processingOver()**：判断处理是否已结束

* **errorRaised()**：判断是否已抛出错误

#### Element 层次结构

**Element 接口**代表程序中的各种元素，构成了 Java 程序的抽象语法树[(40)](https://blog.csdn.net/chuyouyinghe/article/details/132087755)：



* **PackageElement**：表示包元素

* **TypeElement**：表示类或接口元素

* **VariableElement**：表示字段、枚举常量、方法或构造方法参数、局部变量等

* **ExecutableElement**：表示方法、构造方法或初始化块

* **TypeParameterElement**：表示泛型类型参数

这些元素通过**ElementKind 枚举**来区分类型，开发者应使用 getKind () 方法而非 instanceof 操作符来判断元素类型[(43)](https://juejin.cn/post/7135084077408845831)。

### 1.3 Java 版本演进对 APT API 的影响

随着 Java 版本的演进，APT API 经历了重要的变化和改进：

**Java 6.0 的重大改进**：



* APT 工具被直接集成到 javac 编译器中，取代了原来的独立工具

* Mirror API 被**javax.lang.model**包下的新 API 取代，包括 Elements、Types、TypeMirror 等接口

* 引入了新的注解处理流程和生命周期管理机制

**Java 9 模块化系统的影响**：



* Java 9 引入了模块系统（JPMS），对 APT 开发产生了重要影响

* 原来的 com.sun.mirror.\* 包被弃用，建议使用标准的 javax.annotation.processing API

* 新增了**javax.annotation.processing.Generated**注解，用于标记生成的代码

* 需要特别注意模块间的依赖关系配置，避免循环依赖导致的编译错误[(89)](https://openjdk.org/groups/compiler/processing-code.html)

**Java 23 的安全性改进**：



* 从 Java 23 开始，为了安全性考虑，编译器默认不再自动扫描类路径中的注解处理器

* 需要通过**annotationProcessors**插件配置或声明依赖类型为**processor**来显式激活注解处理

* 这一变化要求开发者必须明确指定要执行的处理器，提高了安全性但增加了配置复杂度

## 2. Maven 项目配置完整流程

### 2.1 基础依赖配置

在 Maven 项目中使用 APT 技术，首先需要在 pom.xml 文件中添加必要的依赖。以下是基本的依赖配置示例：



```
\<dependencies>

&#x20;   \<!-- javax.annotation-api 提供注解相关的基础接口 -->

&#x20;   \<dependency>

&#x20;       \<groupId>javax.annotation\</groupId>

&#x20;       \<artifactId>javax.annotation-api\</artifactId>

&#x20;       \<version>1.3.2\</version>

&#x20;   \</dependency>

&#x20;  &#x20;

&#x20;   \<!-- 自动服务注册依赖，简化处理器注册过程 -->

&#x20;   \<dependency>

&#x20;       \<groupId>com.google.auto.service\</groupId>

&#x20;       \<artifactId>auto-service\</artifactId>

&#x20;       \<version>1.0-rc7\</version>

&#x20;   \</dependency>

&#x20;  &#x20;

&#x20;   \<!-- 可选：JavaPoet用于更优雅的代码生成 -->

&#x20;   \<dependency>

&#x20;       \<groupId>com.squareup\</groupId>

&#x20;       \<artifactId>javapoet\</artifactId>

&#x20;       \<version>1.13.0\</version>

&#x20;   \</dependency>

\</dependencies>
```

**依赖说明**：



* javax.annotation-api 提供了 @Nonnull、@Nullable 等基础注解

* auto-service 依赖允许使用 @AutoService 注解自动注册处理器，避免手动配置 META-INF/services 文件

* JavaPoet 是一个优秀的代码生成库，通过类型安全的 API 构建 Java 源码，避免字符串拼接的复杂性

### 2.2 Maven 3.x 版本的处理器配置

对于 Maven 3.x 和 Maven Compiler Plugin 3.x 版本，需要在 maven-compiler-plugin 中配置**annotationProcessorPaths**参数来指定注解处理器：



```
\<build>

&#x20;   \<plugins>

&#x20;       \<plugin>

&#x20;           \<groupId>org.apache.maven.plugins\</groupId>

&#x20;           \<artifactId>maven-compiler-plugin\</artifactId>

&#x20;           \<version>3.8.1\</version> \<!-- 建议使用3.8.1或更高版本 -->

&#x20;           \<configuration>

&#x20;               \<source>1.8\</source>

&#x20;               \<target>1.8\</target>

&#x20;              &#x20;

&#x20;               \<!-- 配置注解处理器路径 -->

&#x20;               \<annotationProcessorPaths>

&#x20;                   \<path>

&#x20;                       \<groupId>com.google.auto.service\</groupId>

&#x20;                       \<artifactId>auto-service\</artifactId>

&#x20;                       \<version>1.0-rc7\</version>

&#x20;                   \</path>

&#x20;                   \<!-- 可以添加多个处理器 -->

&#x20;                   \<path>

&#x20;                       \<groupId>com.example\</groupId>

&#x20;                       \<artifactId>custom-annotation-processor\</artifactId>

&#x20;                       \<version>1.0.0\</version>

&#x20;                   \</path>

&#x20;               \</annotationProcessorPaths>

&#x20;              &#x20;

&#x20;               \<!-- 可选：配置生成代码的输出目录 -->

&#x20;               \<generatedSourcesDirectory>\${project.build.directory}/generated-sources/annotations\</generatedSourcesDirectory>

&#x20;           \</configuration>

&#x20;       \</plugin>

&#x20;   \</plugins>

\</build>
```

**关键配置说明**：



* **annotationProcessorPaths**：指定注解处理器的路径，可以包含多个处理器

* **generatedSourcesDirectory**：指定生成代码的输出目录，默认为 target/generated-sources/annotations

* **source 和 target**：指定 Java 版本，建议使用 1.8 或更高版本

### 2.3 Maven 4.x 版本的新配置方式

从 Maven 4.x 和 Maven Compiler Plugin 4.x 开始，引入了新的依赖类型**processor**来简化配置：



```
\<dependencies>

&#x20;   \<!-- 使用processor依赖类型声明注解处理器 -->

&#x20;   \<dependency>

&#x20;       \<groupId>org.hibernate.orm\</groupId>

&#x20;       \<artifactId>hibernate-processor\</artifactId>

&#x20;       \<version>\${version.hibernate}\</version>

&#x20;       \<type>processor\</type>

&#x20;   \</dependency>

&#x20;  &#x20;

&#x20;   \<!-- 明确指定classpath-processor或modular-processor类型 -->

&#x20;   \<dependency>

&#x20;       \<groupId>com.example\</groupId>

&#x20;       \<artifactId>custom-processor\</artifactId>

&#x20;       \<version>1.0.0\</version>

&#x20;       \<type>classpath-processor\</type> \<!-- 或modular-processor -->

&#x20;   \</dependency>

\</dependencies>
```

**新配置方式的优势**：



* 简化了 plugin 配置，将处理器声明移到了 dependencies 部分

* 支持更细粒度的控制，可以指定**classpath-processor**或**modular-processor**类型

* 使信息可被其他插件使用，提高了集成度

### 2.4 多处理器执行顺序配置

当项目中存在多个处理器时，执行顺序非常重要。例如，在使用 MapStruct 和 Lombok 时，必须确保 MapStruct 处理器在 Lombok 之前执行：



```
\<plugin>

&#x20;   \<groupId>org.apache.maven.plugins\</groupId>

&#x20;   \<artifactId>maven-compiler-plugin\</artifactId>

&#x20;   \<version>3.11.0\</version>

&#x20;   \<configuration>

&#x20;       \<annotationProcessorPaths>

&#x20;           \<!-- MapStruct处理器必须在Lombok之前执行 -->

&#x20;           \<path>

&#x20;               \<groupId>org.mapstruct\</groupId>

&#x20;               \<artifactId>mapstruct-processor\</artifactId>

&#x20;               \<version>1.5.2.Final\</version>

&#x20;           \</path>

&#x20;           \<path>

&#x20;               \<groupId>org.projectlombok\</groupId>

&#x20;               \<artifactId>lombok\</artifactId>

&#x20;               \<version>1.18.30\</version>

&#x20;           \</path>

&#x20;       \</annotationProcessorPaths>

&#x20;   \</configuration>

\</plugin>
```

**执行顺序注意事项**：



* 处理器按配置顺序执行，前面的处理器生成的代码可供后面的处理器使用

* 某些框架（如 MapStruct）依赖于其他处理器（如 Lombok）生成的方法，必须正确配置顺序

* 避免处理器之间的循环依赖，这可能导致编译错误

### 2.5 处理器作用域配置

注解处理器在运行时并不需要，因此应使用**provided**或专门的**annotationProcessor**作用域：



```
\<dependency>

&#x20;   \<groupId>com.example\</groupId>

&#x20;   \<artifactId>custom-processor\</artifactId>

&#x20;   \<version>1.0.0\</version>

&#x20;   \<scope>provided\</scope>

\</dependency>
```

**作用域选择建议**：



* **provided**：标准的 Maven 作用域，确保处理器只在编译时使用

* **annotationProcessor**：某些构建工具支持的专门作用域，提供更好的隔离性

* 避免使用**compile**或**runtime**作用域，防止处理器被打包进最终应用

## 3. IntelliJ IDEA 配置与开发设置

### 3.1 启用 Annotation Processing 功能

在 IntelliJ IDEA 中启用 APT 功能需要进行以下配置：



1. 打开 IntelliJ IDEA 的设置：**File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors**

2. 勾选**Enable annotation processing**选项

3. 选择**Obtain processors from project classpath**（从项目类路径获取处理器）

4. 点击**OK**保存配置[(116)](https://www.jetbrains.com.cn/help/idea/annotation-processors-support.html?section=flex_reference.xml)

**配置说明**：



* **Enable annotation processing**：必须勾选此选项才能启用注解处理功能

* **Obtain processors from project classpath**：如果使用 Maven 管理依赖，建议保持此选项启用

* **Processor path**：如果处理器存储在项目外部，需要手动指定路径

### 3.2 注解配置文件管理

IntelliJ IDEA 使用**注解配置文件**来管理不同模块的注解处理配置：



1. 打开注解处理器设置页面

2. 在右上方的配置文件下拉菜单中，可以创建新的配置文件

3. 使用**F6 快捷键**将模块移动到不同的配置文件中

4. 每个配置文件可以独立配置处理器路径、输出目录等选项

**配置文件优势**：



* 支持为不同模块设置不同的注解处理配置

* 可以为测试模块和主模块配置不同的处理器

* 便于管理复杂项目中的多个处理器配置

### 3.3 生成代码输出目录配置

配置生成代码的输出目录是确保项目结构清晰的重要步骤：



1. 在注解处理器设置页面，选择要配置的配置文件

2. 勾选**Store generated sources relative to**选项

3. 在**Production sources**和**Test sources**字段中指定输出目录

4. 如果留空，生成的文件将存储在项目输出目录下

**推荐的目录结构**：



* **target/generated-sources/annotations**：默认的生成代码目录

* **src/generated/java**：某些项目偏好使用的目录

* 保持与 Maven 配置一致，避免路径冲突

### 3.4 处理器全限定名配置

如果需要精确控制运行哪些处理器，可以指定处理器的全限定名：



1. 在注解处理器设置页面的**Processor FQ names**字段中输入处理器类名

2. 如果未指定任何内容，IntelliJ IDEA 将启动在指定位置检测到的所有处理器

3. 可以输入多个处理器类名，用逗号分隔

**使用场景**：



* 当项目中有多个处理器但只需要执行特定处理器时

* 当需要调试特定处理器时

* 当处理器之间存在冲突需要选择性执行时

### 3.5 处理器选项配置

可以为处理器配置运行选项，格式为 \*\*-key=value**或**key=value\*\*：



1. 在注解处理器设置页面的**Annotation processor options**区域输入选项

2. 使用空格分隔各个选项

3. 这些选项将通过 javac 的 \*\*-A\*\* 参数传递给处理器

**示例配置**：



* **-Adebug=true**：启用处理器的调试模式

* **-AoutputDir=generated**：指定处理器的输出目录

* **-Aloglevel=INFO**：设置处理器的日志级别

### 3.6 常见问题与解决方案

**问题 1：Lombok 注解未被处理**

如果 Lombok 注解（如 @Data、@Getter 等）未被正确处理，需要检查以下配置：



1. 确保已在 pom.xml 中添加 Lombok 依赖

2. 检查 IntelliJ IDEA 的注解处理器设置，确保已启用注解处理

3. 确认**Obtain processors from project classpath**选项已选中

4. 如果使用 SSH Gateway，可能需要特殊配置[(121)](https://intellij-support.jetbrains.com/hc/en-us/community/posts/9545937342482-Lombok-annotations-not-processed-when-using-Gateway-over-SSH)

**问题 2：处理器未被识别**

如果自定义处理器未被 IDE 识别：



1. 检查 Maven 依赖是否正确配置

2. 确认处理器已通过 @AutoService 注解或 META-INF/services 文件注册

3. 尝试重新导入 Maven 项目

4. 检查处理器类是否继承了 AbstractProcessor 并实现了必要的方法

**问题 3：生成代码未显示在项目中**

如果生成的代码未在 IDE 中显示：



1. 确保生成代码的目录已被标记为**Generated Sources Root**

2. 在 IntelliJ IDEA 中，右键点击生成目录并选择**Mark Directory as > Generated Sources Root**

3. 重新编译项目，确保生成代码被正确索引

## 4. javax.annotation.processing API 详解

### 4.1 Processor 接口深度解析

**Processor 接口**是所有注解处理器的基础，定义了注解处理器的完整生命周期。理解其工作流程对于开发高效的处理器至关重要。

#### 生命周期方法

Processor 接口的生命周期遵循以下严格顺序：



1. **构造函数调用**：工具调用处理器类的无参构造函数创建实例

2. **init () 方法调用**：初始化处理器，传递 ProcessingEnvironment 对象

3. **getSupportedAnnotationTypes () 方法调用**：获取支持的注解类型

4. **getSupportedSourceVersion () 方法调用**：获取支持的 Java 版本

5. **process () 方法调用**：处理注解的核心方法，可能被调用多次

#### process () 方法详解

**process () 方法**是处理器的核心，其定义如下：



```
boolean process(Set\<? extends TypeElement> annotations, RoundEnvironment roundEnv)
```

**参数说明**：



* **annotations**：当前轮次需要处理的注解类型集合

* **roundEnv**：当前轮次的环境信息，包括被注解的元素等

**返回值含义**：



* 返回**true**表示已处理这些注解，其他处理器将不再处理

* 返回**false**表示未处理或部分处理，其他处理器仍可处理

* 如果处理器支持 "\*" 通配符并返回 true，将声明对所有注解的独占处理

**重要注意事项**：



* 处理器必须优雅地处理空注解集合

* 如果处理器抛出未捕获的异常，工具可能会停止其他活动处理器

* 处理器应具有正交性、一致性、交换性和独立性等特性

### 4.2 ProcessingEnvironment 环境对象

**ProcessingEnvironment 接口**提供了处理器与编译环境交互的各种工具：



```
public interface ProcessingEnvironment {

&#x20;   Map\<String, String> getOptions();

&#x20;   Types getTypeUtils();

&#x20;   Elements getElementUtils();

&#x20;   Filer getFiler();

&#x20;   Messager getMessager();

&#x20;   SourceVersion getSourceVersion();

&#x20;   ...

}
```

#### 核心工具类



1. **Types**：提供类型操作的工具方法，如类型比较、类型检查等

2. **Elements**：提供元素操作的工具方法，如获取元素信息、查找类型元素等

3. **Filer**：用于创建新文件，是代码生成的核心工具

4. **Messager**：用于报告错误、警告和其他通知

5. **SourceVersion**：表示 Java 源版本

#### 初始化示例



```
@Override

public synchronized void init(ProcessingEnvironment processingEnv) {

&#x20;   super.init(processingEnv);

&#x20;   Types typeUtils = processingEnv.getTypeUtils();

&#x20;   Elements elementUtils = processingEnv.getElementUtils();

&#x20;   Filer filer = processingEnv.getFiler();

&#x20;   Messager messager = processingEnv.getMessager();

&#x20;   // 存储这些工具供后续使用

}
```

### 4.3 Filer 接口：代码生成核心

**Filer 接口**是生成新文件的核心工具，支持创建 Java 源文件、类文件和资源文件：

#### 创建 Java 源文件



```
JavaFileObject javaFile = filer.createSourceFile("com.example.GeneratedClass");

try (PrintWriter out = new PrintWriter(javaFile.openWriter())) {

&#x20;   out.println("package com.example;");

&#x20;   out.println("public class GeneratedClass { }");

} catch (IOException e) {

&#x20;   // 处理异常

}
```

#### 创建类文件



```
JavaFileObject classFile = filer.createClassFile("com.example.GeneratedClass");
```

#### 创建资源文件



```
FileObject resourceFile = filer.createResource(StandardLocation.CLASS\_OUTPUT,&#x20;

&#x20;                                           "com.example.resources",&#x20;

&#x20;                                           "data.properties");
```

**注意事项**：



* Filer 确保生成文件的唯一性，避免重复写入

* 生成的文件会自动参与后续的编译过程

* 需要处理可能的 I/O 异常

### 4.4 Messager 接口：错误报告机制

**Messager 接口**用于向开发者报告错误、警告和其他信息：



```
Messager messager = processingEnv.getMessager();

messager.printMessage(Diagnostic.Kind.ERROR, "错误信息", element);

messager.printMessage(Diagnostic.Kind.WARNING, "警告信息", element);

messager.printMessage(Diagnostic.Kind.NOTE, "提示信息", element);

messager.printMessage(Diagnostic.Kind.OTHER, "其他信息", element);
```

**诊断级别说明**：



* **ERROR**：阻止编译继续，必须修复

* **WARNING**：提示潜在问题，不中断编译

* **NOTE**：提供额外信息，如生成提示

* **OTHER**：普通输出，通常不显示

### 4.5 元素操作与类型检查

在注解处理器中，**Element 接口**是操作程序元素的核心：

#### 元素类型判断



```
for (Element element : elements) {

&#x20;   if (element.getKind() == ElementKind.FIELD) {

&#x20;       VariableElement field = (VariableElement) element;

&#x20;       // 处理字段

&#x20;   } else if (element.getKind() == ElementKind.METHOD) {

&#x20;       ExecutableElement method = (ExecutableElement) element;

&#x20;       // 处理方法

&#x20;   }

}
```

#### 类型检查

使用**Types 工具类**进行类型检查：



```
Types typeUtils = processingEnv.getTypeUtils();

TypeElement stringType = elementUtils.getTypeElement("java.lang.String");

TypeMirror fieldType = field.asType();

if (typeUtils.isSubtype(fieldType, stringType.asType())) {

&#x20;   // 字段类型是String或其子类型

}
```

#### 元素信息获取



```
String className = typeElement.getSimpleName().toString();

String qualifiedName = typeElement.getQualifiedName().toString();

List\<? extends Element> enclosedElements = typeElement.getEnclosedElements();
```

## 5. 基础到进阶的开发实践示例

### 5.1 Hello World 处理器示例

让我们从一个最简单的处理器开始，展示 APT 的基本工作流程。

#### 自定义注解定义



```
package com.example.annotations;

import java.lang.annotation.ElementType;

import java.lang.annotation.Retention;

import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)

@Target(ElementType.TYPE)

public @interface HelloWorldAnnotation {

&#x20;   String value() default "Hello, APT!";

}
```

#### 注解处理器实现



```
package com.example.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;

import javax.annotation.processing.RoundEnvironment;

import javax.annotation.processing.SupportedAnnotationTypes;

import javax.annotation.processing.SupportedSourceVersion;

import javax.lang.model.SourceVersion;

import javax.lang.model.element.Element;

import javax.lang.model.element.TypeElement;

import java.util.Set;

@AutoService(javax.annotation.processing.Processor.class)

@SupportedAnnotationTypes("com.example.annotations.HelloWorldAnnotation")

@SupportedSourceVersion(SourceVersion.RELEASE\_8)

public class HelloWorldProcessor extends AbstractProcessor {

&#x20;   @Override

&#x20;   public boolean process(Set\<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

&#x20;       for (Element element : roundEnv.getElementsAnnotatedWith(HelloWorldAnnotation.class)) {

&#x20;           HelloWorldAnnotation annotation = element.getAnnotation(HelloWorldAnnotation.class);

&#x20;           processingEnv.getMessager().printMessage(

&#x20;               javax.tools.Diagnostic.Kind.NOTE,

&#x20;               "Processing class: " + element.getSimpleName() +&#x20;

&#x20;               " with value: " + annotation.value()

&#x20;           );

&#x20;       }

&#x20;       return true;

&#x20;   }

}
```

#### 使用示例



```
package com.example;

import com.example.annotations.HelloWorldAnnotation;

@HelloWorldAnnotation("Hello, Annotation Processing!")

public class MyAnnotatedClass {

&#x20;   // 类实现

}
```

#### 运行结果

当编译 MyAnnotatedClass 时，处理器会输出：



```
Processing class: MyAnnotatedClass with value: Hello, Annotation Processing!
```

### 5.2 自动生成 Getter 方法示例

这个示例展示如何根据 @GenerateGetter 注解自动生成 getter 方法。

#### 自定义注解



```
@Retention(RetentionPolicy.SOURCE)

@Target(ElementType.FIELD)

public @interface GenerateGetter {}
```

#### 处理器实现



```
@AutoService(Processor.class)

@SupportedAnnotationTypes("com.example.annotations.GenerateGetter")

@SupportedSourceVersion(SourceVersion.RELEASE\_8)

public class GetterGeneratorProcessor extends AbstractProcessor {

&#x20;   @Override

&#x20;   public boolean process(Set\<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

&#x20;       Filer filer = processingEnv.getFiler();

&#x20;      &#x20;

&#x20;       for (Element element : roundEnv.getElementsAnnotatedWith(GenerateGetter.class)) {

&#x20;           if (element.getKind() != ElementKind.FIELD) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   "@GenerateGetter can only be applied to fields",

&#x20;                   element

&#x20;               );

&#x20;               continue;

&#x20;           }

&#x20;          &#x20;

&#x20;           VariableElement field = (VariableElement) element;

&#x20;           TypeElement enclosingClass = (TypeElement) field.getEnclosingElement();

&#x20;          &#x20;

&#x20;           // 生成getter方法代码

&#x20;           String fieldName = field.getSimpleName().toString();

&#x20;           String capitalizedName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

&#x20;           String getterMethod = String.format(

&#x20;               "public %s get%s() { return %s; }",

&#x20;               field.asType(), capitalizedName, fieldName

&#x20;           );

&#x20;          &#x20;

&#x20;           // 创建或更新包含getter的类

&#x20;           try {

&#x20;               JavaFileObject jfo = filer.createSourceFile(enclosingClass.getQualifiedName().toString());

&#x20;               try (PrintWriter out = new PrintWriter(jfo.openWriter())) {

&#x20;                   // 简化的类定义，实际应用需要更复杂的处理

&#x20;                   out.println("package " + enclosingClass.getQualifiedName() + ";");

&#x20;                   out.println("public class " + enclosingClass.getSimpleName() + " {");

&#x20;                   out.println("    private " + field.asType() + " " + fieldName + ";");

&#x20;                   out.println("    " + getterMethod);

&#x20;                   out.println("}");

&#x20;               }

&#x20;           } catch (IOException e) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   "Failed to generate getter for field: " + fieldName,

&#x20;                   element

&#x20;               );

&#x20;           }

&#x20;       }

&#x20;       return true;

&#x20;   }

}
```

#### 使用示例



```
public class User {

&#x20;   @GenerateGetter

&#x20;   private String name;

&#x20;  &#x20;

&#x20;   @GenerateGetter

&#x20;   private int age;

}
```

#### 生成结果

处理器将生成包含 getter 方法的 User 类：



```
public class User {

&#x20;   private String name;

&#x20;   private int age;

&#x20;  &#x20;

&#x20;   public String getName() { return name; }

&#x20;   public int getAge() { return age; }

}
```

### 5.3 代码验证示例：检查方法参数

这个示例展示如何使用 APT 进行编译时代码验证，确保方法参数符合特定规则。

#### 自定义注解



```
@Retention(RetentionPolicy.SOURCE)

@Target(ElementType.METHOD)

public @interface ValidateParams {

&#x20;   String message() default "Invalid method parameters";

}
```

#### 处理器实现



```
@AutoService(Processor.class)

@SupportedAnnotationTypes("com.example.annotations.ValidateParams")

@SupportedSourceVersion(SourceVersion.RELEASE\_8)

public class ParameterValidatorProcessor extends AbstractProcessor {

&#x20;   @Override

&#x20;   public boolean process(Set\<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

&#x20;       for (Element element : roundEnv.getElementsAnnotatedWith(ValidateParams.class)) {

&#x20;           if (element.getKind() != ElementKind.METHOD) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   "@ValidateParams can only be applied to methods",

&#x20;                   element

&#x20;               );

&#x20;               continue;

&#x20;           }

&#x20;          &#x20;

&#x20;           ExecutableElement method = (ExecutableElement) element;

&#x20;           ValidateParams annotation = method.getAnnotation(ValidateParams.class);

&#x20;          &#x20;

&#x20;           // 验证方法参数规则

&#x20;           int paramCount = method.getParameters().size();

&#x20;           if (paramCount < 2) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   annotation.message() + ": Method must have at least 2 parameters",

&#x20;                   method

&#x20;               );

&#x20;           }

&#x20;          &#x20;

&#x20;           // 验证第一个参数必须是String类型

&#x20;           VariableElement firstParam = method.getParameters().get(0);

&#x20;           TypeElement stringType = elementUtils.getTypeElement("java.lang.String");

&#x20;           if (!processingEnv.getTypeUtils().isSubtype(

&#x20;               firstParam.asType(), stringType.asType()

&#x20;           )) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   annotation.message() + ": First parameter must be String",

&#x20;                   firstParam

&#x20;               );

&#x20;           }

&#x20;       }

&#x20;       return true;

&#x20;   }

}
```

#### 使用示例



```
public class Calculator {

&#x20;  &#x20;

&#x20;   @ValidateParams(message = "Add method parameters invalid")

&#x20;   public int add(String operation, int a, int b) {

&#x20;       return a + b;

&#x20;   }

&#x20;  &#x20;

&#x20;   @ValidateParams

&#x20;   public int multiply(int a, int b) {

&#x20;       // 这里会触发错误，因为第一个参数不是String

&#x20;       return a \* b;

&#x20;   }

}
```

#### 验证结果

编译时会产生以下错误：



```
Error: Add method parameters invalid: First parameter must be String

&#x20; at Calculator.multiply(Calculator.java:10)

Error: @ValidateParams: Method must have at least 2 parameters

&#x20; at Calculator.multiply(Calculator.java:10)
```

### 5.4 复杂示例：自动生成 Builder 模式

这个示例展示如何实现类似 Lombok 的 @Builder 注解，自动生成建造者模式代码。

#### 自定义注解



```
@Retention(RetentionPolicy.SOURCE)

@Target(ElementType.TYPE)

public @interface Builder {

&#x20;   String builderClassName() default "";

}
```

#### 处理器实现



```
@AutoService(Processor.class)

@SupportedAnnotationTypes("com.example.annotations.Builder")

@SupportedSourceVersion(SourceVersion.RELEASE\_8)

public class BuilderGeneratorProcessor extends AbstractProcessor {

&#x20;   @Override

&#x20;   public boolean process(Set\<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

&#x20;       Filer filer = processingEnv.getFiler();

&#x20;      &#x20;

&#x20;       for (Element element : roundEnv.getElementsAnnotatedWith(Builder.class)) {

&#x20;           if (element.getKind() != ElementKind.CLASS) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   "@Builder can only be applied to classes",

&#x20;                   element

&#x20;               );

&#x20;               continue;

&#x20;           }

&#x20;          &#x20;

&#x20;           TypeElement classElement = (TypeElement) element;

&#x20;           Builder annotation = classElement.getAnnotation(Builder.class);

&#x20;          &#x20;

&#x20;           // 获取类的所有字段

&#x20;           List\<VariableElement> fields = new ArrayList<>();

&#x20;           for (Element enclosed : classElement.getEnclosedElements()) {

&#x20;               if (enclosed.getKind() == ElementKind.FIELD) {

&#x20;                   fields.add((VariableElement) enclosed);

&#x20;               }

&#x20;           }

&#x20;          &#x20;

&#x20;           // 生成Builder类代码

&#x20;           try {

&#x20;               String builderClassName = annotation.builderClassName().isEmpty()&#x20;

&#x20;                   ? classElement.getSimpleName() + "Builder"&#x20;

&#x20;                   : annotation.builderClassName();

&#x20;              &#x20;

&#x20;               JavaFileObject jfo = filer.createSourceFile(

&#x20;                   classElement.getQualifiedName() + "\$" + builderClassName

&#x20;               );

&#x20;              &#x20;

&#x20;               try (PrintWriter out = new PrintWriter(jfo.openWriter())) {

&#x20;                   // 生成Builder类

&#x20;                   out.println("package " + classElement.getQualifiedName() + ";");

&#x20;                   out.println();

&#x20;                   out.println("public class " + builderClassName + " {");

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // 为每个字段创建对应的builder字段

&#x20;                   for (VariableElement field : fields) {

&#x20;                       String fieldName = field.getSimpleName().toString();

&#x20;                       out.println("    private " + field.asType() + " " + fieldName + ";");

&#x20;                   }

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // 生成构造方法

&#x20;                   out.println("    private " + builderClassName + "() { }");

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // 生成setter方法

&#x20;                   for (VariableElement field : fields) {

&#x20;                       String fieldName = field.getSimpleName().toString();

&#x20;                       String capitalized = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

&#x20;                       out.println("    public " + builderClassName + " " + fieldName + "(" +&#x20;

&#x20;                                   field.asType() + " " + fieldName + ") {");

&#x20;                       out.println("        this." + fieldName + " = " + fieldName + ";");

&#x20;                       out.println("        return this;");

&#x20;                       out.println("    }");

&#x20;                       out.println();

&#x20;                   }

&#x20;                  &#x20;

&#x20;                   // 生成build方法

&#x20;                   out.println("    public " + classElement.getSimpleName() + " build() {");

&#x20;                   out.println("        return new " + classElement.getSimpleName() + "(");

&#x20;                  &#x20;

&#x20;                   // 收集所有字段用于构造方法调用

&#x20;                   List\<String> paramList = fields.stream()

&#x20;                       .map(f -> f.getSimpleName().toString())

&#x20;                       .collect(Collectors.toList());

&#x20;                  &#x20;

&#x20;                   out.println("            " + String.join(", ", paramList) + ");");

&#x20;                   out.println("    }");

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // 生成newBuilder方法

&#x20;                   out.println("    public static " + builderClassName + " newBuilder() {");

&#x20;                   out.println("        return new " + builderClassName + "();");

&#x20;                   out.println("    }");

&#x20;                   out.println("}");

&#x20;               }

&#x20;           } catch (IOException e) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   "Failed to generate builder class for: " + classElement.getSimpleName(),

&#x20;                   element

&#x20;               );

&#x20;           }

&#x20;          &#x20;

&#x20;           // 为目标类添加newBuilder方法

&#x20;           try {

&#x20;               JavaFileObject originalFile = filer.createSourceFile(

&#x20;                   classElement.getQualifiedName().toString()

&#x20;               );

&#x20;              &#x20;

&#x20;               try (PrintWriter out = new PrintWriter(originalFile.openWriter())) {

&#x20;                   // 简化的类定义，实际需要更复杂的处理

&#x20;                   out.println("package " + classElement.getQualifiedName() + ";");

&#x20;                   out.println();

&#x20;                   out.println("public class " + classElement.getSimpleName() + " {");

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // 字段定义

&#x20;                   for (VariableElement field : fields) {

&#x20;                       out.println("    private final " + field.asType() + " " + field.getSimpleName() + ";");

&#x20;                   }

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // 构造方法

&#x20;                   out.println("    public " + classElement.getSimpleName() + "(" +&#x20;

&#x20;                               String.join(", ", fields.stream()

&#x20;                                   .map(f -> f.asType() + " " + f.getSimpleName())

&#x20;                                   .collect(Collectors.toList())) + ") {");

&#x20;                   for (VariableElement field : fields) {

&#x20;                       out.println("        this." + field.getSimpleName() + " = " + field.getSimpleName() + ";");

&#x20;                   }

&#x20;                   out.println("    }");

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // newBuilder方法

&#x20;                   out.println("    public static " + builderClassName + " newBuilder() {");

&#x20;                   out.println("        return " + builderClassName + ".newBuilder();");

&#x20;                   out.println("    }");

&#x20;                   out.println("}");

&#x20;               }

&#x20;           } catch (IOException e) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   "Failed to update original class with newBuilder method",

&#x20;                   element

&#x20;               );

&#x20;           }

&#x20;       }

&#x20;       return true;

&#x20;   }

}
```

#### 使用示例



```
@Builder

public class User {

&#x20;   private final String name;

&#x20;   private final int age;

&#x20;   private final String email;

}
```

#### 生成结果

处理器将生成 UserBuilder 类和相关方法：



```
public class User {

&#x20;   private final String name;

&#x20;   private final int age;

&#x20;   private final String email;

&#x20;  &#x20;

&#x20;   public User(String name, int age, String email) {

&#x20;       this.name = name;

&#x20;       this.age = age;

&#x20;       this.email = email;

&#x20;   }

&#x20;  &#x20;

&#x20;   public static UserBuilder newBuilder() {

&#x20;       return UserBuilder.newBuilder();

&#x20;   }

}

public class UserBuilder {

&#x20;   private String name;

&#x20;   private int age;

&#x20;   private String email;

&#x20;  &#x20;

&#x20;   private UserBuilder() { }

&#x20;  &#x20;

&#x20;   public UserBuilder name(String name) {

&#x20;       this.name = name;

&#x20;       return this;

&#x20;   }

&#x20;  &#x20;

&#x20;   public UserBuilder age(int age) {

&#x20;       this.age = age;

&#x20;       return this;

&#x20;   }

&#x20;  &#x20;

&#x20;   public UserBuilder email(String email) {

&#x20;       this.email = email;

&#x20;       return this;

&#x20;   }

&#x20;  &#x20;

&#x20;   public User build() {

&#x20;       return new User(name, age, email);

&#x20;   }

&#x20;  &#x20;

&#x20;   public static UserBuilder newBuilder() {

&#x20;       return new UserBuilder();

&#x20;   }

}
```

### 5.5 数据库映射代码生成示例

这个示例展示如何根据 @Entity 和 @Column 注解自动生成数据库映射代码。

#### 自定义注解



```
@Retention(RetentionPolicy.SOURCE)

@Target(ElementType.TYPE)

public @interface Entity {

&#x20;   String tableName();

}

@Retention(RetentionPolicy.SOURCE)

@Target(ElementType.FIELD)

public @interface Column {

&#x20;   String columnName();

&#x20;   String type();

}
```

#### 处理器实现



```
@AutoService(Processor.class)

@SupportedAnnotationTypes({

&#x20;   "com.example.annotations.Entity",

&#x20;   "com.example.annotations.Column"

})

@SupportedSourceVersion(SourceVersion.RELEASE\_8)

public class ORMMapperGenerator extends AbstractProcessor {

&#x20;   @Override

&#x20;   public boolean process(Set\<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

&#x20;       Filer filer = processingEnv.getFiler();

&#x20;      &#x20;

&#x20;       // 处理@Entity注解的类

&#x20;       for (Element element : roundEnv.getElementsAnnotatedWith(Entity.class)) {

&#x20;           if (element.getKind() != ElementKind.CLASS) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   "@Entity can only be applied to classes",

&#x20;                   element

&#x20;               );

&#x20;               continue;

&#x20;           }

&#x20;          &#x20;

&#x20;           TypeElement classElement = (TypeElement) element;

&#x20;           Entity entityAnnotation = classElement.getAnnotation(Entity.class);

&#x20;          &#x20;

&#x20;           // 收集字段信息

&#x20;           List\<FieldInfo> fieldInfos = new ArrayList<>();

&#x20;           for (Element enclosed : classElement.getEnclosedElements()) {

&#x20;               if (enclosed.getKind() == ElementKind.FIELD) {

&#x20;                   VariableElement field = (VariableElement) enclosed;

&#x20;                   Column columnAnnotation = field.getAnnotation(Column.class);

&#x20;                   if (columnAnnotation != null) {

&#x20;                       fieldInfos.add(new FieldInfo(

&#x20;                           field.getSimpleName().toString(),

&#x20;                           columnAnnotation.columnName(),

&#x20;                           columnAnnotation.type(),

&#x20;                           field.asType()

&#x20;                       ));

&#x20;                   }

&#x20;               }

&#x20;           }

&#x20;          &#x20;

&#x20;           // 生成映射器代码

&#x20;           try {

&#x20;               JavaFileObject mapperFile = filer.createSourceFile(

&#x20;                   classElement.getQualifiedName() + "Mapper"

&#x20;               );

&#x20;              &#x20;

&#x20;               try (PrintWriter out = new PrintWriter(mapperFile.openWriter())) {

&#x20;                   out.println("package " + classElement.getQualifiedName() + ";");

&#x20;                   out.println();

&#x20;                   out.println("import java.sql.ResultSet;");

&#x20;                   out.println("import java.sql.SQLException;");

&#x20;                   out.println();

&#x20;                   out.println("public class " + classElement.getSimpleName() + "Mapper {");

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // 生成从ResultSet到实体的映射方法

&#x20;                   out.println("    public static " + classElement.getSimpleName() + " map(ResultSet rs) throws SQLException {");

&#x20;                   out.println("        " + classElement.getSimpleName() + " entity = new " +&#x20;

&#x20;                               classElement.getSimpleName() + "();");

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   for (FieldInfo field : fieldInfos) {

&#x20;                       String javaType = field.type.toString();

&#x20;                       String methodName = getResultSetMethod(javaType);

&#x20;                      &#x20;

&#x20;                       if (methodName != null) {

&#x20;                           out.println("        entity.set" + capitalize(field.fieldName) + "(" +&#x20;

&#x20;                                       "rs." + methodName + "(" +&#x20;

&#x20;                                       "\\"" + field.columnName + "\\"));");

&#x20;                       } else {

&#x20;                           processingEnv.getMessager().printMessage(

&#x20;                               Diagnostic.Kind.WARNING,

&#x20;                               "Unsupported type for field: " + field.fieldName,

&#x20;                               classElement

&#x20;                           );

&#x20;                       }

&#x20;                   }

&#x20;                  &#x20;

&#x20;                   out.println("        return entity;");

&#x20;                   out.println("    }");

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // 生成SQL语句相关方法

&#x20;                   out.println("    public static String getInsertSQL() {");

&#x20;                   out.println("        return \\"INSERT INTO " + entityAnnotation.tableName() + " (\\" +");

&#x20;                  &#x20;

&#x20;                   List\<String> columnList = fieldInfos.stream()

&#x20;                       .map(f -> "\\"" + f.columnName + "\\"")

&#x20;                       .collect(Collectors.toList());

&#x20;                  &#x20;

&#x20;                   out.println("            " + String.join(" + \\", \\" + ", columnList) + " + \\") VALUES (\\" +");

&#x20;                  &#x20;

&#x20;                   List\<String> paramList = fieldInfos.stream()

&#x20;                       .map(f -> "?")

&#x20;                       .collect(Collectors.toList());

&#x20;                  &#x20;

&#x20;                   out.println("            " + String.join(" + \\", \\" + ", paramList) + " + \\")\\";");

&#x20;                   out.println("    }");

&#x20;                   out.println();

&#x20;                  &#x20;

&#x20;                   // 生成获取所有字段名的方法

&#x20;                   out.println("    public static String\[] getColumnNames() {");

&#x20;                   out.println("        return new String\[] {" +&#x20;

&#x20;                               String.join(", ", fieldInfos.stream()

&#x20;                                   .map(f -> "\\"" + f.columnName + "\\"")

&#x20;                                   .collect(Collectors.toList())) + "};");

&#x20;                   out.println("    }");

&#x20;                   out.println("}");

&#x20;               }

&#x20;           } catch (IOException e) {

&#x20;               processingEnv.getMessager().printMessage(

&#x20;                   Diagnostic.Kind.ERROR,

&#x20;                   "Failed to generate mapper for entity: " + classElement.getSimpleName(),

&#x20;                   element

&#x20;               );

&#x20;           }

&#x20;       }

&#x20;       return true;

&#x20;   }

&#x20;  &#x20;

&#x20;   private String getResultSetMethod(String javaType) {

&#x20;       Map\<String, String> typeMap = new HashMap<>();

&#x20;       typeMap.put("java.lang.String", "getString");

&#x20;       typeMap.put("int", "getInt");

&#x20;       typeMap.put("java.lang.Integer", "getInt");

&#x20;       typeMap.put("double", "getDouble");

&#x20;       typeMap.put("java.lang.Double", "getDouble");

&#x20;       typeMap.put("boolean", "getBoolean");

&#x20;       typeMap.put("java.lang.Boolean", "getBoolean");

&#x20;       return typeMap.get(javaType);

&#x20;   }

&#x20;  &#x20;

&#x20;   private String capitalize(String str) {

&#x20;       return str.substring(0, 1).toUpperCase() + str.substring(1);

&#x20;   }

&#x20;  &#x20;

&#x20;   private static class FieldInfo {

&#x20;       String fieldName;

&#x20;       String columnName;

&#x20;       String sqlType;

&#x20;       TypeMirror type;

&#x20;      &#x20;

&#x20;       FieldInfo(String fieldName, String columnName, String sqlType, TypeMirror type) {

&#x20;           this.fieldName = fieldName;

&#x20;           this.columnName = columnName;

&#x20;           this.sqlType = sqlType;

&#x20;           this.type = type;

&#x20;       }

&#x20;   }

}
```

#### 使用示例



```
@Entity(tableName = "users")

public class User {

&#x20;   @Column(columnName = "id", type = "INT")

&#x20;   private int id;

&#x20;  &#x20;

&#x20;   @Column(columnName = "name", type = "VARCHAR")

&#x20;   private String name;

&#x20;  &#x20;

&#x20;   @Column(columnName = "age", type = "INT")

&#x20;   private int age;

&#x20;  &#x20;

&#x20;   // 省略getter和setter

}
```

#### 生成结果

处理器将生成 UserMapper 类：



```
public class UserMapper {

&#x20;  &#x20;

&#x20;   public static User map(ResultSet rs) throws SQLException {

&#x20;       User entity = new User();

&#x20;       entity.setId(rs.getInt("id"));

&#x20;       entity.setName(rs.getString("name"));

&#x20;       entity.setAge(rs.getInt("age"));

&#x20;       return entity;

&#x20;   }

&#x20;  &#x20;

&#x20;   public static String getInsertSQL() {

&#x20;       return "INSERT INTO users (id, name, age) VALUES (?, ?, ?)";

&#x20;   }

&#x20;  &#x20;

&#x20;   public static String\[] getColumnNames() {

&#x20;       return new String\[] {"id", "name", "age"};

&#x20;   }

}
```

## 6. 高级主题与最佳实践

### 6.1 多处理器协同工作

在复杂项目中，通常需要多个处理器协同工作。正确管理处理器间的依赖关系和执行顺序至关重要。

#### 处理器执行顺序规则



1. **配置顺序决定执行顺序**：在 annotationProcessorPaths 中，前面的处理器先执行

2. **处理器独立性**：每个处理器应该独立工作，不依赖其他处理器的状态

3. **避免循环依赖**：确保处理器之间不会形成循环调用

#### 协同工作示例：Lombok 与 MapStruct 集成

以下是一个典型的配置，展示了如何配置多个处理器：



```
\<annotationProcessorPaths>

&#x20;   \<!-- 1. MapStruct处理器需要先执行，因为它依赖于Lombok生成的getter/setter -->

&#x20;   \<path>

&#x20;       \<groupId>org.mapstruct\</groupId>

&#x20;       \<artifactId>mapstruct-processor\</artifactId>

&#x20;       \<version>1.5.2.Final\</version>

&#x20;   \</path>

&#x20;  &#x20;

&#x20;   \<!-- 2. Lombok处理器生成getter/setter方法 -->

&#x20;   \<path>

&#x20;       \<groupId>org.projectlombok\</groupId>

&#x20;       \<artifactId>lombok\</artifactId>

&#x20;       \<version>1.18.30\</version>

&#x20;   \</path>

&#x20;  &#x20;

&#x20;   \<!-- 3. 自定义处理器，可能依赖于前两个处理器生成的代码 -->

&#x20;   \<path>

&#x20;       \<groupId>com.example\</groupId>

&#x20;       \<artifactId>custom-processor\</artifactId>

&#x20;       \<version>1.0.0\</version>

&#x20;   \</path>

\</annotationProcessorPaths>
```

#### 处理器间通信

处理器间的通信可以通过以下方式实现：



1. **通过生成的代码通信**：前一个处理器生成的代码可供后续处理器使用

2. **使用 roundEnv.getRootElements ()**：获取所有根元素，包括其他处理器生成的元素

3. **使用注解标记**：处理器可以在元素上添加特殊注解，供其他处理器识别

### 6.2 性能优化策略

APT 处理器的性能直接影响编译速度，以下是一些优化策略：

#### 增量处理

**只处理变化的文件**：



```
@Override

public boolean process(Set\<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

&#x20;   if (roundEnv.processingOver() || annotations.isEmpty()) {

&#x20;       return false;

&#x20;   }

&#x20;  &#x20;

&#x20;   // 只处理本轮新增或修改的元素

&#x20;   for (Element element : roundEnv.getRootElements()) {

&#x20;       if (roundEnv.processingOver()) {

&#x20;           break;

&#x20;       }

&#x20;       // 处理元素

&#x20;   }

&#x20;   return true;

}
```

#### 缓存策略

**缓存常用的 TypeElement**：



```
private Map\<String, TypeElement> typeElementCache = new HashMap<>();

private TypeElement getTypeElement(String className) {

&#x20;   return typeElementCache.computeIfAbsent(className,&#x20;

&#x20;       key -> processingEnv.getElementUtils().getTypeElement(className));

}
```

#### 批量处理

**减少文件操作次数**：



* 将多个相关的代码生成操作合并

* 使用 StringBuilder 批量构建代码

* 最后一次性写入文件

#### 避免不必要的处理

**快速失败策略**：



```
if (element.getKind() != ElementKind.FIELD) {

&#x20;   return; // 只处理字段

}

if (!element.getModifiers().contains(Modifier.PRIVATE)) {

&#x20;   processingEnv.getMessager().printMessage(

&#x20;       Diagnostic.Kind.WARNING,

&#x20;       "Field must be private",

&#x20;       element

&#x20;   );

&#x20;   return;

}
```

### 6.3 模块化环境下的开发

Java 9 引入的模块系统（JPMS）对 APT 开发带来了新的挑战：

#### 模块依赖配置

在 module-info.java 中声明依赖：



```
module my.module {

&#x20;   requires javax.annotation.processing;

&#x20;   requires com.google.auto.service;

&#x20;  &#x20;

&#x20;   // 如果需要访问其他模块的内部API

&#x20;   opens com.example.annotations to my.processor.module;

}
```

#### 处理器注册

在 META-INF/services/javax.annotation.processing.Processor 文件中声明处理器：



```
com.example.processors.MyProcessor
```

#### 跨模块访问限制

如果处理器需要访问其他模块的非公开 API，可能需要使用：



* **--add-exports**命令行选项

* **opens**模块声明

* 谨慎使用，避免破坏封装性

### 6.4 调试技巧与工具

调试 APT 处理器比普通 Java 代码更具挑战性，以下是一些实用技巧：

#### 日志输出

使用 Messager 打印详细信息：



```
processingEnv.getMessager().printMessage(

&#x20;   Diagnostic.Kind.NOTE,

&#x20;   "Processing field: " + field.getSimpleName(),

&#x20;   field

);
```

#### 断点调试

在 IntelliJ IDEA 中调试处理器：



1. 在 processor 的 process () 方法设置断点

2. 使用**mvn compile**命令启动编译

3. IDE 会在断点处暂停，允许查看变量和调用栈

#### 生成代码验证

检查生成的代码是否符合预期：



1. 定位生成代码的输出目录（通常是 target/generated-sources/annotations）

2. 直接查看生成的 Java 文件

3. 使用 diff 工具比较生成代码与预期结果

#### 错误处理最佳实践



1. **使用 try-catch 包围所有可能抛出异常的代码**

2. **提供清晰的错误消息**，包括错误位置和原因

3. **区分错误级别**：ERROR 阻止编译，WARNING 仅提示

4. **避免空指针异常**，总是检查对象是否为 null

### 6.5 与其他技术集成

#### 与构建工具集成

**Gradle 集成**：



```
dependencies {

&#x20;   annotationProcessor 'com.google.auto.service:auto-service:1.0-rc7'

&#x20;   annotationProcessor project(':my-processor')

}
```

**Ant 集成**：

需要使用专门的 Ant 任务来支持 APT 处理

#### 与持续集成系统集成

在 CI/CD 流程中，确保：



* 正确配置构建环境以支持 APT

* 生成的代码被正确提交或忽略

* 编译错误能够被及时捕获和报告

#### 与 IDE 插件集成

某些 IDE（如 Android Studio）提供了专门的 APT 支持：



* 实时预览生成的代码

* 错误提示和代码补全

* 性能优化选项

### 6.6 常见陷阱与解决方案

#### 陷阱 1：无限循环处理

**问题**：处理器生成的代码又触发自身处理，导致无限循环

**解决方案**：



* 使用 roundEnv.processingOver () 判断是否已完成处理

* 记录已处理的元素，避免重复处理

* 合理设计处理器，确保生成的代码不会再次触发相同的处理逻辑

#### 陷阱 2：类路径问题

**问题**：处理器找不到依赖的类或接口

**解决方案**：



* 确保所有依赖都正确添加到类路径

* 使用 Maven 的 annotationProcessorPaths 正确配置

* 检查模块依赖是否正确声明

#### 陷阱 3：线程安全问题

**问题**：多个线程同时访问处理器的共享状态

**解决方案**：



* 确保处理器是无状态的或线程安全的

* 避免在处理器中使用非线程安全的全局变量

* 使用 ThreadLocal 存储线程本地状态

#### 陷阱 4：性能问题

**问题**：大型项目中处理器导致编译速度显著下降

**解决方案**：



* 实现增量处理，只处理变化的部分

* 使用缓存减少重复计算

* 优化算法，避免不必要的操作

* 考虑将复杂处理移到独立的构建步骤

## 7. 总结与最佳实践总结

### 7.1 APT 技术的核心价值

通过本指南的详细讲解，我们深入了解了 Java APT 技术的各个方面。APT 的核心价值在于：



1. **编译期代码生成**：在不影响原有代码的情况下，自动生成辅助代码

2. **类型安全和编译期检查**：所有验证都在编译阶段完成，避免运行时错误

3. **零运行时开销**：生成的代码直接执行，无需反射等运行时机制

4. **提高开发效率**：减少样板代码，自动化重复性任务

### 7.2 关键概念回顾

在掌握 APT 开发时，需要深入理解以下核心概念：



* **Processor 接口**：定义了处理器的生命周期和核心方法

* **Element 层次结构**：表示程序中的各种元素，是处理的基本单位

* **RoundEnvironment**：提供当前轮次的环境信息

* **Filer 和 Messager**：分别用于代码生成和错误报告

* **多轮处理机制**：支持复杂的依赖解析和代码生成

### 7.3 开发最佳实践

基于本指南的示例和讨论，以下是 APT 开发的最佳实践：



1. **项目结构设计**：

* 将注解、处理器和使用代码分离到不同模块

* 使用标准的 Maven 目录结构

* 明确区分编译时依赖和运行时依赖

1. **处理器设计原则**：

* 保持处理器的独立性和正交性

* 实现增量处理以提高性能

* 提供清晰的错误信息和警告

* 避免修改原有类，只生成新文件

1. **性能优化策略**：

* 使用缓存减少重复计算

* 批量处理以减少 I/O 操作

* 实现智能的变化检测

* 避免不必要的类型检查

1. **调试和测试**：

* 使用日志输出详细信息

* 利用 IDE 的调试功能

* 编写单元测试验证生成的代码

* 建立完善的错误处理机制

1. **版本兼容性**：

* 明确声明支持的 Java 版本

* 处理不同 Java 版本间的 API 差异

* 测试在不同环境下的兼容性

### 7.4 未来发展趋势

随着 Java 生态系统的不断演进，APT 技术也在持续发展：



1. **与模块化系统的深度集成**：Java 9 + 的模块系统为 APT 带来了新的可能性和挑战

2. **性能优化**：编译器团队在不断优化 APT 的性能

3. **新的应用场景**：从简单的代码生成到复杂的静态分析

4. **工具支持的增强**：IDE 和构建工具对 APT 的支持越来越好

### 7.5 学习建议

对于希望深入掌握 APT 技术的开发者，建议：



1. **深入阅读官方文档**：javax.annotation.processing 包的 Javadoc 是最好的参考资料

2. **研究开源项目**：如 Lombok、Dagger、MapStruct 等优秀的 APT 应用

3. **实践项目**：从简单的示例开始，逐步构建复杂的处理器

4. **参与社区**：关注相关技术论坛和开源项目的讨论

5. **持续学习**：随着 Java 版本的更新，及时了解 APT 技术的新特性

通过本指南的学习，你已经掌握了在 IntelliJ IDEA + Maven 环境下使用 javax.annotation.processing API 进行 APT 开发的完整知识体系。从基础概念到复杂应用，从环境配置到性能优化，我们都进行了详细的讲解和实践。希望这份指南能够成为你在 APT 技术学习和实践道路上的得力助手，帮助你充分发挥这项强大技术的潜力，提高开发效率，创造更加优雅和高效的 Java 代码。

**参考资料&#x20;**

\[1] 注釈処理ツール (apt ) 入門 apt 目次[ https://docs.oracle.com/javase/jp/1.5.0/guide/apt/GettingStarted.html](https://docs.oracle.com/javase/jp/1.5.0/guide/apt/GettingStarted.html)

\[2] 注解处理器(APT)了解一下\_轩辕的技术博客\_51CTO博客[ https://blog.51cto.com/u\_12227/14368868](https://blog.51cto.com/u_12227/14368868)

\[3] Java编译期魔法揭秘:如何用APT生成高效代码(附完整案例)-CSDN博客[ https://blog.csdn.net/PoliVein/article/details/155193161](https://blog.csdn.net/PoliVein/article/details/155193161)

\[4] Android 源码 看 不懂 ， 设计 思想 学 不会 ？ 架构 师 是 怎么 炼成 的 ？ 8 、 性能 优化 ， SPI 机制 与 字节 码 插 桩 的 优化 实践 （ 下 ） # Android # 源码 # 架构 师 # 设计 思想 # 性能 优化[ https://www.iesdouyin.com/share/video/7293734102841183542/?region=\&mid=7293734404743088933\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=f1iWW\_fh42YuoaHmyVrA0gSlvG3x3d04Ev.YefYxhqw-\&share\_version=280700\&ts=1773053617\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7293734102841183542/?region=\&mid=7293734404743088933\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=f1iWW_fh42YuoaHmyVrA0gSlvG3x3d04Ev.YefYxhqw-\&share_version=280700\&ts=1773053617\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[5] 解析 Java 的注解处理器:自定义注解与代码生成\_自定义注解处理器-CSDN博客[ https://blog.csdn.net/weixin\_73355603/article/details/146727706](https://blog.csdn.net/weixin_73355603/article/details/146727706)

\[6] 深入解析 ARouter 中的 APT 技术:从注解到代码生成的自动化魔法一、APT 技术基础:编译期的 "代码自动生成 - 掘金[ https://juejin.cn/post/7507207338689609739](https://juejin.cn/post/7507207338689609739)

\[7] Package javax.annotation.processing[ http://docs.oracle.com/en/java/javase/24/docs/api/java.compiler/javax/annotation/processing/package-summary.html](http://docs.oracle.com/en/java/javase/24/docs/api/java.compiler/javax/annotation/processing/package-summary.html)

\[8] Java注解 编译\_Java注解(3)-注解处理器(编译期|RetentionPolicy.SOURCE)-CSDN博客[ https://blog.csdn.net/weixin\_36331058/article/details/114251511](https://blog.csdn.net/weixin_36331058/article/details/114251511)

\[9] Java自定义注解实战解析与防重复提交应用[ https://www.iesdouyin.com/share/video/7399997062760238363/?region=\&mid=7399997268687997734\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=MkroxAiWNIdMmofEAAJfXp.4i46RrDCV8ohfrazttbw-\&share\_version=280700\&ts=1773053617\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7399997062760238363/?region=\&mid=7399997268687997734\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=MkroxAiWNIdMmofEAAJfXp.4i46RrDCV8ohfrazttbw-\&share_version=280700\&ts=1773053617\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[10] Compiler Package Overview[ http://openjdk.org/groups/compiler/doc/package-overview/](http://openjdk.org/groups/compiler/doc/package-overview/)

\[11] java 机机接口定义\_自定义Java里的注解处理器(二)-CSDN博客[ https://blog.csdn.net/weixin\_39559469/article/details/114564197](https://blog.csdn.net/weixin_39559469/article/details/114564197)

\[12] Overview-javax.annotation.processing-Java API References - HUAWEI Developers[ https://developer.harmonyos.com/en/docs/documentation/doc-references/overview-0000001123534682](https://developer.harmonyos.com/en/docs/documentation/doc-references/overview-0000001123534682)

\[13] Java 注解与 APT(Annotation Processing Tool)\_java springboot 编译期注解处理器(apt)-CSDN博客[ https://blog.csdn.net/qq\_44608856/article/details/151617672](https://blog.csdn.net/qq_44608856/article/details/151617672)

\[14] 注釈処理ツール (apt ) 入門 apt 目次[ https://docs.oracle.com/javase/jp/1.5.0/guide/apt/GettingStarted.html](https://docs.oracle.com/javase/jp/1.5.0/guide/apt/GettingStarted.html)

\[15] Using annotation processor in IDE[ https://immutables.github.io/apt.html](https://immutables.github.io/apt.html)

\[16] Processing Code[ https://openjdk.org/groups/compiler/processing-code.html](https://openjdk.org/groups/compiler/processing-code.html)

\[17] Getting Started with&#x20;

&#x20;the Annotation Processing Tool (apt) apt Contents[ https://web.mit.edu/java\_v1.5.0\_22/distrib/share/docs/guide/apt/GettingStarted.html](https://web.mit.edu/java_v1.5.0_22/distrib/share/docs/guide/apt/GettingStarted.html)

\[18] Annotation Processing Tool (apt) Documetation Contents[ https://www.cs.auckland.ac.nz/references/java/java1.5/guide/apt/index.html](https://www.cs.auckland.ac.nz/references/java/java1.5/guide/apt/index.html)

\[19] Java Annotation Processing[ https://peerdh.com/blogs/programming-insights/java-annotation-processing](https://peerdh.com/blogs/programming-insights/java-annotation-processing)

\[20] Java Platform, Standard Edition What’s New in Oracle JDK 9[ https://docs.oracle.com/javase//9/whatsnew/toc.htm](https://docs.oracle.com/javase//9/whatsnew/toc.htm)

\[21] Annotation processors using javax.annotation.\* fail when cross compiling to 8[ https://bugs.openjdk.org/browse/JDK-8157671](https://bugs.openjdk.org/browse/JDK-8157671)

\[22] java: Annotation processing is not supported for module cycles. Please ensure that all modules from cycle \[core,feign] are excluded from annotation processing - CSDN文库[ https://wenku.csdn.net/answer/38fn6su3fj](https://wenku.csdn.net/answer/38fn6su3fj)

\[23] Java 9 引入的 模块系统(Module System)-CSDN博客[ https://blog.csdn.net/D1237890/article/details/151161216](https://blog.csdn.net/D1237890/article/details/151161216)

\[24] The Java 9 Module System In Action[ http://slides.nipafx.dev/jpms/2016-11-15-Modconf/#/](http://slides.nipafx.dev/jpms/2016-11-15-Modconf/#/)

\[25] Unused import of javax.annotation.Generated breaks annotation processing on JDK9 with Jigsaw #880[ https://github.com/google/dagger/issues/880](https://github.com/google/dagger/issues/880)

\[26] Java 9 Features and Enhancements[ https://howtodoinjava.com/java9/java9-new-features-enhancements/](https://howtodoinjava.com/java9/java9-new-features-enhancements/)

\[27] Interface Processor[ http://docs.oracle.com/en/java/javase/24/docs/api/java.compiler/javax/annotation/processing/Processor.html](http://docs.oracle.com/en/java/javase/24/docs/api/java.compiler/javax/annotation/processing/Processor.html)

\[28] JVM——注解处理器-CSDN博客[ https://blog.csdn.net/qq\_41478243/article/details/148051681](https://blog.csdn.net/qq_41478243/article/details/148051681)

\[29] How to use process method in

javax.annotation.processing.Processor[ https://www.tabnine.com/code/java/methods/javax.annotation.processing.Processor/process](https://www.tabnine.com/code/java/methods/javax.annotation.processing.Processor/process)

\[30] Uses of Interface[ https://www.cs.usfca.edu/\~cs212/javadoc/api/java.compiler/javax/annotation/processing/class-use/Processor.html](https://www.cs.usfca.edu/~cs212/javadoc/api/java.compiler/javax/annotation/processing/class-use/Processor.html)

\[31] インタフェースProcessor[ https://docs.oracle.com/javase/jp/8/docs/api/javax/annotation/processing/Processor.html](https://docs.oracle.com/javase/jp/8/docs/api/javax/annotation/processing/Processor.html)

\[32] Uses of Package[ https://download.java.net/java/early\_access/loom/docs/api/java.compiler/javax/annotation/processing/package-use.html](https://download.java.net/java/early_access/loom/docs/api/java.compiler/javax/annotation/processing/package-use.html)

\[33] Class AbstractProcessor[ https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/annotation/processing/AbstractProcessor.html](https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/annotation/processing/AbstractProcessor.html)

\[34] Java编译期魔法揭秘:如何用APT生成高效代码(附完整案例)-CSDN博客[ https://blog.csdn.net/PoliVein/article/details/155193161](https://blog.csdn.net/PoliVein/article/details/155193161)

\[35] Android-Notes/blogs/Android/APT.md at master · Omooo/Android-Notes · GitHub[ https://github.com/Omooo/Android-Notes/blob/master/blogs/Android/APT.md](https://github.com/Omooo/Android-Notes/blob/master/blogs/Android/APT.md)

\[36] Processing Code[ https://openjdk.org/groups/compiler/processing-code.html](https://openjdk.org/groups/compiler/processing-code.html)

\[37] abstractprocessor - CSDN文库[ https://wenku.csdn.net/answer/68kgfcnt04](https://wenku.csdn.net/answer/68kgfcnt04)

\[38] Getting Started with

&#x20;the Annotation Processing Tool (apt)[ http://icpc.cs.uchicago.edu/mcpc2012/ref/jdk/technotes/guides/apt/GettingStarted.html](http://icpc.cs.uchicago.edu/mcpc2012/ref/jdk/technotes/guides/apt/GettingStarted.html)

\[39] Class AbstractProcessor[ https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/AbstractProcessor.html](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/AbstractProcessor.html)

\[40] APT 系列 (三):APT 技术探究\_apt技术-CSDN博客[ https://blog.csdn.net/chuyouyinghe/article/details/132087755](https://blog.csdn.net/chuyouyinghe/article/details/132087755)

\[41] Package javax.lang.model.element[ http://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/compact3-package-summary.html](http://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/compact3-package-summary.html)

\[42] パッケージ javax.lang.model.element[ https://docs.oracle.com/javase/jp/6/api/javax/lang/model/element/package-summary.html](https://docs.oracle.com/javase/jp/6/api/javax/lang/model/element/package-summary.html)

\[43] Android开源系列-组件化框架Arouter-(三)APT技术详解[ https://juejin.cn/post/7135084077408845831](https://juejin.cn/post/7135084077408845831)

\[44] VariableElement-Interface-javax.lang.model.element-Java API References - HUAWEI Developers[ https://developer.harmonyos.com/en/docs/documentation/doc-references/variableelement-0000001123534702](https://developer.harmonyos.com/en/docs/documentation/doc-references/variableelement-0000001123534702)

\[45] Interface RoundEnvironment[ https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/RoundEnvironment.html](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/RoundEnvironment.html)

\[46] Package javax.annotation.processing[ https://download.java.net/java/early\_access/jdk27/docs/api/java.compiler/javax/annotation/processing/package-summary.html](https://download.java.net/java/early_access/jdk27/docs/api/java.compiler/javax/annotation/processing/package-summary.html)

\[47] 【架构师私藏干货】:深入Annotation Processor核心机制，洞悉编译期处理黑科技-CSDN博客[ https://blog.csdn.net/PixelIsle/article/details/154451532](https://blog.csdn.net/PixelIsle/article/details/154451532)

\[48] Java Examples for javax.annotation.processing.RoundEnvironment[ https://www.javatips.net/api/javax.annotation.processing.roundenvironment](https://www.javatips.net/api/javax.annotation.processing.roundenvironment)

\[49] Mastering Java Annotation Processing: Compile-Time Code Generation Guide[ https://dev.to/nithinbharathwaj/mastering-java-annotation-processing-compile-time-code-generation-guide-5b3k](https://dev.to/nithinbharathwaj/mastering-java-annotation-processing-compile-time-code-generation-guide-5b3k)

\[50] RoundEnvironment-Interface-javax.annotation.processing-Java API References - HUAWEI Developers[ https://developer.harmonyos.com/en/docs/documentation/doc-references/roundenvironment-0000001123694518](https://developer.harmonyos.com/en/docs/documentation/doc-references/roundenvironment-0000001123694518)

\[51] Uses of Package[ https://cs.duke.edu/csed/java/jdk1.6/api/javax/annotation/processing/package-use.html](https://cs.duke.edu/csed/java/jdk1.6/api/javax/annotation/processing/package-use.html)

\[52] Java注解处理:toBeBetterJavaer编译时处理-CSDN博客[ https://blog.csdn.net/gitblog\_00988/article/details/154590890](https://blog.csdn.net/gitblog_00988/article/details/154590890)

\[53] GitHub - jbosstools/m2e-apt: Maven integration with Eclipse JDT Annotation Processor Toolkit[ https://github.com/jbosstools/m2e-apt](https://github.com/jbosstools/m2e-apt)

\[54] MyBatis-Flex APT 配置[ https://github.com/mybatis-flex/mybatis-flex/blob/main/docs/zh/others/apt.md](https://github.com/mybatis-flex/mybatis-flex/blob/main/docs/zh/others/apt.md)

\[55] Maven dependencies | IntelliJ IDEA Documentation[ https://www.jetbrains.com.cn/en-us/help/idea/work-with-maven-dependencies.html](https://www.jetbrains.com.cn/en-us/help/idea/work-with-maven-dependencies.html)

\[56] m2e-apt/pom.xml at master · jbosstools/m2e-apt · GitHub[ https://github.com/jbosstools/m2e-apt/blob/master/pom.xml](https://github.com/jbosstools/m2e-apt/blob/master/pom.xml)

\[57] Java Annotation Processing With Maven[ https://peerdh.com/blogs/programming-insights/java-annotation-processing-with-maven](https://peerdh.com/blogs/programming-insights/java-annotation-processing-with-maven)

\[58] anno-docimal-apt[ https://central.sonatype.com/artifact/com.blackbuild.annodocimal/anno-docimal-apt/0.1.0](https://central.sonatype.com/artifact/com.blackbuild.annodocimal/anno-docimal-apt/0.1.0)

\[59] Apache Maven Compiler Plugin 编译器插件\_maven compile插件选项-CSDN博客[ https://blog.csdn.net/ystyaoshengting/article/details/104038230](https://blog.csdn.net/ystyaoshengting/article/details/104038230)

\[60] Maven插件开发(三)——Java中Processor使用与maven-compiler-plugin的结合使用\_在哪些地方用到maven-compiler-plugin-CSDN博客[ https://blog.csdn.net/anumbrella/article/details/105030398](https://blog.csdn.net/anumbrella/article/details/105030398)

\[61] Java注解处理(Annotation Processor):(三) 代码生成\_gradle annotationprocessorpaths-CSDN博客[ https://blog.csdn.net/jjxojm/article/details/90349756](https://blog.csdn.net/jjxojm/article/details/90349756)

\[62] maven compile插件 原创[ https://blog.csdn.net/john1337/article/details/53841514](https://blog.csdn.net/john1337/article/details/53841514)

\[63] Maven编译和打包插件根模块 pom.xml 具有主启动类的子模块 pom.xml根模块 pom.xml 具有主启动类 - 掘金[ https://juejin.cn/post/7522865190138052643](https://juejin.cn/post/7522865190138052643)

\[64] 配置注解处理器 | IntelliJ IDEA 文档[ https://www.jetbrains.com.cn/help/idea/annotation-processors-support.html?section=flex\_reference.xml](https://www.jetbrains.com.cn/help/idea/annotation-processors-support.html?section=flex_reference.xml)

\[65] Using annotation processor in IDE[ https://immutables.github.io/apt.html](https://immutables.github.io/apt.html)

\[66] Maven dependencies[ https://www.jetbrains.com/help/idea/work-with-maven-dependencies.html](https://www.jetbrains.com/help/idea/work-with-maven-dependencies.html)

\[67] Set up Immutables in your IDE[ https://www.lagomframework.com/documentation/1.2.x/java/ImmutablesInIDEs.html](https://www.lagomframework.com/documentation/1.2.x/java/ImmutablesInIDEs.html)

\[68] Configure annotation processors | IntelliJ IDEA Documentation[ https://www.jetbrains.com/help/idea/annotation-processors-support.html?\_ga=2.165512831.1786240413.1632378981-477149720.1631588513](https://www.jetbrains.com/help/idea/annotation-processors-support.html?_ga=2.165512831.1786240413.1632378981-477149720.1631588513)

\[69] annotations﻿[ https://www.jetbrains.com.cn/en-us/help/idea/annotating-source-code.html](https://www.jetbrains.com.cn/en-us/help/idea/annotating-source-code.html)

\[70] IDE Configuration · doanduyhai/Achilles Wiki · GitHub[ https://github.com/doanduyhai/Achilles/wiki/IDE-Configuration/28680136ae463aff61eb6a8e642fd15056d5173c](https://github.com/doanduyhai/Achilles/wiki/IDE-Configuration/28680136ae463aff61eb6a8e642fd15056d5173c)

\[71] Using IntelliJ IDEA to develop Micronaut applications[ https://micronaut-projects.github.io/micronaut-guides-mn3/latest/micronaut-intellij-idea-ide-setup-gradle-java.html](https://micronaut-projects.github.io/micronaut-guides-mn3/latest/micronaut-intellij-idea-ide-setup-gradle-java.html)

\[72] Mastering Java Annotation Processing: Compile-Time Code Generation Guide[ https://dev.to/aaravjoshi/mastering-java-annotation-processing-compile-time-code-generation-guide-5b3k](https://dev.to/aaravjoshi/mastering-java-annotation-processing-compile-time-code-generation-guide-5b3k)

\[73] Maven dependencies | IntelliJ IDEA Documentation[ https://www.jetbrains.com.cn/en-us/help/idea/work-with-maven-dependencies.html](https://www.jetbrains.com.cn/en-us/help/idea/work-with-maven-dependencies.html)

\[74] 7. Annotator[ https://intellij-sdk-docs-cn.github.io/intellij/sdk/docs/tutorials/custom\_language\_support/annotator.html](https://intellij-sdk-docs-cn.github.io/intellij/sdk/docs/tutorials/custom_language_support/annotator.html)

\[75] Using IntelliJ IDEA to develop Micronaut applications[ https://micronaut-projects.github.io/micronaut-guides-mn3/latest/micronaut-intellij-idea-ide-setup-maven-java.html](https://micronaut-projects.github.io/micronaut-guides-mn3/latest/micronaut-intellij-idea-ide-setup-maven-java.html)

\[76] A way to trigger Annotation processing[ https://intellij-support.jetbrains.com/hc/en-us/community/posts/206867745-A-way-to-trigger-Annotation-processing](https://intellij-support.jetbrains.com/hc/en-us/community/posts/206867745-A-way-to-trigger-Annotation-processing)

\[77] abstractprocessor - CSDN文库[ https://wenku.csdn.net/answer/68kgfcnt04](https://wenku.csdn.net/answer/68kgfcnt04)

\[78] Java APT高级编程(自定义注解处理器与代码生成黑科技)-CSDN博客[ https://blog.csdn.net/ProceSeed/article/details/155192944](https://blog.csdn.net/ProceSeed/article/details/155192944)

\[79] 注釈処理ツール (apt ) 入門 apt 目次[ https://docs.oracle.com/javase/jp/1.5.0/guide/apt/GettingStarted.html](https://docs.oracle.com/javase/jp/1.5.0/guide/apt/GettingStarted.html)

\[80] Java Annotation Processing[ https://peerdh.com/blogs/programming-insights/java-annotation-processing](https://peerdh.com/blogs/programming-insights/java-annotation-processing)

\[81] java注解-使用注解处理器实现动态生成get和set方法[ https://www.cnblogs.com/linmt/p/16916472.html](https://www.cnblogs.com/linmt/p/16916472.html)

\[82] an annotation processor to generate GWT events and action/result DTOs[ https://github.com/stephenh/gwt-mpv-apt](https://github.com/stephenh/gwt-mpv-apt)

\[83] Android APT 实例讲解APT(Annotation Processing Tool) 即注解处理器，是一种注解 - 掘金[ https://juejin.cn/post/6844903753108160525](https://juejin.cn/post/6844903753108160525)

\[84] Java 注解与 APT(Annotation Processing Tool)\_java springboot 编译期注解处理器(apt)-CSDN博客[ https://blog.csdn.net/qq\_44608856/article/details/151617672](https://blog.csdn.net/qq_44608856/article/details/151617672)

\[85] GitHub - avaje/avaje-validator: POJO validation using annotation processing[ https://github.com/avaje/avaje-validator/](https://github.com/avaje/avaje-validator/)

\[86] Chapter 12. Annotation Processor[ https://docs.jboss.org/hibernate/validator/5.2/reference/en-US/html/ch12.html](https://docs.jboss.org/hibernate/validator/5.2/reference/en-US/html/ch12.html)

\[87] Getting Started with

&#x20;the Annotation Processing Tool (apt)[ http://icpc.cs.uchicago.edu/mcpc2012/ref/jdk/technotes/guides/apt/GettingStarted.html](http://icpc.cs.uchicago.edu/mcpc2012/ref/jdk/technotes/guides/apt/GettingStarted.html)

\[88] AptAnnotationParserTest xref[ https://opensource.salesforce.com/AptSpring/1.1.0/AptSpringProcessor/xref-test/com/salesforce/aptspring/processor/metaannotation/AptAnnotationParserTest.html](https://opensource.salesforce.com/AptSpring/1.1.0/AptSpringProcessor/xref-test/com/salesforce/aptspring/processor/metaannotation/AptAnnotationParserTest.html)

\[89] Processing Code[ https://openjdk.org/groups/compiler/processing-code.html](https://openjdk.org/groups/compiler/processing-code.html)

\[90] Anpassen von Java-Compiler, der Ihre Annotation verarbeitet (Annotation Processing Tool)[ https://codestory.de/10303/eclipse-java-annotation-processing-tool](https://codestory.de/10303/eclipse-java-annotation-processing-tool)

\[91] Java编译期魔法揭秘:如何用APT生成高效代码(附完整案例)-CSDN博客[ https://blog.csdn.net/PoliVein/article/details/155193161](https://blog.csdn.net/PoliVein/article/details/155193161)

\[92] TRAP/J: Transparent Generation of Adaptable Java Programs(pdf)[ http://www.cse.msu.edu/\~mckinley/Pubs/files/doa-2004.pdf](http://www.cse.msu.edu/~mckinley/Pubs/files/doa-2004.pdf)

\[93] Eclipse JDT-APT Project[ https://eclipse.dev/jdt/apt/index.php](https://eclipse.dev/jdt/apt/index.php)

\[94] Master Java Reflection and Runtime Introspection[ https://webreference.com/java/advanced/reflection/](https://webreference.com/java/advanced/reflection/)

\[95] Configure Native Image with the Tracing Agent[ http://docs.oracle.com/en/graalvm/enterprise/22/docs/reference-manual/native-image/guides/configure-with-tracing-agent/?embed=1](http://docs.oracle.com/en/graalvm/enterprise/22/docs/reference-manual/native-image/guides/configure-with-tracing-agent/?embed=1)

\[96] Creating a New Instance with a Dynamic Reference Variable Type in Java Using Reflection[ https://learn-it-university.com/creating-a-new-instance-with-a-dynamic-reference-variable-type-in-java-using-reflection/](https://learn-it-university.com/creating-a-new-instance-with-a-dynamic-reference-variable-type-in-java-using-reflection/)

\[97] Dynamically Creating Class Instances in Java: How to Instantiate a Class by Name Using Reflection - Learn IT University[ https://learn-it-university.com/instantiating-a-class-by-name-in-java-methods-and-examples/](https://learn-it-university.com/instantiating-a-class-by-name-in-java-methods-and-examples/)

\[98] 告别重复编码，用这7款Java代码生成工具让生产力翻倍-CSDN博客[ https://blog.csdn.net/DeepNest/article/details/153258319](https://blog.csdn.net/DeepNest/article/details/153258319)

\[99] Introducing Hibernate Data Repositories[ https://docs.hibernate.org/orm/7.0/repositories/pdf/Hibernate\_Data\_Repositories.pdf](https://docs.hibernate.org/orm/7.0/repositories/pdf/Hibernate_Data_Repositories.pdf)

\[100] Configuring the annotation processor[ https://docs.datastax.com/en/developer/java-driver/4.5/manual/mapper/config/index.html](https://docs.datastax.com/en/developer/java-driver/4.5/manual/mapper/config/index.html)

\[101] lorislab-sql-data[ https://github.com/lorislab/lorislab-sql-data/blob/main/README.md](https://github.com/lorislab/lorislab-sql-data/blob/main/README.md)

\[102] Generating JPA Mappings[ http://docs.oracle.com/cd/F25597\_01/document/products/workshop/docs92/studio321/ormworkbench/GeneratingEJB3Mappings.html](http://docs.oracle.com/cd/F25597_01/document/products/workshop/docs92/studio321/ormworkbench/GeneratingEJB3Mappings.html)

\[103] Object-Relational Mappings: Annotating Java Classes[ http://docs.oracle.com/cd/E14545\_01/help/oracle.eclipse.tools.orm.doc/html/files/annotateClasses.html](http://docs.oracle.com/cd/E14545_01/help/oracle.eclipse.tools.orm.doc/html/files/annotateClasses.html)

\[104] Mastering Java Annotation Processing: Compile-Time Code Generation Guide[ https://dev.to/nithinbharathwaj/mastering-java-annotation-processing-compile-time-code-generation-guide-5b3k](https://dev.to/nithinbharathwaj/mastering-java-annotation-processing-compile-time-code-generation-guide-5b3k)

\[105] 问题:Annotation processor加载失败如何解决?\_编程语言-CSDN问答[ https://ask.csdn.net/questions/8750255](https://ask.csdn.net/questions/8750255)

\[106] Generating Your Own Metadata by Using the Annotation Processor[ https://docs.spring.io/spring-boot/specification/configuration-metadata/annotation-processor.html](https://docs.spring.io/spring-boot/specification/configuration-metadata/annotation-processor.html)

\[107] Java Annotation Processing With Maven[ https://peerdh.com/blogs/programming-insights/java-annotation-processing-with-maven](https://peerdh.com/blogs/programming-insights/java-annotation-processing-with-maven)

\[108] java17如何使用processor - CSDN文库[ https://wenku.csdn.net/answer/3m6v1tswrq](https://wenku.csdn.net/answer/3m6v1tswrq)

\[109] 注解处理器路径选择什么 - CSDN文库[ https://wenku.csdn.net/answer/4anojn3j47](https://wenku.csdn.net/answer/4anojn3j47)

\[110] GitHub - jbosstools/m2e-apt: Maven integration with Eclipse JDT Annotation Processor Toolkit[ https://github.com/jbosstools/m2e-apt](https://github.com/jbosstools/m2e-apt)

\[111] Annotation processors[ https://maven.apache.org/plugins/maven-compiler-plugin-4.x/examples/annotation-processor.html](https://maven.apache.org/plugins/maven-compiler-plugin-4.x/examples/annotation-processor.html)

\[112] GitHub - querydsl/apt-maven-plugin: Maven APT plugin[ https://github.com/querydsl/apt-maven-plugin/](https://github.com/querydsl/apt-maven-plugin/)

\[113] MyBatis-Flex APT 配置 - MyBatis-Flex 官方网站[ https://mybatis-flex.com/zh/others/apt.html](https://mybatis-flex.com/zh/others/apt.html)

\[114] apt-maven-plugin/src/main/java/com/mysema/maven/apt/AbstractProcessorMojo.java at master · querydsl/apt-maven-plugin · GitHub[ https://github.com/querydsl/apt-maven-plugin/blob/master/src/main/java/com/mysema/maven/apt/AbstractProcessorMojo.java](https://github.com/querydsl/apt-maven-plugin/blob/master/src/main/java/com/mysema/maven/apt/AbstractProcessorMojo.java)

\[115] Installation[ https://maven.apache.org/install.html?fw-lang=java%2Ftestng](https://maven.apache.org/install.html?fw-lang=java%2Ftestng)

\[116] 配置注解处理器 | IntelliJ IDEA 文档[ https://www.jetbrains.com.cn/help/idea/annotation-processors-support.html?section=flex\_reference.xml](https://www.jetbrains.com.cn/help/idea/annotation-processors-support.html?section=flex_reference.xml)

\[117] Using annotation processor in IDE[ https://immutables.github.io/apt.html](https://immutables.github.io/apt.html)

\[118] Maven dependencies | IntelliJ IDEA Documentation[ https://www.jetbrains.com.cn/en-us/help/idea/work-with-maven-dependencies.html](https://www.jetbrains.com.cn/en-us/help/idea/work-with-maven-dependencies.html)

\[119] Как запустить annotation processor для kotlin файлов при сброке из среды Intellij Idea без сборщиков?[ https://qna.habr.com/q/529581](https://qna.habr.com/q/529581)

\[120] Configure annotation processors[ https://www.jetbrains.com/help/idea/annotation-processors-support.html?trk=article-ssr-frontend-pulse\_little-text-block](https://www.jetbrains.com/help/idea/annotation-processors-support.html?trk=article-ssr-frontend-pulse_little-text-block)

\[121] Lombok annotations not processed when using Gateway over SSH[ https://intellij-support.jetbrains.com/hc/en-us/community/posts/9545937342482-Lombok-annotations-not-processed-when-using-Gateway-over-SSH](https://intellij-support.jetbrains.com/hc/en-us/community/posts/9545937342482-Lombok-annotations-not-processed-when-using-Gateway-over-SSH)

\[122] Kotlin Annotation Processing With Gradle[ https://peerdh.com/blogs/programming-insights/kotlin-annotation-processing-with-gradle-1](https://peerdh.com/blogs/programming-insights/kotlin-annotation-processing-with-gradle-1)

\[123] Maven dependencies[ https://www.jetbrains.com/help/idea/work-with-maven-dependencies.html](https://www.jetbrains.com/help/idea/work-with-maven-dependencies.html)

\[124] Maven + Intellij IDEA - enable lombok annotation processor from pom.xml instead of Intellij IDEA level[ https://dirask.com/posts/Maven-Intellij-IDEA-enable-lombok-annotation-processor-from-pom-xml-instead-of-Intellij-IDEA-level-jo4A31](https://dirask.com/posts/Maven-Intellij-IDEA-enable-lombok-annotation-processor-from-pom-xml-instead-of-Intellij-IDEA-level-jo4A31)

\[125] Annotations[ https://www.jetbrains.com/help/idea/annotating-source-code.html](https://www.jetbrains.com/help/idea/annotating-source-code.html)

\[126] Maven 依存関係[ https://pleiades.io/help/idea/work-with-maven-dependencies.html](https://pleiades.io/help/idea/work-with-maven-dependencies.html)

\[127] Maven の依存関係を処理する﻿[ https://pleiades.io/help/idea/2019.2/work-with-maven-dependencies.html](https://pleiades.io/help/idea/2019.2/work-with-maven-dependencies.html)

\[128] GitHub - osundblad/intellij-annotations-instrumenter-maven-plugin: IntelliJ IDEA annotations instrumenter maven plugin[ https://github.com/osundblad/intellij-annotations-instrumenter-maven-plugin](https://github.com/osundblad/intellij-annotations-instrumenter-maven-plugin)

\[129] Java APT高级编程(自定义注解处理器与代码生成黑科技)-CSDN博客[ https://blog.csdn.net/ProceSeed/article/details/155192944](https://blog.csdn.net/ProceSeed/article/details/155192944)

\[130] Java 注解与 APT(Annotation Processing Tool)\_java springboot 编译期注解处理器(apt)-CSDN博客[ https://blog.csdn.net/qq\_44608856/article/details/151617672](https://blog.csdn.net/qq_44608856/article/details/151617672)

\[131] Automating Java POJOs: OpenAPI Generator, Lombok, and Validation[ https://iifx.dev/en/articles/457540441/automating-java-pojos-openapi-generator-lombok-and-validation](https://iifx.dev/en/articles/457540441/automating-java-pojos-openapi-generator-lombok-and-validation)

\[132] Answer Code Validation Program with Test Data Generation for Code Writing Problem in Java Programming Learning Assistant System[ https://www.preprints.org/manuscript/202306.0153/v1](https://www.preprints.org/manuscript/202306.0153/v1)

\[133] java code generator[ https://futurewebdeveloper.com/java-code-generator-2/](https://futurewebdeveloper.com/java-code-generator-2/)

\[134] org.apache.commons.validator (Apache Commons Validator 1.10.1 API)[ https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/package-summary.html](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/package-summary.html)

\[135] ALPHATrans: A Neuro-Symbolic Compositional Approach for Repository-Level Code Translation and Validation[ https://dl.acm.org/doi/pdf/10.1145/3729379](https://dl.acm.org/doi/pdf/10.1145/3729379)

\[136] Annotation Processingを使ったソースコード生成プログラムを作ってみる。(Java編)[ https://qiita.com/LyricalMaestro0/items/9a4e3ec3ea7bda9ee523](https://qiita.com/LyricalMaestro0/items/9a4e3ec3ea7bda9ee523)

\[137] an annotation processor to generate GWT events and action/result DTOs[ https://github.com/stephenh/gwt-mpv-apt](https://github.com/stephenh/gwt-mpv-apt)

\[138] 如何用 APT(Annotation Processing Tool)自动生成代码\_的技术博客\_51CTO博客[ https://blog.51cto.com/u\_4176761/5973314](https://blog.51cto.com/u_4176761/5973314)

\[139] Android APT 实例讲解APT(Annotation Processing Tool) 即注解处理器，是一种注解 - 掘金[ https://juejin.cn/post/6844903753108160525](https://juejin.cn/post/6844903753108160525)

\[140] apt 根据注解，编译时生成代码[ https://www.cnblogs.com/CharlesGrant/p/5811338.html](https://www.cnblogs.com/CharlesGrant/p/5811338.html)

\[141] titanium-sdk/android/kroll-apt/README.md at main · tidev/titanium-sdk · GitHub[ https://github.com/tidev/titanium-sdk/blob/main/android/kroll-apt/README.md](https://github.com/tidev/titanium-sdk/blob/main/android/kroll-apt/README.md)

> （注：文档部分内容可能由 AI 生成）