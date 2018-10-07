package com.security.config;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@AutoConfigureBefore({ XADataSourceAutoConfiguration.class, DataSourceAutoConfiguration.class })
@EnableTransactionManagement
@Resource(name="jdbc/review_rbac", type=javax.sql.DataSource.class, lookup="jdbc/review_rbac")
public class RbacConfig {

	@Autowired
    private Environment env;
	
	@Autowired
	private ApplicationContext context;
	
    @Bean(name="dataSource")
    public DataSource dataSource() throws NamingException {
    	//DataSource dataSource = (DataSource)new JndiTemplate().lookup(env.getProperty("spring.rbac.datasource.jndi-name"));
    	JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
    	DataSource dataSource = dataSourceLookup.getDataSource(env.getProperty("spring.rbac.datasource.jndi-name"));
    	excludeMBeanIfNecessary(dataSource, "dataSource");
    	return dataSource;
    }
    
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        System.out.println("RbacConfig.jdbcTemplate() " + dataSource.toString());
        return new JdbcTemplate(dataSource);
    }
    
    @Bean(name = "namedJdbcTemplate")
    public NamedParameterJdbcTemplate namedJdbcTemplate(DataSource dataSource) {
        System.out.println("RbacConfig.namedJdbcEzedebit() " + dataSource.toString());
        return new NamedParameterJdbcTemplate(dataSource);
    }
    
	private void excludeMBeanIfNecessary(Object candidate, String beanName) {
		try {
			MBeanExporter mbeanExporter = this.context.getBean(MBeanExporter.class);
			if (JmxUtils.isMBean(candidate.getClass())) {
				mbeanExporter.addExcludedBean(beanName);
			}
		} catch (NoSuchBeanDefinitionException ex) {
			// No exporter. Exclusion is unnecessary
		}
	}
}
