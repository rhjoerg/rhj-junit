package ch.rhj.junit.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import ch.rhj.junit.resource.Resource;
import ch.rhj.junit.resource.WithResources;
import ch.rhj.junit.testdir.TestDirectory;

@WithResources
@WithGradle
public class GradleExtensionTests {

	@Test
	public void help(
			@Gradle GradleRunner runner,
			@TestDirectory Path directory,
			@Resource("GradleExtensionTests.build.gradle") byte[] buildGradle,
			@Resource("GradleExtensionTests.settings.gradle") byte[] settingsGradle)
		throws Exception {
		
		Files.write(directory.resolve("build.gradle"), buildGradle);
		Files.write(directory.resolve("settings.gradle"), settingsGradle);
		
		BuildResult result = runner.withArguments("help").forwardOutput().build();
		
		assertEquals(TaskOutcome.SUCCESS, result.task(":help").getOutcome());
	}
}
