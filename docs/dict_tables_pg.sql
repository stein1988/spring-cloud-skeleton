-- =============================================================
-- SaaS 低代码平台 —— 字典模块建表语句（PostgreSQL）
-- 规范：
--   1. 主键：UUID，默认值 gen_random_uuid()（PG 13+内置）
--   2. 逻辑删除：is_deleted SMALLINT + deleted_at TIMESTAMPTZ
--   3. 乐观锁：version INTEGER，每次更新 +1
--   4. 审计字段：created_by / created_at / updated_by / updated_at
--   5. 多租户：tenant_id = '00000000-0000-0000-0000-000000000000' 表示平台级内置数据
--   6. 时区：所有时间字段使用 TIMESTAMPTZ（带时区）
-- =============================================================

-- 启用 pgcrypto（若 PG 版本 < 13 不支持 gen_random_uuid() 时使用）
-- CREATE EXTENSION IF NOT EXISTS pgcrypto;
-- 并将所有 gen_random_uuid() 替换为 gen_random_uuid()（pgcrypto提供同名函数，无需改动）


-- =============================================================
-- 0. 租户表
-- =============================================================
CREATE TABLE tenant (
  id           UUID          NOT NULL DEFAULT gen_random_uuid() ,
  tenant_code  VARCHAR(64)   NOT NULL,
  tenant_name  VARCHAR(128)  NOT NULL,
  status       SMALLINT      NOT NULL DEFAULT 1,
  remark       VARCHAR(512)           DEFAULT NULL,
  created_by   UUID                   DEFAULT NULL,
  created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_by   UUID                   DEFAULT NULL,
  updated_at   TIMESTAMPTZ            DEFAULT NULL,
  version      INTEGER       NOT NULL DEFAULT 0,
  is_deleted   SMALLINT      NOT NULL DEFAULT 0,
  deleted_at   TIMESTAMPTZ            DEFAULT NULL,
  CONSTRAINT pk_tenant             PRIMARY KEY (id),
  CONSTRAINT uk_tenant_code        UNIQUE (tenant_code, is_deleted),
  CONSTRAINT ck_tenant_status      CHECK (status IN (0, 1)),
  CONSTRAINT ck_tenant_is_deleted  CHECK (is_deleted IN (0, 1))
);

COMMENT ON TABLE  tenant              IS '租户表';
COMMENT ON COLUMN tenant.id          IS '主键 UUID';
COMMENT ON COLUMN tenant.tenant_code IS '租户编码，全局唯一';
COMMENT ON COLUMN tenant.tenant_name IS '租户名称';
COMMENT ON COLUMN tenant.status      IS '状态：1=启用，0=禁用';
COMMENT ON COLUMN tenant.created_by  IS '创建人ID';
COMMENT ON COLUMN tenant.created_at  IS '创建时间';
COMMENT ON COLUMN tenant.updated_by  IS '最后修改人ID';
COMMENT ON COLUMN tenant.updated_at  IS '最后修改时间';
COMMENT ON COLUMN tenant.version     IS '乐观锁版本号';
COMMENT ON COLUMN tenant.is_deleted  IS '逻辑删除标志：0=未删除，1=已删除';
COMMENT ON COLUMN tenant.deleted_at  IS '删除时间';


-- =============================================================
-- 1. 字典分组表（支持多级树形，物化路径）
-- =============================================================
CREATE TABLE dict_group (
  id           UUID          NOT NULL DEFAULT gen_random_uuid(),
  tenant_id    UUID          NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
  parent_id    UUID                   DEFAULT NULL,
  group_code   VARCHAR(64)   NOT NULL,
  group_name   VARCHAR(128)  NOT NULL,
  level        SMALLINT      NOT NULL DEFAULT 1,
  path         VARCHAR(512)  NOT NULL DEFAULT '/',
  sort_order   INTEGER       NOT NULL DEFAULT 0,
  status       SMALLINT      NOT NULL DEFAULT 1,
  remark       VARCHAR(512)           DEFAULT NULL,
  created_by   UUID                   DEFAULT NULL,
  created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_by   UUID                   DEFAULT NULL,
  updated_at   TIMESTAMPTZ            DEFAULT NULL,
  version      INTEGER       NOT NULL DEFAULT 0,
  is_deleted   SMALLINT      NOT NULL DEFAULT 0,
  deleted_at   TIMESTAMPTZ            DEFAULT NULL,
  CONSTRAINT pk_dict_group            PRIMARY KEY (id),
  CONSTRAINT uk_dict_group_code       UNIQUE (tenant_id, group_code, is_deleted),
  CONSTRAINT fk_dict_group_tenant     FOREIGN KEY (tenant_id)  REFERENCES tenant (id),
  CONSTRAINT fk_dict_group_parent     FOREIGN KEY (parent_id)  REFERENCES dict_group (id),
  CONSTRAINT ck_dict_group_status     CHECK (status IN (0, 1)),
  CONSTRAINT ck_dict_group_is_deleted CHECK (is_deleted IN (0, 1)),
  CONSTRAINT ck_dict_group_level      CHECK (level >= 1)
);

