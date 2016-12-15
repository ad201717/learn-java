## spring quartz guide
### spring quartz standalone mode.
* as standalone mode use RAMJobStore as its storage device, this mode dose not support persistence.
and this mode is not clustered, which means that if multi instance exists, job will cause repetitive execution.

* How-To :
1. solve jar dependencies.
    > 1. spring-core/spring-context/spring-context-support/spring-web/spring-tx
    > 2. quartz
    > 3. log4j/logback-core/logback-classic

2. create job
    1. arbitrary class with a method.
    ```
    public class HelloWorldTask {
        public void execute(JobExecutionContext context){
            ...//do something work
        }
    }
    ```

    2. class implement interface Job.
    ```
    public class AnotherTask implements Job{
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            ...//do something work
        }
    }
    ```

3. define job bean in applicationContext.xml
    ```
    <bean id="helloWorldTask" class="com.howe.learn.quartz.HelloWorldTask"/>
    <bean id="anotherTask" class="com.howe.learn.quartz.AnotherTask"/>
     ```

4. define jobDetail/jobTrigger/schedulerFactory in scheduler-task.xml
    1. jobDetail
    ```xml
    <!-- 使用MethodInvokingJobDetailFactoryBean，任务类可以不实现Job接口，通过targetMethod指定调用方法 -->
    <bean id="helloWorldJobDetail"
        class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
        <!--false表示等上一个任务执行完后再开启新的任务 -->
        <property name="concurrent" value="false" />
        <property name="targetObject">
            <ref bean="helloWorldTask" />
        </property>
        <property name="targetMethod">
            <value>run</value>
        </property>
    </bean>
    ```
    ```xml
    <!-- JobDetailFactoryBean，任务类需要实现Job接口 -->
    <bean id="anotherJobDetail"
          class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
        <property name="jobClass" value="com.howe.learn.quartz.AnotherTask"/>
        <property name="durability" value="true"/>
    </bean>
    ```

    2. jobTrigger
    ```xml
    <!-- 调度触发器 -->
    <bean id="helloWorldTrigger"
        class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
        <property name="jobDetail">
            <ref bean="helloWorldJobDetail" />
        </property>
        <!-- 每1分钟执行 -->
        <property name="cronExpression">
            <value>0 0/1 * * * ?</value>
        </property>
    </bean>
 	```
    3. schedulerFactory
    ```xml
    <!-- 调度工厂 -->
    <bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
        <property name="triggers">
            <list>
                <ref bean="helloWorldTrigger" />
                <ref bean="anotherTrigger" />
            </list>
        </property>
    </bean>
 	```

5. import scheduler-task.xml in web.xml
    ```xml
    <context-param>
       <param-name>contextConfigLocation</param-name>
       <param-value>classpath*:spring/applicationContext.xml,classpath*:spring/scheduler-task.xml</param-value>
    </context-param>

    <listener>
       <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
   ```

6. done!

### spring quartz cluster mode.
* In cluster mode all instances will report jobs to DB, only one instance will execute the job.
* If instance in working failed, job will reallocate to other instance.
* How-To:
1. add more dependencies
    > 1. spring-jdbc
    > 2. mysql-connector-java

2. create job
    * Only class implement interface Job is allowed, because quartz will store job in DB.

3. create a database.
    * DB is used to support persistence, synchronization between instants.
    * see [table schema](spring-quartz-cluster/doc/tables_mysql_innodb.sql)

4. define a dataSource in applicationContext.xml
    * use dbcp or c3p0.
    ```xml
    <bean id="propertyConfigurer"
        class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
               <property name="location">
               <value>classpath:jdbc.properties</value>
               </property>
    </bean>

    <bean id="quartzDs" class="com.mchange.v2.c3p0.ComboPooledDataSource"
        destroy-method="close">
        <property name="driverClass">
             <value>${driver}</value>
        </property>
        <property name="jdbcUrl">
             <value>${url}</value>
        </property>
        <property name="user">
             <value>${username}</value>
        </property>
        <property name="password">
             <value>${password}</value>
        </property>
        <property name="maxPoolSize" value="10"></property>
        <property name="initialPoolSize" value="5"></property>
        <property name="maxIdleTime" value="60"></property>
    </bean>
 	```

