package nl.first8.services;


public class MakeBidDto {
	public static final String DEFAULT_CURRENCY = "EUR";
	private final long value;
	private final String currency = DEFAULT_CURRENCY;
	
	public MakeBidDto(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public String getCurrency() {
		return currency;
	}
	
}
