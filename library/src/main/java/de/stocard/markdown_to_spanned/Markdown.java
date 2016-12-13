package de.stocard.markdown_to_spanned;
/*
 * Copyright 2016 Stocard GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

/**
 * This class processes Markdown strings into displayable styled text.
 */
public class Markdown {
	public static void setMarkdown(String markdown, TextView view) {
		Log.d(Config.TAG, "md: " + markdown);
		try {
			String html = new Markdown4jProcessor().process(markdown);
			Log.d(Config.TAG, "html: " + html);
			Spanned spanned = Html.fromHtml(html, null, new HTMLTagHandler());
			CharSequence trimmed = trim(spanned, 0, spanned.length());
			view.setText(trimmed);
		} catch (IOException e) {
			Log.e(Config.TAG, "error: " + e.getMessage());
		}
		view.setMovementMethod(LocalLinkMovementMethod.getInstance());

	}

	public static Spanned fromMarkdown(String markdown) {
		Log.d(Config.TAG, "md: " + markdown);
		try {
			String html = new Markdown4jProcessor().process(markdown);
			html = workaroundCodeBlocks(html);
			Log.d(Config.TAG, "html: " + html);
			Spanned spanned = Html.fromHtml(html, null, new HTMLTagHandler());
			CharSequence trimmed = trim(spanned, 0, spanned.length());
			return (Spanned) trimmed;
		} catch (IOException e) {
			Log.e(Config.TAG, "error: " + e.getMessage());
		}
		return null;
	}


	private static String workaroundCodeBlocks(String html) {
		// as pre tags are not properly handled and generated for codeblocks, we replace them with p
		html = html.replaceAll("<pre>", "<p>");
		html = html.replaceAll("</pre>", "</p>");

		// for each newline in a code block we need to replace them with closing code, break, opening code
		StringBuilder builder = new StringBuilder();
		int idx = 0;
		int codeStart = -1;
		int codeEnd = -1;
		while ((codeStart = html.indexOf("<code>", idx)) != -1 && (codeEnd = html.indexOf("</code>", idx)) != -1) {
			// append everything until codeblock start
			builder.append(html.substring(idx, codeStart));

			// get content of codeblock, workaround newline foo, and append it
			String tmp = new String(html.substring(codeStart,codeEnd));
			tmp = tmp.replaceAll("\n","</code><br /><code>");
			builder.append(tmp);

			// new start index is
			idx = codeEnd;
		}
		builder.append(html.substring(idx));

		return builder.toString();
	}

	public static String replaceOld(
			final String aInput,
			final String aOldPattern,
			final String aNewPattern
	){
		if ( aOldPattern.equals("") ) {
			throw new IllegalArgumentException("Old pattern must have content.");
		}

		final StringBuffer result = new StringBuffer();
		//startIdx and idxOld delimit various chunks of aInput; these
		//chunks always end where aOldPattern begins
		int startIdx = 0;
		int idxOld = 0;
		while ((idxOld = aInput.indexOf(aOldPattern, startIdx)) >= 0) {
			//grab a part of aInput which does not include aOldPattern
			result.append( aInput.substring(startIdx, idxOld) );
			//add aNewPattern to take place of aOldPattern
			result.append( aNewPattern );

			//reset the startIdx to just after the current match, to see
			//if there are any further matches
			startIdx = idxOld + aOldPattern.length();
		}
		//the final chunk will go to the end of aInput
		result.append( aInput.substring(startIdx) );
		return result.toString();
	}


	static class LocalLinkMovementMethod extends LinkMovementMethod {
		static LocalLinkMovementMethod sInstance;

		public static LocalLinkMovementMethod getInstance() {
			if (sInstance == null)
				sInstance = new LocalLinkMovementMethod();

			return sInstance;
		}

		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_UP ||
					action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);

				ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer,
								buffer.getSpanStart(link[0]),
								buffer.getSpanEnd(link[0]));
					}

//					if (widget instanceof HtmlTextView) {
//						((HtmlTextView) widget).mLinkHit = true;
//					}
					return true;
				} else {
					Selection.removeSelection(buffer);
					Touch.onTouchEvent(widget, buffer, event);
					return false;
				}
			}
			return Touch.onTouchEvent(widget, buffer, event);
		}
	}
	public static CharSequence trim(CharSequence s, int start, int end) {
		while (start < end && Character.isWhitespace(s.charAt(start))) {
			start++;
		}

		while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
			end--;
		}

		return s.subSequence(start, end);
	}
}