5. configure quartz in quartz.properties
    ```
    ##Quartz 调度任务所需的配置文件

    ##org.quartz.scheduler.instanceName属性可为任何值，用在 JDBC JobStore 中来唯一标识实例，但是所有集群节点中必须相同。
    org.quartz.scheduler.instanceName = ClusterScheduler
    ##org.quartz.scheduler.instanceId　属性为 AUTO即可，基于主机名和时间戳来产生实例 ID。
    org.quartz.scheduler.instanceId = AUTO

    orgorg.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
    org.quartz.threadPool.threadCount = 2
    org.quartz.threadPool.threadPriority = 5
    org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread = true

    org.quartz.jobStore.misfireThreshold = 60000
    ##org.quartz.jobStore.class属性为 JobStoreTX，将任务持久化到数据中。
    ##因为集群中节点依赖于数据库来传播 Scheduler 实例的状态，你只能在使用 JDBC JobStore 时应用 Quartz 集群。
    ##这意味着你必须使用 JobStoreTX 或是 JobStoreCMT 作为 Job 存储；你不能在集群中使用 RAMJobStore。
    orgorg.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX
    orgorg.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
    org.quartz.jobStore.tablePrefix = qrtz_
    org.quartz.jobStore.maxMisfiresToHandleAtATime=10
    ##org.quartz.jobStore.isClustered 属性为 true，你就告诉了 Scheduler 实例要它参与到一个集群当中。
    ##这一属性会贯穿于调度框架的始终，用于修改集群环境中操作的默认行为。
    org.quartz.jobStore.isClustered = true
    ##org.quartz.jobStore.clusterCheckinInterval 属性定义了Scheduler 实例检入到数据库中的频率(单位：毫秒)。
    ##Scheduler 检查是否其他的实例到了它们应当检入的时候未检入；这能指出一个失败的 Scheduler 实例，且当前 Scheduler 会以此来接管任何执行失败并可恢复的 Job。
    ##通过检入操作，Scheduler 也会更新自身的状态记录。clusterChedkinInterval 越小，Scheduler 节点检查失败的 Scheduler 实例就越频繁。默认值是 15000 (即15 秒)。
    org.quartz.jobStore.clusterCheckinInterval = 20000

    ```

6. define jobDetail/jobTrigger/schedulerFactory in scheduler-task.xml
    1. jobDetail
    ```xml
    <!-- JobDetailFactoryBean，任务类需要实现Job接口 -->
    <bean id="clusteredJobDetail"
          class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
        <property name="jobClass" value="com.howe.learn.quartz.ClusteredTask"/>
        <property name="durability" value="true"/>
    </bean>
    ```

    2. jobTrigger
    ```xml
    <!-- 调度触发器 -->
    <bean id="clusteredTrigger"
        class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
        <property name="jobDetail">
            <ref bean="clusteredJobDetail" />
        </property>
        <!-- 每1分钟执行 -->
        <property name="cronExpression">
            <value>0 0/1 * * * ?</value>
        </property>
    </bean>
 	```
    3. schedulerFactory
    * add dataSource and properties to schedulerFactoryBean
    ```xml
    <!-- 调度工厂 -->
    <bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
       <property name="dataSource">
 			<ref bean="quartzDs"/>
 		</property>
 		<property name="applicationContextSchedulerContextKey" value="applicationContextKey"/>
 		<property name="configLocation" value="classpath:quartz.properties"/>
        <property name="triggers">
            <list>
                <ref bean="clusteredTrigger" />
            </list>
        </property>
    </bean>
 	```

7. import scheduler-task.xml in web.xml
    ```xml
    <context-param>
       <param-name>contextConfigLocation</param-name>
       <param-value>classpath*:spring/applicationContext.xml,classpath*:spring/scheduler-task.xml</param-value>
    </context-param>

    <listener>
       <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
   ```

8. done!