CREATE INDEX idx_dict_group_parent     ON dict_group (tenant_id, parent_id);
CREATE INDEX idx_dict_group_path       ON dict_group (tenant_id, path varchar_pattern_ops);
CREATE INDEX idx_dict_group_is_deleted ON dict_group (is_deleted);

COMMENT ON TABLE  dict_group             IS '字典分组表（多级树形，物化路径）';
COMMENT ON COLUMN dict_group.id         IS '主键 UUID';
COMMENT ON COLUMN dict_group.tenant_id  IS '租户ID，00000000...=平台级';
COMMENT ON COLUMN dict_group.parent_id  IS '父分组ID，NULL=根节点';
COMMENT ON COLUMN dict_group.group_code IS '分组编码，同租户同层唯一';
COMMENT ON COLUMN dict_group.group_name IS '分组名称';
COMMENT ON COLUMN dict_group.level      IS '层级深度，根节点=1';
COMMENT ON COLUMN dict_group.path       IS '物化路径，如 /{id}/{id}/，用于快速子树查询';
COMMENT ON COLUMN dict_group.sort_order IS '同级排序权重，越小越靠前';
COMMENT ON COLUMN dict_group.status     IS '状态：1=启用，0=禁用';
COMMENT ON COLUMN dict_group.version    IS '乐观锁版本号';
COMMENT ON COLUMN dict_group.is_deleted IS '逻辑删除标志：0=未删除，1=已删除';
COMMENT ON COLUMN dict_group.deleted_at IS '删除时间';


-- =============================================================
-- 2. 字典类型表
-- =============================================================
CREATE TABLE dict_type (
  id           UUID          NOT NULL DEFAULT gen_random_uuid(),
  tenant_id    UUID          NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
  group_id     UUID          NOT NULL,
  type_code    VARCHAR(64)   NOT NULL,
  type_name    VARCHAR(128)  NOT NULL,
  is_system    SMALLINT      NOT NULL DEFAULT 0,
  status       SMALLINT      NOT NULL DEFAULT 1,
  remark       VARCHAR(512)           DEFAULT NULL,
  created_by   UUID                   DEFAULT NULL,
  created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_by   UUID                   DEFAULT NULL,
  updated_at   TIMESTAMPTZ            DEFAULT NULL,
  version      INTEGER       NOT NULL DEFAULT 0,
  is_deleted   SMALLINT      NOT NULL DEFAULT 0,
  deleted_at   TIMESTAMPTZ            DEFAULT NULL,
  CONSTRAINT pk_dict_type            PRIMARY KEY (id),
  CONSTRAINT uk_dict_type_code       UNIQUE (tenant_id, type_code, is_deleted),
  CONSTRAINT fk_dict_type_tenant     FOREIGN KEY (tenant_id) REFERENCES tenant (id),
  CONSTRAINT fk_dict_type_group      FOREIGN KEY (group_id)  REFERENCES dict_group (id),
  CONSTRAINT ck_dict_type_is_system  CHECK (is_system  IN (0, 1)),
  CONSTRAINT ck_dict_type_status     CHECK (status     IN (0, 1)),
  CONSTRAINT ck_dict_type_is_deleted CHECK (is_deleted IN (0, 1))
);

CREATE INDEX idx_dict_type_group      ON dict_type (tenant_id, group_id);
CREATE INDEX idx_dict_type_is_deleted ON dict_type (is_deleted);

