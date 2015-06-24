package nl.first8.cluster;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.wildfly.clustering.dispatcher.CommandDispatcher;
import org.wildfly.clustering.dispatcher.CommandDispatcherFactory;
import org.wildfly.clustering.group.Group;

@Singleton
@Startup
@Local(CommandDispatcherFactory.class)
public class CommandDispatcherFactoryBean implements CommandDispatcherFactory {
	private static final Logger LOG = Logger.getLogger(CommandDispatcherFactoryBean.class.getName());

	@Resource(lookup = "java:jboss/clustering/dispatcher/server")
	private CommandDispatcherFactory factory;

	@PostConstruct
	public void init() {
		LOG.info("Startup CommandDispatcherFactoryBean");
	}

	@PreDestroy
	public void tearDown() {
		LOG.info("Stopping CommandDispatcherFactoryBean");
	}

	@Override
	public <C> CommandDispatcher<C> createCommandDispatcher(Object service, C context) {
		return this.factory.createCommandDispatcher(service, context);
	}

	@Override
	public Group getGroup() {
		return this.factory.getGroup();
	}
}
