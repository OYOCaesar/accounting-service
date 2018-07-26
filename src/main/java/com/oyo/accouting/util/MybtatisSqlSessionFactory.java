package com.oyo.accouting.util;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.github.pagehelper.PageInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MybtatisSqlSessionFactory {

	public SqlSessionFactory createSqlSessionFactory(DataSource dataSource,String dbName){
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();  
        bean.setDataSource(dataSource);  
        //分页插件  
        PageInterceptor pageHelper = new PageInterceptor();  
        Properties props = new Properties();  
        props.setProperty("supportMethodsArguments", "true");  
        props.setProperty("returnPageInfo", "check");  
        props.setProperty("params", "count=countSql");  
        pageHelper.setProperties(props);
        
        //添加插件  
        bean.setPlugins(new Interceptor[]{pageHelper});
        try {  
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();  
            bean.setConfigLocation(resolver.getResource("classpath:mybatis-config.xml"));
            bean.setMapperLocations(resolver.getResources("classpath*:mapper/"+dbName+"/*Mapper.xml"));
            return bean.getObject();  
        } catch (Exception e) {
        	log.error("["+dbName+"]Mybatis初始化失败",e);
            return null;  
        }  
	}
}