COMMENT ON TABLE  dict_type             IS '字典类型表';
COMMENT ON COLUMN dict_type.id         IS '主键 UUID';
COMMENT ON COLUMN dict_type.tenant_id  IS '租户ID，00000000...=平台级';
COMMENT ON COLUMN dict_type.group_id   IS '所属分组ID，关联 dict_group.id';
COMMENT ON COLUMN dict_type.type_code  IS '字典类型编码，同租户唯一';
COMMENT ON COLUMN dict_type.type_name  IS '字典类型名称';
COMMENT ON COLUMN dict_type.is_system  IS '是否系统内置：1=是（禁止删除），0=否';
COMMENT ON COLUMN dict_type.status     IS '状态：1=启用，0=禁用';
COMMENT ON COLUMN dict_type.version    IS '乐观锁版本号';
COMMENT ON COLUMN dict_type.is_deleted IS '逻辑删除标志：0=未删除，1=已删除';
COMMENT ON COLUMN dict_type.deleted_at IS '删除时间';


-- =============================================================
-- 3. 字典项表
-- =============================================================
CREATE TABLE dict_item (
  id           UUID          NOT NULL DEFAULT gen_random_uuid(),
  tenant_id    UUID          NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
  type_id      UUID          NOT NULL,
  item_code    VARCHAR(64)   NOT NULL,
  item_label   VARCHAR(256)  NOT NULL,
  item_value   VARCHAR(256)  NOT NULL,
  extra_props  JSONB                  DEFAULT NULL,
  sort_order   INTEGER       NOT NULL DEFAULT 0,
  is_default   SMALLINT      NOT NULL DEFAULT 0,
  status       SMALLINT      NOT NULL DEFAULT 1,
  remark       VARCHAR(512)           DEFAULT NULL,
  created_by   UUID                   DEFAULT NULL,
  created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_by   UUID                   DEFAULT NULL,
  updated_at   TIMESTAMPTZ            DEFAULT NULL,
  version      INTEGER       NOT NULL DEFAULT 0,
  is_deleted   SMALLINT      NOT NULL DEFAULT 0,
  deleted_at   TIMESTAMPTZ            DEFAULT NULL,
  CONSTRAINT pk_dict_item            PRIMARY KEY (id),
  CONSTRAINT uk_dict_item_code       UNIQUE (type_id, item_code, is_deleted),
  CONSTRAINT fk_dict_item_tenant     FOREIGN KEY (tenant_id) REFERENCES tenant (id),
  CONSTRAINT fk_dict_item_type       FOREIGN KEY (type_id)   REFERENCES dict_type (id),
  CONSTRAINT ck_dict_item_is_default CHECK (is_default IN (0, 1)),
  CONSTRAINT ck_dict_item_status     CHECK (status     IN (0, 1)),
  CONSTRAINT ck_dict_item_is_deleted CHECK (is_deleted IN (0, 1))
);

CREATE INDEX idx_dict_item_type_sort  ON dict_item (type_id, sort_order);
CREATE INDEX idx_dict_item_tenant     ON dict_item (tenant_id);
CREATE INDEX idx_dict_item_is_deleted ON dict_item (is_deleted);
CREATE INDEX idx_dict_item_extra      ON dict_item USING GIN (extra_props);

COMMENT ON TABLE  dict_item              IS '字典项表';
COMMENT ON COLUMN dict_item.id          IS '主键 UUID';
COMMENT ON COLUMN dict_item.tenant_id   IS '租户ID，00000000...=平台级';
COMMENT ON COLUMN dict_item.type_id     IS '字典类型ID，关联 dict_type.id';
COMMENT ON COLUMN dict_item.item_code   IS '字典项编码，同类型下唯一';
COMMENT ON COLUMN dict_item.item_label  IS '显示文本（默认语言）';
COMMENT ON COLUMN dict_item.item_value  IS '存储值';
COMMENT ON COLUMN dict_item.extra_props IS '扩展属性 JSONB，如颜色、图标、CSS class 等';
COMMENT ON COLUMN dict_item.sort_order  IS '同类型内排序权重，越小越靠前';
COMMENT ON COLUMN dict_item.is_default  IS '是否默认选中：1=是，0=否';
COMMENT ON COLUMN dict_item.status      IS '状态：1=启用，0=禁用';
COMMENT ON COLUMN dict_item.version     IS '乐观锁版本号';
COMMENT ON COLUMN dict_item.is_deleted  IS '逻辑删除标志：0=未删除，1=已删除';
COMMENT ON COLUMN dict_item.deleted_at  IS '删除时间';


