package org.daisy.dotify.impl.input.markdown;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.option.UserOption;
import org.daisy.streamline.api.option.UserOptionValue;
import org.daisy.streamline.api.tasks.InternalTaskException;
import org.daisy.streamline.api.tasks.ReadWriteTask;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

public class MarkdownTask extends ReadWriteTask {
	private static final String SOURCE_ENCODING = "source-encoding";
	private static final String SOURCE_LANGUAGE = "source-language";
	private static final String AUTO_LINKS = "auto-link";
	private static final String STRIKETHROUGH = "github-strikethrough";
	private static final String TABLES = "github-tables";
	private static final String TASK_LISTS = "github-tasks";
	private static final String DEFAULT_ENCODING = "utf-8";
	private static final String DEFAULT_LANGUAGE = Locale.getDefault().toLanguageTag();
	private static final String TRUE = "true";
	private static List<UserOption> options = null;
	private final String language;
	private final String encoding;
	private final List<Extension> exts;
	
	public MarkdownTask(Map<String, Object> params) {
		super("Markdown to HTML");
		this.language = getLanguage(params);
		this.encoding = getEncoding(params);
		exts = new ArrayList<>();
		if (TRUE.equals(params.get(TABLES))) {
			exts.add(TablesExtension.create());
		}
		if (TRUE.equals(params.get(STRIKETHROUGH))) {
			exts.add(StrikethroughExtension.create());
		}
		if (TRUE.equals(params.get(TASK_LISTS))) {
			exts.add(TaskListExtension.create());
		}
		if (TRUE.equals(params.get(AUTO_LINKS))) {
			exts.add(AutolinkExtension.create());
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

	@Override
	public void execute(File input, File output) throws InternalTaskException {
		execute(new DefaultAnnotatedFile.Builder(input).build(), output);
	}

	@Override
	public AnnotatedFile execute(AnnotatedFile input, File output) throws InternalTaskException {
		MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, exts);
		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();
		try {
			byte[] data = Files.readAllBytes(input.getFile().toPath());
			String res = renderer.render(parser.parse(new String(data, encoding)));
			String outputEncoding = "utf-8";
			try (PrintWriter w = new PrintWriter(output, outputEncoding)) {
				w.println("<?xml version=\"1.0\" encoding=\""+outputEncoding+"\"?>");
				w.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\""+language+"\">");
				w.println("<head>");
				w.println("<meta charset=\""+outputEncoding+"\"/>");
				w.println("</head>");
				w.println("<body>");
				w.print(res);
				w.println("</body>");
				w.println("</html>");
			}
			return new DefaultAnnotatedFile.Builder(output).extension("html").mediaType("application/xhtml+xml").build();
		} catch (IOException e) {
			throw new InternalTaskException(e);
		}
	}
	
	private static synchronized List<UserOption> getOptionsInternal() {
		if (options==null) {
			options = new ArrayList<>();
			options.add(new UserOption.Builder(SOURCE_ENCODING).description("The encoding of the input file").defaultValue(DEFAULT_ENCODING).build());
			options.add(new UserOption.Builder(SOURCE_LANGUAGE).description("The language of the input file").defaultValue(DEFAULT_LANGUAGE).build());
			options.add(new UserOption.Builder(TABLES).description("Enables support for github tables").addValue(booleanTrue()).addValue(booleanFalse()).defaultValue("false").build());
			options.add(new UserOption.Builder(STRIKETHROUGH).description("Enables support for strikethrough (e.g. ~~strike~~)").addValue(booleanTrue()).addValue(booleanFalse()).defaultValue("false").build());
			options.add(new UserOption.Builder(TASK_LISTS).description("Enables support for github tasks").addValue(booleanTrue()).addValue(booleanFalse()).defaultValue("false").build());
			options.add(new UserOption.Builder(AUTO_LINKS).description("Enables auto links").addValue(booleanTrue()).addValue(booleanFalse()).defaultValue("false").build());
		}
		return options;		
	}
	
	private static UserOptionValue booleanTrue() {
		return new UserOptionValue.Builder("true").description("enable").build();
	}
	
	private static UserOptionValue booleanFalse() {
		return new UserOptionValue.Builder("false").description("disable").build();
	}

	@Override
	public List<UserOption> getOptions() {
		return getOptionsInternal();
	}

}
