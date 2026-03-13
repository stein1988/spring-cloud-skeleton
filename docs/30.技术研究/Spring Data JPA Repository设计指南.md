# Spring Data JPA Repository 设计指南

## 1. Spring Data JPA Repository 设计理念

### 1.1 Repository 模式理论基础与 DDD 定位

在领域驱动设计（DDD）中，Repository 模式扮演着至关重要的角色，它作为领域层和数据映射层之间的中介，其行为类似于一个内存中的领域对象集合[(21)](https://blog.csdn.net/qq_29328443/article/details/149855876)。Repository 模式的核心意图是封装被持久化的对象集合以及对这些对象执行的操作，从而为持久化层提供一个更加面向对象的视图[(21)](https://blog.csdn.net/qq_29328443/article/details/149855876)。

与传统的 DAO（数据访问对象）模式相比，Repository 模式在抽象层次上有显著差异。DAO 是一个更低层次的模式，更贴近数据库，它是以数据为中心的，其方法通常与数据库表一一对应，返回的是数据传输对象（DTOs）或原始数据结构。而 Repository 是一个更高层次的模式，更贴近领域，它是以领域为中心的，处理的是业务 / 领域对象（即聚合），其方法使用通用语言（Ubiquitous Language）来表达，例如 findActiveCustomers ()（查找活跃客户），而不是一个通用的 find () 方法。

在 DDD 的架构中，聚合是一致性边界，聚合是一组业务上紧密关联的实体和值对象的集合，它们必须作为一个整体来维护数据的一致性。聚合根是该聚合中唯一可以被外部对象引用的实体，所有对聚合内部状态的修改都必须通过聚合根来执行，由聚合根负责强制执行其内部的业务规则（即 "不变量"，Invariants）。通过让 Repository 加载和保存整个聚合（通过其根），我们确保了所有的不变量检查得以执行，并且整个聚合作为一个不可分割的单元被持久化，这是在 DDD 中维护数据完整性的核心机制。

### 1.2 约定优于配置原则的体现

Spring Data JPA 的设计哲学充分体现了 "约定优于配置"（Convention over Configuration）的原则。这一理念的核心是用约定替代繁琐的配置框架，提前定义一套默认的规则，开发者只需要遵循这些规则就不用去手动配置大量重复的跟业务无关的内容，从而聚焦在业务逻辑本身。

在 Spring Data JPA 中，这一原则体现在多个方面：

**实体映射约定**：Spring Boot 默认采用 lower snake case 约定处理 JPA 实体及其映射的数据库元素，实体类名保持不变（如 User、Product），而数据库表名会自动转换为小写蛇形命名（如 user、product）[(49)](https://www.hyper-leap.com/2024/07/09/mastering-the-name-game-jpa-naming-conventions-in-spring-boot/)。

**Repository 接口约定**：开发者只需要定义一个继承自 JpaRepository\<T, ID> 的空接口，即可自动获得包括 findById、save、delete、findAll、count、existsById 等 40 + 个通用 CRUD 方法[(2)](https://wenku.csdn.net/doc/2pz0xa4q14)。更进一步，开发者可通过方法命名规则（如 findByUsernameAndStatus、findByCreatedAtAfter、findTop5ByOrderByScoreDesc）零配置生成类型安全的 JPQL 查询，编译期即可校验字段合法性，避免运行时 SQL 错误[(2)](https://wenku.csdn.net/doc/2pz0xa4q14)。

**查询方法命名约定**：Spring Data JPA 支持通过特定的方法命名规则自动解析生成对应的 SQL 查询，无需编写任何查询语句。命名规则需遵循 "动词 + 条件 + 属性" 的格式，常用动词包括 findBy、countBy、deleteBy 等，条件支持等值、范围、模糊匹配等[(112)](https://blog.51cto.com/u_16213665/14499073)。

### 1.3 Repository 接口体系架构与设计哲学

Spring Data JPA 的 Repository 接口体系采用了清晰的层次结构设计，主要包括以下几个核心接口：

**Repository 接口**：最顶层接口，标记接口，无任何方法，用于标识该接口为一个 Repository 组件[(79)](https://blog.csdn.net/hwh22/article/details/149916652)。

**CrudRepository 接口**：继承 Repository，提供基本的 CRUD 操作，包括 save、findById、findAll、count、delete、existsById 等方法[(79)](https://blog.csdn.net/hwh22/article/details/149916652)。

**PagingAndSortingRepository 接口**：继承 CrudRepository，提供分页和排序功能，包括 findAll (Sort sort)、Page findAll (Pageable pageable) 等方法[(79)](https://blog.csdn.net/hwh22/article/details/149916652)。

**JpaRepository 接口**：继承 PagingAndSortingRepository，提供更丰富的 JPA 相关操作，包括批量操作、flush、saveAndFlush 等方法[(79)](https://blog.csdn.net/hwh22/article/details/149916652)。

Spring Data JPA 的设计哲学体现在以下几个方面：

**接口驱动开发**：Spring Data JPA 采用接口驱动的设计方式，开发者只需要定义接口，无需编写实现类，框架会在运行时自动生成实现。这种设计大大减少了样板代码，提高了开发效率。

**方法名解析机制**：通过方法名解析机制，Spring Data JPA 能够根据方法名称自动生成相应的查询语句。例如，定义 findByUsernameAndEmail (String username, String email) 方法，框架会自动生成 SELECT u FROM User u WHERE u.username = ?1 AND u.email = ?2 的 JPQL 查询[(149)](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)。

**灵活的查询策略**：Spring Data JPA 支持多种查询创建策略，优先级从高到低依次为：使用 @Query 注解定义的查询、使用命名查询、解析方法名生成的查询、自定义实现。这种设计提供了极大的灵活性，开发者可以根据具体需求选择最合适的查询方式。

### 1.4 2026 年最新版本特性与演进趋势

截至 2026 年 3 月，Spring Data JPA 的最新版本体现了几个重要的技术演进趋势：

**Jakarta Persistence 4.0 支持**：Spring Data JPA 正在积极支持 Jakarta Persistence 4.0 规范，引入了新的注解如 @StaticQuery、@StaticNativeQuery、@ReadQueryOptions 和 @WriteQueryOptions，这些注解主要用于支持 Jakarta Data Repositories，可作为 @Query/@NativeQuery 和 @QueryHint 的替代方案[(33)](https://github.com/spring-projects/spring-data-jpa/issues/4141)。

**Spring Data AOT（Ahead of Time）特性**：这是 Spring Boot 4 中最被低估的新特性之一。AOT 处理器会在编译期完成所有 Repository 方法的分析、方法语义解析、字段存在性校验、完整查询构建，并生成真实的实现类（而不是运行时反射）。这一特性带来的优势包括：缩短应用启动时间、减少内存使用、提供更好的可调试性（可以直接查看和调试生成的代码）。

**虚拟线程支持**：Spring Boot 4 基于 Java 21，默认对虚拟线程（Virtual Threads）提供了极佳的支持。这对数据库事务管理产生了积极影响，意味着我们不再需要担心 "长事务" 把线程池耗尽的问题。在虚拟线程环境下，数据库连接依然是物理资源，但线程本身的开销被极度压缩。

**只读事务优化**：在 Spring Boot 4 中，JPA 对只读事务做了底层重构，引入了 Immutable Entity Mode（不可变实体模式）。当检测到 @Transactional (readOnly = true) 时，Hibernate 6.x（Spring Boot 4 默认集成）会彻底关闭脏检查机制，直接以 DTO 投影的方式处理实体，内存占用降低 40% 以上。

**批量操作增强**：Spring Boot 4 引入了 @BatchSize 的智能自动配置和 Windowed Fetching 机制，提供了更高效的批量处理能力。例如，在海量数据导入场景中，可以使用 Window 迭代器进行流式处理，避免一次性加载全部数据进内存。

## 2. 实际项目中的最佳实践

### 2.1 Repository 分层设计策略

在企业级应用中，采用合理的 Repository 分层设计策略是实现代码可维护性和可扩展性的关键。以下是推荐的分层设计方案：

**基础 Repository 层**：定义最基础的通用操作，例如 BaseRepository 接口，继承 JpaRepository，并添加一些通用方法如批量更新、自定义查询等。这种设计可以避免在每个具体的 Repository 中重复编写通用代码。



```
public interface BaseRepository\<T, ID> extends JpaRepository\<T, ID> {

&#x20;   List\<T> findAllByIdIn(Collection\<ID> ids);

&#x20;  &#x20;

&#x20;   @Modifying

&#x20;   @Query("UPDATE #{#entityName} e SET e.status = :status WHERE e.id IN :ids")

&#x20;   int batchUpdateStatus(@Param("ids") Collection\<ID> ids, @Param("status") String status);

}
```

**领域特定 Repository 层**：针对每个聚合根创建特定的 Repository 接口，继承基础 Repository。这种设计确保每个聚合根都有且只有一个 Repository，符合 DDD 的设计原则。



```
public interface UserRepository extends BaseRepository\<User, Long> {

&#x20;   List\<User> findByUsernameContainingIgnoreCase(String keyword);

&#x20;  &#x20;

&#x20;   Page\<User> findByStatus(Status status, Pageable pageable);

&#x20;  &#x20;

&#x20;   @Query("SELECT u FROM User u WHERE u.email = :email")

&#x20;   Optional\<User> findByEmail(@Param("email") String email);

}
```

**自定义 Repository 实现层**：当基础 Repository 和领域特定 Repository 无法满足复杂业务需求时，可以创建自定义的 Repository 实现。例如，创建 UserRepositoryCustom 接口及其实现类 UserRepositoryImpl，实现特殊的查询逻辑。



```
public interface UserRepositoryCustom {

&#x20;   List\<User> searchUsersWithComplexConditions(UserSearchCriteria criteria);

}

@Repository

public class UserRepositoryImpl implements UserRepositoryCustom {

&#x20;   private final EntityManager entityManager;

&#x20;  &#x20;

&#x20;   public UserRepositoryImpl(EntityManager entityManager) {

&#x20;       this.entityManager = entityManager;

&#x20;   }

&#x20;  &#x20;

&#x20;   @Override

&#x20;   public List\<User> searchUsersWithComplexConditions(UserSearchCriteria criteria) {

&#x20;       // 使用Criteria API或原生SQL实现复杂查询逻辑

&#x20;   }

}
```

### 2.2 事务管理最佳实践

事务管理是保证数据一致性的关键，以下是 Spring Data JPA 事务管理的最佳实践：

**事务边界控制**：事务边界应当尽可能小，只包含必要的数据库操作，避免在事务中执行非数据库操作，如调用外部服务、进行复杂计算等[(88)](https://blog.csdn.net/weixin_55344375/article/details/146196819)。



```
@Service

public class OrderService {

&#x20;   private final OrderRepository orderRepository;

&#x20;   private final InventoryClient inventoryClient;

&#x20;  &#x20;

&#x20;   @Transactional

&#x20;   public void createOrder(Order order) {

&#x20;       // 1. 数据库操作 - 开始事务

&#x20;       orderRepository.save(order);

&#x20;      &#x20;

&#x20;       // 2. 调用外部服务 - 不建议在事务中执行

&#x20;       inventoryClient.deductStock(order.getProductId(), order.getQuantity());

&#x20;      &#x20;

&#x20;       // 3. 数据库操作 - 事务提交

&#x20;       order.setStatus(OrderStatus.CONFIRMED);

&#x20;       orderRepository.save(order);

&#x20;   }

}
```

**只读事务优化**：对于只读操作，使用 @Transactional (readOnly = true) 可以带来显著的性能提升。在 Spring Boot 4 中，这一特性得到了进一步优化，Hibernate 会彻底关闭脏检查机制，内存占用降低 40% 以上。



```
@Service

public class ReportService {

&#x20;   @Transactional(readOnly = true)

&#x20;   public List\<UserSummary> generateDailyReport() {

&#x20;       return userRepository.findAll()

&#x20;               .stream()

&#x20;               .map(u -> new UserSummary(u.getUsername(), u.getEmail()))

&#x20;               .toList();

&#x20;   }

}
```

**事务传播行为选择**：根据业务需求选择合适的事务传播行为。REQUIRED 是默认选项，表示如果当前存在事务则加入，否则创建新事务；REQUIRES\_NEW 表示总是创建新事务，挂起当前事务；MANDATORY 表示必须在现有事务中执行，否则抛出异常[(91)](https://www.codingshuttle.com/spring-boot-handbook/transactional-annotation-in-spring-boot/)。

**隔离级别配置**：根据业务需求选择合适的隔离级别，平衡数据一致性和性能。READ\_COMMITTED 是大多数情况下的推荐值，它可以防止脏读[(96)](https://blog.csdn.net/listeningsea/article/details/122418385)。

### 2.3 性能优化策略

性能优化是生产环境中必须考虑的重要因素，以下是 Spring Data JPA 的性能优化策略：

**批量操作优化**：对于批量插入或更新操作，应使用 Spring Data JPA 的批量操作功能，并正确配置 Hibernate 的批量参数。



```
spring.jpa.properties.hibernate.jdbc.batch\_size=50

spring.jpa.properties.hibernate.order\_inserts=true

spring.jpa.properties.hibernate.order\_updates=true
```

配置说明：



* hibernate.jdbc.batch\_size：告诉 Hibernate 累积 50 条 SQL 后批量执行，减少数据库交互次数

* hibernate.order\_inserts：确保同类型实体的插入语句被排序，进一步提升批处理效率

在 MySQL 环境下，还需要在 JDBC URL 中添加 rewriteBatchedStatements=true 参数以启用批量操作，性能可提升约 20 倍[(130)](https://blog.csdn.net/qq_33556185/article/details/121482679)。

**分页查询优化**：对于大数据集，必须使用分页查询，避免一次性加载所有数据。



```
Page\<User> usersPage = userRepository.findAll(PageRequest.of(0, 20, Sort.by("createdAt").descending()));
```

**查询优化策略**：



* 为频繁查询的字段创建索引，使用 @Index 注解

* 避免 SELECT \*，只查询必要的字段

* 使用 JOIN FETCH 或 @EntityGraph 优化关联查询，避免 N+1 问题

* 对于只读查询，使用 @Transactional (readOnly = true) 禁用脏检查

**二级缓存配置**：对于频繁读取且不经常更新的数据，可以启用二级缓存。



```
spring.jpa.properties.hibernate.cache.use\_second\_level\_cache=true

spring.jpa.properties.hibernate.cache.use\_query\_cache=true

spring.jpa.properties.hibernate.cache.region.factory\_class=org.hibernate.cache.jcache.JCacheRegionFactory
```

### 2.4 与其他 Spring 组件集成实践

Spring Data JPA 与其他 Spring 组件的集成可以提供更强大的功能：

**与 Spring Security 集成**：在 Repository 中可以直接使用 Spring Security 的认证信息，实现安全相关的查询。



```
public interface UserRepository extends JpaRepository\<User, Long> {

&#x20;   @Query("SELECT u FROM User u WHERE u.username = :username AND u.tenantId = :tenantId")

&#x20;   Optional\<User> findByUsernameAndTenantId(@Param("username") String username,&#x20;

&#x20;                                           @Param("tenantId") UUID tenantId);

}

// 在Service中使用SecurityContextHolder获取当前用户

@Service

public class UserService {

&#x20;   public User getCurrentUser() {

&#x20;       String username = SecurityContextHolder.getContext().getAuthentication().getName();

&#x20;       UUID tenantId = ((TenantAwareAuthentication) SecurityContextHolder.getContext().getAuthentication()).getTenantId();

&#x20;       return userRepository.findByUsernameAndTenantId(username, tenantId).orElseThrow();

&#x20;   }

}
```

**与 Spring Cache 集成**：对于频繁访问且不经常变化的数据，可以使用 Spring Cache 进行缓存。



```
@Service

@CacheConfig(cacheNames = "users")

public class UserService {

&#x20;   @Cacheable(key = "#id")

&#x20;   public User getUserById(Long id) {

&#x20;       return userRepository.findById(id).orElse(null);

&#x20;   }

&#x20;  &#x20;

&#x20;   @CacheEvict(key = "#user.id")

&#x20;   public void updateUser(User user) {

&#x20;       userRepository.save(user);

&#x20;   }

}
```

**与 Spring Data Elasticsearch 集成**：在需要全文搜索的场景中，可以结合 Spring Data Elasticsearch 实现复杂的搜索功能。

**与 Spring Integration 集成**：在需要异步处理或消息驱动的场景中，可以结合 Spring Integration 实现。

## 3. 基于不同业务场景的灵活设计

### 3.1 简单 CRUD 场景设计

简单 CRUD 场景是最常见的业务场景，Spring Data JPA 提供了简洁高效的解决方案：

**基本 CRUD 操作**：通过继承 JpaRepository，可以自动获得 40 + 个 CRUD 方法，包括：



```
public interface ProductRepository extends JpaRepository\<Product, Long> {

&#x20;   // 继承JpaRepository自动获得的方法示例：

&#x20;   // save(S entity)

&#x20;   // findById(ID id)

&#x20;   // findAll()

&#x20;   // findAllById(Iterable\<ID> ids)

&#x20;   // count()

&#x20;   // existsById(ID id)

&#x20;   // deleteById(ID id)

&#x20;   // delete(T entity)

}
```

**分页和排序支持**：使用 Pageable 接口实现分页查询，Sort 接口实现排序。



```
// 分页查询，第一页（从0开始），每页20条记录，按价格降序排序

Page\<Product> products = productRepository.findAll(PageRequest.of(0, 20, Sort.by("price").descending()));

// 排序查询，按创建时间升序

List\<Product> products = productRepository.findAll(Sort.by("createdAt"));
```

**批量操作**：使用 saveAll 方法进行批量保存，默认情况下会执行批量 SQL 操作。



```
List\<Product> productsToSave = new ArrayList<>();

// 添加多个Product对象到列表

productRepository.saveAll(productsToSave);
```

但需要注意的是，默认的 saveAll 方法在某些情况下可能不是真正的批量操作，需要正确配置 Hibernate 的批量参数以获得最佳性能。

### 3.2 复杂查询场景设计

复杂查询场景需要更灵活的查询机制，以下是几种解决方案：

**@Query 注解方式**：使用 @Query 注解可以编写自定义的 JPQL 或原生 SQL 查询。



```
public interface OrderRepository extends JpaRepository\<Order, Long> {

&#x20;  &#x20;

&#x20;   // JPQL查询，使用命名参数

&#x20;   @Query("SELECT o FROM Order o " +

&#x20;          "JOIN FETCH o.user u " +

&#x20;          "JOIN FETCH o.orderItems oi " +

&#x20;          "WHERE o.status = :status " +

&#x20;          "AND u.country = :country")

&#x20;   List\<Order> findByStatusAndUserCountry(@Param("status") OrderStatus status,&#x20;

&#x20;                                         @Param("country") String country);

&#x20;  &#x20;

&#x20;   // 原生SQL查询

&#x20;   @Query(value = "SELECT \* FROM orders o " +

&#x20;                 "JOIN users u ON o.user\_id = u.id " +

&#x20;                 "WHERE o.total\_amount > :minAmount",&#x20;

&#x20;          nativeQuery = true)

&#x20;   List\<Order> findByTotalAmountGreaterThan(@Param("minAmount") BigDecimal minAmount);

}
```

**Specification 模式**：适用于动态查询场景，支持运行时动态构建查询条件。



```
public interface ProductRepository extends JpaRepository\<Product, Long>, JpaSpecificationExecutor\<Product> {

}

// 使用Specification进行动态查询

public List\<Product> searchProducts(ProductSearchCriteria criteria) {

&#x20;   Specification\<Product> spec = Specification.where(null);

&#x20;  &#x20;

&#x20;   if (StringUtils.isNotBlank(criteria.getName())) {

&#x20;       spec = spec.and((root, query, cb) ->&#x20;

&#x20;           cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));

&#x20;   }

&#x20;  &#x20;

&#x20;   if (criteria.getMinPrice() != null) {

&#x20;       spec = spec.and((root, query, cb) ->&#x20;

&#x20;           cb.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));

&#x20;   }

&#x20;  &#x20;

&#x20;   if (criteria.getMaxPrice() != null) {

&#x20;       spec = spec.and((root, query, cb) ->&#x20;

&#x20;           cb.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));

&#x20;   }

&#x20;  &#x20;

&#x20;   return productRepository.findAll(spec);

}
```

**QueryDSL 集成**：对于非常复杂的查询场景，可以集成 QueryDSL，提供类型安全的查询构建。



```
// 首先需要生成Q类（使用Annotation Processor）

@Entity

public class Product {

&#x20;   @Id

&#x20;   private Long id;

&#x20;   private String name;

&#x20;   private BigDecimal price;

&#x20;   // 其他属性和getter/setter

}

// 使用QueryDSL进行复杂查询

public List\<Product> complexSearch(ProductSearchCriteria criteria) {

&#x20;   QProduct qProduct = QProduct.product;

&#x20;   BooleanBuilder builder = new BooleanBuilder();

&#x20;  &#x20;

&#x20;   if (StringUtils.isNotBlank(criteria.getName())) {

&#x20;       builder.and(qProduct.name.containsIgnoreCase(criteria.getName()));

&#x20;   }

&#x20;  &#x20;

&#x20;   if (criteria.getMinPrice() != null) {

&#x20;       builder.and(qProduct.price.goe(criteria.getMinPrice()));

&#x20;   }

&#x20;  &#x20;

&#x20;   if (criteria.getMaxPrice() != null) {

&#x20;       builder.and(qProduct.price.loe(criteria.getMaxPrice()));

&#x20;   }

&#x20;  &#x20;

&#x20;   return queryFactory.selectFrom(qProduct)

&#x20;           .where(builder)

&#x20;           .orderBy(qProduct.price.desc())

&#x20;           .fetch();

}
```

### 3.3 批量操作场景设计

批量操作场景需要特别注意性能优化，以下是几种批量操作的设计方案：

**批量插入优化**：



```
@Service

public class ProductImportService {

&#x20;   private static final int BATCH\_SIZE = 500;

&#x20;  &#x20;

&#x20;   @Autowired

&#x20;   private ProductRepository productRepository;

&#x20;  &#x20;

&#x20;   @Transactional

&#x20;   public void importProducts(List\<Product> products) {

&#x20;       int batchNumber = 0;

&#x20;      &#x20;

&#x20;       for (int i = 0; i < products.size(); i++) {

&#x20;           productRepository.save(products.get(i));

&#x20;          &#x20;

&#x20;           if (i > 0 && i % BATCH\_SIZE == 0) {

&#x20;               // 每500条执行一次flush和clear

&#x20;               productRepository.flush();

&#x20;               productRepository.clear();

&#x20;               batchNumber++;

&#x20;               System.out.println("Processed batch: " + batchNumber);

&#x20;           }

&#x20;       }

&#x20;      &#x20;

&#x20;       // 处理最后一批

&#x20;       productRepository.flush();

&#x20;       productRepository.clear();

&#x20;   }

}
```

**批量更新和删除**：使用 JPQL 批量操作，避免逐条更新。



```
public interface ProductRepository extends JpaRepository\<Product, Long> {

&#x20;  &#x20;

&#x20;   @Modifying

&#x20;   @Query("UPDATE Product p SET p.status = :status WHERE p.category.id = :categoryId")

&#x20;   int updateStatusByCategory(@Param("status") ProductStatus status,&#x20;

&#x20;                             @Param("categoryId") Long categoryId);

&#x20;  &#x20;

&#x20;   @Modifying

&#x20;   @Query("DELETE FROM Product p WHERE p.createdAt < :date")

&#x20;   int deleteAllCreatedBefore(@Param("date") LocalDate date);

}
```

**使用 Hibernate 的批量操作特性**：



```
@Service

public class BatchService {

&#x20;   @PersistenceContext

&#x20;   private EntityManager entityManager;

&#x20;  &#x20;

&#x20;   public void batchUpdateProducts(List\<Product> products) {

&#x20;       int batchSize = 20;

&#x20;      &#x20;

&#x20;       for (int i = 0; i < products.size(); i++) {

&#x20;           Product product = products.get(i);

&#x20;           entityManager.merge(product);

&#x20;          &#x20;

&#x20;           if (i % batchSize == 0) {

&#x20;               // 执行批量更新

&#x20;               entityManager.flush();

&#x20;               entityManager.clear();

&#x20;           }

&#x20;       }

&#x20;   }

}
```

### 3.4 事务处理场景设计

不同的事务处理场景需要采用不同的策略：

**单事务处理多个聚合**：在某些场景下，需要在一个事务中处理多个聚合根。



```
@Service

@Transactional

public class OrderProcessingService {

&#x20;   private final OrderRepository orderRepository;

&#x20;   private final InventoryRepository inventoryRepository;

&#x20;   private final CustomerRepository customerRepository;

&#x20;  &#x20;

&#x20;   public void processOrder(Order order) {

&#x20;       // 1. 保存订单

&#x20;       orderRepository.save(order);

&#x20;      &#x20;

&#x20;       // 2. 更新库存

&#x20;       Inventory inventory = inventoryRepository.findById(order.getProductId()).orElseThrow();

&#x20;       inventory.decreaseStock(order.getQuantity());

&#x20;       inventoryRepository.save(inventory);

&#x20;      &#x20;

&#x20;       // 3. 更新客户积分

&#x20;       Customer customer = customerRepository.findById(order.getUserId()).orElseThrow();

&#x20;       customer.addPoints(order.getTotalAmount() \* 0.01); // 1%积分

&#x20;       customerRepository.save(customer);

&#x20;   }

}
```

**跨服务事务处理**：在微服务架构中，可能需要跨多个服务的事务处理，这时可以使用分布式事务解决方案如 Saga 模式或使用消息队列实现最终一致性。



```
// 使用消息队列实现最终一致性

@Service

public class OrderService {

&#x20;   private final OrderRepository orderRepository;

&#x20;   private final RabbitTemplate rabbitTemplate;

&#x20;  &#x20;

&#x20;   @Transactional

&#x20;   public void createOrder(Order order) {

&#x20;       // 1. 保存订单

&#x20;       orderRepository.save(order);

&#x20;      &#x20;

&#x20;       // 2. 发送消息到库存服务

&#x20;       rabbitTemplate.convertAndSend("inventory-exchange", "deduct-stock",&#x20;

&#x20;           new InventoryMessage(order.getProductId(), order.getQuantity(), order.getId()));

&#x20;      &#x20;

&#x20;       // 3. 发送消息到积分服务

&#x20;       rabbitTemplate.convertAndSend("points-exchange", "add-points",

&#x20;           new PointsMessage(order.getUserId(), order.getTotalAmount() \* 0.01, order.getId()));

&#x20;   }

}
```

**只读事务优化场景**：对于报表生成、数据统计等只读场景，使用只读事务可以获得显著的性能提升。



```
@Service

public class StatisticsService {

&#x20;   @Transactional(readOnly = true)

&#x20;   public Map\<String, Object> generateMonthlyStatistics() {

&#x20;       Map\<String, Object> statistics = new HashMap<>();

&#x20;      &#x20;

&#x20;       // 统计订单数量

&#x20;       long orderCount = orderRepository.count();

&#x20;      &#x20;

&#x20;       // 统计总销售额

&#x20;       BigDecimal totalSales = orderRepository.totalSales();

&#x20;      &#x20;

&#x20;       // 统计活跃用户数

&#x20;       long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);

&#x20;      &#x20;

&#x20;       statistics.put("orderCount", orderCount);

&#x20;       statistics.put("totalSales", totalSales);

&#x20;       statistics.put("activeUsers", activeUsers);

&#x20;      &#x20;

&#x20;       return statistics;

&#x20;   }

}
```

## 4. 实现技巧与高级特性

### 4.1 自定义查询方法实现技巧

自定义查询方法是 Spring Data JPA 提供的强大功能，以下是一些实现技巧：

**方法命名规则高级用法**：Spring Data JPA 支持丰富的方法命名规则，包括各种查询关键字：



| 关键字         | 示例                       | 生成的 JPQL 片段                       |
| ----------- | ------------------------ | --------------------------------- |
| And         | findByNameAndAge         | WHERE name = ?1 AND age = ?2      |
| Or          | findByNameOrEmail        | WHERE name = ?1 OR email = ?2     |
| Between     | findByAgeBetween         | WHERE age BETWEEN ?1 AND ?2       |
| LessThan    | findByAgeLessThan        | WHERE age < ?1                    |
| GreaterThan | findByAgeGreaterThan     | WHERE age > ?1                    |
| Like        | findByNameLike           | WHERE name LIKE ?1                |
| OrderBy     | findByAgeOrderByNameDesc | WHERE age = ?1 ORDER BY name DESC |
| Not         | findByNameNot            | WHERE name != ?1                  |
| In          | findByNameIn             | WHERE name IN (?1)                |

**@Query 注解高级特性**：



```
public interface UserRepository extends JpaRepository\<User, Long> {

&#x20;  &#x20;

&#x20;   // 使用JPQL，支持命名参数

&#x20;   @Query("SELECT u FROM User u WHERE u.username = :username")

&#x20;   Optional\<User> findByUsername(@Param("username") String username);

&#x20;  &#x20;

&#x20;   // 使用原生SQL

&#x20;   @Query(value = "SELECT \* FROM users WHERE created\_at > :date", nativeQuery = true)

&#x20;   List\<User> findAllCreatedAfter(@Param("date") LocalDate date);

&#x20;  &#x20;

&#x20;   // 批量更新

&#x20;   @Modifying

&#x20;   @Query("UPDATE User u SET u.status = :status WHERE u.id IN :userIds")

&#x20;   int updateStatusInBatch(@Param("status") UserStatus status, @Param("userIds") List\<Long> userIds);

&#x20;  &#x20;

&#x20;   // 存储过程调用

&#x20;   @Procedure(name = "sp\_calculate\_tax")

&#x20;   BigDecimal calculateTax(@Param("amount") BigDecimal amount, @Param("taxRate") BigDecimal taxRate);

}
```

**使用 @QueryHints 设置查询提示**：可以设置各种查询提示来优化查询执行。



```
public interface ProductRepository extends JpaRepository\<Product, Long> {

&#x20;  &#x20;

&#x20;   @QueryHints({

&#x20;       @QueryHint(name = "org.hibernate.cacheable", value = "true"), // 启用查询缓存

&#x20;       @QueryHint(name = "org.hibernate.fetchSize", value = "50"), // 设置fetch size

&#x20;       @QueryHint(name = "org.hibernate.timeout", value = "10") // 设置超时时间（秒）

&#x20;   })

&#x20;   List\<Product> findByCategoryId(Long categoryId);

}
```

### 4.2 Repository 接口继承层次设计

合理的 Repository 接口继承层次设计可以大大提高代码的复用性和可维护性：

**基础 Repository 设计**：创建一个基础的 BaseRepository 接口，包含所有 Repository 都需要的通用方法。



```
public interface BaseRepository\<T, ID> extends JpaRepository\<T, ID> {

&#x20;   List\<T> findAllByIdIn(Collection\<ID> ids);

&#x20;  &#x20;

&#x20;   @Modifying

&#x20;   @Query("UPDATE #{#entityName} e SET e.updatedBy = :updatedBy, e.updatedAt = CURRENT\_TIMESTAMP WHERE e.id IN :ids")

&#x20;   int markAsUpdated(@Param("ids") Collection\<ID> ids, @Param("updatedBy") String updatedBy);

&#x20;  &#x20;

&#x20;   // 其他通用方法...

}
```

**分页和排序基础接口**：创建 PagingAndSortingBaseRepository，包含常用的分页和排序方法。



```
public interface PagingAndSortingBaseRepository\<T, ID> extends BaseRepository\<T, ID>, PagingAndSortingRepository\<T, ID> {

&#x20;   Page\<T> findAllByStatus(String status, Pageable pageable);

&#x20;  &#x20;

&#x20;   // 其他分页相关方法...

}
```

**领域特定 Repository 继承结构**：



```
// 用户Repository

public interface UserRepository extends PagingAndSortingBaseRepository\<User, Long> {

&#x20;   List\<User> findByUsernameContaining(String keyword);

&#x20;  &#x20;

&#x20;   Page\<User> findByEmail(String email, Pageable pageable);

&#x20;  &#x20;

&#x20;   // 自定义方法...

}

// 订单Repository

public interface OrderRepository extends PagingAndSortingBaseRepository\<Order, Long> {

&#x20;   List\<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

&#x20;  &#x20;

&#x20;   Page\<Order> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

&#x20;  &#x20;

&#x20;   // 自定义方法...

}
```

**多级继承设计**：对于更复杂的继承结构，可以设计多级 Repository 接口。



```
// 第一级：最基础的Repository

public interface BaseRepository\<T, ID> extends JpaRepository\<T, ID> {

&#x20;   // 通用方法

}

// 第二级：添加审计功能的Repository

public interface AuditableRepository\<T, ID> extends BaseRepository\<T, ID> {

&#x20;   @Modifying

&#x20;   @Query("UPDATE #{#entityName} e SET e.createdBy = :createdBy, e.createdAt = CURRENT\_TIMESTAMP WHERE e.id IN :ids")

&#x20;   int markAsCreated(@Param("ids") Collection\<ID> ids, @Param("createdBy") String createdBy);

&#x20;  &#x20;

&#x20;   // 其他审计相关方法...

}

// 第三级：添加分页排序功能的Repository

public interface PagingAuditableRepository\<T, ID> extends AuditableRepository\<T, ID>, PagingAndSortingRepository\<T, ID> {

&#x20;   // 分页相关方法...

}

// 用户Repository继承自PagingAuditableRepository

public interface UserRepository extends PagingAuditableRepository\<User, Long> {

&#x20;   // 用户特定的方法...

}
```

### 4.3 与其他数据访问技术集成

Spring Data JPA 可以与多种数据访问技术集成，提供更灵活的解决方案：

**与原生 SQL 集成**：在某些复杂查询场景下，使用原生 SQL 可能更高效。



```
public interface UserRepository extends JpaRepository\<User, Long> {

&#x20;  &#x20;

&#x20;   @Query(value = "SELECT \* FROM users WHERE username = ?1", nativeQuery = true)

&#x20;   @ResultTransformer(Transformers.aliasToBean(User.class))

&#x20;   User findByUsernameNative(String username);

&#x20;  &#x20;

&#x20;   @Query(value = "CALL sp\_get\_user\_orders(?)", nativeQuery = true)

&#x20;   List\<Order> getOrdersByUserIdNative(Long userId);

}
```

**与存储过程集成**：使用 @Procedure 注解可以方便地调用存储过程。



```
// 方式一：使用@NamedStoredProcedureQuery在实体类上定义

@Entity

@NamedStoredProcedureQuery(

&#x20;   name = "User.calculateTotalOrders",

&#x20;   procedureName = "sp\_calculate\_total\_orders",

&#x20;   parameters = {

&#x20;       @StoredProcedureParameter(mode = ParameterMode.IN, name = "user\_id", type = Long.class),

&#x20;       @StoredProcedureParameter(mode = ParameterMode.OUT, name = "total\_orders", type = Integer.class)

&#x20;   }

)

public class User {

&#x20;   // 实体属性...

}

// 在Repository中调用

public interface UserRepository extends JpaRepository\<User, Long> {

&#x20;   @Procedure(name = "User.calculateTotalOrders")

&#x20;   int calculateTotalOrders(@Param("user\_id") Long userId);

}

// 方式二：直接在Repository方法上使用@Procedure

public interface UserRepository extends JpaRepository\<User, Long> {

&#x20;   @Procedure(procedureName = "sp\_get\_user\_details")

&#x20;   UserDetails getUserDetails(@Param("user\_id") Long userId);

}
```

**与 NoSQL 数据库集成**：在某些场景下，可能需要同时使用关系型数据库和 NoSQL 数据库，可以结合 Spring Data MongoDB 等实现。



```
// Spring Data MongoDB Repository

public interface UserMongoRepository extends MongoRepository\<User, String> {

&#x20;   List\<User> findByUsername(String username);

}

// Service层集成使用

@Service

public class UserService {

&#x20;   private final UserRepository userRepository; // JPA Repository

&#x20;   private final UserMongoRepository userMongoRepository; // MongoDB Repository

&#x20;  &#x20;

&#x20;   public User findUser(Long userId) {

&#x20;       // 先从关系型数据库查询

&#x20;       return userRepository.findById(userId).orElseGet(() -> {

&#x20;           // 如果不存在，从MongoDB查询（示例场景）

&#x20;           return userMongoRepository.findById(userId.toString()).orElse(null);

&#x20;       });

&#x20;   }

}
```

**与 Elasticsearch 集成**：对于全文搜索场景，可以集成 Spring Data Elasticsearch。



```
// Elasticsearch Repository

public interface UserSearchRepository extends ElasticsearchRepository\<User, String> {

&#x20;   List\<User> findByUsernameContaining(String username);

}

// 同步数据到Elasticsearch

@Service

public class UserService {

&#x20;   private final UserRepository userRepository;

&#x20;   private final UserSearchRepository userSearchRepository;

&#x20;  &#x20;

&#x20;   @Transactional

&#x20;   public void saveUser(User user) {

&#x20;       // 1. 保存到关系型数据库

&#x20;       userRepository.save(user);

&#x20;      &#x20;

&#x20;       // 2. 同步到Elasticsearch（可以使用异步方式）

&#x20;       userSearchRepository.save(user);

&#x20;   }

}
```

### 4.4 测试策略与实践

良好的测试策略是保证代码质量的关键，以下是 Spring Data JPA 的测试实践：

**使用 @DataJpaTest 进行 Repository 测试**：



```
@DataJpaTest

class UserRepositoryTest {

&#x20;   @Autowired

&#x20;   private UserRepository userRepository;

&#x20;  &#x20;

&#x20;   @Autowired

&#x20;   private TestEntityManager testEntityManager;

&#x20;  &#x20;

&#x20;   @Test

&#x20;   void testFindByUsername() {

&#x20;       // 创建测试数据

&#x20;       User user = new User();

&#x20;       user.setUsername("testuser");

&#x20;       user.setEmail("test@example.com");

&#x20;       testEntityManager.persist(user);

&#x20;       testEntityManager.flush();

&#x20;      &#x20;

&#x20;       // 执行查询

&#x20;       Optional\<User> foundUser = userRepository.findByUsername("testuser");

&#x20;      &#x20;

&#x20;       // 验证结果

&#x20;       assertTrue(foundUser.isPresent());

&#x20;       assertEquals("testuser", foundUser.get().getUsername());

&#x20;       assertEquals("test@example.com", foundUser.get().getEmail());

&#x20;   }

&#x20;  &#x20;

&#x20;   @Test

&#x20;   void testSaveAndFindAll() {

&#x20;       // 创建多个测试数据

&#x20;       User user1 = new User("user1", "user1@example.com");

&#x20;       User user2 = new User("user2", "user2@example.com");

&#x20;      &#x20;

&#x20;       userRepository.save(user1);

&#x20;       userRepository.save(user2);

&#x20;      &#x20;

&#x20;       List\<User> allUsers = userRepository.findAll();

&#x20;       assertEquals(2, allUsers.size());

&#x20;       assertTrue(allUsers.stream().anyMatch(u -> "user1".equals(u.getUsername())));

&#x20;       assertTrue(allUsers.stream().anyMatch(u -> "user2".equals(u.getUsername())));

&#x20;   }

}
```

**测试自定义查询方法**：



```
@DataJpaTest

class OrderRepositoryTest {

&#x20;   @Autowired

&#x20;   private OrderRepository orderRepository;

&#x20;  &#x20;

&#x20;   @Test

&#x20;   void testFindByStatusAndUserCountry() {

&#x20;       // 创建测试数据

&#x20;       User user1 = new User("user1", "user1@example.com", "US");

&#x20;       User user2 = new User("user2", "user2@example.com", "CA");

&#x20;      &#x20;

&#x20;       Order order1 = new Order(user1, OrderStatus.COMPLETED, 100.0);

&#x20;       Order order2 = new Order(user2, OrderStatus.COMPLETED, 200.0);

&#x20;       Order order3 = new Order(user1, OrderStatus.PENDING, 150.0);

&#x20;      &#x20;

&#x20;       orderRepository.saveAll(Arrays.asList(order1, order2, order3));

&#x20;      &#x20;

&#x20;       // 查询状态为COMPLETED且国家为US的订单

&#x20;       List\<Order> orders = orderRepository.findByStatusAndUserCountry(OrderStatus.COMPLETED, "US");

&#x20;      &#x20;

&#x20;       assertEquals(1, orders.size());

&#x20;       assertEquals(100.0, orders.get(0).getTotalAmount());

&#x20;   }

}
```

**使用 Testcontainers 进行真实数据库测试**：对于更可靠的测试，可以使用 Testcontainers 连接真实的数据库。



```
@Testcontainers

@DataJpaTest

class ProductRepositoryIntegrationTest {

&#x20;   @Container

&#x20;   private static final PostgreSQLContainer\<?> postgresqlContainer =&#x20;

&#x20;       new PostgreSQLContainer<>("postgres:13-alpine")

&#x20;           .withDatabaseName("testdb")

&#x20;           .withUsername("testuser")

&#x20;           .withPassword("testpass");

&#x20;  &#x20;

&#x20;   @Autowired

&#x20;   private ProductRepository productRepository;

&#x20;  &#x20;

&#x20;   @BeforeEach

&#x20;   void setUp() {

&#x20;       // 设置数据源

&#x20;       System.setProperty("spring.datasource.url", postgresqlContainer.getJdbcUrl());

&#x20;       System.setProperty("spring.datasource.username", postgresqlContainer.getUsername());

&#x20;       System.setProperty("spring.datasource.password", postgresqlContainer.getPassword());

&#x20;   }

&#x20;  &#x20;

&#x20;   @Test

&#x20;   void testBatchInsertPerformance() {

&#x20;       List\<Product> products = new ArrayList<>();

&#x20;       for (int i = 0; i < 1000; i++) {

&#x20;           products.add(new Product("Product " + i, 10.0 + i \* 0.1));

&#x20;       }

&#x20;      &#x20;

&#x20;       long start = System.currentTimeMillis();

&#x20;       productRepository.saveAll(products);

&#x20;       long end = System.currentTimeMillis();

&#x20;      &#x20;

&#x20;       System.out.println("Batch insert of 1000 products took: " + (end - start) + "ms");

&#x20;       assertTrue(end - start < 500); // 期望在500ms内完成

&#x20;   }

}
```

**Mock 测试策略**：在某些场景下，可能需要使用 Mockito 进行 Mock 测试。



```
@RunWith(MockitoJUnitRunner.class)

class UserServiceTest {

&#x20;   @Mock

&#x20;   private UserRepository userRepository;

&#x20;  &#x20;

&#x20;   @InjectMocks

&#x20;   private UserService userService;

&#x20;  &#x20;

&#x20;   @Test

&#x20;   void testGetUserById() {

&#x20;       // 设置Mock行为

&#x20;       User user = new User("testuser", "test@example.com");

&#x20;       when(userRepository.findById(1L)).thenReturn(Optional.of(user));

&#x20;      &#x20;

&#x20;       // 调用服务方法

&#x20;       User result = userService.getUserById(1L);

&#x20;      &#x20;

&#x20;       // 验证

&#x20;       assertEquals("testuser", result.getUsername());

&#x20;       verify(userRepository, times(1)).findById(1L);

&#x20;   }

}
```

## 5. 总结与学习建议

### 5.1 核心要点回顾

通过对 Spring Data JPA Repository 设计的深入分析，我们可以总结出以下核心要点：

**设计理念层面**：



* Repository 模式在 DDD 中扮演着领域对象 "内存集合" 的角色，作为领域层和数据映射层之间的中介，提供面向对象的数据访问接口[(21)](https://blog.csdn.net/qq_29328443/article/details/149855876)

* Spring Data JPA 充分体现了 "约定优于配置" 的原则，通过方法命名规则、接口继承体系等约定，极大减少了样板代码

* 2026 年的最新版本引入了 AOT 编译、虚拟线程支持、只读事务优化等重要特性，显著提升了性能和开发体验

**最佳实践层面**：



* 采用分层设计策略，包括基础 Repository、领域特定 Repository 和自定义实现，提高代码复用性

* 事务管理应遵循 "最小化原则"，使用只读事务优化读取操作，选择合适的传播行为和隔离级别[(88)](https://blog.csdn.net/weixin_55344375/article/details/146196819)

* 性能优化方面，正确配置批量操作参数、使用分页查询、合理使用缓存，可以获得显著的性能提升[(130)](https://blog.csdn.net/qq_33556185/article/details/121482679)

**灵活设计层面**：



* 简单 CRUD 场景直接使用 JpaRepository 提供的默认方法

* 复杂查询场景可选择 @Query 注解、Specification 模式或 QueryDSL，根据具体需求选择合适的方案

* 批量操作场景需要特别注意性能优化，正确配置 Hibernate 批量参数并使用批量方法

* 事务处理场景需要根据业务需求选择合适的事务策略，包括单事务处理多个聚合、跨服务事务等

**实现技巧层面**：



* 熟练掌握方法命名规则和 @Query 注解的高级用法，能够灵活应对各种查询需求

* 合理设计 Repository 接口继承层次，实现代码的高度复用

* 掌握与原生 SQL、存储过程、NoSQL 数据库、Elasticsearch 等技术的集成方法

* 建立完善的测试策略，使用 @DataJpaTest、Testcontainers 等工具确保代码质量

### 5.2 学习路径建议

基于不同的学习阶段和目标，以下是分层的学习路径建议：

**入门阶段（0-3 个月）**：



1. 首先理解 JPA 的基本概念，包括实体映射、基本 CRUD 操作

2. 学习 Spring Data JPA 的基础使用，掌握 Repository 接口的基本用法

3. 练习使用方法命名规则创建简单查询

4. 了解基本的事务管理和分页查询

推荐资源：



* Spring 官方文档：[https://spring.io/projects/spring-data-jpa](https://spring.io/projects/spring-data-jpa)

* 《Spring Data JPA 实战》等入门书籍

* 官方示例项目：[https://github.com/spring-projects/spring-data-examples](https://github.com/spring-projects/spring-data-examples)

**进阶阶段（3-6 个月）**：



1. 深入理解 Repository 模式和 DDD 的关系

2. 掌握 @Query 注解的高级用法，包括 JPQL 和原生 SQL

3. 学习 Specification 模式和动态查询构建

4. 了解性能优化技巧，特别是批量操作和缓存策略

5. 掌握基本的集成测试方法

推荐资源：



* 《领域驱动设计：软件核心复杂性应对之道》

* Spring Data JPA 高级教程和最佳实践文章

* 参与开源项目的 JPA 模块开发

**高级阶段（6 个月以上）**：



1. 深入研究 Spring Data JPA 的底层原理和实现机制

2. 掌握 QueryDSL 等高级查询技术

3. 学习与其他数据访问技术的集成方案

4. 了解分布式事务处理和最终一致性实现

5. 掌握性能调优和监控技术

推荐资源：



* Spring Data JPA 源码分析

* 高级技术博客和论文

* 参与技术社区讨论，分享实践经验

**实战项目建议**：



1. 从简单的 CRUD 应用开始，逐步增加复杂度

2. 尝试在实际项目中应用所学知识，解决具体问题

3. 遇到问题时，先尝试自己分析和解决，然后查阅资料

4. 定期总结经验，形成自己的最佳实践指南

### 5.3 常见误区与避坑指南

在学习和使用 Spring Data JPA 的过程中，常见的误区和陷阱包括：

**性能误区**：



* 误区：认为 Spring Data JPA 的 saveAll 方法总是批量操作


  * 避坑：需要正确配置 Hibernate 的 batch\_size 参数，并在 MySQL 中启用 rewriteBatchedStatements

* 误区：不区分读写操作，所有方法都使用默认事务


  * 避坑：对只读操作使用 @Transactional (readOnly = true)，特别是在 Spring Boot 4 中效果显著

**设计误区**：



* 误区：为每个数据库表创建一个 Repository，违背 DDD 原则


  * 避坑：应该为每个聚合根创建一个 Repository，而不是每个表

* 误区：在 Repository 中编写复杂的业务逻辑


  * 避坑：Repository 应该只负责数据访问，业务逻辑应放在 Service 层

**查询误区**：



* 误区：过度使用方法命名规则，导致方法名过长难以理解


  * 避坑：复杂查询使用 @Query 注解，保持方法名简洁

* 误区：忽略 N+1 问题，导致性能问题


  * 避坑：使用 JOIN FETCH、@EntityGraph 等技术优化关联查询

**事务误区**：



* 误区：在事务中执行长时间操作或调用外部服务


  * 避坑：保持事务简短，将非数据库操作移出事务

* 误区：不理解事务传播行为，导致意外的事务边界


  * 避坑：深入理解 REQUIRED、REQUIRES\_NEW 等传播行为的区别

**集成误区**：



* 误区：直接在 Repository 中使用 EntityManager 的原生方法


  * 避坑：保持 Repository 接口的整洁，复杂操作通过自定义实现类处理

* 误区：不进行充分的测试，特别是边界条件


  * 避坑：建立完善的测试体系，包括单元测试和集成测试

通过系统学习和不断实践，结合最新的技术特性和最佳实践，你将能够熟练掌握 Spring Data JPA Repository 的设计和使用，在实际项目中发挥其强大的功能，构建高效、可维护的数据访问层。记住，技术的学习是一个持续的过程，保持好奇心和实践精神，不断探索和创新，你将在 Spring Data JPA 的世界中找到属于自己的最佳实践。

**参考资料&#x20;**

\[1] Java语言Spring开发访问数据库:集成JPA\_圣逸的技术博客\_51CTO博客[ https://blog.51cto.com/u\_17035323/14433272](https://blog.51cto.com/u_17035323/14433272)

\[2] Spring Data JPA核心原理与实战:从注解配置、Repository开发到主流ORM框架对比 - CSDN文库[ https://wenku.csdn.net/doc/2pz0xa4q14](https://wenku.csdn.net/doc/2pz0xa4q14)

\[3] Spring Data JPA详解Spring Data JPA详解:从入门到实战 前言 Spring Data JPA - 掘金[ https://juejin.cn/post/7580202945147437071](https://juejin.cn/post/7580202945147437071)

\[4] Defining Repository Interfaces (Defining Repository Interfaces) | Spring Data JDBC3.3.4中文文档|Spring官方文档|SpringBoot 教程|Spring中文网[ https://www.spring-doc.cn/spring-data-jdbc/3.3.4/repositories\_definition.en.html](https://www.spring-doc.cn/spring-data-jdbc/3.3.4/repositories_definition.en.html)

\[5] General ORM Integration Considerations (General ORM Integration Considerations) | Spring Framework6.1.14-SNAPSHOT中文文档|Spring官方文档|SpringBoot 教程|Spring中文网[ https://www.spring-doc.cn/spring-framework/6.1.14-SNAPSHOT/data-access\_orm\_general.en.html](https://www.spring-doc.cn/spring-framework/6.1.14-SNAPSHOT/data-access_orm_general.en.html)

\[6] Spring Data JPA 入门:从理论到实践的完整指南\_spring jpa 编程指南-CSDN博客[ https://blog.csdn.net/hwh22/article/details/149916652](https://blog.csdn.net/hwh22/article/details/149916652)

\[7] springdatajpa最佳实践【2/2】:存储库设计指南[ https://juejin.cn/post/7576669556150091814](https://juejin.cn/post/7576669556150091814)

\[8] Spring Data JPA原理与实战 Repository接口的魔法揭秘-CSDN博客[ https://blog.csdn.net/sinat\_41617212/article/details/156655290](https://blog.csdn.net/sinat_41617212/article/details/156655290)

\[9] Spring Data简化数据访问层开发及多数据库支持[ https://www.iesdouyin.com/share/video/7529832093040446777/?region=\&mid=6959472181726267429\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=YeH5YuAQLSdvSA2OVWK20hLXUgAwobqU4IynbAaGpgQ-\&share\_version=280700\&ts=1773041382\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7529832093040446777/?region=\&mid=6959472181726267429\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=YeH5YuAQLSdvSA2OVWK20hLXUgAwobqU4IynbAaGpgQ-\&share_version=280700\&ts=1773041382\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[10] Best Practice Using Spring Data JPA[ https://bytegoblin.io/blog/best-practice-using-spring-data-jpa.mdx](https://bytegoblin.io/blog/best-practice-using-spring-data-jpa.mdx)

\[11] 架构师之Spring Data JPA详解-腾讯云开发者社区-腾讯云[ https://cloud.tencent.cn/developer/article/2576823](https://cloud.tencent.cn/developer/article/2576823)

\[12] Add support for @Static\[Native]Query(…) , @ReadQueryOptions  and @WriteQueryOptions #4141[ https://github.com/spring-projects/spring-data-jpa/issues/4141](https://github.com/spring-projects/spring-data-jpa/issues/4141)

\[13] Issues · spring-projects/spring-data-jpa · GitHub[ https://github.com/spring-projects/spring-data-jpa/issues](https://github.com/spring-projects/spring-data-jpa/issues)

\[14] Spring Data JPA Specification - Generic Query Builder[ https://github.com/prasadgaikwad/spring-data-jpa-specification/blob/main/README.md](https://github.com/prasadgaikwad/spring-data-jpa-specification/blob/main/README.md)

\[15] Spring Boot CrudRepository with Example: A Deep, Practical Guide for 2026[ https://thelinuxcode.com/spring-boot-crudrepository-with-example-a-deep-practical-guide-for-2026/](https://thelinuxcode.com/spring-boot-crudrepository-with-example-a-deep-practical-guide-for-2026/)

\[16] Spring Data JPA 3.5.0中文-英文对照API文档(含Maven/Gradle依赖与源码地址) - CSDN文库[ https://wenku.csdn.net/doc/7o93x5opu2](https://wenku.csdn.net/doc/7o93x5opu2)

\[17] Refactor entity metadata resolution to use JpaPersistentEntity. #4183[ https://github.com/spring-projects/spring-data-jpa/pull/4183](https://github.com/spring-projects/spring-data-jpa/pull/4183)

\[18] The Repository Pattern\_the repository pattern microsoft-CSDN博客[ https://blog.csdn.net/jingrenhai/article/details/6417464](https://blog.csdn.net/jingrenhai/article/details/6417464)

\[19] Projeto: Integração de Domain-Driven Design (DDD) e Repository Pattern no Django[ https://github.com/RobsonFe/DDD-Repository-Django](https://github.com/RobsonFe/DDD-Repository-Django)

\[20] Mastering DDD: Repository Design Patterns in Go[ https://bytegoblin.io/blog/mastering-ddd-repository-design-patterns-in-go](https://bytegoblin.io/blog/mastering-ddd-repository-design-patterns-in-go)

\[21] DDD Repository模式权威指南:从理论到Java实践\_java ddd money embedded-CSDN博客[ https://blog.csdn.net/qq\_29328443/article/details/149855876](https://blog.csdn.net/qq_29328443/article/details/149855876)

\[22] 领域驱动设计(DDD)是什么?——从理论到实践的全方位解析-CSDN博客[ https://blog.csdn.net/hyc010110/article/details/145667007](https://blog.csdn.net/hyc010110/article/details/145667007)

\[23] 领域驱动设计(DDD):大型分布式电商网站开发的核心方法论\_PlutokitCoder社区[ http://m.toutiao.com/group/7594521887389008422/?upstream\_biz=doubao](http://m.toutiao.com/group/7594521887389008422/?upstream_biz=doubao)

\[24] DDD资源库核心概念解析与实战应用[ https://www.iesdouyin.com/share/video/7499481896250887465/?region=\&mid=7499484015779760931\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=.Ijka26om829JHxmABhSJIaa8XhPYMer6YSChVhUUrM-\&share\_version=280700\&ts=1773041412\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7499481896250887465/?region=\&mid=7499484015779760931\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=.Ijka26om829JHxmABhSJIaa8XhPYMer6YSChVhUUrM-\&share_version=280700\&ts=1773041412\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[25] DDD Repository 模式解析\_ddd。reposity-CSDN博客[ https://blog.csdn.net/JavaCodePro/article/details/150466013](https://blog.csdn.net/JavaCodePro/article/details/150466013)

\[26] 领域驱动设计(DDD):从理论到实践，构建复杂业务系统的核心方法论-腾讯云开发者社区-腾讯云[ https://cloud.tencent.com/developer/article/2577634](https://cloud.tencent.com/developer/article/2577634)

\[27] DAO vs Repository Patterns | Baeldung[ https://www.baeldung.com/java-dao-vs-repository](https://www.baeldung.com/java-dao-vs-repository)

\[28] Understanding the Differences Between Repository and Data Access Object (DAO)[ https://dzone.com/articles/differences-between-repository-and-dao](https://dzone.com/articles/differences-between-repository-and-dao)

\[29] \[hibernate] DAO와 리포지토리 패턴의 차이점은 무엇입니까?[ http://daplus.net/hibernate-dao%EC%99%80-%EB%A6%AC%ED%8F%AC%EC%A7%80%ED%86%A0%EB%A6%AC-%ED%8C%A8%ED%84%B4%EC%9D%98-%EC%B0%A8%EC%9D%B4%EC%A0%90%EC%9D%80-%EB%AC%B4%EC%97%87%EC%9E%85%EB%8B%88%EA%B9%8C/](http://daplus.net/hibernate-dao%EC%99%80-%EB%A6%AC%ED%8F%AC%EC%A7%80%ED%86%A0%EB%A6%AC-%ED%8C%A8%ED%84%B4%EC%9D%98-%EC%B0%A8%EC%9D%B4%EC%A0%90%EC%9D%80-%EB%AC%B4%EC%97%87%EC%9E%85%EB%8B%88%EA%B9%8C/)

\[30] DAO vs Repository[ https://velog.io/@lilychoi/DAO-vs-Repository](https://velog.io/@lilychoi/DAO-vs-Repository)

\[31] DAO vs Repository[ https://velog.io/@seokhwan-an/DAO-vs-Repository](https://velog.io/@seokhwan-an/DAO-vs-Repository)

\[32] DAO와 Repository[ https://tecoble.techcourse.co.kr/post/2023-04-24-DAO-Repository/](https://tecoble.techcourse.co.kr/post/2023-04-24-DAO-Repository/)

\[33] Add support for @Static\[Native]Query(…) , @ReadQueryOptions  and @WriteQueryOptions #4141[ https://github.com/spring-projects/spring-data-jpa/issues/4141](https://github.com/spring-projects/spring-data-jpa/issues/4141)

\[34] Spring Data JPA 3.5.0中文-英文对照API文档(含Maven/Gradle依赖与源码地址) - CSDN文库[ https://wenku.csdn.net/doc/7o93x5opu2](https://wenku.csdn.net/doc/7o93x5opu2)

\[35] Bump org.springframework.boot:spring-boot-starter-data-jpa from 4.0.2 to 4.0.3[ https://github.com/MrBean355/zak-bagans-bot/pull/339](https://github.com/MrBean355/zak-bagans-bot/pull/339)

\[36] Upgrade to Jakarta Persistence 4.0 #4187[ https://github.com/spring-projects/spring-data-jpa/issues/4187](https://github.com/spring-projects/spring-data-jpa/issues/4187)

\[37] Issues · spring-projects/spring-data-jpa · GitHub[ https://github.com/spring-projects/spring-data-jpa/issues](https://github.com/spring-projects/spring-data-jpa/issues)

\[38] Quarkus Extension for Spring Data JPA API[ https://quarkus.io/extensions/io.quarkus/quarkus-spring-data-jpa/](https://quarkus.io/extensions/io.quarkus/quarkus-spring-data-jpa/)

\[39] Artifacts using spring-boot-starter-data-jpa version 3.1.8 (12)[ https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.1.8/used-by](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.1.8/used-by)

\[40] Artifacts using spring-boot-starter-data-jpa version 2.7.6 (89)[ https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/2.7.6/used-by?p=5](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/2.7.6/used-by?p=5)

\[41] Spring News Roundup: Second Milestone Releases of Boot, Security, Integration, Modulith, AMQP[ https://www.infoq.com/news/2026/02/spring-news-roundup-feb16-2026/?topicpagesponsorship=f9a848c7-75b7-4d7c-be4a-a1949f068f6a](https://www.infoq.com/news/2026/02/spring-news-roundup-feb16-2026/?topicpagesponsorship=f9a848c7-75b7-4d7c-be4a-a1949f068f6a)

\[42] Ahead of Time Optimizations[ https://docs.spring.io/spring-data/relational/reference/jdbc/aot.html](https://docs.spring.io/spring-data/relational/reference/jdbc/aot.html)

\[43] Consider enabling -parameters by default for AOT code compilation #49268[ https://github.com/spring-projects/spring-boot/issues/49268](https://github.com/spring-projects/spring-boot/issues/49268)

\[44] Ahead-of-Time Processing With the JVM[ https://docs.spring.io/spring-boot/4.1-SNAPSHOT/reference/packaging/aot.html](https://docs.spring.io/spring-boot/4.1-SNAPSHOT/reference/packaging/aot.html)

\[45] Ahead of Time Optimizations[ https://docs.spring.io/spring/reference/6.1/core/aot.html](https://docs.spring.io/spring/reference/6.1/core/aot.html)

\[46] Spring Data Ahead of Time Repositories[ https://spring.io/blog/2025/05/22/spring-data-ahead-of-time-repositories/](https://spring.io/blog/2025/05/22/spring-data-ahead-of-time-repositories/)

\[47] Spring Data AOT Repositories: Faster Startup and Build-Time Query Validation[ https://www.danvega.dev/blog/spring-data-aot-repositories](https://www.danvega.dev/blog/spring-data-aot-repositories)

\[48] Spring Data JPA - Reference Documentation[ https://docs.spring.io/spring-data/jpa/docs/1.6.x/reference/htmlsingle/](https://docs.spring.io/spring-data/jpa/docs/1.6.x/reference/htmlsingle/)

\[49] Mastering the Name Game: JPA Naming Conventions in Spring Boot - Hyper Leap[ https://www.hyper-leap.com/2024/07/09/mastering-the-name-game-jpa-naming-conventions-in-spring-boot/](https://www.hyper-leap.com/2024/07/09/mastering-the-name-game-jpa-naming-conventions-in-spring-boot/)

\[50] Spring Data JPA Tutorial[ https://mangohost.net/blog/spring-data-jpa-tutorial/](https://mangohost.net/blog/spring-data-jpa-tutorial/)

\[51] Spring Data JPA - Reference Documentation[ https://docs.spring.io/spring-data/data-jpa/docs/1.0.3.RELEASE/reference/html/](https://docs.spring.io/spring-data/data-jpa/docs/1.0.3.RELEASE/reference/html/)

\[52] Configuring Spring Data JPA with Spring Boot[ https://thorben-janssen.com/configuring-spring-data-jpa-with-spring-boot/](https://thorben-janssen.com/configuring-spring-data-jpa-with-spring-boot/)

\[53] Spring in Action Fourth Edition[ https://manning-content.s3.amazonaws.com/download/6/7fe668a-6e30-4dea-a255-19e631dc16d4/SpringiA4\_CH12.pdf](https://manning-content.s3.amazonaws.com/download/6/7fe668a-6e30-4dea-a255-19e631dc16d4/SpringiA4_CH12.pdf)

\[54] Spring Data JPA方法命名规则的完整总结，按场景分类整理-CSDN博客[ https://blog.csdn.net/zp357252539/article/details/146810482](https://blog.csdn.net/zp357252539/article/details/146810482)

\[55] JPA 查询方法 (JPA Query Methods) | Spring Data JPA3.4.0-SNAPSHOT中文文档|Spring官方文档|SpringBoot 教程|Spring中文网[ https://www.spring-doc.cn/spring-data-jpa/3.4.0-SNAPSHOT/jpa\_query-methods.html](https://www.spring-doc.cn/spring-data-jpa/3.4.0-SNAPSHOT/jpa_query-methods.html)

\[56] Java 面试 必 问 之 Spring Boot 的 约定 优于 配置 ， 你 的 理解 是 什么 ？ # 计算机 # 编程 # java # 求职 # 面试[ https://www.iesdouyin.com/share/video/7545797230339378495/?region=\&mid=7545797254318164775\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=Rbkck971ZywvODJA0mmB86r6vYWCq6GBDb8Ek1QT10g-\&share\_version=280700\&ts=1773041463\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7545797230339378495/?region=\&mid=7545797254318164775\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=Rbkck971ZywvODJA0mmB86r6vYWCq6GBDb8Ek1QT10g-\&share_version=280700\&ts=1773041463\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[57] SpringData专题(一)-JPA入门\_博观而约取，厚积而薄发的技术博客\_mob64ca140beea5的技术博客\_51CTO博客[ https://blog.51cto.com/u\_16213665/14499073](https://blog.51cto.com/u_16213665/14499073)

\[58] Spring Data JPA 自定义查询方法命名规范详解-java教程-PHP中文网[ https://m.php.cn/faq/2145690.html](https://m.php.cn/faq/2145690.html)

\[59] spring-data-jpa命名规则\_springdatajpa方法命名-CSDN博客[ https://blog.csdn.net/shencong520/article/details/147112609](https://blog.csdn.net/shencong520/article/details/147112609)

\[60] JPA Query Methods[ https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html?trk=public\_post\_comment-text](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html?trk=public_post_comment-text)

\[61] Spring Data JPA方法名命名规则\_spring data jpa命名规范-CSDN博客[ https://blog.csdn.net/zzhongcy/article/details/134372292](https://blog.csdn.net/zzhongcy/article/details/134372292)

\[62] 自定义查询方法 通过方法名自动生成查询: Java public interface UserRepository extends JpaRepository { // 精确匹配 List findByName(String name); // 模糊查询 List findByEmailContaining(String keyword); // 组合条件 List findByNameAndEmail(String name, String email); }\_Spring JPA自动生成查询\_ - CSDN文库[ https://wenku.csdn.net/answer/78m54a3238](https://wenku.csdn.net/answer/78m54a3238)

\[63] Defining Query Methods (Defining Query Methods) | Spring Data JPA3.3.4中文文档|Spring官方文档|SpringBoot 教程|Spring中文网[ https://www.spring-doc.cn/spring-data-jpa/3.3.4/repositories\_query-methods-details.en.html](https://www.spring-doc.cn/spring-data-jpa/3.3.4/repositories_query-methods-details.en.html)

\[64] JPA Naming Method Conventions: A Complete Guide for Developers[ https://dev.to/devcorner/jpa-naming-method-conventions-a-complete-guide-for-developers-4p5](https://dev.to/devcorner/jpa-naming-method-conventions-a-complete-guide-for-developers-4p5)

\[65] Spring Data JPA Magic: How Queries Are Generated from Method Names[ https://www.javaguides.net/2025/05/spring-data-jpa-magic-how-queries-are-generated-from-method-names.html?amp=1\&m=1](https://www.javaguides.net/2025/05/spring-data-jpa-magic-how-queries-are-generated-from-method-names.html?amp=1\&m=1)

\[66] Spring Data JPA Method Naming Conventions for Query[ https://www.java4coding.com/contents/spring/springdatajpa/spring-data-jpa-method-naming-conventions](https://www.java4coding.com/contents/spring/springdatajpa/spring-data-jpa-method-naming-conventions)

\[67] working-with-spring-boot-notes/spring-data-jpa-core-concepts.md at master · bradfordja/working-with-spring-boot-notes · GitHub[ https://github.com/bradfordja/working-with-spring-boot-notes/blob/master/spring-data-jpa-core-concepts.md](https://github.com/bradfordja/working-with-spring-boot-notes/blob/master/spring-data-jpa-core-concepts.md)

\[68] Best Integration Practices for Using Spring Data JPA with Hibernate[ https://moldstud.com/articles/p-best-integration-practices-for-using-spring-data-jpa-with-hibernate](https://moldstud.com/articles/p-best-integration-practices-for-using-spring-data-jpa-with-hibernate)

\[69] Mastering Spring Boot Starter Data JPA - Effective Hibernate Integration Explained[ https://moldstud.com/articles/p-mastering-spring-boot-starter-data-jpa-effective-hibernate-integration-explained](https://moldstud.com/articles/p-mastering-spring-boot-starter-data-jpa-effective-hibernate-integration-explained)

\[70] 10 Spring Data JPA Best Practices[ https://climbtheladder.com/10-spring-data-jpa-best-practices/](https://climbtheladder.com/10-spring-data-jpa-best-practices/)

\[71] Best Practice Using Spring Data JPA[ https://bytegoblin.io/blog/best-practice-using-spring-data-jpa.mdx](https://bytegoblin.io/blog/best-practice-using-spring-data-jpa.mdx)

\[72] 2. JPA Repositories[ https://docs.spring.io/spring-data/jpa/docs/1.5.x/reference/html/jpa.repositories.html](https://docs.spring.io/spring-data/jpa/docs/1.5.x/reference/html/jpa.repositories.html)

\[73] Best Practice using Spring Data JPA[ https://readmedium.com/best-practice-using-spring-data-jpa-883c95472274](https://readmedium.com/best-practice-using-spring-data-jpa-883c95472274)

\[74] Spring Data JPA原理与实战 Repository接口的魔法揭秘-CSDN博客[ https://blog.csdn.net/sinat\_41617212/article/details/156655290](https://blog.csdn.net/sinat_41617212/article/details/156655290)

\[75] SpringBoot 与 JPA 整合全解析:架构优势、应用场景、集成指南与最佳实践\_springboot jpa-CSDN博客[ https://blog.csdn.net/hugejiletuhugejiltu/article/details/149217521](https://blog.csdn.net/hugejiletuhugejiltu/article/details/149217521)

\[76] Spring Data JPA核心查询事务管理与性能优化实践-开发者社区-阿里云[ https://developer.aliyun.com/article/1680174](https://developer.aliyun.com/article/1680174)

\[77] Spring Boot ： 重塑 Java 开发 的 利器 Spring Boot 彻底 改变 了 Java 开发 — — 它 消除 了 XML 配置 的 繁琐 ， 为 开发者 提供 了 一种 快速 、 灵活 的 方式 来 构建 可 投入 生产 环境 的 应用 程序 。&#x20;

&#x20;

&#x20;在 本次 深入 探讨 中 ， 我们 将 详细 介绍 Spring Boot 是 如何 做到 以下 几点 的 ：&#x20;

&#x20;

&#x20;\- 用 自动 配置 取代 冗长 的 配置&#x20;

&#x20;\- 仅 通过 注解 即可 实现 依赖 注入&#x20;

&#x20;\- 无需 额外 设置 就能 启动 嵌入式 Tomcat 服务器&#x20;

&#x20;\- 借助 application . yml 管理 配置&#x20;

&#x20;\- 与 服务 发现 、 API 网关 、 配置 服务器 和 断路器 等 组件 无缝 协作 ， 轻松 适配 现代 微 服务 架构&#x20;

&#x20;

&#x20;你 还 将 了解 到 Spring Boot 如何 融入 分层 架构 （ 控制器 → 服务 → 仓库 ） 、 JPA 如何 将 模型 映射 到 数据库 ， 以及 如何 轻松 构建 可 扩展 的 系统 。&#x20;

&#x20;

&#x20;\# Java # Spring # Spring Boot[ https://www.iesdouyin.com/share/video/7527946452098682121/?region=\&mid=7527946677676739371\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=AxXkDljXzDTRJzHNaZYc00m3xfvp\_0NQ\_FcABySU\_AE-\&share\_version=280700\&ts=1773041471\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7527946452098682121/?region=\&mid=7527946677676739371\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=AxXkDljXzDTRJzHNaZYc00m3xfvp_0NQ_FcABySU_AE-\&share_version=280700\&ts=1773041471\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[78] Spring Data JPA实战:企业级ORM框架的完整使用指南-CSDN博客[ https://blog.csdn.net/gitblog\_02779/article/details/144343847](https://blog.csdn.net/gitblog_02779/article/details/144343847)

\[79] Spring Data JPA 入门:从理论到实践的完整指南\_spring jpa 编程指南-CSDN博客[ https://blog.csdn.net/hwh22/article/details/149916652](https://blog.csdn.net/hwh22/article/details/149916652)

\[80] 强大!Spring Boot + JPA 实体类设计五大实战技巧-51CTO.COM[ https://www.51cto.com/article/812489.html](https://www.51cto.com/article/812489.html)

\[81] SpringData JPA事务管理:@Transactional注解与事务传播\_jpa 事务-CSDN博客[ https://blog.csdn.net/weixin\_55344375/article/details/146196819](https://blog.csdn.net/weixin_55344375/article/details/146196819)

\[82] Transactionality[ https://www.spring-doc.cn/spring-data-jpa/3.4.0/jpa\_transactions.en.html](https://www.spring-doc.cn/spring-data-jpa/3.4.0/jpa_transactions.en.html)

\[83] JPA Transactions[ https://www.educative.io/courses/spring-data-bridging-multiple-databases/jpa-transactions](https://www.educative.io/courses/spring-data-bridging-multiple-databases/jpa-transactions)

\[84] What should you be aware of when using Spring for database operations? - Tencent Cloud[ https://www.tencentcloud.com/techpedia/135226](https://www.tencentcloud.com/techpedia/135226)

\[85] How to Use Transactional Annotation Like a Pro[ https://bytegoblin.io/blog/how-to-use-transactional-annotation-like-a-pro.mdx](https://bytegoblin.io/blog/how-to-use-transactional-annotation-like-a-pro.mdx)

\[86] 5.5 Applying best practices to the middle tier[ https://docs.spring.io/s2-dmserver/2.0.x/getting-started/html/ch05s05.html](https://docs.spring.io/s2-dmserver/2.0.x/getting-started/html/ch05s05.html)

\[87] Chapter 9. Transaction management[ https://docs.spring.io/spring-framework/docs/2.5.5/reference/transaction.html](https://docs.spring.io/spring-framework/docs/2.5.5/reference/transaction.html)

\[88] SpringData JPA事务管理:@Transactional注解与事务传播\_jpa 事务-CSDN博客[ https://blog.csdn.net/weixin\_55344375/article/details/146196819](https://blog.csdn.net/weixin_55344375/article/details/146196819)

\[89] How to Use Transactional Annotation Like a Pro[ https://bytegoblin.io/blog/how-to-use-transactional-annotation-like-a-pro.mdx](https://bytegoblin.io/blog/how-to-use-transactional-annotation-like-a-pro.mdx)

\[90] Spring Boot Guidelines[ https://github.com/JetBrains/junie-guidelines/blob/main/guidelines/java/spring-boot/guidelines.md](https://github.com/JetBrains/junie-guidelines/blob/main/guidelines/java/spring-boot/guidelines.md)

\[91] Transactional Annotation in Spring Boot | Coding Shuttle[ https://www.codingshuttle.com/spring-boot-handbook/transactional-annotation-in-spring-boot/](https://www.codingshuttle.com/spring-boot-handbook/transactional-annotation-in-spring-boot/)

\[92] Transaction Propagation :: Spring Framework[ https://docs.spring.io/spring/reference/6.1/data-access/transaction/declarative/tx-propagation.html](https://docs.spring.io/spring/reference/6.1/data-access/transaction/declarative/tx-propagation.html)

\[93] Tips to Understand the Difference Between Propagation.REQUIRED and REQUIRES\_NEW[ https://bytegoblin.io/blog/tips-to-understand-the-difference-between-propagation-required-and-requires-new.mdx](https://bytegoblin.io/blog/tips-to-understand-the-difference-between-propagation-required-and-requires-new.mdx)

\[94] 详解传播行为与隔离级别的组合陷阱与最佳实践-CSDN博客[ https://blog.csdn.net/m0\_73735578/article/details/149181638](https://blog.csdn.net/m0_73735578/article/details/149181638)

\[95] Spring事务隔离级别解析与应用场景推荐[ https://www.iesdouyin.com/share/video/7534636644725443878/?region=\&mid=7534636754507549483\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=eo.ajN1PjeyNC.dLnrsmjlHrllhH.AeDCJX1VgOl2iI-\&share\_version=280700\&ts=1773041500\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7534636644725443878/?region=\&mid=7534636754507549483\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=eo.ajN1PjeyNC.dLnrsmjlHrllhH.AeDCJX1VgOl2iI-\&share_version=280700\&ts=1773041500\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[96] Spring Data JPA 从入门到精通\~事务的处理及其讲解\_jpa事务-CSDN博客[ https://blog.csdn.net/listeningsea/article/details/122418385](https://blog.csdn.net/listeningsea/article/details/122418385)

\[97] 《深入理解spring》事务管理——数据一致性的守护者[ https://developer.aliyun.com/article/1685441](https://developer.aliyun.com/article/1685441)

\[98] spring事务解析:传播行为、隔离级别、声明式与编程式实战指南[ https://blog.csdn.net/2301\_76740633/article/details/148098546](https://blog.csdn.net/2301_76740633/article/details/148098546)

\[99] Transactionality[ https://www.spring-doc.cn/spring-data-jpa/3.4.0/jpa\_transactions.en.html](https://www.spring-doc.cn/spring-data-jpa/3.4.0/jpa_transactions.en.html)

\[100] Spring Data JPA: Avoiding the N+1 Query Problem with EntityGraph[ https://runebook.dev/en/docs/spring\_boot/documentation/documentation.data](https://runebook.dev/en/docs/spring_boot/documentation/documentation.data)

\[101] JPA 트랜잭션 성능 최적화 사이드에 적용해보기[ https://velog.io/@sleekydevzero86/jpa-transaction-performance-optimization](https://velog.io/@sleekydevzero86/jpa-transaction-performance-optimization)

\[102] What’s the impact of @Transactional(readOnly = true) in a method?[ https://learnitweb.com/spring-boot/whats-the-impact-of-transactionalreadonly-true-in-a-method/](https://learnitweb.com/spring-boot/whats-the-impact-of-transactionalreadonly-true-in-a-method/)

\[103] 🍃 Using @Transactional(readOnly = true) in Spring applications is highly beneficial[ https://blog.vvauban.com/blog/using-transactional-readonly-true-in-spring-applications-is-highly](https://blog.vvauban.com/blog/using-transactional-readonly-true-in-spring-applications-is-highly)

\[104] SpringBoot与SpringData JPA整合实践指南-CSDN博客[ https://blog.csdn.net/weixin\_32999557/article/details/149932131](https://blog.csdn.net/weixin_32999557/article/details/149932131)

\[105] Data Modeling using JPA[ https://www.inflearn.com/en/course/jpa%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%AA%A8%EB%8D%B8%EB%A7%81?cid=340988](https://www.inflearn.com/en/course/jpa%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%AA%A8%EB%8D%B8%EB%A7%81?cid=340988)

\[106] 2. JPA Repositories[ https://docs.spring.io/spring-data/jpa/docs/1.5.x/reference/html/jpa.repositories.html](https://docs.spring.io/spring-data/jpa/docs/1.5.x/reference/html/jpa.repositories.html)

\[107] Spring Data JPA Tutorial[ https://mangohost.net/blog/spring-data-jpa-tutorial/](https://mangohost.net/blog/spring-data-jpa-tutorial/)

\[108] Understanding Spring Boot Repository Pattern[ https://toxigon.com/spring-boot-repository-pattern](https://toxigon.com/spring-boot-repository-pattern)

\[109] Using Spring Data JPA for Optimized Database Access in Java Microservices[ https://www.springfuse.com/spring-data-jpa-for-database-access/](https://www.springfuse.com/spring-data-jpa-for-database-access/)

\[110] Spring Data Composable Repositories[ https://www.baeldung.com/spring-data-composable-repositories?trk=article-ssr-frontend-pulse\_little-text-block](https://www.baeldung.com/spring-data-composable-repositories?trk=article-ssr-frontend-pulse_little-text-block)

\[111] 【企业级应用必备技能】:Spring Data JPA中Specification构建灵活查询的5种模式-CSDN博客[ https://blog.csdn.net/Algorhythm/article/details/154135060](https://blog.csdn.net/Algorhythm/article/details/154135060)

\[112] SpringData专题(一)-JPA入门\_博观而约取，厚积而薄发的技术博客\_mob64ca140beea5的技术博客\_51CTO博客[ https://blog.51cto.com/u\_16213665/14499073](https://blog.51cto.com/u_16213665/14499073)

\[113] (一)从“手写SQL秃头”到“JPA一键CRUD”——JPA实战进阶指南-CSDN博客[ https://blog.csdn.net/qq\_43116655/article/details/158648953](https://blog.csdn.net/qq_43116655/article/details/158648953)

\[114] 解析Spring框架核心设计模式的应用与源码实现[ https://www.iesdouyin.com/share/video/7579857815213446266/?region=\&mid=7579857872026733338\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=a5v8lY19Pmzf0bh5DJ31fFw9eYSP7QYohE.AtVEx29Y-\&share\_version=280700\&ts=1773041508\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7579857815213446266/?region=\&mid=7579857872026733338\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=a5v8lY19Pmzf0bh5DJ31fFw9eYSP7QYohE.AtVEx29Y-\&share_version=280700\&ts=1773041508\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[115] 为什么大厂都用Specification处理复杂查询?Spring Data JPA专家告诉你真相-CSDN博客[ https://blog.csdn.net/StepLens/article/details/154135492](https://blog.csdn.net/StepLens/article/details/154135492)

\[116] 如何用Specification优雅实现动态查询?Spring Data JPA高手都在用的4个技巧-CSDN博客[ https://blog.csdn.net/FuncLens/article/details/154135313](https://blog.csdn.net/FuncLens/article/details/154135313)

\[117] 揭秘Spring Data JPA复杂查询难题:如何用Specification实现高性能多条件筛选-CSDN博客[ https://blog.csdn.net/codeink/article/details/154134761](https://blog.csdn.net/codeink/article/details/154134761)

\[118] Specifications[ https://www.spring-doc.cn/spring-data-jpa/3.4.0/jpa\_specifications.en.html](https://www.spring-doc.cn/spring-data-jpa/3.4.0/jpa_specifications.en.html)

\[119] Java - Spring Data Specification with JPA subquery[ https://www.homedutech.com/program-example/java--spring-data-specification-with-jpa-subquery.html](https://www.homedutech.com/program-example/java--spring-data-specification-with-jpa-subquery.html)

\[120] spring data jpa 动态查询Specification(包括各个In、like、Between等等各种工具类，及完整(分页查询)用法步骤(到返回给前端的结果))\_jpa specification in-CSDN博客[ https://blog.csdn.net/qq\_32786139/article/details/86473544](https://blog.csdn.net/qq_32786139/article/details/86473544)

\[121] ORM框架之Spring Data JPA(三)高级查询---复杂查询\_springdata jpa 高级查询-CSDN博客[ https://blog.csdn.net/u011174699/article/details/102257246](https://blog.csdn.net/u011174699/article/details/102257246)

\[122] Advanced Spring Data JPA - Specifications and Querydsl[ https://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl](https://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl)

\[123] spring data jpa Specification 复杂查询+分页查询[ https://www.cnblogs.com/hankuikui/p/11414316.html](https://www.cnblogs.com/hankuikui/p/11414316.html)

\[124] How do you handle bulk operations in Spring Data JPA?[ https://www.bestdivision.com/questions/how-do-you-handle-bulk-operations-in-spring-data-jpa](https://www.bestdivision.com/questions/how-do-you-handle-bulk-operations-in-spring-data-jpa)

\[125] Implementing Bulk Updates with Spring Data JPA[ https://thorben-janssen.com/implementing-bulk-updates-with-spring-data-jpa/](https://thorben-janssen.com/implementing-bulk-updates-with-spring-data-jpa/)

\[126] Spring Data JPA Batch Inserts[ https://www.baeldung.com/spring-data-jpa-batch-inserts](https://www.baeldung.com/spring-data-jpa-batch-inserts)

\[127] Best Practice Using Spring Data JPA[ https://bytegoblin.io/blog/best-practice-using-spring-data-jpa.mdx](https://bytegoblin.io/blog/best-practice-using-spring-data-jpa.mdx)

\[128] Spring Data JPA — Batching using Streams[ https://bytegoblin.io/blog/spring-data-jpa-batching-using-streams.mdx](https://bytegoblin.io/blog/spring-data-jpa-batching-using-streams.mdx)

\[129] Spring Boot JPA Batch Insert: How to Skip Select?[ https://devsolus.com/spring-boot-jpa-batch-insert-how-to-skip-select/](https://devsolus.com/spring-boot-jpa-batch-insert-how-to-skip-select/)

\[130] Spring Data JPA批量处理性能优化\_jpa 重写saveall-CSDN博客[ https://blog.csdn.net/qq\_33556185/article/details/121482679](https://blog.csdn.net/qq_33556185/article/details/121482679)

\[131] Spring Boot线程池优化百万数据插入效率解析[ https://www.iesdouyin.com/share/video/7481552411358809398/?region=\&mid=7481553054229924634\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=BezjGTpGpmRuLaBeNlxZhfQymOHpAwppHmPRvvpVsIs-\&share\_version=280700\&ts=1773041526\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7481552411358809398/?region=\&mid=7481553054229924634\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=BezjGTpGpmRuLaBeNlxZhfQymOHpAwppHmPRvvpVsIs-\&share_version=280700\&ts=1773041526\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[132] SpringBoot实战:5个高效开发技巧让你的项目性能提升200%-CSDN博客[ https://blog.csdn.net/2501\_90631445/article/details/150856246](https://blog.csdn.net/2501_90631445/article/details/150856246)

\[133] 【Spring】优化Spring Boot JPA API 处理大数据集缓慢的方案\_PlutokitCoder社区[ http://m.toutiao.com/group/7585334657979056703/?upstream\_biz=doubao](http://m.toutiao.com/group/7585334657979056703/?upstream_biz=doubao)

\[134] JPA 删除大数据量时的内存优化策略-java教程-PHP中文网[ https://m.php.cn/faq/2150252.html](https://m.php.cn/faq/2150252.html)

\[135] Optimizing Database Interactions in Large Scale Applications with Spring Data[ https://moldstud.com/articles/p-optimizing-database-interactions-in-large-scale-applications-with-spring-data](https://moldstud.com/articles/p-optimizing-database-interactions-in-large-scale-applications-with-spring-data)

\[136] saveALl和for循环save的区别 - CSDN文库[ https://wenku.csdn.net/answer/2tz8fqd47k](https://wenku.csdn.net/answer/2tz8fqd47k)

\[137] Spring Boot中JPA/Hibernate批量插入性能差如何优化?\_编程语言-CSDN问答[ https://ask.csdn.net/questions/9252443](https://ask.csdn.net/questions/9252443)

\[138] Spring Boot线程池优化百万数据插入效率[ https://www.iesdouyin.com/share/video/7479797964123163958/?region=\&mid=7479799569870523186\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&share\_sign=iTMcEWrrBalbEtTaZLza5U8RaX47lof6PVT13drUNJY-\&share\_version=280700\&ts=1773041526\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/video/7479797964123163958/?region=\&mid=7479799569870523186\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&share_sign=iTMcEWrrBalbEtTaZLza5U8RaX47lof6PVT13drUNJY-\&share_version=280700\&ts=1773041526\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[139] MyBatis 与 Spring Data JPA 核心对比:选型指南与最佳实践概述 在 Java 持久层框架中，MyB - 掘金[ https://juejin.cn/post/7583598943072026659](https://juejin.cn/post/7583598943072026659)

\[140] spring-data-jpa saveall慢的原因\_spring jpa 批量保存数据慢-CSDN博客[ https://blog.csdn.net/fenglllle/article/details/156869720](https://blog.csdn.net/fenglllle/article/details/156869720)

\[141] 🚀 数据库插入 1000 万数据?别再傻傻用 for 循环了!实测 5 种方式效率对比在日常的后端开发中，我们经常会遇 - 掘金[ https://juejin.cn/post/7592451588849057811](https://juejin.cn/post/7592451588849057811)

\[142] Querydsl - Unified Queries for Java[ http://querydsl.com/](http://querydsl.com/)

\[143] Specifications[ https://www.spring-doc.cn/spring-data-jpa/3.4.0/jpa\_specifications.en.html](https://www.spring-doc.cn/spring-data-jpa/3.4.0/jpa_specifications.en.html)

\[144] 超越MyBatis!Spring Boot + JPA动态查询终极方案:Specification与QueryDSL全对比\_jpa specification-CSDN博客[ https://fmill.blog.csdn.net/article/details/154738096](https://fmill.blog.csdn.net/article/details/154738096)

\[145] Mastering Spring Data JPA Specifications for Robust Data Access[ https://readmedium.com/mastering-spring-data-jpa-specifications-for-robust-data-access-24e7626d169a](https://readmedium.com/mastering-spring-data-jpa-specifications-for-robust-data-access-24e7626d169a)

\[146] \[실전! 스프링 데이터 JPA] 다양한 쿼리 작성 도구 (Specifications, Query By Example, Projections, Native Query 👉 QueryDSL)[ https://velog.io/@peanut\_/%EC%8B%A4%EC%A0%84-%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-JPA-%EB%8F%99%EC%A0%81-%EC%BF%BC%EB%A6%AC-Criteria-Query-By-Example](https://velog.io/@peanut_/%EC%8B%A4%EC%A0%84-%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-JPA-%EB%8F%99%EC%A0%81-%EC%BF%BC%EB%A6%AC-Criteria-Query-By-Example)

\[147] 2.2. Querying JPA[ http://querydsl.com/static/querydsl/2.0.1/reference/html/ch02s02.html](http://querydsl.com/static/querydsl/2.0.1/reference/html/ch02s02.html)

\[148] Spring Data Extensions[ https://docs.spring.io/spring-data/jpa/reference/repositories/core-extensions.html](https://docs.spring.io/spring-data/jpa/reference/repositories/core-extensions.html)

\[149] JPA Query Methods[ https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)

\[150] spring data jpa的自定义查询，修改以及分页\_jpa 自定义sql复杂查询-CSDN博客[ https://blog.csdn.net/qq\_41452805/article/details/102608877](https://blog.csdn.net/qq_41452805/article/details/102608877)

\[151] Spring Boot JPA多对多关系查询注解应用解析[ https://www.iesdouyin.com/share/note/7422457862070324515/?region=\&mid=0\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&schema\_type=37\&share\_sign=A9CyK8fOY3FRsov9dR.8QALn.R9x\_FLeguYjIPiAwo8-\&share\_version=280700\&ts=1773041534\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/note/7422457862070324515/?region=\&mid=0\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&schema_type=37\&share_sign=A9CyK8fOY3FRsov9dR.8QALn.R9x_FLeguYjIPiAwo8-\&share_version=280700\&ts=1773041534\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[152] 【Java】Spring Data JPA 详解:ORM 映射、查询方法与复杂 SQL 处理\_spring jpa-CSDN博客[ https://blog.csdn.net/lbbxmx111/article/details/156400528](https://blog.csdn.net/lbbxmx111/article/details/156400528)

\[153] 12、《Spring Data JPA:极简数据库操作实战》-CSDN博客[ https://blog.csdn.net/ldz\_wolf/article/details/145627738](https://blog.csdn.net/ldz_wolf/article/details/145627738)

\[154] Spring Data JPA 最佳实践【2/2】:存储库设计指南为开发出更好的 Spring Data JPA 存储库 - 掘金[ https://juejin.cn/post/7576669556150091814](https://juejin.cn/post/7576669556150091814)

\[155] Stored Procedures (Stored Procedures) | Spring Data JPA3.2.11-SNAPSHOT中文文档|Spring官方文档|SpringBoot 教程|Spring中文网[ https://www.spring-doc.cn/spring-data-jpa/3.2.11-SNAPSHOT/jpa\_stored-procedures.en.html](https://www.spring-doc.cn/spring-data-jpa/3.2.11-SNAPSHOT/jpa_stored-procedures.en.html)

\[156] Spring Data JPA Best Practices: Repositories Design Guide[ https://dev.to/protsenko/spring-data-jpa-best-practices-repositories-design-guide-2f6j](https://dev.to/protsenko/spring-data-jpa-best-practices-repositories-design-guide-2f6j)

\[157] Calling Stored Procedures with JPA and Spring Data JPA: A Practical Guide – Adam Gamboa G – Developer[ https://blog.adamgamboa.dev/calling-stored-procedures-with-jpa-and-spring-data-jpa-a-practical-guide/](https://blog.adamgamboa.dev/calling-stored-procedures-with-jpa-and-spring-data-jpa-a-practical-guide/)

\[158] Stored Procedures[ https://docs.spring.io/spring-data/data-jpa/reference/3.4/jpa/stored-procedures.html](https://docs.spring.io/spring-data/data-jpa/reference/3.4/jpa/stored-procedures.html)

\[159] Calling Stored Procedures from Spring Data JPA Repositories[ https://www.baeldung.com/spring-data-jpa-stored-procedures](https://www.baeldung.com/spring-data-jpa-stored-procedures)

\[160] next-spring-skills/skills/spring/repository\_design.md at af8aa754967e98eb7527d781448cc66582629e9b · longtq2501/next-spring-skills · GitHub[ https://github.com/longtq2501/next-spring-skills/blob/af8aa754967e98eb7527d781448cc66582629e9b/skills/spring/repository\_design.md](https://github.com/longtq2501/next-spring-skills/blob/af8aa754967e98eb7527d781448cc66582629e9b/skills/spring/repository_design.md)

\[161] JPA测试用例详解:Spring Data JPA单元测试与集成测试最佳实践 - CSDN文库[ https://wenku.csdn.net/doc/2ww5q3iryt](https://wenku.csdn.net/doc/2ww5q3iryt)

\[162] Lesson 7: Testing Spring Data Repositories[ https://www.baeldung.com/members/courses/learn-spring-data-jpa/lessons/lesson-7-testing-spring-data-repositories](https://www.baeldung.com/members/courses/learn-spring-data-jpa/lessons/lesson-7-testing-spring-data-repositories)

\[163] 41. Testing[ https://docs.spring.io/spring-boot/docs/1.5.3.RELEASE/reference/html/boot-features-testing.html](https://docs.spring.io/spring-boot/docs/1.5.3.RELEASE/reference/html/boot-features-testing.html)

\[164] Effective Integration Testing Strategies for Spring and Hibernate - A Comprehensive Guide[ https://moldstud.com/articles/p-effective-integration-testing-strategies-for-spring-and-hibernate-a-comprehensive-guide](https://moldstud.com/articles/p-effective-integration-testing-strategies-for-spring-and-hibernate-a-comprehensive-guide)

\[165] Spring Data JPA Unit Test: Repository Layer[ https://readmedium.com/spring-data-jpa-unit-test-repository-layer-9e875390645e](https://readmedium.com/spring-data-jpa-unit-test-repository-layer-9e875390645e)

\[166] Spring Boot & Spring Data JPA Code Review Checklist[ https://dev.to/kavitha\_pazhanee\_034b29ef/spring-boot-spring-data-jpa-code-review-checklist-595m](https://dev.to/kavitha_pazhanee_034b29ef/spring-boot-spring-data-jpa-code-review-checklist-595m)

\[167] 是否需要为纯 Spring Data JPA Repository 编写测试?-java教程-PHP中文网[ https://m.php.cn/faq/2081405.html](https://m.php.cn/faq/2081405.html)

\[168] SpringData——JPA的集成测试和单元测试\_spring data jpa 单元测试-CSDN博客[ https://blog.csdn.net/2401\_82884096/article/details/138162391](https://blog.csdn.net/2401_82884096/article/details/138162391)

\[169] 日本IT项目中结合测试的策略与技术实现解析[ https://www.iesdouyin.com/share/note/7545794088478461218/?region=\&mid=0\&u\_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with\_sec\_did=1\&video\_share\_track\_ver=\&titleType=title\&schema\_type=37\&share\_sign=\_cUnAXv2ydnEyPMa0QVwAqdpjjsBT14o6SJ7ZpRaEGs-\&share\_version=280700\&ts=1773041555\&from\_aid=1128\&from\_ssr=1\&share\_track\_info=%7B%22link\_description\_type%22%3A%22%22%7D](https://www.iesdouyin.com/share/note/7545794088478461218/?region=\&mid=0\&u_code=0\&did=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ\&with_sec_did=1\&video_share_track_ver=\&titleType=title\&schema_type=37\&share_sign=_cUnAXv2ydnEyPMa0QVwAqdpjjsBT14o6SJ7ZpRaEGs-\&share_version=280700\&ts=1773041555\&from_aid=1128\&from_ssr=1\&share_track_info=%7B%22link_description_type%22%3A%22%22%7D)

\[170] 《深入理解Spring》单元测试——高质量代码的守护神-阿里云开发者社区[ https://developer.aliyun.com/article/1685443](https://developer.aliyun.com/article/1685443)

\[171] Spring Boot 测试:单元、集成与契约测试全解析-CSDN博客[ https://blog.csdn.net/weixin\_46619605/article/details/145928709](https://blog.csdn.net/weixin_46619605/article/details/145928709)

\[172] 攻克数据持久层测试难关:JUnit4与Spring Data JPA集成实战指南-CSDN博客[ https://blog.csdn.net/gitblog\_00462/article/details/151672273](https://blog.csdn.net/gitblog_00462/article/details/151672273)

\[173] Spring Boot 测试 Service 层:@DataJpaTest 与内存数据库 H2\_鱼弦CTO的技术博客\_51CTO博客[ https://blog.51cto.com/chenfenglove/14494689](https://blog.51cto.com/chenfenglove/14494689)

\[174] Spring Boot @DataJpaTest Annotation | Unit Testing Spring Data JPA Repositories[ https://www.javaguides.net/2025/03/spring-boot-datajpatest-annotation.html?m=1](https://www.javaguides.net/2025/03/spring-boot-datajpatest-annotation.html?m=1)

\[175] Annotation Interface DataJpaTest[ https://docs.spring.io/spring-boot/api/java/org/springframework/boot/data/jpa/test/autoconfigure/DataJpaTest.html](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/data/jpa/test/autoconfigure/DataJpaTest.html)

\[176] Spring Boot Data JPA Repository Unit Test[ https://www.netjstech.com/2025/09/spring-boot-data-jpa-repository-unit-test.html?m=1](https://www.netjstech.com/2025/09/spring-boot-data-jpa-repository-unit-test.html?m=1)

\[177] @DataJpaTest Spring Boot Example[ https://www.javaguides.net/2022/03/datajpatest-spring-boot-example.html?m=1](https://www.javaguides.net/2022/03/datajpatest-spring-boot-example.html?m=1)

\[178] Common Issues and Solutions for Spring Boot Data JPA Tests[ https://runebook.dev/en/docs/spring\_boot/features/features.testing.spring-boot-applications.autoconfigured-spring-data-jpa](https://runebook.dev/en/docs/spring_boot/features/features.testing.spring-boot-applications.autoconfigured-spring-data-jpa)

\[179] @DataJpaTest and Repository Class in JUnit[ https://www.baeldung.com/junit-datajpatest-repository](https://www.baeldung.com/junit-datajpatest-repository)

> （注：文档部分内容可能由 AI 生成）