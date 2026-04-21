# LonBon OAuth API 文档

## 一、项目概述

本项目是朗邦科技（LonBon Technologies）的OAuth认证服务系统，主要提供用户账号管理、设备登录认证、Token管理等核心功能。

### 技术栈
- **框架**: Spring Boot + Spring Security OAuth2
- **数据库**: MyBatis-Plus
- **缓存**: Redis
- **加密**: RSA
- **ID生成**: 分布式UID生成器

---

## 二、API接口汇总

本项目共包含 **2个Controller**，**5个有效API接口**：

| 序号 | 接口路径 | 请求方法 | 功能描述 |
|------|----------|----------|----------|
| 1 | `/api/v2.0/accounts` | POST | 账号注册 |
| 2 | `/api/v2.0/accounts/verify` | POST | 校验账号信息 |
| 3 | `/api/v2.0/auth/login` | POST | 账号登录 |
| 4 | `/api/v2.0/auth/refresh` | POST | 刷新Token |
| 5 | `/api/v2.0/auth/account` | POST | 账号Token查询 |

---

## 三、详细接口说明

### 1. 账号注册

**接口路径**: `/api/v2.0/accounts`

**请求方式**: `POST`

**功能描述**: 创建新账号，支持设备账号和普通用户账号的注册。设备类型账号注册时会自动关联角色信息。

#### 请求参数 (Request Body)

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 账号名称，设备类型为设备唯一标识 |
| password | String | 否 | 账号密码（APP类型需要） |
| accountType | Integer | 是 | 账号类型：<br>1000 - 物联设备类型<br>1100 - 对讲机设备类型<br>44 - 馨刻APP |
| orgId | String | 否 | 机构ID |
| deviceObj | Object | 条件必填 | 设备附加信息（设备类型必填） |

**deviceObj对象结构**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| deviceCategory | Integer | 设备类型必填 | 设备类别码，详见设备分类枚举 |

#### 请求示例

```json
{
    "username": "device_mac_001",
    "password": "",
    "accountType": 1000,
    "orgId": "org_123",
    "deviceObj": {
        "deviceCategory": 0
    }
}
```

#### 响应参数 (Response)

| 参数名 | 类型 | 说明 |
|--------|------|------|
| status | String | 状态码：<br>200 - 成功<br>201 - 错误<br>202 - 数据已存在<br>301 - 参数错误<br>303 - 数据不存在 |
| msg | String | 响应消息 |
| body | Object | 响应体，通常为空HashMap |

#### 响应示例

成功响应：
```json
{
    "status": "200",
    "msg": "注册成功",
    "body": {}
}
```

账号已存在响应：
```json
{
    "status": "202",
    "msg": "当前账号已注册",
    "body": {}
}
```

#### 业务逻辑说明

1. **参数校验**：
   - 检查username是否为空
   - 检查accountType是否为空

2. **重复性检查**：
   - 查询数据库验证账号是否已存在
   - 如已存在，返回错误信息

3. **账号创建**：
   - 生成唯一账号ID（使用分布式UID生成器）
   - 记录创建时间和更新时间

4. **设备类型特殊处理**（accountType为1000或1100）：
   - 根据设备唯一标识和设备类别获取设备信息
   - 验证设备是否存在，不存在则报错
   - 自动创建角色信息（Role）
   - 自动创建账号-角色关联（AccountRole）
   - 根据设备类别设置角色名称和角色类型

5. **APP类型**（accountType为44）：
   - 目前实现为空，直接返回失败

---

### 2. 校验账号信息

**接口路径**: `/api/v2.0/accounts/verify`

**请求方式**: `POST`

**功能描述**: 校验账号信息是否存在，根据账号类型和账号值查询账号详情。

#### 请求参数 (Request Body)

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| accountType | Integer | 是 | 账号类型：<br>1000 - 物联设备账号<br>1100 - 对讲机设备<br>44 - APP |
| accountValue | String | 是 | 参数值：<br>用户账号 - 用户accountId<br>设备账号 - 设备账号名 |

#### 请求示例

```json
{
    "accountType": 1000,
    "accountValue": "device_mac_001"
}
```

