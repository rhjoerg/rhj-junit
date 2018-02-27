package ch.rhj.junit.tempdir;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

public class TemporaryDirectoryExtension implements ParameterResolver, AfterEachCallback {

	private static final String KEY = "temporaryDirectory";
	
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {

		Parameter parameter = parameterContext.getParameter();
		
		return AnnotationSupport.isAnnotated(parameter, TemporaryDirectory.class)
				&& Path.class.equals(parameter.getType());
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {

		return getStore(extensionContext).getOrComputeIfAbsent(KEY, key -> createTemporaryDirectory(extensionContext));
	}
	
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		
		Path path = (Path) getStore(context).get(KEY);
		
		if (path != null)
			deleteTemporaryDirectory(path);
	}
	
	private Path createTemporaryDirectory(ExtensionContext context) {
		
		try {
			
			return Files.createTempDirectory("tmp-");
			
		} catch (IOException e) {
			
			throw new ParameterResolutionException("Could not create temporary directory", e);
		}
	}
	
	private void deleteTemporaryDirectory(Path path) throws IOException {
		
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				
				return deleteAndContinue(path);
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

				return deleteAndContinue(path);
			}

			private FileVisitResult deleteAndContinue(Path path) throws IOException {
				
				Files.delete(path);
				
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	private Namespace getNamespace(ExtensionContext context) {
		
		return Namespace.create(TemporaryDirectoryExtension.class, context);
	}
	
	private Store getStore(ExtensionContext context) {
		
		return context.getStore(getNamespace(context));
	}
}
