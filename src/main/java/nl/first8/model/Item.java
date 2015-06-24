package nl.first8.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: Item
 *
 */
@Entity
@SequenceGenerator(name = "item_id_seq", sequenceName = "item_id_seq", allocationSize = 1)
public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", columnDefinition = "SERIAL")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_id_seq")
	private Long id;

	@NotNull
	private String name;

	@NotNull
	private String description;

	@NotNull
	@Column(name = "image_url")
	private String imageUrl;

	@NotNull
	@Column(name = "auction_time")
	private Long auctionTime;

	@OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
	private List<Auction> auctions;

	public Item() {

	}

	public Item(String name, String description, String imageUrl, long auctionTime) {
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
		this.auctionTime = auctionTime;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void addAuction(Auction auction) {
		auction.setItem(this);
		getAuctions().add(auction);
	}

	public List<Auction> getAuctions() {
		return auctions;
	}

	public void setAuctions(List<Auction> auctions) {
		this.auctions = auctions;
	}

	public Long getAuctionTime() {
		return auctionTime;
	}

	public void setAuctionTime(Long auctionTime) {
		this.auctionTime = auctionTime;
	}

}
