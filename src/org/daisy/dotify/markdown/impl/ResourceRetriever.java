package org.daisy.dotify.markdown.impl;

import java.util.ArrayList;
import java.util.List;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.ast.Visitor;

class ResourceRetriever {
		private List<String> refs = new ArrayList<>();
		NodeVisitor visitor = new NodeVisitor(
			new VisitHandler<Image>(Image.class, new Visitor<Image>() {
				@Override
				public void visit(Image text) {
					refs.add(text.getUrl().unescape());
					visitor.visitChildren(text);
				}
			})
		);
		
		List<String> getRefs(Document d) {
			synchronized (refs) {
				refs.clear();
				visitor.visit(d);
				return new ArrayList<>(refs);
			}
		}
	}