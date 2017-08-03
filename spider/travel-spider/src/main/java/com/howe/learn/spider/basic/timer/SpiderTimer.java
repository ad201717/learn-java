package com.howe.learn.spider.basic.timer;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;

import com.howe.learn.spider.basic.core.Spider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sleepycat.je.dbi.DbEnvPool;
import com.sleepycat.je.dbi.EnvironmentImpl;


@Component
public class SpiderTimer implements ApplicationContextAware {
	
	@Scheduled(initialDelay = 1000, fixedRate = 24 * 60 * 60 * 1000)
	public void start() throws Exception {
		Spider.getInstance().start();
		Spider.getInstance().awaitTerminal();
	}
	
	private void closeEnvironments() {
		Collection<EnvironmentImpl> envs = DbEnvPool.getInstance().getEnvImpls();
		for (EnvironmentImpl env : envs) {
			if (!env.isClosed() && !env.isClosing()) {
				env.close();
			}
		}

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while(drivers.hasMoreElements()){
			try {
				DriverManager.deregisterDriver(drivers.nextElement());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
