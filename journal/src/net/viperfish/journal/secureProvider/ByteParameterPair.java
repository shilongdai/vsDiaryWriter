package net.viperfish.journal.secureProvider;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

final class ByteParameterPair {
	private final byte[] first;
	private final byte[] second;

	ByteParameterPair(byte[] first, byte[] second) {
		this.first = Arrays.copyOf(first, first.length);
		this.second = Arrays.copyOf(second, second.length);
	}

	public byte[] getFirst() {
		return first.clone();
	}

	public byte[] getSecond() {
		return second.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(first);
		result = prime * result + Arrays.hashCode(second);
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
		ByteParameterPair other = (ByteParameterPair) obj;
		if (!Arrays.equals(first, other.first))
			return false;
		if (!Arrays.equals(second, other.second))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String firstString = Base64.encodeBase64String(first);
		String secondString = Base64.encodeBase64String(second);
		return firstString + "&" + secondString;
	}

	static ByteParameterPair valueOf(String data) {
		String[] pair = data.split("&");
		if (pair.length != 2) {
			throw new IllegalArgumentException(
					"There must be 2 segments in the data seperated by &, got " + pair.length + " segment");
		}
		byte[] first = Base64.decodeBase64(pair[0]);
		byte[] second = Base64.decodeBase64(pair[1]);
		return new ByteParameterPair(first, second);
	}

}