#### 响应参数 (Response)

| 参数名 | 类型 | 说明 |
|--------|------|------|
| status | String | 状态码：<br>200 - 成功<br>203 - 没有符合条件的数据<br>301 - 参数错误 |
| msg | String | 响应消息 |
| body | Object | 响应体，包含account对象 |

**body.account对象结构**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| accountId | String | 账号唯一标识 |
| username | String | 用户名/设备名 |
| accountType | Integer | 账号类型 |

#### 响应示例

成功响应：
```json
{
    "status": "200",
    "msg": "查询成功",
    "body": {
        "account": {
            "accountId": "123456789",
            "username": "device_mac_001",
            "accountType": 1000
        }
    }
}
```

账号不存在响应：
```json
{
    "status": "303",
    "msg": "账号信息不存在",
    "body": {}
}
```

#### 业务逻辑说明

1. **参数校验**：
   - 检查accountValue是否为空
   - 检查accountType是否为空

2. **查询逻辑**：
   - 设备类型（1000、1100）：使用username字段查询
   - 其他类型：使用accountId字段查询

3. **返回结果**：
   - 账号存在时返回完整的账号信息
   - 账号不存在时返回303状态码

---

### 3. 账号登录

**接口路径**: `/api/v2.0/auth/login`

**请求方式**: `POST`

**功能描述**: 用户/设备登录认证，支持设备Token认证和APP用户认证两种模式。

#### 请求参数 (Request Body)

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userName | String | 是 | 账号信息 |
| password | String | 条件必填 | 密码（APP类型需要） |
| accountType | Integer | 是 | 账号类型：<br>1000 - 设备类型<br>1100 - 对讲机类型<br>44 - 馨刻APP |
| orgId | String | 否 | 机构ID，默认为空字符串 |

#### 请求示例

设备登录：
```json
{
    "userName": "device_mac_001",
    "password": "",
    "accountType": 1000,
    "orgId": "org_123"
}
```

APP登录：
```json
{
    "userName": "user@example.com",
    "password": "encrypted_password",
    "accountType": 44,
    "orgId": ""
}
```

#### 响应参数 (Response)

| 参数名 | 类型 | 说明 |
|--------|------|------|
| status | String | 状态码：<br>200 - 登录成功<br>201 - 登录失败<br>202 - 账号不存在<br>203 - 设备未绑定角色 |
| msg | String | 响应消息 |
| body | Object | 响应体，包含Token信息 |

**body对象结构**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| accessToken | String | 访问令牌（RSA加密） |
| expireTime | Long | 过期时间（秒），设备Token有效期90天 |
| refreshToken | String | 刷新令牌（RSA加密） |

#### 响应示例

成功响应：
```json
{
    "status": "200",
    "msg": "登陆成功",
    "body": {
        "accessToken": "eyJhbGciOiJSU0ExMjM0NTY3NDkwIiwidW5pcXVlIjoie1widGlc3RpZGUiOjE2...",
        "expireTime": 7776000,
        "refreshToken": "eyJhbGciOiJSU0ExMjM0NTY3NDkwIiwidW5pcXVlIjoie1widGZyZXNoX3RpZGUiOjE2..."
    }
}
```

#### 业务逻辑说明

1. **参数校验**：
   - 检查userName是否为空
   - 检查accountType是否为空

2. **账号验证**：
   - 查询数据库验证账号是否存在
   - 账号不存在返回错误

3. **设备类型登录**（accountType为1000）：
   - **清理旧Token**：
     - 删除Redis中的旧Token和刷新Token
     - 删除数据库中的旧Token和刷新Token记录
   - **构建Token信息**（AccountSummary）：
     - 当前时间戳
     - 设备名称
     - 账号类型
     - 账号ID
     - 机构ID
   - **生成TokenId**：使用分布式UID生成器
   - **角色验证**：查询账号是否绑定角色，未绑定返回错误
   - **构建刷新Token信息**（DeviceRefreshTokenInfo）：
     - 机构ID
     - 当前时间戳
     - 设备账号
     - 账号类型
     - 设备类型
   - **存储Token**：
     - 数据库：Token表（有效期90天）和RefreshToken表（有效期5年）
     - Redis：Token（有效期90天）和刷新Token（有效期50年）
   - **Token加密**：使用RSA公钥加密Token和刷新Token
   - **返回结果**：加密后的accessToken、expireTime、refreshToken

