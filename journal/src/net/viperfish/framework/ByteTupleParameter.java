package net.viperfish.framework;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

public final class ByteTupleParameter {

	private byte[] first;
	private byte[] second;
	private byte[] third;

	public ByteTupleParameter(byte[] first, byte[] second, byte[] third) {
		super();
		this.first = first.clone();
		this.second = second.clone();
		this.third = third.clone();
	}

	public byte[] getFirst() {
		return first.clone();
	}

	public byte[] getSecond() {
		return second.clone();
	}

	public byte[] getThird() {
		return third.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(first);
		result = prime * result + Arrays.hashCode(second);
		result = prime * result + Arrays.hashCode(third);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ByteTupleParameter other = (ByteTupleParameter) obj;
		if (!Arrays.equals(first, other.first))
			return false;
		if (!Arrays.equals(second, other.second))
			return false;
		if (!Arrays.equals(third, other.third))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Base64.encodeBase64String(first)).append(";").append(Base64.encodeBase64String(second))
				.append(";").append(Base64.encodeBase64String(third));
		return builder.toString();
	}

	public static ByteTupleParameter valueOf(String data) {
		String[] parts = data.split(";");
		if (parts.length != 3) {
			throw new IllegalArgumentException();
		}

		byte[] first = Base64.decodeBase64(parts[0]);
		byte[] second = Base64.decodeBase64(parts[1]);
		byte[] third = Base64.decodeBase64(parts[2]);
		return new ByteTupleParameter(first, second, third);
	}

}
