package org.daisy.dotify.markdown.impl;


import org.daisy.streamline.api.identity.Identifier;
import org.daisy.streamline.api.identity.IdentifierFactory;
import org.daisy.streamline.api.media.FileDetails;

/**
 * Provides a markdown identifier factory.
 * @author Joel Håkansson
 */
public class MarkdownIdentifierFactory implements IdentifierFactory {

	@Override
	public Identifier newIdentifier() {
		return new MarkdownIdentifier();
	}

	@Override
	public boolean accepts(FileDetails type) {
		return type.getFormatName()==null || type.getMediaType()==null;
	}

}
