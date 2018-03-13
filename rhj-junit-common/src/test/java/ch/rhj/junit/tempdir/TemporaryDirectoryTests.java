package ch.rhj.junit.tempdir;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class TemporaryDirectoryTests {

	@Test
	public void hasTemporaryDirectory(@TemporaryDirectory Path path) {
		
		assertNotNull(path);
		assertTrue(path.toFile().isDirectory());
	}
}