4. **APP类型登录**（accountType为44）：
   - 使用Spring Security的AuthenticationManager进行认证
   - 设置SecurityContext
   - 目前实现未完整，仅返回成功

---

### 4. 刷新Token

**接口路径**: `/api/v2.0/auth/refresh`

**请求方式**: `POST`

**功能描述**: 使用刷新令牌获取新的访问令牌，延长会话有效期。

#### 请求参数 (Query Parameter)

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| refreshToken | String | 是 | 刷新令牌（RSA加密） |

#### 请求示例

```
POST /api/v2.0/auth/refresh?refreshToken=eyJhbGciOiJSU0ExMjM0NTY3NDkwIiwidW5pcXVlIjoie1wiZnJlc2hfdGltZSI6MTZ...
```

#### 响应参数 (Response)

| 参数名 | 类型 | 说明 |
|--------|------|------|
| status | String | 状态码：<br>200 - 刷新成功<br>201 - 刷新失败<br>202 - 令牌信息有误<br>203 - 刷新令牌已失效<br>204 - 登陆令牌失效<br>205 - 刷新令牌不存在 |
| msg | String | 响应消息 |
| body | Object | 响应体，包含新Token信息 |

**body对象结构**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| accessToken | String | 新的访问令牌（RSA加密） |
| expireTime | Long | 过期时间（秒） |
| refreshToken | String | 新的刷新令牌（RSA加密） |

#### 响应示例

成功响应：
```json
{
    "status": "200",
    "msg": "刷新成功",
    "body": {
        "accessToken": "eyJhbGciOiJSU0ExMjM0NTY3NDkwIiwidW5pcXVlIjoie1widGlc3RpZGUiOjE2...",
        "expireTime": 7776000,
        "refreshToken": "eyJhbGciOiJSU0ExMjM0NTY3NDkwIiwidW5pcXVlIjoie1wiZnJlc2hfdGltZSI6MTZ..."
    }
}
```

#### 业务逻辑说明

1. **参数校验**：
   - 检查refreshToken是否为空

2. **Token解析**：
   - 使用RSA私钥解密refreshToken
   - 解析JSON为DeviceRefreshTokenInfo对象
   - 解析失败返回错误

3. **设备Token刷新**（accountType为1000）：
   - **提取设备标识**：从解析的信息中获取设备账号
   - **验证Redis中的刷新Token**：
     - 构建刷新Token的Redis Key
     - 检查Key是否存在，不存在说明已失效
   - **查询数据库Token记录**：
     - 根据账号ID查询Token表
     - Token不存在说明登陆令牌失效
   - **查询数据库刷新Token记录**：
     - 根据TokenId查询RefreshToken表
     - 记录不存在返回错误
   - **验证Token一致性**：
     - 比对请求的刷新Token时间戳与数据库中的一致性
     - 不一致说明Token被篡改
   - **更新Token信息**：
     - 更新时间戳
     - 生成新的Token字符串
     - 更新Redis中的Token和刷新Token
     - 更新数据库中的Token和RefreshToken记录
   - **Token加密**：使用RSA公钥加密新Token
   - **返回结果**：加密后的新accessToken、expireTime、新refreshToken

---

### 5. 账号Token查询

**接口路径**: `/api/v2.0/auth/account`

**请求方式**: `POST`

**功能描述**: 查询当前账号的Token信息（预留接口）。

#### 请求参数

无

#### 响应参数 (Response)

| 参数名 | 类型 | 说明 |
|--------|------|------|
| status | String | 状态码：200 - 成功 |
| msg | String | 响应消息 |
| body | Object | 响应体 |

#### 响应示例

```json
{
    "status": "200",
    "msg": "成功",
    "body": {}
}
```

#### 业务逻辑说明

该接口为预留接口，当前仅返回成功状态。

---

