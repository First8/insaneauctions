/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.first8.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import nl.first8.model.Auction;
import nl.first8.model.Bid;
import nl.first8.model.Item;

@ApplicationScoped
public class AuctionRepository {

	@Inject
	private EntityManager em;

	public Item findItemById(Long id) {
		return em.find(Item.class, id);
	}

	public Auction findAuctionById(long id) {
		return em.find(Auction.class, id);
	}

	public Auction findLatestAuction() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Auction> criteria = cb.createQuery(Auction.class);
		Root<Auction> auction = criteria.from(Auction.class);
		criteria.select(auction);
		criteria.orderBy(cb.desc(auction.get("id")));
		return em.createQuery(criteria).setMaxResults(1).getSingleResult();
	}

	public List<Item> getAllItems() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> criteria = cb.createQuery(Item.class);
		Root<Item> item = criteria.from(Item.class);
		criteria.select(item);
		return em.createQuery(criteria).getResultList();
	}

	public Bid findBidById(Long id) {
		return em.find(Bid.class, id);
	}

	public List<Bid> getAllBids() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Bid> criteria = cb.createQuery(Bid.class);
		Root<Bid> bid = criteria.from(Bid.class);
		criteria.select(bid);
		return em.createQuery(criteria).getResultList();
	}

	public List<Auction> getAllAuctions() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Auction> criteria = cb.createQuery(Auction.class);
		Root<Auction> auction = criteria.from(Auction.class);
		criteria.select(auction);
		criteria.orderBy(cb.desc(auction.get("id")));
		TypedQuery<Auction> query = em.createQuery(criteria);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<Bid> getBidsSortedById(Auction auction) {
		List<Bid> result = new ArrayList<>(auction.getBids());
		// we cannot use @OrderColumn here due to hibernate weirdness with
		// sequences
		Collections.sort(result, new Comparator<Bid>() {
			@Override
			public int compare(Bid o1, Bid o2) {
				return o2.getId().compareTo(o1.getId());
			}
		});
		return result;
	}

	public List<Bid> getBidsSortedByDate(Auction auction) {
		List<Bid> result = new ArrayList<>(auction.getBids());
		Collections.sort(result, new Comparator<Bid>() {
			@Override
			public int compare(Bid o1, Bid o2) {
				return o2.getDateCreated().compareTo(o1.getDateCreated());
			}
		});
		return result;
	}

	public Bid persist(Bid bid) {
		em.persist(bid);
		return bid;
	}

	public Auction persist(Auction auction) {
		em.persist(auction);
		return auction;
	}

}
