package nl.first8.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Amount implements Comparable<Amount> {

	@Column(name = "value", nullable = false)
	private Long value;

	@Column(name = "currency", nullable = false, length = 3)
	private String currency;

	public Amount() {
	}

	public Amount(Long value, String currency) {
		this.value = value;
		this.currency = currency;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public int compareTo(Amount o) {
		return value.compareTo(o.value);
	}

	@Override
	public String toString() {
		return getCurrency() + getValue();
	}
}
