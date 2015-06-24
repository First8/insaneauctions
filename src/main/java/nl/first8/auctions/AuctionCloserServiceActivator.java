package nl.first8.auctions;

import org.jboss.as.server.ServerEnvironment;
import org.jboss.as.server.ServerEnvironmentService;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.clustering.singleton.SingletonServiceBuilderFactory;
import org.wildfly.clustering.singleton.election.NamePreference;
import org.wildfly.clustering.singleton.election.PreferredSingletonElectionPolicy;
import org.wildfly.clustering.singleton.election.SimpleSingletonElectionPolicy;

public class AuctionCloserServiceActivator  implements ServiceActivator {
    private static final String CONTAINER_NAME = "server";
    private static final String CACHE_NAME = "default";
    public static final String PREFERRED_NODE = AuctionCloserService.NODE_2;

    @Override
    public void activate(ServiceActivatorContext context) {
        install(AuctionCloserService.DEFAULT_SERVICE_NAME, 1, context);
    }

    private static void install(ServiceName name, int quorum, ServiceActivatorContext context) {
        InjectedValue<ServerEnvironment> env = new InjectedValue<>();
        AuctionCloserService service = new AuctionCloserService(env);
        ServiceController<?> factoryService = context.getServiceRegistry().getRequiredService(SingletonServiceBuilderFactory.SERVICE_NAME.append(CONTAINER_NAME, CACHE_NAME));
        SingletonServiceBuilderFactory factory = (SingletonServiceBuilderFactory) factoryService.getValue();
        factory.createSingletonServiceBuilder(name, service) //
            .electionPolicy(new PreferredSingletonElectionPolicy(new SimpleSingletonElectionPolicy(), new NamePreference(PREFERRED_NODE + "/" + CONTAINER_NAME)))
            .requireQuorum(quorum) //
            .build(context.getServiceTarget()) //
                .addDependency(ServerEnvironmentService.SERVICE_NAME, ServerEnvironment.class, env) //
                .setInitialMode(ServiceController.Mode.ACTIVE) //
                .install() //
        ;
    }
}