package org.daisy.dotify.markdown.impl;

import java.util.Locale;

import org.daisy.streamline.api.identity.IdentificationFailedException;
import org.daisy.streamline.api.identity.Identifier;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;

/**
 * Provides a markdown identifier.
 * @author Joel Håkansson
 *
 */
public class MarkdownIdentifier implements Identifier {
	private static final String MIME = "text/markdown";
	private static final String NAME = "markdown";

	@Override
	public AnnotatedFile identify(AnnotatedFile f) throws IdentificationFailedException {
		if (f.getMediaType()!=null && !f.getMediaType().equalsIgnoreCase(MIME) || f.getFormatName()!=null && !f.getFormatName().equalsIgnoreCase(NAME)) {
			throw new IdentificationFailedException();
		}
		String name = f.getPath().getFileName().toString().toLowerCase(Locale.ROOT);
		if (name.endsWith(".md")) {
			return newAnnotatedFile(f, "md"); 
		} else if (name.endsWith(".markdown")) {
			return newAnnotatedFile(f, "markdown"); 
		}else {
			throw new IdentificationFailedException();
		}
	}
	
	private static AnnotatedFile newAnnotatedFile(AnnotatedFile f, String ext) {
		return new DefaultAnnotatedFile.Builder(f.getPath())
			.formatName(NAME)
			.mediaType(MIME)
			.extension(ext)
			.build();
	}

}
