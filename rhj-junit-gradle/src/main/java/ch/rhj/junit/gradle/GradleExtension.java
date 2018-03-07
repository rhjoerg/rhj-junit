package ch.rhj.junit.gradle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import ch.rhj.junit.AbstractExtension;
import ch.rhj.junit.support.DirectorySupport;
import ch.rhj.junit.support.ParameterSupport;
import ch.rhj.junit.support.ResourcesSupport;

public class GradleExtension extends AbstractExtension implements ParameterResolver, BeforeEachCallback {
	
	private static final String GRADLE_RUNNER_KEY = "gradleRunner";
	private static final String WORKING_DIRECTORY_KEY = "workingDirectory";

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		return ParameterSupport.supports(parameterContext, Gradle.class,
				GradleRunner.class, Path.class, File.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		Class<?> parameterType = parameterContext.getParameter().getType();
		
		if (GradleRunner.class.equals(parameterType))
			return getOrCreateGradleRunner(extensionContext);
		
		if (Path.class.equals(parameterType))
			return getOrCreateWorkingDirectory(extensionContext);
		
		if (File.class.equals(parameterType))
			return getOrCreateWorkingDirectory(extensionContext).toFile();
		
		throw new IllegalStateException();
	}
	
	protected GradleRunner getOrCreateGradleRunner(ExtensionContext context) {
		
		return getOrCreateObject(context, GRADLE_RUNNER_KEY, GradleRunner.class, this::createGradleRunner);
	}
	
	protected GradleRunner createGradleRunner(ExtensionContext context) {
		
		Path directory = getOrCreateWorkingDirectory(context);
		
		try {
			
			copyGradleFiles(context, directory);
			
		} catch (IOException e) {

			throw new ParameterResolutionException("cannot copy gradle files", e);
		}
		
		return GradleRunner.create().withProjectDir(directory.toFile());
	}
	
	protected void copyGradleFiles(ExtensionContext context, Path directory) throws IOException {
		
		URL settingsUrl = ResourcesSupport.resource(context, "settings.gradle");
		
		if (settingsUrl != null)
			Files.write(directory.resolve("settings.gradle"), ResourcesSupport.bytes(settingsUrl));
		
		URL buildUrl = ResourcesSupport.resource(context, "build.gradle");
		
		if (buildUrl != null) {
			
			Files.write(directory.resolve("build.gradle"), ResourcesSupport.bytes(buildUrl));
		}
	}
	
	protected Path getOrCreateWorkingDirectory(ExtensionContext context) {
		
		return getOrCreateObject(context, WORKING_DIRECTORY_KEY, Path.class, this::createWorkingDirectory);
	}
	
	protected Path createWorkingDirectory(ExtensionContext context) {
		
		Path directory = DirectorySupport.of(Paths.get("build", "test"), context);
		
		try {
			
			return Files.createDirectories(directory);
			
		} catch (IOException e) {

			throw new ParameterResolutionException("cannot create directory '" + directory + "'", e);
		}
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {

		Path directory = getOrCreateWorkingDirectory(context);
		
		DirectorySupport.delete(directory);
		Files.createDirectories(directory);
	}
}
