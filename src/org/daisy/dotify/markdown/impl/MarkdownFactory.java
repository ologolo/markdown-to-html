package org.daisy.dotify.markdown.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskGroupFactory;
import org.daisy.streamline.api.tasks.TaskGroupInformation;
import org.daisy.streamline.api.tasks.TaskGroupSpecification;

public class MarkdownFactory implements TaskGroupFactory {
	private static final Logger LOGGER = Logger.getLogger(MarkdownFactory.class.getCanonicalName());
	private final Set<TaskGroupInformation> information;
	
	public MarkdownFactory() {
		Set<TaskGroupInformation> tmp = new HashSet<>();
		tmp.add(TaskGroupInformation.newConvertBuilder("md", "html").build());
		tmp.add(TaskGroupInformation.newConvertBuilder("md", "xhtml").build());
		tmp.add(TaskGroupInformation.newConvertBuilder("markdown", "html").build());
		tmp.add(TaskGroupInformation.newConvertBuilder("markdown", "xhtml").build());
		information = Collections.unmodifiableSet(tmp);
	}

	@Override
	public boolean supportsSpecification(TaskGroupInformation specification) {
		return information.contains(specification);
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification specification) {
		if ("md".equalsIgnoreCase(specification.getInputType().getIdentifier())) {
			LOGGER.log(Level.WARNING, "Format identifier \"md\" is deprecated, use \"markdown\" instead.");
		}
		return new MarkdownGroup();
	}

	@Override
	public Set<TaskGroupInformation> listAll() {
		return information;
	}

}
