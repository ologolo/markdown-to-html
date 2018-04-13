package org.daisy.dotify.markdown.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskGroupFactory;
import org.daisy.streamline.api.tasks.TaskGroupInformation;
import org.daisy.streamline.api.tasks.TaskGroupSpecification;

public class MarkdownFactory implements TaskGroupFactory {
	private final Set<TaskGroupInformation> information;
	
	public MarkdownFactory() {
		Set<TaskGroupInformation> tmp = new HashSet<>();
		tmp.add(TaskGroupInformation.newConvertBuilder("md", "html").build());
		tmp.add(TaskGroupInformation.newConvertBuilder("markdown", "html").build());
		information = Collections.unmodifiableSet(tmp);
	}

	@Override
	public boolean supportsSpecification(TaskGroupInformation specification) {
		return information.contains(specification);
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification specification) {
		return new MarkdownGroup();
	}

	@Override
	public Set<TaskGroupInformation> listAll() {
		return information;
	}

}
