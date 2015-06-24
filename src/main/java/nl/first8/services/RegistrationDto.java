package nl.first8.services;

import nl.first8.services.EventDto;

import java.util.Date;

public class RegistrationDto extends EventDto {
	private final String nickname;
	private final Date dateLoggedIn;
	private final boolean loggedIn;
	
	public RegistrationDto(String nickname, Date dateLoggedIn) {
		super("registered");
		this.nickname = nickname;
		this.dateLoggedIn = dateLoggedIn;
		this.loggedIn = true;
	}

	public RegistrationDto() {
		super("registered");
		this.nickname = null;
		this.dateLoggedIn = null;
		this.loggedIn = false;
	}

	public String getNickname() {
		return nickname;
	}

	public Date getDateLoggedIn() {
		return dateLoggedIn;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

}
