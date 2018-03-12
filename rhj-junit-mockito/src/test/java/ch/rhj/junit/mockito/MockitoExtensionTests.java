package ch.rhj.junit.mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@WithMockito
public class MockitoExtensionTests {

	public static interface Foo {}
	
	@Mock
	public Foo foo;
	
	@Test
	public void test(@Mock Foo foo) {
		
		assertNotNull(foo);
		assertNotNull(this.foo);
	}
}
