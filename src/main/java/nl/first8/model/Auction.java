package nl.first8.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@SequenceGenerator(name = "auction_id_seq", sequenceName = "auction_id_seq", allocationSize = 1)
public class Auction {

	@Id
	@Column(name = "id", columnDefinition = "SERIAL")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_id_seq")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@OneToMany(mappedBy = "auction", fetch = FetchType.EAGER)
	private List<Bid> bids;

	@Column(name = "date_created", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;

	@Column(name = "date_end", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEnd;

	@Column(name = "date_closed", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateClosed;

	public Auction() {
		bids = new ArrayList<Bid>();
	}

	public Auction(Item item, Date dateStart, Date dateEnd) {
		this();
		item.addAuction(this);
		this.dateCreated = dateStart;
		this.dateEnd = dateEnd;
	}

	public void addBid(Bid bid) {
		bid.setAuction(this);
		getBids().add(bid);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public List<Bid> getBids() {
		if (bids == null) {
			bids = new ArrayList<>();
		}
		return bids;
	}

	public void setBids(List<Bid> bids) {
		this.bids = bids;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Date getDateClosed() {
		return dateClosed;
	}

	public void setDateClosed(Date dateClosed) {
		this.dateClosed = dateClosed;
	}

}