-- =============================================================
-- 4. 字典项多语言表
-- =============================================================
CREATE TABLE dict_item_i18n (
  id          UUID          NOT NULL DEFAULT gen_random_uuid(),
  item_id     UUID          NOT NULL,
  locale      VARCHAR(16)   NOT NULL,
  item_label  VARCHAR(256)  NOT NULL,
  created_by  UUID                   DEFAULT NULL,
  created_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  updated_by  UUID                   DEFAULT NULL,
  updated_at  TIMESTAMPTZ            DEFAULT NULL,
  version     INTEGER       NOT NULL DEFAULT 0,
  is_deleted  SMALLINT      NOT NULL DEFAULT 0,
  deleted_at  TIMESTAMPTZ            DEFAULT NULL,
  CONSTRAINT pk_dict_item_i18n            PRIMARY KEY (id),
  CONSTRAINT uk_dict_item_i18n_locale     UNIQUE (item_id, locale, is_deleted),
  CONSTRAINT fk_dict_item_i18n_item       FOREIGN KEY (item_id) REFERENCES dict_item (id),
  CONSTRAINT ck_dict_item_i18n_is_deleted CHECK (is_deleted IN (0, 1))
);

CREATE INDEX idx_dict_item_i18n_is_deleted ON dict_item_i18n (is_deleted);

COMMENT ON TABLE  dict_item_i18n             IS '字典项多语言表';
COMMENT ON COLUMN dict_item_i18n.id         IS '主键 UUID';
COMMENT ON COLUMN dict_item_i18n.item_id    IS '字典项ID，关联 dict_item.id';
COMMENT ON COLUMN dict_item_i18n.locale     IS '语言代码，如 zh-CN、en-US';
COMMENT ON COLUMN dict_item_i18n.item_label IS '对应语言的显示文本';
COMMENT ON COLUMN dict_item_i18n.version    IS '乐观锁版本号';
COMMENT ON COLUMN dict_item_i18n.is_deleted IS '逻辑删除标志：0=未删除，1=已删除';
COMMENT ON COLUMN dict_item_i18n.deleted_at IS '删除时间';


-- =============================================================
-- 5. 字典变更版本历史表
-- =============================================================
CREATE TABLE dict_version (
  id           UUID          NOT NULL DEFAULT gen_random_uuid(),
  tenant_id    UUID          NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
  type_id      UUID          NOT NULL,
  ver_no       INTEGER       NOT NULL,
  snapshot     TEXT          NOT NULL,
  change_desc  VARCHAR(512)           DEFAULT NULL,
  operated_by  UUID                   DEFAULT NULL,
  operated_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  version      INTEGER       NOT NULL DEFAULT 0,
  is_deleted   SMALLINT      NOT NULL DEFAULT 0,
  deleted_at   TIMESTAMPTZ            DEFAULT NULL,
  CONSTRAINT pk_dict_version            PRIMARY KEY (id),
  CONSTRAINT uk_dict_version_type_ver   UNIQUE (type_id, ver_no),
  CONSTRAINT fk_dict_version_tenant     FOREIGN KEY (tenant_id) REFERENCES tenant (id),
  CONSTRAINT fk_dict_version_type       FOREIGN KEY (type_id)   REFERENCES dict_type (id),
  CONSTRAINT ck_dict_version_is_deleted CHECK (is_deleted IN (0, 1)),
  CONSTRAINT ck_dict_version_ver_no     CHECK (ver_no >= 1)
);

CREATE INDEX idx_dict_version_tenant     ON dict_version (tenant_id);
CREATE INDEX idx_dict_version_is_deleted ON dict_version (is_deleted);

COMMENT ON TABLE  dict_version              IS '字典变更版本历史表';
COMMENT ON COLUMN dict_version.id          IS '主键 UUID';
COMMENT ON COLUMN dict_version.tenant_id   IS '租户ID';
COMMENT ON COLUMN dict_version.type_id     IS '字典类型ID，关联 dict_type.id';
COMMENT ON COLUMN dict_version.ver_no      IS '版本号，同类型内从1开始自增';
COMMENT ON COLUMN dict_version.snapshot    IS '变更时全量字典项 JSON 快照';
COMMENT ON COLUMN dict_version.change_desc IS '变更说明';
COMMENT ON COLUMN dict_version.operated_by IS '操作人ID';
COMMENT ON COLUMN dict_version.operated_at IS '操作时间';
COMMENT ON COLUMN dict_version.version     IS '乐观锁版本号';
COMMENT ON COLUMN dict_version.is_deleted  IS '逻辑删除标志：0=未删除，1=已删除';
COMMENT ON COLUMN dict_version.deleted_at  IS '删除时间';
