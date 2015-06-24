package nl.first8.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.first8.services.AuctionDto;
import nl.first8.services.AuctionService;
import nl.first8.services.BidDto;
import nl.first8.services.BidResultDto;
import nl.first8.services.MakeBidDto;

@Path("/auction")
@RequestScoped
public class AuctionEndpoint {
	@Inject
	private AuctionService auctionService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public AuctionDto getActiveAuction() {
		return auctionService.getActiveAuction();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public BidResultDto bid(@FormParam("value") Long value) {
		return bid(new MakeBidDto(value));
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BidResultDto bid(MakeBidDto makeBid) {
		return auctionService.bid(makeBid);
	}

	@GET
	@Path("/bids")
	@Produces(MediaType.APPLICATION_JSON)
	public List<BidDto> getBids() {
		return auctionService.getBids();
	}
}
