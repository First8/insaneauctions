package nl.first8.services;

import java.util.Date;

public class RegistrationResultDto extends EventDto {
	
	private final boolean existing;
	private final Date dateLoggedIn;

	public RegistrationResultDto(boolean existing, Date dateLoggedIn) {
		super("register");
		this.existing = existing;
		this.dateLoggedIn = dateLoggedIn;
	}

	public boolean isExisting() {
		return existing;
	}

	public Date getDateLoggedIn() {
		return dateLoggedIn;
	}
}
