package com.education.ext.boot.mybatis;

import com.education.ext.boot.DatabaseConfiguration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * 定义MyBati配置
 */
@Configuration
@AutoConfigureAfter({DatabaseConfiguration.class})
@EnableTransactionManagement
public class MyBatisDatabaseConfig {

    /**
     * 将mybatis的sqlSessionFactory交给spring管理
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException {

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource);
            sessionFactory.setMapperLocations(resolver.getResources("classpath:mapper/*/*.xml"));
            sessionFactory.setTypeAliasesPackage("com.education.ext.pojo");

            Properties properties=new Properties();

            properties.setProperty("cacheEnabled","true");
            properties.setProperty("lazyLoadingEnabled","true");
            properties.setProperty("aggressiveLazyLoading","true");
            properties.setProperty("multipleResultSetsEnabled","true");
            properties.setProperty("useColumnLabel","true");
            properties.setProperty("useGeneratedKeys","false");
            properties.setProperty("autoMappingBehavior","");

            properties.setProperty("defaultExecutorType","SIMPLE");
            properties.setProperty("mapUnderscoreToCamelCase","true");
            properties.setProperty("localCacheScope","SESSION");
            properties.setProperty("jdbcTypeForNull","NULL");
            sessionFactory.setConfigurationProperties(properties);

            return sessionFactory;
    }


    /**
     * 使用tk，通用mapper配置mybatis
     * @return
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {

        //mapper配置
        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        MapperHelper mapperHelper = new MapperHelper();

        //特殊配置
        Config config = new Config();

        config.setIDENTITY("MYSQL");
        config.setNotEmpty(true);
        config.setEnableMethodAnnotation(false);

        //注册通用mapper
        mapperHelper.registerMapper(MyMapper.class);
        mapperHelper.setConfig(config);

        scannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        scannerConfigurer.setBasePackage("com.education.ext.dao"); //扫描mapper目录
        scannerConfigurer.setMapperHelper(mapperHelper);

        return scannerConfigurer;
    }

}
