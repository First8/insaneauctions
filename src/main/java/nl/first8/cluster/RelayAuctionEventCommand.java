package nl.first8.cluster;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.wildfly.clustering.dispatcher.Command;
import org.wildfly.clustering.group.Node;

public class RelayAuctionEventCommand implements Command<Object, Node> {
	private static final Logger LOG = Logger.getLogger(RelayAuctionEventCommand.class.getName());

	private static final long serialVersionUID = 1L;

	private RemoteAuctionEvent event;

	public RelayAuctionEventCommand(RemoteAuctionEvent event) {
		this.event = event;
	}

	@Override
	public Object execute(Node node) throws Exception {
		try {
			LOG.log(Level.INFO, "We have a event from node {0}", node.getName());
			Properties prop = new Properties();
			prop.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			InitialContext ctx = new InitialContext(prop);
			ClusterEventService clusterEventService = (ClusterEventService) ctx.lookup("java:global/ROOT/"
					+ ClusterEventService.class.getSimpleName());
			clusterEventService.receiveEvent(event);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
}