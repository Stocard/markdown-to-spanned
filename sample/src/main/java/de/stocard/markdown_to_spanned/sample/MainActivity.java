package de.stocard.markdown_to_spanned.sample;
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


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import de.stocard.markdown_to_spanned.Markdown;


public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final TextView setTextHeader1 = (TextView) findViewById(R.id.settext_header1);
		setTextHeader1.setText(Markdown.fromMarkdown("H1 settext\n" +
				"=="));

		final TextView setTextHeader2 = (TextView) findViewById(R.id.settext_header2);
		setTextHeader2.setText(Markdown.fromMarkdown("H2 settext\n" +
				"--"));

		final TextView atxHeader1 = (TextView) findViewById(R.id.atx_header1);
		atxHeader1.setText(Markdown.fromMarkdown("# H1 atx\n"));

		final TextView atxHeader2 = (TextView) findViewById(R.id.atx_header2);
		atxHeader2.setText(Markdown.fromMarkdown("## H2 atx\n"));

		final TextView blockQuote = (TextView) findViewById(R.id.blockquote);
		blockQuote.setText(Markdown.fromMarkdown("> blockquote\n" +
				"blockquote"));

		final TextView boldAsterisk = (TextView) findViewById(R.id.bold_asterisk);
		boldAsterisk.setText(Markdown.fromMarkdown("**bold asterisk**"));

		final TextView emphAsterisk = (TextView) findViewById(R.id.emph_asterisk);
		emphAsterisk.setText(Markdown.fromMarkdown("*emph asterisk*"));

		final TextView boldUnderscore = (TextView) findViewById(R.id.bold_underscore);
		boldUnderscore.setText(Markdown.fromMarkdown("__bold underscore__"));

		final TextView emphUnderscore = (TextView) findViewById(R.id.emph_underscore);
		emphUnderscore.setText(Markdown.fromMarkdown("_emph underscore_"));

		final TextView strikeout = (TextView) findViewById(R.id.strikeout);
		strikeout.setText(Markdown.fromMarkdown("~~strikeout~~"));

		final TextView inlineCode = (TextView) findViewById(R.id.code_inline);
		inlineCode.setText(Markdown.fromMarkdown("inline `code()` is great."));

		final TextView listAsterisk = (TextView) findViewById(R.id.list_asterisk);
		listAsterisk.setText(Markdown.fromMarkdown(" * asterisk list item 1\n" + //
				" * asterisk list item 2"));

		final TextView listPlus = (TextView) findViewById(R.id.list_plus);
		listPlus.setText(Markdown.fromMarkdown(" + plus list item 1\n" + //
				" + plus list item 2"));

		final TextView listMinus = (TextView) findViewById(R.id.list_minus);
		listMinus.setText(Markdown.fromMarkdown(" - minus list item 1\n" + //
				" - miuns list item 2"));

		final TextView sublist = (TextView) findViewById(R.id.list_sublist);
		sublist.setText(Markdown.fromMarkdown("* item 1\n" +
				"* item 2\n" +
				"    * sublist item 1\n" +
				"    * sublist item 2\n"));

		final TextView enumeration = (TextView) findViewById(R.id.list_enumeration);
		enumeration.setText(Markdown.fromMarkdown(" 1. enumeration 1\n" + //
				" 1. enumeration 2"));

		final TextView code4spaces = (TextView) findViewById(R.id.code_4_spaces);
		code4spaces.setText(Markdown.fromMarkdown("    codeWith4Spaces(); \n " + //
				"    isCode();"));

		final TextView codeTab = (TextView) findViewById(R.id.code_tab);
		codeTab.setText(Markdown.fromMarkdown("\tcodeWithTab(); \n " + //
				"\tisCode();"));

		final TextView codeFencedBacktick = (TextView) findViewById(R.id.code_fenced_backtick);
		codeFencedBacktick.setText(Markdown.fromMarkdown("here comes the code\n" +
				"```\n" + //
				"fencedCodeWithBackticks();\n" + //
				"fencedCodeWithBackticks2();\n" + //
				" **great** ;\n" + //
				"```\n" +
				"yay" +
				"fadsfasdfasdfasdfasdf \n" +
				"adfasdfsaf"));

		final TextView codeFencedTilde = (TextView) findViewById(R.id.code_fenced_tilde);
		codeFencedTilde.setText(Markdown.fromMarkdown("~~~\n" + //
				"fence();\n" + //
				"fence();\n" + //
				"~~~"));

		final TextView linkWithTitle = (TextView) findViewById(R.id.link_with_title);
		Markdown.setMarkdown("This is [an example](http://example.com/ \"Title\") inline link with title.", linkWithTitle);

		final TextView linkWithoutTitle = (TextView) findViewById(R.id.link_without_title);
		Markdown.setMarkdown("This is [an example](http://example.com/ \"Title\") inline link without title.", linkWithoutTitle);

		final TextView linkWithRef = (TextView) findViewById(R.id.link_with_ref);
		Markdown.setMarkdown("This is [an example][id] reference-style link.\n" + //
				"[id]: http://example.com/  \"Optional Title Here\"", linkWithRef);

		final TextView linebreak = (TextView) findViewById(R.id.linebreak);
		Markdown.setMarkdown("Text with line\n break", linebreak);
	}


}
