package nl.first8.services;

import nl.first8.model.Item;


public class ItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final String imageUrl;

    public ItemDto(Item item) {
    	this(item.getId(), item.getName(), item.getDescription(), item.getImageUrl());
    }
    
    public ItemDto( Long id, String name, String description, String imageUrl) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
    }

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getImageUrl() {
		return imageUrl;
	}
    
}
