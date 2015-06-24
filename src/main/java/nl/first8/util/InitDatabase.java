package nl.first8.util;

import java.math.BigInteger;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import nl.first8.model.Item;

@Singleton
@Startup
public class InitDatabase {
	private static final Logger log = Logger.getLogger(InitDatabase.class.getName());

	@Inject
	private EntityManager em;

	@PostConstruct
	public void initDatabase() {
		BigInteger count = (BigInteger) em.createNativeQuery("SELECT COUNT(*) FROM Item").getSingleResult();
		if (count.intValue() == 0) {
			log.info("Inserting items in empty database");

			insert("Java Security", "A must read for all developers", "images/item-book.jpg", 60000L);
			insert("A night at the Oval Office", "The ultimate holiday experience", "images/item-ovaloffice.jpg",
					50000L);
			insert("PHP Tools for Visual Studio", "Who can live without it?", "images/item-phptools.png", 40000L);
			insert("Money tree", "Grow your own money", "images/item-tree.jpg", 10000L);
			insert("A train", "An Italian train", "images/item-train.jpg", 20000L);
			insert("A stadion", "A nice Frisian Stadion", "images/Abe-Lenstra.png", 25000L);
			insert("A database management system", "The most successful database management system for microcomputers",
					"images/item-db3p11transition.jpg", 35000L);

			log.info("Items inserted");
		} else {
			log.info("Items already exist in database, not inserting them");
		}
	}

	private void insert(String name, String description, String imageUrl, long auctionTime) {
		em.persist(new Item(name, description, imageUrl, auctionTime));
	}
}
