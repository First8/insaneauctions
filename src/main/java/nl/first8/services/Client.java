package nl.first8.services;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class Client implements Serializable {
	private static final long serialVersionUID = 1L;

	private String nickname;
	private boolean isLoggedIn;
	private Date dateLoggedIn;

	public Client() {
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public Date getDateLoggedIn() {
		return dateLoggedIn;
	}

	public void setDateLoggedIn(Date dateLoggedIn) {
		this.dateLoggedIn = dateLoggedIn;
	}
}
