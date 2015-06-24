package nl.first8.cluster;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import nl.first8.auctions.AuctionEvent;
import nl.first8.auctions.AuctionUpdated;

import org.wildfly.clustering.dispatcher.CommandDispatcher;
import org.wildfly.clustering.dispatcher.CommandDispatcherFactory;
import org.wildfly.clustering.group.Node;

@Singleton(name = "ClusterEventService")
@Startup
public class ClusterEventServiceBean implements ClusterEventService {
	private static final Logger LOG = Logger.getLogger(ClusterEventServiceBean.class.getName());

	@Inject
	private CommandDispatcherFactory commandDispatcherFactory;

	@Inject
	private CommandDispatcher<Node> dispatcher;

	@Inject
	@AuctionUpdated
	private Event<AuctionEvent> auctionUpdatedEvent;

	@PostConstruct
	public void init() {
		LOG.info("Startup ClusterEventServiceBean");
	}

	@PreDestroy
	public void tearDown() {
		LOG.info("Stopping ClusterEventServiceBean");
	}

	@Override
	public void receiveEvent(RemoteAuctionEvent event) {
		LOG.log(Level.INFO, "Received event from {0}", event.getSource());
		if (!isSourceThisNode(event)) {
			auctionUpdatedEvent.fire(new RemoteAuctionEvent(getNodeId()));
		}
	}

	// check the source as we do not want to ping-pong the messages between
	// servers due to the hook in the CDI event.
	@Override
	public void onAuctionUpdated(@Observes @AuctionUpdated AuctionEvent auctionEvent) {
		LOG.log(Level.INFO, "Received CDI event to propagate");
		if (!isSourceThisNode(auctionEvent)) {
			fireRemoteAuctionEvent();
		}
	}

	private boolean isSourceThisNode(AuctionEvent evt) {
		if (RemoteAuctionEvent.class.isInstance(evt)) {
			return getNodeId().equals(((RemoteAuctionEvent) evt).getSource());
		}
		return false;
	}

	private String getNodeId() {
		return commandDispatcherFactory.getGroup().getLocalNode().getName();
	}

	private void fireRemoteAuctionEvent() {
		RemoteAuctionEvent remoteEvent = new RemoteAuctionEvent(getNodeId());
		List<Node> nodes = commandDispatcherFactory.getGroup().getNodes();
		for (Node node : nodes) {
			if (!getNodeId().equals(node.getName())) {
				LOG.log(Level.INFO, "Despatching to node {0}", node.getName());
				dispatcher.submitOnNode(new RelayAuctionEventCommand(remoteEvent), node);
			}
		}
	}
}
