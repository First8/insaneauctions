package nl.first8.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This class uses CDI to alias Java EE resources, such as the persistence
 * context, to CDI beans
 * 
 * <p>
 * Example injection on a managed bean field:
 * </p>
 * 
 * <pre>
 * &#064;Inject
 * private EntityManager em;
 * </pre>
 */
@RequestScoped
public class Resources {
	// use @SuppressWarnings to tell IDE to ignore warnings about field not
	// being referenced directly
	@SuppressWarnings("unused")
	@Produces
	@PersistenceContext
	private EntityManager em;
}
