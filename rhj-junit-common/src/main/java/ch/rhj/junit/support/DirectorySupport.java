package ch.rhj.junit.support;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.jupiter.api.extension.ExtensionContext;

public class DirectorySupport {
	
	public static Path of(Path start, ExtensionContext context) {
		
		return start
			.resolve(context.getRequiredTestClass().getSimpleName())
			.resolve(context.getRequiredTestMethod().getName());
	}

	public static void delete(Path path) throws IOException {
		
		if (Files.notExists(path, NOFOLLOW_LINKS))
			return;
		
		if (Files.isRegularFile(path, NOFOLLOW_LINKS)) {
			
			Files.delete(path);
			return;
		}
		
		Files.walkFileTree(path, new SimpleFileVisitor<Path> () {
			
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
