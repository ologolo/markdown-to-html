package org.daisy.dotify.markdown.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;

class MarkdownOptions {
	static final String SOURCE_ENCODING = "source-encoding";
	static final String SOURCE_LANGUAGE = "source-language";
	static final String AUTO_LINKS = "auto-link";
	static final String STRIKETHROUGH = "github-strikethrough";
	static final String TABLES = "github-tables";
	static final String TASK_LISTS = "github-tasks";
	static final String DEFAULT_ENCODING = "utf-8";
	static final String DEFAULT_LANGUAGE = Locale.getDefault().toLanguageTag();
	private static final String TRUE = "true";

	private final String language;
	private final String encoding;
	private final List<Extension> config;
	
	private MarkdownOptions(Map<String, Object> params) {
		this.language = getLanguage(params);
		this.encoding = getEncoding(params);
		config = new ArrayList<>();
		if (TRUE.equals(params.get(TABLES))) {
			getConfig().add(TablesExtension.create());
		}
		if (TRUE.equals(params.get(STRIKETHROUGH))) {
			getConfig().add(StrikethroughExtension.create());
		}
		if (TRUE.equals(params.get(TASK_LISTS))) {
			getConfig().add(TaskListExtension.create());
		}
		if (TRUE.equals(params.get(AUTO_LINKS))) {
			getConfig().add(AutolinkExtension.create());
		}

	}
	
	private static String getEncoding(Map<String, Object> params) {
		Object param = params.get(SOURCE_ENCODING);
		return (param!=null)?""+param:DEFAULT_ENCODING;
	}
	
	private static String getLanguage(Map<String, Object> params) {
		Object param = params.get(SOURCE_LANGUAGE);
		return (param!=null)?""+param:DEFAULT_LANGUAGE;
	}

	static MarkdownOptions make(Map<String, Object> params) {
		return new MarkdownOptions(params);
	}

	public String getLanguage() {
		return language;
	}

	public String getEncoding() {
		return encoding;
	}

	public List<Extension> getConfig() {
		return config;
	}
}
