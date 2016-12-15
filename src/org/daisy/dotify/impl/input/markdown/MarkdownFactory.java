package org.daisy.dotify.impl.input.markdown;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.daisy.dotify.api.tasks.TaskGroup;
import org.daisy.dotify.api.tasks.TaskGroupFactory;
import org.daisy.dotify.api.tasks.TaskGroupInformation;
import org.daisy.dotify.api.tasks.TaskGroupSpecification;

public class MarkdownFactory implements TaskGroupFactory {
	private final Set<TaskGroupSpecification> supportedSpecifications;
	private final Set<TaskGroupInformation> information;
	
	public MarkdownFactory() {
		this.supportedSpecifications = new HashSet<>();
		for (Locale l : Locale.getAvailableLocales()) {
			supportedSpecifications.add(new TaskGroupSpecification("md", "html", l.toLanguageTag()));
		}
		Set<TaskGroupInformation> tmp = new HashSet<>();
		tmp.add(TaskGroupInformation.newConvertBuilder("md", "html").build());
		information = Collections.unmodifiableSet(tmp);
	}

	@Override
	public boolean supportsSpecification(TaskGroupSpecification specification) {
		//TODO: move this to default implementation after move to java 8
		for (TaskGroupInformation i : listAll()) {
			if (specification.matches(i)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification specification) {
		return new MarkdownGroup();
	}

	@Override
	@Deprecated
	public Set<TaskGroupSpecification> listSupportedSpecifications() {
		return Collections.unmodifiableSet(supportedSpecifications);
	}

	@Override
	public void setCreatedWithSPI() {
	}

	@Override
	public Set<TaskGroupInformation> listAll() {
		return information;
	}

	@Override
	public Set<TaskGroupInformation> list(String locale) {
		//TODO: move this to default implementation after move to java 8 (and use streams)
		Objects.requireNonNull(locale);
		Set<TaskGroupInformation> ret = new HashSet<>();
		for (TaskGroupInformation info : listAll()) {
			if (info.matchesLocale(locale)) {
				ret.add(info.newCopyBuilder().locale(locale).build());
			}
		}
		return ret;
	}

}
