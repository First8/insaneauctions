package nl.first8.rest;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.first8.services.RegistrationDto;
import nl.first8.services.RegistrationResultDto;
import nl.first8.services.RegistrationService;

@Path("/register")
@RequestScoped
public class RegistrationEndpoint {
	private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    @Inject
    private RegistrationService registrationService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public RegistrationResultDto lookupMemberById(@NotNull @FormParam("nickname") String nickname) {
        return registrationService.lookupMemberById(nickname);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RegistrationDto currentLogin() {
        return registrationService.currentLogin();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public RegistrationDto logout() {
        return registrationService.logout();
    }
}
