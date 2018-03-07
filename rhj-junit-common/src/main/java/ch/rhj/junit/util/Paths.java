package ch.rhj.junit.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Paths {
	
	public static Path of(String first, String... more) {
		
		return java.nio.file.Paths.get(first, more);
	}

	public static boolean exists(Path path) {
		
		return Files.exists(path);
	}
	
	public static boolean isDirectory(Path path) {
		
		return Files.isDirectory(path);
	}
	
	public static boolean isFile(Path path) {
		
		return Files.isRegularFile(path);
	}
	
	public static boolean mkdirs(Path path) {
		
		return path.toFile().mkdirs();
	}
	
	public static void delete(Path path) throws IOException {
		
		if (!exists(path))
			return;
		
		if (isFile(path)) {
			
			Files.delete(path);
			return;
		}
		
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {


			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				
				return deleteAndContinue(file);
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

				return deleteAndContinue(dir);
			}

			private FileVisitResult deleteAndContinue(Path path) throws IOException {
				
				Files.delete(path);
				
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
