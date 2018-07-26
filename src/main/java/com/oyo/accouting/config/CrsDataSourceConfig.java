package com.oyo.accouting.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.oyo.accouting.util.MybtatisSqlSessionFactory;

/**
 * Crs数据源
 * @author liujunyi
 *
 */
@Configuration
@MapperScan(basePackages = "com.oyo.accouting.mapper."+CrsDataSourceConfig.prefix, sqlSessionTemplateRef = CrsDataSourceConfig.prefix+"SqlSessionTemplate")
public class CrsDataSourceConfig extends MybtatisSqlSessionFactory{
	
	public static final String prefix="crs";
	
	@Bean(name = prefix+"DataSource")
	@ConfigurationProperties(prefix = "spring.datasource."+prefix)
	@Primary
	public DataSource setDataSource() {
		return DataSourceBuilder.create().type(DruidXADataSource.class).build();
	}
	
	@Bean(name = prefix+"TransactionManager")
    @Primary
    public DataSourceTransactionManager setTransactionManager(@Qualifier(prefix+"DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
	
	@Bean(name = prefix+"SqlSessionFactory")
    @Primary
    public SqlSessionFactory setSqlSessionFactory(@Qualifier(prefix+"DataSource") DataSource dataSource) throws Exception {
        return createSqlSessionFactory(dataSource, prefix);
    }

    @Bean(name = prefix+"SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate setSqlSessionTemplate(@Qualifier(prefix+"SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
	
	
	
}
