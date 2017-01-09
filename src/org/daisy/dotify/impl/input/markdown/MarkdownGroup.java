package org.daisy.dotify.impl.input.markdown;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.tasks.InternalTask;
import org.daisy.dotify.api.tasks.TaskGroup;
import org.daisy.dotify.api.tasks.TaskOption;
import org.daisy.dotify.api.tasks.TaskSystemException;

public class MarkdownGroup implements TaskGroup {

	@Override
	public List<InternalTask> compile(Map<String, Object> parameters)
			throws TaskSystemException {
		List<InternalTask> ret = new ArrayList<>();
		ret.add(new MarkdownTask(parameters));
		return ret;
	}

}