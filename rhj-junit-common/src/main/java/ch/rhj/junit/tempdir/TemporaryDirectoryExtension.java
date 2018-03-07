package ch.rhj.junit.tempdir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import ch.rhj.junit.AbstractExtension;
import ch.rhj.junit.support.DirectorySupport;
import ch.rhj.junit.support.ParameterSupport;

public class TemporaryDirectoryExtension extends AbstractExtension implements ParameterResolver, AfterEachCallback {

	private static final String KEY = "temporaryDirectory";
	
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		return ParameterSupport.supports(parameterContext, TemporaryDirectory.class, Path.class, File.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		Path directory = getOrCreateDirectory(extensionContext);
		Class<?> parameterType = parameterContext.getParameter().getType();
		
		if (Path.class.equals(parameterType))
			return directory;
		
		if (File.class.equals(parameterType))
			return directory.toFile();
		
		throw new IllegalStateException();
	}
	
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		
		Path path = getObject(context, KEY, Path.class);
		
		if (path != null)
			DirectorySupport.delete(path);
	}
	
	protected Path getOrCreateDirectory(ExtensionContext context) {
		
		return getOrCreateObject(context, KEY, Path.class, this::createDirectory);
	}
	
	private Path createDirectory(ExtensionContext context) {
		
		try {
			
			return Files.createTempDirectory("tmp-");
			
		} catch (IOException e) {
			
			throw new ParameterResolutionException("Could not create temporary directory", e);
		}
	}
}
