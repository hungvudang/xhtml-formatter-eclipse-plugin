package xhtmlformatterplugin.handlers;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.NodeTraversor;

public class XHTMLFormatHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IEditorPart editor = //
				PlatformUI.getWorkbench()//
						.getActiveWorkbenchWindow()//
						.getActivePage()//
						.getActiveEditor();

		if (editor != null) {
			if (editor instanceof ITextEditor //
					&& ((ITextEditor) editor).isEditable()) {
				final IDocumentProvider provider = ((ITextEditor) editor).getDocumentProvider();
				final IDocument document = provider.getDocument(editor.getEditorInput());

				if (StringUtils.endsWithIgnoreCase(editor.getTitle(), ".xhtml")) {

					final String unformattedXhtml = StringUtils.trim(document.get());
					if (!StringUtils.isBlank(unformattedXhtml)) {
						final String formattedXhtml = StringUtils.trim(this.format(unformattedXhtml));
						if (StringUtils.compare(formattedXhtml, unformattedXhtml) != 0) {
							int offset = 0;
							final ISelectionProvider selectionProvider = ((ITextEditor) editor).getSelectionProvider();
							final ISelection selection = selectionProvider.getSelection();
							if (selection instanceof ITextSelection) {
								offset = ((ITextSelection) selection).getOffset();
							}

							document.set(formattedXhtml);

							((ITextEditor) editor).selectAndReveal(offset, 0);
						}
					}
				}
			}
		}
		return ObjectUtils.NULL;
	}

	public String format(final String unformattedXhtml) {
		final Document doc = Jsoup.parse(unformattedXhtml, Parser.xmlParser());
		final StringBuilder accum = new StringBuilder();
		NodeTraversor.traverse(new OuterXhtmlVisitor(accum), doc);
		final String formattedXhtml = accum.toString();
		StringUtils.trim(formattedXhtml);
		return formattedXhtml;
	}
}
