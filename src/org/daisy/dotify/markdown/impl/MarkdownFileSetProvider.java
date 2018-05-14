package org.daisy.dotify.markdown.impl;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.media.DefaultFileSet;
import org.daisy.streamline.api.media.FileDetails;
import org.daisy.streamline.api.media.FileSet;
import org.daisy.streamline.api.media.FileSetException;
import org.daisy.streamline.api.media.FileSetProvider;

import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

/**
 * Provides a markdown identifier factory.
 * @author Joel Håkansson
 */
public class MarkdownFileSetProvider implements FileSetProvider {

	@Override
	public boolean accepts(FileDetails type) {
		return type.getFormatName()!=null && type.getFormatName().equalsIgnoreCase("markdown") || 
				type.getMediaType()!=null && type.getMediaType().equalsIgnoreCase("text/markdown") ||
				type.getExtension()!=null && type.getExtension().equalsIgnoreCase("md");
	}

	@Override
	public FileSet create(AnnotatedFile f, Map<String, Object> params) throws FileSetException {
		MarkdownOptions markdownOpts = MarkdownOptions.make(params);
		MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, markdownOpts.getConfig());
		Parser parser = Parser.builder(options).build();
		try {
			byte[] data = Files.readAllBytes(f.getPath());
			Document d = parser.parse(new String(data, markdownOpts.getEncoding()));
			ResourceRetriever v = new ResourceRetriever();
			List<String> refs = v.getRefs(d);
			List<AnnotatedFile> resources = new ArrayList<>();
			for (String ref : refs) {
				Path res = f.getPath().getParent().resolve(ref);
				if (res.toFile().exists()) {
					resources.add(DefaultAnnotatedFile.create(res));
				}
			}
			return new DefaultFileSet.Builder(f, resources).build();
		} catch (IOException e) {
			throw new FileSetException(e);
		}
	}

}
