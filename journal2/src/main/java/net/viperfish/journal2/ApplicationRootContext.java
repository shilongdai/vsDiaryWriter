package net.viperfish.journal2;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import net.viperfish.journal2.auth.OpenBSDBCryptAuthManager;
import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.crypt.JournalEncryptorChain;
import net.viperfish.journal2.index.JournalLuceneIndexer;
import net.viperfish.journal2.swtGui.GraphicalUserInterface;

@Configuration
@EnableAsync(proxyTargetClass = true, order = 1)
@EnableTransactionManagement(proxyTargetClass = true, order = Ordered.LOWEST_PRECEDENCE)
@EnableJpaRepositories(basePackages = {
		"net.viperfish.journal2.core" }, entityManagerFactoryRef = "entityManagerFactoryBean", transactionManagerRef = "jpaTransactionManager")
@ComponentScan(basePackages = "net.viperfish.journal2")
public class ApplicationRootContext implements AsyncConfigurer {

	@Autowired
	private List<Processor> processors;

	private Logger log = LogManager.getLogger();

	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {
		int level = 2;
		log.info("Creating thread pool with " + level + " threads");
		ThreadPoolTaskScheduler exec = new ThreadPoolTaskScheduler();
		exec.setPoolSize(level);
		exec.setThreadNamePrefix("transaction");
		exec.setAwaitTerminationSeconds(60);
		exec.setWaitForTasksToCompleteOnShutdown(true);
		exec.setRejectedExecutionHandler(new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				StringBuilder errorBuilder = new StringBuilder("Task Rejected");
				log.error(errorBuilder.toString());
			}
		});
		return exec;
	}

	@Bean
	public LocalValidatorFactoryBean localValidatorFactoryBean() throws ClassNotFoundException {
		LocalValidatorFactoryBean result = new LocalValidatorFactoryBean();
		result.setProviderClass(Class.forName("org.hibernate.validator.HibernateValidator"));
		result.setValidationMessageSource(this.messageSource());
		return result;
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() throws ClassNotFoundException {
		MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
		processor.setValidator(this.localValidatorFactoryBean());
		return processor;
	}

	@Override
	public Executor getAsyncExecutor() {
		Executor exec = this.taskScheduler();
		log.info(exec + " ready for use");
		return exec;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncUncaughtExceptionHandler() {

			@Override
			public void handleUncaughtException(Throwable ex, Method method, Object... params) {
				StringBuilder errorBuilder = new StringBuilder("Async execution error on method:")
						.append(method.toString()).append(" with parameters:").append(Arrays.toString(params));
				log.error(errorBuilder.toString());
			}
		};
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setCacheSeconds(-1);
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setBasenames("file:./i18n/messages", "file:./i18n/errors");
		return messageSource;
	}

	@Bean
	public DataSource journalDataSource() {
		BasicDataSource datasource = new BasicDataSource();
		datasource.setUsername("journal");
		datasource.setPassword("journal");
		datasource.setDriverClassName("org.h2.Driver");
		datasource.setUrl("jdbc:h2:./data");
		datasource.setMaxIdle(3);
		datasource.setMaxWaitMillis(5000);
		datasource.setRemoveAbandonedOnBorrow(true);
		datasource.setRemoveAbandonedOnBorrow(true);
		datasource.setRemoveAbandonedTimeout(20);
		datasource.setLogAbandoned(true);
		datasource.setValidationQuery("select 1");
		datasource.setMinEvictableIdleTimeMillis(3600000);
		datasource.setTimeBetweenEvictionRunsMillis(1800000);
		datasource.setNumTestsPerEvictionRun(10);
		datasource.setTestOnBorrow(true);
		datasource.setTestOnReturn(false);
		datasource.addConnectionProperty("useUnicode", "yes");
		datasource.addConnectionProperty("characterEncoding", "utf8");
		return datasource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		Map<String, Object> properties = new Hashtable<>();
		properties.put("javax.persistence.schema-generation.database.action", "none");
		properties.put("hibernate.connection.characterEncoding", "utf8");
		properties.put("hibernate.connection.useUnicode", "true");
		properties.put("hibernate.connection.charSet", "utf8");

		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(adapter);
		factory.setDataSource(this.journalDataSource());
		factory.setPackagesToScan("net.viperfish.journal2");
		factory.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
		factory.setValidationMode(ValidationMode.NONE);
		factory.setJpaPropertyMap(properties);
		return factory;
	}

	@Bean
	public JournalEncryptorChain journalEncryptorChain() throws ConfigurationException, IOException {
		JournalEncryptorChain enc = new JournalEncryptorChain(Paths.get("kdfSalt"));
		enc.setConfig(this.configuration());
		for (Processor p : processors) {
			this.log.info("loaded:" + p.getId());
			enc.addProccessor(p);
		}
		return enc;
	}

	@Bean
	public AuthenticationManager authManager() throws ConfigurationException, IOException {
		OpenBSDBCryptAuthManager auth = new OpenBSDBCryptAuthManager(Paths.get("passwd"), this.journalEncryptorChain());
		if (auth.isSetup()) {
			auth.load();
		}
		return auth;
	}

	@Bean
	public PlatformTransactionManager jpaTransactionManager() {
		return new JpaTransactionManager(this.entityManagerFactoryBean().getObject());
	}

	@Bean
	public GraphicalUserInterface ui() {
		return new GraphicalUserInterface();
	}

	@Bean
	public JournalIndexer journalIndexer() {
		JournalLuceneIndexer indexer = new JournalLuceneIndexer();
		return indexer;
	}

	@Bean
	public FileConfiguration configuration() throws IOException, ConfigurationException {
		Path configFile = Paths.get("config");
		if (!configFile.toFile().exists()) {
			Files.createFile(configFile);
		}
		PropertiesConfiguration config = new PropertiesConfiguration();
		config.setFileName(configFile.toString());
		config.setAutoSave(true);
		config.load();
		return config;
	}

}