## 四、响应状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 201 | 操作失败 |
| 202 | 数据已存在/参数错误 |
| 203 | 没有符合条件的数据 |
| 204 | 设备已在其他服务器绑定 |
| 205 | 设备已在其他项目绑定 |
| 300 | 数据重复 |
| 301 | 参数不全 |
| 302 | 数据不合法 |
| 303 | 数据不存在 |
| 401 | 未登录 |
| 402 | 没有权限/登录超时 |

---

## 五、设备分类枚举

| deviceCategory | 描述 | roleType |
|----------------|------|----------|
| -2 | 全部 | 0 |
| -1 | 未知设备 | 0 |
| 0 | 监护手表 | 1000 |
| 1 | 按钮 | 1001 |
| 2 | 输液报警器 | 1002 |
| 3 | 主控盒 | 1003 |
| 4 | 求救定位器 | 1004 |
| 5 | 报警手环 | 1005 |
| 6 | 定位信标 | 1006 |
| 7 | 生命探测器、生命探测器(通话版) | 1101 |
| 8 | 无线对讲分机（NB-A3）、4G床位分机（NB-A2） | 1102 |
| 9 | 护工手表(NB-R3) 员工工牌(NB-R2) | 1007 |
| 10 | 水浸探测器 | 1008 |
| 11 | 跌倒报警器 | 1009 |
| 12 | 交互终端 | 1010 |
| 13 | 门灯 | 1011 |
| 14 | 温湿度传感器 | 1012 |
| 15 | IP床位分机 | 1013 |
| 16 | 分机 | 1014 |
| 17 | 无线手持 | 1015 |
| 256 | 血压计 | 1016 |
| 257 | 燃气探测器 | 1017 |
| 258 | 烟雾探测器 | 1018 |
| 259 | 智能睡眠监测设备 | 1019 |
| 260 | 智能拐杖 | 1020 |
| 261 | 智能门磁 | 1021 |
| 262 | 防走失主机 | 1022 |
| 263 | 防走失标签 | 1023 |
| 264 | 血糖仪 | 1024 |
| 265 | 极爱手表 | 1025 |
| 266 | 爱牵挂手环 | 1026 |
| 267 | 外购水浸探测器 | 1027 |
| 268 | 卓比求救定位器 | 1028 |
| 269 | 爱牵挂无线对讲分机 | 1103 |
| 270 | 药盒 | 1029 |
| 271 | 小度智能屏 | 1030 |
| 272 | 智能床 | 1031 |
| 273 | 血氧仪 | 1032 |
| 274 | 体温计 | 1033 |
| 275 | 血压手表 | 1034 |
| 276 | 鱼跃血压计 | 1035 |
| 277 | WTD | 1036 |

---

## 六、数据库实体说明

### Account（账号表）
- accountId: 账号唯一标识
- username: 用户名/设备名
- accountType: 账号类型
- createTime: 创建时间
- updateTime: 更新时间
- isDelete: 删除标记

### Token（令牌表）
- tokenId: 令牌唯一标识
- token: 令牌内容
- accountId: 账号ID
- orgId: 机构ID
- createTime: 创建时间
- liveTime: 存活时间（秒）

### RefreshToken（刷新令牌表）
- refreshTokenId: 刷新令牌唯一标识
- tokenId: 关联的Token ID
- refreshToken: 刷新令牌内容
- expireTime: 过期时间
- createTime: 创建时间
- updateTime: 更新时间
- orgId: 机构ID

### Role（角色表）
- roleId: 角色唯一标识
- roleName: 角色名称
- roleType: 角色类型
- orgId: 机构ID
- isSuper: 是否超级角色
- createTime: 创建时间
- updateTime: 更新时间

### AccountRole（账号角色关联表）
- accountRoleId: 关联ID
- accountId: 账号ID
- roleId: 角色ID
- createTime: 创建时间
- updateTime: 更新时间

---

## 七、安全说明

1. **Token加密**：所有Token使用RSA公钥加密传输
2. **Token有效期**：
   - AccessToken：90天
   - RefreshToken：50年
3. **密码传输**：建议使用RSA加密传输密码
4. **Redis缓存**：Token信息同时存储在Redis和数据库中，支持快速验证

---

*文档生成时间：2026-04-21*
