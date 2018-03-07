package ch.rhj.junit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

public class PathsTests {

	@Test
	public void of() {
		
		Path path = Paths.of("hello", "world", "or", "so");
		
		assertEquals(4, StreamSupport.stream(path.spliterator(), false).count());
	}
	
	@Test
	public void isDirectory() {
		
		Path path = Paths.of("build", "test", "PathsTests", "isDirectory");
		
		Paths.mkdirs(path);
		assertTrue(Paths.isDirectory(path));
	}
	
	@Test
	public void delete() throws Exception {
		
		Path folder = Paths.of("build", "test", "PathsTests", "delete");
		Path file = folder.resolve("file");
		byte[] bytes = { 1, 2, 3 };
		
		Paths.mkdirs(folder);
		Paths.delete(file);
		assertFalse(Paths.exists(file));
		
		Files.write(file, bytes);
		assertTrue(Paths.exists(file));
		Paths.delete(file);
		assertFalse(Paths.exists(file));
		
		Files.write(file, bytes);
		assertTrue(Paths.exists(file));
		Paths.delete(folder);
		assertFalse(Paths.exists(folder));
	}
}
