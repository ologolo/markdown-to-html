package org.daisy.dotify.markdown.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.streamline.api.tasks.InternalTask;
import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskSystemException;

public class MarkdownGroup implements TaskGroup {

	@Override
	public List<InternalTask> compile(Map<String, Object> parameters)
			throws TaskSystemException {
		List<InternalTask> ret = new ArrayList<>();
		ret.add(new MarkdownTask(parameters));
		return ret;
	}

}