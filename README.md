# zdapi
API开放平台

目前腾讯、阿里等互联网大厂都有其自家的API开放调用平台、供开发者开发调用使用，而网络上对于此类API开放平台是如何具体实现的内容又少之又少，因此我们想到自己实现一个API开放平台，通过集合第三方API接口或者是我们个人编写的API接口从而实现快速调用，而不需要自己再去实现或者引用，从而节省一些开发上的时间，将精力聚集于主干功能。最终通过总体设计、学习新技术完成了本API开放平台。

## 1、API开放平台设计与架构

### 1.1 设计原理

#### HTTP

#### RPC

#### GATEWAY

#### API签名认证

#### SDK

#### MVC

### 1.3 技术选型

##### 前端

- Ant Design Pro
- React
- Ant Design Procomponents（前端组件库）
- Umi Request (Axios 的封装)

##### 后端

- Java Spring Boot
- Spring Boot Starter (SDK开发)
- Dubbo (RPC框架)
- Nacos（注册中心）
- Spring Cloud Gateway(网关)

## 2、API开放平台具体实现

###  2.1需求分析及项目初始化

#### 需求分析（要做什么）

做一个API接口平台:
1、管理员可以对接口信息进行增删改查

2、用户可以访问前台，查看接口信息
其他要求:
1、防止攻击（安全性)

2、不能随便调用(限制、开通)

3、统计调用次数

4、流量保护

5、API接入

#### 数据库设计

接口信息表：

```sql
-- 接口信息表
create table if not exists mofeng.`interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '用户名',
    `description` varchar(256) null comment '描述',
    `url` varchar(512) not null comment '接口地址',
    `requestHeader` text null comment '请求头',
    `responseHeader` text null comment '响应头',
    `userId` varchar(256) not null comment '创建人',
    `status` int default 0 not null comment '接口状态（0 - 关闭， 1 - 开启））',
    `method` varchar(256) not null comment '请求类型',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
    ) comment '接口信息表';
```

#### 前端初始化

使用ant design pro脚手架初始化项目，同时根据后端生成的符合openai规范的swagger文档进行对接接口代码的自动生成。

#### 后端初始化

使用mybatis-generator插件生成对应java实体类和mapper、service层的初始代码,并套用以前已经完成的登陆注册代码部分。

### 2.2 模拟接口调用及客户端SDK开发

#### 模拟接口调用

**提供三个模拟接口**

1. GET 接口
2. POST 接口（URL 传参）
3. POST 接口 （Restful)

**调用接口**

使用hutool-httputils（java工具类库）进行http接口调用

简单调用示例：

```java
// 最简单的HTTP请求，可以自动通过header等信息判断编码，不区分HTTP和HTTPS
String result1= HttpUtil.get("https://www.baidu.com");

// 当无法识别页面编码的时候，可以自定义请求页面的编码
String result2= HttpUtil.get("https://www.baidu.com", CharsetUtil.CHARSET_UTF_8);

//可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
HashMap<String, Object> paramMap = new HashMap<>();
paramMap.put("city", "北京");

String result3= HttpUtil.get("https://www.baidu.com", paramMap);
```

#### **Starter开发的目的**

在最理想的开发情况下，开发者只需要关心调用哪些接口、传递哪些参数，就跟调用自己写的代码一样简单。
而开发完成了starter：开发者引入之后，可以直接在application.yml中写配置，自动创建客户端

#### **Starter开发流程**

1、引入依赖

```java
	<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
    </dependency>

    <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
    </dependency>

    <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
	</dependency>
```

2、编写配置类（启动类）

```java
@Configuration
// 能读取 application 中的配置属性
@ConfigurationProperties("mofeng.client")
@Data
@ComponentScan
public class MofengClientConfig {

    private String accessKey;
    private String secretKey;

