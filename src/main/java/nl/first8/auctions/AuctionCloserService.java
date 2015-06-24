package nl.first8.auctions;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.as.server.ServerEnvironment;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.Value;

public class AuctionCloserService implements Service<Environment> {
	private static final String JNDI_NAME_AUCTION_CLOSER = "global/ROOT/" + AuctionCloserImpl.class.getSimpleName()
			+ "!" + AuctionCloser.class.getName();
	public static final ServiceName DEFAULT_SERVICE_NAME = ServiceName.JBOSS.append("first8", "AuctionCloser",
			"default");
	public static final String NODE_1 = "nodeOne";
	public static final String NODE_2 = "nodeTwo";

	// cannot inject in a service
	private Logger log = Logger.getLogger(AuctionCloserService.class.getCanonicalName());

	private final Value<ServerEnvironment> env;
	private final AtomicBoolean started = new AtomicBoolean(false);

	public AuctionCloserService(Value<ServerEnvironment> env) {
		this.env = env;
	}

	@Override
	public Environment getValue() {
		if (!this.started.get()) {
			throw new IllegalStateException();
		}
		return new Environment(this.env.getValue().getNodeName());
	}

	@Override
	public void start(StartContext context) throws StartException {
		if (!started.compareAndSet(false, true)) {
			throw new StartException("The service is still started!");
		}
		log.info("Start AuctionCloser timer service '" + this.getClass().getName() + "'");

		try {
			InitialContext ic = new InitialContext();
			AuctionCloser auctionCloser = (AuctionCloser) ic.lookup(JNDI_NAME_AUCTION_CLOSER);
			auctionCloser.initialize("This is AuctionCloser @" + this.env.getValue().getNodeName()
					+ ", initialized at " + new Date());
		} catch (NamingException e) {
			throw new StartException("Could not initialize timer", e);
		}
	}

	@Override
	public void stop(StopContext context) {
		if (!started.compareAndSet(true, false)) {
			log.warning("The service '" + this.getClass().getName() + "' is not active!");
		} else {
			log.info("Stop AuctionCloser timer service '" + this.getClass().getName() + "'");
			try {
				InitialContext ic = new InitialContext();
				AuctionCloser auctionCloser = (AuctionCloser) ic.lookup(JNDI_NAME_AUCTION_CLOSER);
				auctionCloser.stop();
			} catch (NamingException e) {
				log.severe("Could not stop timer");
			}
		}
	}
}