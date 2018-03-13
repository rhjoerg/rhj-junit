package ch.rhj.junit.testdir;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class TestDirectoryExtensionTests {

	@Test
	public void path(@TestDirectory Path directory) {
		
		assertTrue(Files.isDirectory(directory));
	}
	
	@Test
	public void file(@TestDirectory File directory) {
		
		assertTrue(directory.isDirectory());
	}
}
