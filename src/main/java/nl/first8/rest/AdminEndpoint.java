package nl.first8.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.first8.services.AdminAuctionDto;
import nl.first8.services.AdminService;

@Path("/admin")
@RequestScoped
public class AdminEndpoint {
	@Inject
	private AdminService adminService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<AdminAuctionDto> list() {
		return adminService.list();
	}

	@GET
	@Path("/index.html")
	@Produces(MediaType.TEXT_HTML)
	public String listAsHtml() {
		return adminService.listAsHtml();
	}
}