    @Bean
    public MofengApiClient mofengApiClient(){
        return new MofengApiClient(accessKey, secretKey);
    }
}
```

3、注册配置类

resources/META_INF/spring.factories

```java
# starter
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.mofeng.mofengclientsdk.MofengClientConfig
```

mvn install 打包为本地项目、创建新项目（复用 server 项目）测试效果，完成后其他项目导入即可使用SDK简化开发流程。

### 2.3 API签名认证实现

#### API 签名认证

本质：

1. 签名签发
2. 使用签名（校验签名）

使用签名认证的目的就是保证安全性，防止恶意调用

#### 如何实现

通过 http request header 头传递参数

- 参数1： accessKey 调用的标识 userA, userB (复杂、无序、无规律)

- 参数2： secretKey 密钥 （复杂、无序、无规律），且该参数不能放在请求头中

  类似于用户名和密码，区别：accessKey、 secretKey 是无状态的

  密钥一般不用在服务器之间的传递，因为在传递过程中可能被拦截

- 参数3： 用户请求参数

- 参数4：sign
  加密方式：对称加密、非对称加密、d5签名（不可解密）；用户参数和密钥通过签名生成算法(MD5、HMac、Sha1)加密最终得到不可解密的值
  服务端使用用一致的参数和算法去生成签名，只要和用户传的的一致，就表示认证成功。

- 参数5：通过加入nonce随机数（只用一次）,且服务端要保存用过的随机数来防止重放攻击，

- 参数6：通过加入timestamp时间戳，校验时间戳是否过期。

实际上API 签名认证是一个很灵活的设计，具体要有哪个参数、参数名如何需要根据场景来设计。比如： userId、 appId、version、固定值等。

### 2.4 API接口功能开发

#### API接口主要功能

1. 开发接口发布、下线的功能（管理员）
2. 前端去浏览接口、查看接口文档、申请签名（注册）
3. 在线调试（用户）
4. 统计用户调用接口的次数
5. 优化系统 - API 网关

#### 开发接口发布/下线功能

发布接口逻辑（仅管理员可操作）

1. 校验该接口是否存在
2. 判断该接口是否可以调用
3. 修改接口数据库中的状态字段为 1

下线接口逻辑（仅管理员可操作）

1. 校验接口是否存在
2. 修改接口数据库中的状态字段为 0

#### 查看接口文档

动态路由，用 url 来传递 id, 加载不同的接口信息

#### 申请签名

用户在注册成功时，自动分配 accessKey、secretKey 

扩展点：用户可以申请更换签名

### 2.5 API网关配置

#### 网关的作用

1. 路由
2. 负载均衡
3. 统一鉴权
4. 跨域
5. 统一业务处理（缓存）
6. 访问控制
7. 发布控制
8. 流量染色
9. 接口保护
   1. 限制请求
   2. 信息脱敏
   3. 降级（熔断）
   4. 超时时间
10. 统一日志
11. 统一文档

**路由**

起到转发的作用，比如有接口 A 和接口 B， 网关会记录这些信息，根据用户访问的地址和参数，转发请求到对应的接口（服务器/集群）

例如

路径/a 转到 接口 A

路径/b 转到 接口 B

**负载均衡**

在路由的基础上

路径/c 转到 服务 A / 集群 A （随机转发到其中的某一个机器）

**发布控制**

灰度发布，比如上线新接口，先给新接口分配 20% 的流量，老接口 80%， 再慢慢调整比例

**流量染色**

给请求（流量）添加一些标识，一般是设置请求头中，添加新的请求头

**统一接口保护**

1. 信息脱敏：指对某些敏感信息通过脱敏规则进行数据的变形，实现敏感隐私数据的可靠保护
2. 超时时间：RPC调用的每个调用都是要提供超时时间的，超时之后对应相应处理
3. 重试（业务保护：在一次请求中，往往需要经过多个服务之间的调用，由于网络波动或者其他原因，请求可能无法正常到达服务端或者服务端的请求无法正常的返回，从而导致请求失败，这种失败往往可以通过重试的方式来解决

**统一业务处理**

把每个项目中都要做的通用逻辑放到上层（网关），统一处理，比如本项目的次数统计

**统一鉴权**

判断用户是否有权限进行操作，无论访问什么接口，都统一验证权限，避免重复写验证权限操作。

**访问控制**

黑白名单，比如限制 DDOS IP之类的网络攻击

**统一日志**

统一的请求、响应信息记录

### 2.6 RPC实现（基于Dubbo框架）

#### RPC

**作用：像本地方法一样调用远程方法**

1. 对开发者更透明，减少了很多的沟通 成本
2. RPC 想远程服务器发送请求时，未必要使用 HTTP 请求，比如还可以用 TCP/IP，性能更高（内部服务更适用）

#### Dubbo 框架（RPC 实现）

两种使用方式：

1. Spring Boot 代码（注解 + 编程式）：写 Java 接口，服务提供者和消费者都去引用这个接口
2. IDL（接口调用语言）：创建一个公共的接口定义文件，服务提供者和消费者都去读取这个文件。优点是跨语言，所有的框架都熟悉。

#### 整合运用

1. backend 项目作为服务提供者，提供 3 个方法：
   1. 实际情况应该是去数据库中查是否已分配给用户
   2. 从数据库中查询模拟接口是否存在，以及请求方法是否匹配（还可以校验请求参数）
   3. 调用成功，接口调用次数 + 1 invokeCount
2. gateway 项目作为服务调用者，调用这 3 个方法

整合 Nacos 作为注册中心，管理服务提供者与服务消费者。

注意：

1. 服务调用类必须在同一包下，建议是抽象出一个公共项目（放接口、实体类等）
2. 设置注解（比如启动类的 EnableDubbo、接口实现类和 Bean 引用的注解）
3. 添加配置
4. 服务调用项目和提供者项目尽量引入相同的依赖和配置

#### **依赖引入**

```java
       <!-- https://mvnrepository.com/artifact/org.apache.dubbo/dubbo -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>3.1.3</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>2.1.0</version>
        </dependency>
```

### 2.7 接口统计分析功能实现

#### 需求

各接口的总调用次数占比（通过饼图展示）取调用最多的前3个接口，从而分析出哪些接口没有人用（降低资源、或者下
线)，高频接口（增加资源）。

#### 实现

**前端**
使用echarts

**后端**
步骤：
1.SQL查询调用数据：

```sql
select interfacelnfold,sum(totalNum) as totalNum from user_interface_info
group by interfacelnfold order by totalNum desc limit 3;
```

2.业务层去关联查询接口信息

