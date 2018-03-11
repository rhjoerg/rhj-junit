package ch.rhj.junit.gradle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
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
	
	private static final String RUNNER_KEY = "runner";
	private static final String PROJECT_KEY = "project";
	private static final String DIRECTORY_KEY = "directory";

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		return ParameterSupport.supports(parameterContext, Gradle.class,
				GradleRunner.class, Project.class, Path.class, File.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		
		Class<?> parameterType = parameterContext.getParameter().getType();
		
		if (GradleRunner.class.equals(parameterType))
			return getOrCreateRunner(extensionContext);
		
		if (Project.class.equals(parameterType))
			return getOrCreateProject(extensionContext);
		
		if (Path.class.equals(parameterType))
			return getOrCreateDirectory(extensionContext);
		
		if (File.class.equals(parameterType))
			return getOrCreateDirectory(extensionContext).toFile();
		
		throw new IllegalStateException();
	}
	
	protected GradleRunner getOrCreateRunner(ExtensionContext context) {
		
		return getOrCreateObject(context, RUNNER_KEY, GradleRunner.class, this::createGradleRunner);
	}
	
	protected GradleRunner createGradleRunner(ExtensionContext context) {
		
		Path directory = getOrCreateDirectory(context);
		
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
	
	protected Project getOrCreateProject(ExtensionContext context) {
		
		return getOrCreateObject(context, PROJECT_KEY, Project.class, this::createProject);
	}
	
	protected Project createProject(ExtensionContext context) {
		
		String name = context.getRequiredTestClass().getSimpleName() + "_" + context.getRequiredTestMethod().getName();
		File directory = getOrCreateDirectory(context).toFile();
		
		return ProjectBuilder.builder()
			.withName(name)
			.withProjectDir(directory)
			.build();
	}
	
	protected Path getOrCreateDirectory(ExtensionContext context) {
		
		return getOrCreateObject(context, DIRECTORY_KEY, Path.class, this::createDirectory);
	}
	
	protected Path createDirectory(ExtensionContext context) {
		
		Path directory = DirectorySupport.of(Paths.get("build", "test"), context).toAbsolutePath();
		
		try {
			
			return Files.createDirectories(directory);
			
		} catch (IOException e) {

			throw new ParameterResolutionException("cannot create directory '" + directory + "'", e);
		}
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {

		Path directory = getOrCreateDirectory(context);
		
		DirectorySupport.delete(directory);
		Files.createDirectories(directory);
	}
}
