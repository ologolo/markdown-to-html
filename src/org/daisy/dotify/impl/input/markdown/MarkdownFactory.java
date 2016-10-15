package org.daisy.dotify.impl.input.markdown;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.daisy.dotify.api.tasks.TaskGroup;
import org.daisy.dotify.api.tasks.TaskGroupFactory;
import org.daisy.dotify.api.tasks.TaskGroupSpecification;

public class MarkdownFactory implements TaskGroupFactory {
	private final Set<TaskGroupSpecification> supportedSpecifications;
	
	public MarkdownFactory() {
		this.supportedSpecifications = new HashSet<>();
		for (Locale l : Locale.getAvailableLocales()) {
			supportedSpecifications.add(new TaskGroupSpecification("md", "html", l.toLanguageTag()));
		}
	}

	@Override
	public boolean supportsSpecification(TaskGroupSpecification specification) {
		return supportedSpecifications.contains(specification);
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification specification) {
		return new MarkdownGroup();
	}

	@Override
	public Set<TaskGroupSpecification> listSupportedSpecifications() {
		return Collections.unmodifiableSet(supportedSpecifications);
	}

	@Override
	public void setCreatedWithSPI() {
	}

}
