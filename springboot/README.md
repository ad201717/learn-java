## springBoot 学习

### 快速使用 springBoot 方式：

1. pom 继承 spring-boot-starter-parent

    ```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.4.3.RELEASE</version>
    </parent>
    ```
2. pom 加入依赖
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>1.4.3.RELEASE</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
     ```

### 默认端口:8080。修改方式(按优先级低到高排列)：
1. 设置 run/debug configuration => VM options: -Dserver.port=9090
2. 在配置文件 application.yml 中配置端口
    ```yml
    server:
        port:10010
    ```
3. 在配置文件 application.properties 中配置端口
    ```properties
    server.port=10020
    ```
4. 启动类实现 EmbeddedServletContainerCustomizer
    ```
    public class Main implements EmbeddedServletContainerCustomizer {

        public static void main(String[] args){
            SpringApplication.run(Main.class, args);
        }

        @Override
        public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
            configurableEmbeddedServletContainer.setPort(9090);
        }
    }
    ```

### springBoot 默认是不支持JSP的，需要打成war包。建议使用 Thymeleaf 模板引擎

1. springBoot 官方集成 Thymeleaf 模板引擎

    引入 Thymeleaf 引擎需导入jar包
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    ```
2. 模板文件位置

   默认情况下我们需要把编写的模板文件放在src/main/resources/templates目录下，如图

   templates下面可以按工程需要建立子目录，例如图中的components子目录。

   如果想要更换templates目录可以修改spring.thymeleaf.prefix配置项

3. 启用 ThymeleafViewResolver 。在 Main 中配置 viewResolver
    ```
    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(springTemplateEngine);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
    ```

4. 引用静态资源
- 继承 WebMvcConfigurerAdapter 实现 addResourceHandlers 方法
    ```
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //引用外部资源
        registry.addResourceHandler("/out/**").addResourceLocations("file:D:/static/");
        //引用内部资源
        registry.addResourceHandler("/**").addResourceLocations("classpath:static/");
        super.addResourceHandlers(registry);
    }
    ```

### references
1. [Spring Boot——开发新一代Spring Java应用](https://www.tianmaying.com/tutorial/spring-boot-overview)
