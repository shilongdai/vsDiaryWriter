package net.viperfish.utils.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectSerializer<T> {

	private Class<? extends T> caster;

	public ObjectSerializer(Class<? extends T> c) {
		this.caster = c;
	}

	public byte[] serialize(T o) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ObjectOutputStream serial = new ObjectOutputStream(out)) {
			serial.writeObject(o);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toByteArray();
	}

	public T deSerilize(byte[] serialized) {
		ByteArrayInputStream in = new ByteArrayInputStream(serialized);
		try (ObjectInputStream deSerial = new ObjectInputStream(in)) {
			return caster.cast(deSerial.readObject());
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
