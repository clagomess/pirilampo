package br.com.pirilampo.core.compilers;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Document;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ParseToMarkdown {
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer;

    public ParseToMarkdown() {
        this.renderer = HtmlRenderer.builder()
                .nodeRendererFactory(SkipParentWrapperParagraphsRenderer::new)
                .build();
    }

    public String build(String txt){
        try {
            return renderer.render(parser.parse(txt));
        } catch (Throwable e) {
            log.warn(log.getName(), e);
            return txt;
        }
    }

    private static class SkipParentWrapperParagraphsRenderer extends CoreHtmlNodeRenderer {
        public SkipParentWrapperParagraphsRenderer(HtmlNodeRendererContext context) {
            super(context);
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            Set<Class<? extends Node>> toReturn = new HashSet<>();
            toReturn.add(Paragraph.class);

            return toReturn;
        }

        @Override
        public void render(Node node) {
            if (node.getParent() instanceof Document) {
                visitChildren(node);
            } else {
                visit((Paragraph) node);
            }
        }
    }
}
