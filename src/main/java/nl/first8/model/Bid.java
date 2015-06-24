package nl.first8.model;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity implementation class for Entity: Bid
 *
 */
@Entity
@SequenceGenerator(name="bid_id_seq", sequenceName="bid_id_seq", allocationSize=1)
public class Bid implements Serializable {

    @Id
    @Column(name="id", columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bid_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auction_id", nullable=false)
    private Auction auction;

    @Column(name="date_created", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Embedded
    private Amount amount;

    @Column(name="bidder_name")
    private String bidderName;

    private static final long serialVersionUID = 1L;

    public Bid() {
    }

    public Bid(Auction auction, Amount amount, String bidderName) {
    	this.auction = auction;
    	this.amount = amount;
    	this.bidderName = bidderName;
    	this.dateCreated = new Date();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Auction getAuction() {
        return this.auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getBidderName() {
        return this.bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }
    
}
