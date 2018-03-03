package ch.rhj.junit.resource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

@WithResources
public class ResourcesTest {
	
	private static final String RESOURCE = "ResourcesTest.txt";
	private static final String EXPECTED = "Hello, world!";

	@Test
	public void buffer(@Resource(RESOURCE) ByteBuffer buffer) {
		
		String actual = new String(buffer.array(), UTF_8);
		
		assertEquals(EXPECTED, actual);
	}
	
	@Test
	public void bytes(@Resource(RESOURCE) byte[] bytes) {
		
		String actual = new String(bytes, UTF_8);
		
		assertEquals(EXPECTED, actual);
	}
	
	@Test
	public void string(@Resource(RESOURCE) String actual) {
		
		assertEquals(EXPECTED, actual);
	}
}
