package org.daisy.dotify.markdown.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.BaseFolder;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.media.DefaultFileSet;
import org.daisy.streamline.api.media.FileSet;
import org.daisy.streamline.api.media.ModifiableFileSet;
import org.daisy.streamline.api.option.UserOption;
import org.daisy.streamline.api.option.UserOptionValue;
import org.daisy.streamline.api.tasks.InternalTaskException;
import org.daisy.streamline.api.tasks.ReadWriteTask;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

public class MarkdownTask extends ReadWriteTask {
	private static final Logger logger = Logger.getLogger(MarkdownTask.class.getCanonicalName());
	private static List<UserOption> options = null;
	private final MarkdownOptions markdownOpts;

	public MarkdownTask(Map<String, Object> params) {
		super("Markdown to HTML");
		this.markdownOpts = MarkdownOptions.make(params);
	}

	@Override
	@Deprecated
	public void execute(File input, File output) throws InternalTaskException {
		execute(new DefaultAnnotatedFile.Builder(input).build(), output);
	}
	
	@Override
	public AnnotatedFile execute(AnnotatedFile input, File output) throws InternalTaskException {
		MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, markdownOpts.getConfig());
		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();
		try {
			byte[] data = Files.readAllBytes(input.getPath());
			Document d = parser.parse(new String(data, markdownOpts.getEncoding()));
			ResourceRetriever v = new ResourceRetriever();
			v.getRefs(d).forEach(System.out::println);
			String res = renderer.render(d);
			String outputEncoding = "utf-8";
			try (PrintWriter w = new PrintWriter(output, outputEncoding)) {
				w.println("<?xml version=\"1.0\" encoding=\""+outputEncoding+"\"?>");
				w.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\""+markdownOpts.getLanguage()+"\">");
				w.println("<head>");
				w.println("<meta charset=\""+outputEncoding+"\"/>");
				w.println("</head>");
				w.println("<body>");
				w.print(res);
				w.println("</body>");
				w.println("</html>");
			}
			return new DefaultAnnotatedFile.Builder(output.toPath()).extension("html").mediaType("application/xhtml+xml").build();
		} catch (IOException e) {
			throw new InternalTaskException(e);
		}
	}

	@Override
	public ModifiableFileSet execute(FileSet input, BaseFolder output) throws InternalTaskException {
		MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, markdownOpts.getConfig());
		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();
		try {
			byte[] data = Files.readAllBytes(input.getManifest().getPath());
			Document d = parser.parse(new String(data, markdownOpts.getEncoding()));
			Path p = input.getBaseFolder().getPath().relativize(input.getManifest().getPath().getParent());
			Path px = output.getPath();
			Path p2 = px.resolve(p);
			Path outputMainfest = p2.resolve("mainfest.html");
			ResourceRetriever v = new ResourceRetriever();
			List<String> refs = v.getRefs(d);
			String res = renderer.render(d);
			String outputEncoding = "utf-8";
			try (PrintWriter w = new PrintWriter(outputMainfest.toFile(), outputEncoding)) {
				w.println("<?xml version=\"1.0\" encoding=\""+outputEncoding+"\"?>");
				w.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\""+markdownOpts.getLanguage()+"\">");
				w.println("<head>");
				w.println("<meta charset=\""+outputEncoding+"\"/>");
				w.println("</head>");
				w.println("<body>");
				w.print(res);
				w.println("</body>");
				w.println("</html>");
			}
			DefaultFileSet.Builder builder = DefaultFileSet.with(output, new DefaultAnnotatedFile.Builder(outputMainfest).extension("html").mediaType("application/xhtml+xml").build());
			for (String ref : refs) {
				Path r = input.getManifest().getPath().getParent().resolve(ref);
				if (r.toFile().exists()) {
					builder.add(DefaultAnnotatedFile.create(r), ref);
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("File does not exist: " + r);
					}
				}
			}
			return builder.build();
		} catch (IOException e) {
			throw new InternalTaskException(e);
		}
	}

	private static synchronized List<UserOption> getOptionsInternal() {
		if (options==null) {
			options = new ArrayList<>();
			options.add(new UserOption.Builder(MarkdownOptions.SOURCE_ENCODING).description("The encoding of the input file").defaultValue(MarkdownOptions.DEFAULT_ENCODING).build());
			options.add(new UserOption.Builder(MarkdownOptions.SOURCE_LANGUAGE).description("The language of the input file").defaultValue(MarkdownOptions.DEFAULT_LANGUAGE).build());
			options.add(new UserOption.Builder(MarkdownOptions.TABLES).description("Enables support for github tables").addValue(booleanTrue()).addValue(booleanFalse()).defaultValue("false").build());
			options.add(new UserOption.Builder(MarkdownOptions.STRIKETHROUGH).description("Enables support for strikethrough (e.g. ~~strike~~)").addValue(booleanTrue()).addValue(booleanFalse()).defaultValue("false").build());
			options.add(new UserOption.Builder(MarkdownOptions.TASK_LISTS).description("Enables support for github tasks").addValue(booleanTrue()).addValue(booleanFalse()).defaultValue("false").build());
			options.add(new UserOption.Builder(MarkdownOptions.AUTO_LINKS).description("Enables auto links").addValue(booleanTrue()).addValue(booleanFalse()).defaultValue("false").build());
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
