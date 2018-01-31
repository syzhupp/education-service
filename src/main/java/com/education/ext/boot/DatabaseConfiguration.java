package com.education.ext.boot;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;
import java.util.List;


/**
 * @author 连接池配置
 */

@Configuration
public class DatabaseConfiguration {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DruidConfigEntity druidConfigEntity;

    @Bean
    public DruidDataSource dataSource() {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(druidConfigEntity.getDriverClassName());
        dataSource.setUrl(druidConfigEntity.getUrl());
        dataSource.setUsername(druidConfigEntity.getUsername());
        dataSource.setPassword(druidConfigEntity.getPassword());
        //配置初始化大小、最小、最大
        dataSource.setInitialSize(Integer.valueOf(druidConfigEntity.getInitialSize()));
        dataSource.setMinIdle(Integer.valueOf(druidConfigEntity.getMinIdle()));
        dataSource.setMaxActive(Integer.valueOf(druidConfigEntity.getMaxActive()));
        //配置获取连接等待超时的时间
        dataSource.setMaxWait(Long.valueOf(druidConfigEntity.getMaxWait()));

        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        //打开PSCache，并且指定每个连接上PSCache的大小
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        //超过时间限制是否回收
        dataSource.setRemoveAbandoned(true);
        //超时时间；单位为秒
        dataSource.setRemoveAbandonedTimeout(30);
        //关闭abanded连接时输出错误日志
        dataSource.setLogAbandoned(true);

        //在一定毫秒数内关闭空闲连接
        dataSource.setTimeBetweenConnectErrorMillis(Long.valueOf(druidConfigEntity.getTimeBetweenEvictionRunsMillis()));
        dataSource.setMinEvictableIdleTimeMillis(Long.valueOf(druidConfigEntity.getMinEvictableIdleTimeMillis()));

        //过滤器
        List<Filter> filterList = Lists.newArrayList();
        filterList.add(wallFilter());
        dataSource.setProxyFilters(filterList);

        //配置监控统计拦截的filters
        try {
            dataSource.setFilters("stat,wall,log4j");
            dataSource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000"); //设置慢查询
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataSource;
    }


    @Bean
    public WallFilter wallFilter(){
        WallFilter wallFilter =  new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;

    }

    @Bean
    public WallConfig wallConfig(){
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        return wallConfig;
    }




//HikariConfig 连接池
//    @Bean(destroyMethod = "close")
//    public DataSource dataSource() {
//        logger.debug("Configuring Datasource");
//
//        if (druidConfigEntity.getUrl() == null) {
//            logger.error("Your database connection pool configuration is incorrect! The application" +
//                            " cannot start. Please check your Spring profile, current profiles are: {}");
//            throw new ApplicationContextException("Database connection pool is not configured correctly");
//        }
//        HikariConfig config = new HikariConfig();
//        config.setDataSourceClassName(druidConfigEntity.getDriverClassName());
//        config.addDataSourceProperty("url",druidConfigEntity.getUrl());
//        config.setUsername(druidConfigEntity.getUsername());
//        config.setPassword(druidConfigEntity.getPassword());
//        config.setConnectionTestQuery("SELECT 1");
//        config.setConnectionInitSql("SELECT 1");
//        config.setMaximumPoolSize(5);
////        config.setLeakDetectionThreshold(reolver.getProperty("leakDetectionThreshold", Long.class, 0L));
//        config.setPoolName("springhim");
//        //
//        //MySQL optimizations, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
//        if ("com.mysql.jdbc.jdbc2.optional.MysqlDataSource".equals(druidConfigEntity.getDriverClassName())) {
//            config.addDataSourceProperty("cachePrepStmts", "true");
//            config.addDataSourceProperty("prepStmtCacheSize", "250");
//            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//        }
//        //        if (metricRegistry != null) {
//        //            config.setMetricRegistry(metricRegistry);
//        //        }
//        return new HikariDataSource(config);
//    }

//    @Bean(initMethod = "migrate")
//    public Flyway flyway(){
//
//        Flyway fly = new Flyway();
//        fly.setDataSource(druidConfigEntity.getUrl(),druidConfigEntity.getUsername(),druidConfigEntity.getPassword());
//        fly.setBaselineOnMigrate(true);
//
////        fly.clean();
//        fly.migrate();
//        return fly;
//
//    }


}
