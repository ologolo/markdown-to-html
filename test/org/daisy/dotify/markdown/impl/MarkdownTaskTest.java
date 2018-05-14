package org.daisy.dotify.markdown.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.streamline.api.media.BaseFolder;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.media.DefaultFileSet;
import org.daisy.streamline.api.media.FileSet;
import org.daisy.streamline.api.tasks.InternalTaskException;
import org.daisy.streamline.engine.PathTools;
import org.junit.Test;

public class MarkdownTaskTest {

	@Test
	public void test_01() throws IOException, InternalTaskException, URISyntaxException {
		Map<String, Object> params = new HashMap<>();
		params.put("source-language", "en");
		MarkdownTask mt = new MarkdownTask(params);
		File out = File.createTempFile("test", ".tmp");
		out.deleteOnExit();
		mt.execute(DefaultAnnotatedFile.with(Paths.get(this.getClass().getResource("resource-files/input.md").toURI())).build(), out);
		List<String> actual = Files.readAllLines(out.toPath());
		List<String> expected = Files.readAllLines(
						Paths.get(this.getClass().getResource("resource-files/expected.html").toURI()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_02() throws IOException, InternalTaskException, URISyntaxException {
		Map<String, Object> params = new HashMap<>();
		params.put("source-language", "en");
		MarkdownTask mt = new MarkdownTask(params);
		Path manifest = Paths.get(this.getClass().getResource("resource-files/input2.md").toURI());
		FileSet input = DefaultFileSet.with(
				BaseFolder.with(manifest.getParent()), 
				DefaultAnnotatedFile.with(manifest).build()
			).build();
		Path outFolder = Paths.get("build", "tmp", "test");
		try {
			Files.createDirectories(outFolder);
			BaseFolder output = BaseFolder.with(outFolder);
			FileSet fs = mt.execute(input, output);
			List<String> actual = Files.readAllLines(fs.getManifest().getPath());
			List<String> expected = Files.readAllLines(
							Paths.get(this.getClass().getResource("resource-files/expected2.html").toURI()));
			assertEquals(expected, actual);
		} finally {
			PathTools.deleteRecursive(outFolder);
		}
	}
}
