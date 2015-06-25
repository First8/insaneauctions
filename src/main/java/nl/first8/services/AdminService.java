package nl.first8.services;

import nl.first8.data.AuctionRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class AdminService {
	@Inject
	private AuctionRepository auctionRepository;

	public List<AdminAuctionDto> list() {
		return auctionRepository.getAllAuctions().stream()
				.map(a -> new AdminAuctionDto(a, auctionRepository.getBidsSortedById(a))).collect(Collectors.toList());
	}

	public String listAsHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");

		sb.append("<ul>");
		for (AdminAuctionDto a : list()) {
			sb.append("<li>");
			append(sb, a);
			sb.append("</li>");
		}
		sb.append("</ul>");
		sb.append("</body></html>");

		return sb.toString();
	}

	private void append(StringBuilder sb, AdminAuctionDto a) {
		sb.append("<b>Auction " + a.getId() + "</b> for item " + a.getItem().getId() + " ('" + a.getItem().getName()
				+ "')");
		sb.append("<br>created: " + a.getDateCreated());
		sb.append("<br>end:     " + a.getDateEnd());
		sb.append("<br>closed:  " + a.getDateClosed());

		if (a.getDateEnd() != null && a.getDateClosed() != null) {
			long diff = (a.getDateEnd().getTime() - a.getDateClosed().getTime());
			if (Math.abs(diff) > 1000) {
				sb.append("<span style='color:red'> End and closed date are too far off: " + diff + "ms</span>");
			}
		}

		BidDto previousBid = null;
		sb.append("<ol>");
		for (BidDto b : a.getBids()) {
			sb.append("<li>");
			append(sb, a, b, previousBid);
			previousBid = b;
			sb.append("</li>");
		}
		sb.append("</ol>");

	}

	private void append(StringBuilder sb, AdminAuctionDto a, BidDto b, BidDto previousBid) {
		sb.append("<b>Bid " + b.getId() + "</b> (" + b.getBidderName() + ")");
		sb.append("<br><b>" + b.getAmount() + "</b> " + b.getDateCreated());
		if (a.getDateClosed() != null && b.getDateCreated().compareTo(a.getDateClosed()) > 0) {
			sb.append("<span style='color:red'>Bid date created > auction date closed</span>");
		}
		if (previousBid != null && previousBid.getAmount().compareTo(b.getAmount()) < 0) {
			sb.append("<span style='color:red'>Previous bid had lower amount</span>");
		}
	}
}
