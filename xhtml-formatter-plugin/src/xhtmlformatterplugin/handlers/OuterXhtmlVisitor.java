package xhtmlformatterplugin.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

public class OuterXhtmlVisitor implements NodeVisitor {

	private final Appendable accum;

	OuterXhtmlVisitor(Appendable accum) {
		this.accum = accum;
	}

	@Override
	public void head(Node node, int depth) {
		try {
			serializeOpenTag(node, depth);
		} catch (IOException e) {
		}
	}

	@Override
	public void tail(Node node, int depth) {
		try {
			serializeEndTag(node, depth);
		} catch (IOException e) {
		}
	}

	public void serializeOpenTag(final Node node, final int depth) throws IOException {
		final String tagName = node.nodeName();
		if (!StringUtils.startsWith(tagName, "#") || Arrays.asList("#text", "#comment").contains(tagName)) {
			int padding = 3 * (depth - 1);
			if (node instanceof TextNode) {
				if (!isIndent((TextNode) node)) {
					final String enhance = StringUtils.trim(//
							StringUtils.replace(((TextNode) node).getWholeText(), "\n", " "));

					if (!StringUtils.isBlank(enhance)) {
						indentIfRequired(padding);
						this.accum.append(enhance);
					} else {
						this.accum.append("\n");
					}
				}
			}

			else if (node instanceof Comment) {
				indentIfRequired(padding);
				String data = StringUtils.trim(((Comment) node).getData());

				data = StringUtils.prependIfMissing(data, " ");
				data = StringUtils.appendIfMissing(data, " ");

				this.accum.append("<!--").append(data).append("-->");
			}

			else if (node instanceof Element) {
				indentIfRequired(padding);
				final List<Attribute> attributes = new ArrayList<Attribute>(node.attributes().asList());
				this.accum.append("<").append(tagName);
				padding += (1 + tagName.length());

				if (!attributes.isEmpty()) {
					padding++;
					this.accum.append(" ").append(attributes.get(0).html());
					for (int i = 1; i < attributes.size() - 1; i++) {
						this.accum.append("\n").append(StringUtils.rightPad("", padding));
						this.accum.append(attributes.get(i).html());
					}

					if (attributes.size() - 1 > 0) {
						this.accum.append("\n").append(StringUtils.rightPad("", padding));
						this.accum.append(attributes.get(attributes.size() - 1).html());
					}
				}

				if (node.childNodes().isEmpty()) {
					this.accum.append(" />");
				} else {
					this.accum.append(">");
				}
			}
		}
	}

	public void serializeEndTag(final Node node, final int depth) throws IOException {
		final String tagName = node.nodeName();
		if (!StringUtils.startsWith(tagName, "#")) {
			if (node instanceof Element && !node.childNodes().isEmpty()) {
				int padding = 3 * (depth - 1);
				this.accum.append("\n").append(StringUtils.rightPad("", padding));
				this.accum.append("</").append(tagName).append('>');
			}
		}
	}

	private void indentIfRequired(int padding) throws IOException {
		if (this.accum instanceof StringBuilder) {
			final StringBuilder sb = (StringBuilder) this.accum;
			if (sb.length() > 0) {
				this.accum.append("\n").append(StringUtils.rightPad("", padding));
			}
		}
	}

	private boolean isIndent(final TextNode tn) {
		return Pattern.matches("^\\n +", tn.getWholeText());
	}

}
