package ch.rhj.junit.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

@WithGradle
public class GradleExtensionTests {

	@Test
	public void help(@Gradle GradleRunner runner, @Gradle Path directory) throws Exception {
		
		assertTrue(Files.isRegularFile(directory.resolve("settings.gradle")));
		assertTrue(Files.isRegularFile(directory.resolve("build.gradle")));
		
		BuildResult result = runner.withArguments("help").forwardOutput().build();
		
		assertEquals(TaskOutcome.SUCCESS, result.task(":help").getOutcome());
	}
}
