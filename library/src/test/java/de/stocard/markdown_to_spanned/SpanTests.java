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

import android.graphics.Typeface;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.util.StringBuilderPrinter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * This paramterized tests converts a set of Markdown samples in order to verifiy handling of all relevant html tags.
 * Created by fbarth on 21/04/15.
 */
@RunWith(ManifestedRobolectricGradeTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 21, manifest = Config.NONE)
public class SpanTests {

    @Test
    public void testAtxTextHeader1() throws Exception {
        String content = "# H1\n";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);
        assertEquals(RelativeSizeSpan.class, spans[0].getClass());
        assertEquals(StyleSpan.class, spans[1].getClass());
    }

    @Test
    public void testAtxTextHeader2() throws Exception {
        String content = "## H1\n";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);
        assertEquals(RelativeSizeSpan.class, spans[0].getClass());
        assertEquals(StyleSpan.class, spans[1].getClass());
    }

    @Test
    public void testBlockQuote() throws Exception {
        String content = "> blockquote\n" +
                "still blockquote";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(QuoteSpan.class, spans[0].getClass());
    }

    @Test
    public void testBoldAsterisk() throws Exception {
        String content = "**markdown**";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(Typeface.BOLD, ((StyleSpan) spans[0]).getStyle());
    }

    @Test
    public void testBoldUnderscore() throws Exception {
        String content = "__markdown__";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(Typeface.BOLD, ((StyleSpan) spans[0]).getStyle());
    }

    @Test
    public void testCode4Spaces() throws Exception {
        String content = "    code\n" +
                "    more code";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);
        assertEquals(TypefaceSpan.class, spans[0].getClass());
        assertEquals("monospace", ((TypefaceSpan) spans[0]).getFamily());
        assertEquals(TypefaceSpan.class, spans[1].getClass());
        assertEquals("monospace", ((TypefaceSpan) spans[1]).getFamily());
    }

    @Test
    public void testCodeFencedBacktick() throws Exception {
        String content = "``` \n" +
                "code\n" +
                "more code\n" +
                "```";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);
        assertEquals(TypefaceSpan.class, spans[0].getClass());
        assertEquals("monospace", ((TypefaceSpan) spans[0]).getFamily());
        assertEquals(TypefaceSpan.class, spans[1].getClass());
        assertEquals("monospace", ((TypefaceSpan) spans[1]).getFamily());
    }

    @Test
    public void testCodeFencedTilde() throws Exception {
        String content = "~~~\n" +
                "code\n" +
                "more code\n" +
                "~~~";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);
        assertEquals(TypefaceSpan.class, spans[0].getClass());
        assertEquals("monospace", ((TypefaceSpan) spans[0]).getFamily());
        assertEquals(TypefaceSpan.class, spans[1].getClass());
        assertEquals("monospace", ((TypefaceSpan) spans[1]).getFamily());
    }

    @Test
    public void testCodeInline() throws Exception {
        String content = "Use the `printf()` function.";
        Spanned result = Markdown.fromMarkdown(content);

        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(TypefaceSpan.class, spans[0].getClass());
        assertEquals("monospace", ((TypefaceSpan) spans[0]).getFamily());
    }

    @Test
    public void testCodeTab() throws Exception {
        String content = "\tcode\n" +
                "\tmore code";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);
        assertEquals(TypefaceSpan.class, spans[0].getClass());
        assertEquals("monospace", ((TypefaceSpan) spans[0]).getFamily());
        assertEquals(TypefaceSpan.class, spans[1].getClass());
        assertEquals("monospace", ((TypefaceSpan) spans[1]).getFamily());
    }

    @Test
    public void testEmphAsterisk() throws Exception {
        String content = "*markdown*";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(Typeface.ITALIC, ((StyleSpan) spans[0]).getStyle());
    }

    @Test
    public void testEmphUnderscore() throws Exception {
        String content = "_markdown_";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(Typeface.ITALIC, ((StyleSpan) spans[0]).getStyle());
    }

    @Test
    public void testEnumeration() throws Exception {
        String content = " 1. item1\n" +
                " 1. item2";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);
        assertEquals(LeadingMarginSpan.Standard.class, spans[0].getClass());
        assertEquals('1', result.charAt(0));
        assertEquals(LeadingMarginSpan.Standard.class, spans[1].getClass());
        assertEquals('2', result.charAt(9));
    }

    @Test
    public void testLinkWithRef() throws Exception {
        String content = "This is [an example][id] reference-style link.\n" +
                "[id]: http://example.com/  \"Optional Title Here\"";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(URLSpan.class, spans[0].getClass());
        assertEquals("http://example.com/", ((URLSpan) spans[0]).getURL());
    }

    @Test
    public void testLinkWithTitle() throws Exception {
        String content = "This is [an example](http://example.com/ \"Title\") inline link.";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(URLSpan.class, spans[0].getClass());
        assertEquals("http://example.com/", ((URLSpan) spans[0]).getURL());
    }

    @Test
    public void testLinkWithoutTitle() throws Exception {
        String content = "[This link](http://example.net/) has no title attribute.";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(URLSpan.class, spans[0].getClass());
        assertEquals("http://example.net/", ((URLSpan) spans[0]).getURL());
    }

    @Test
    public void testListAsterisk() throws Exception {
        String content = " * item1\n" +
                " * item2";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(4, spans.length);
        assertEquals(LeadingMarginSpan.Standard.class, spans[0].getClass());
        assertEquals(BulletSpan.class, spans[1].getClass());
        assertEquals(LeadingMarginSpan.Standard.class, spans[2].getClass());
        assertEquals(BulletSpan.class, spans[3].getClass());
    }

    @Test
    public void testListMinus() throws Exception {
        String content = " * item1\n" +
                " * item2";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(4, spans.length);
        assertEquals(LeadingMarginSpan.Standard.class, spans[0].getClass());
        assertEquals(BulletSpan.class, spans[1].getClass());
        assertEquals(LeadingMarginSpan.Standard.class, spans[2].getClass());
        assertEquals(BulletSpan.class, spans[3].getClass());
    }

    @Test
    public void testListPlus() throws Exception {
        String content = " * item1\n" +
                " * item2";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(4, spans.length);
        assertEquals(LeadingMarginSpan.Standard.class, spans[0].getClass());
        assertEquals(BulletSpan.class, spans[1].getClass());
        assertEquals(LeadingMarginSpan.Standard.class, spans[2].getClass());
        assertEquals(BulletSpan.class, spans[3].getClass());
    }

    @Test
    public void testSetTextHeader1() throws Exception {
        String content = "H1\n" +
                "==";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);
        assertEquals(RelativeSizeSpan.class, spans[0].getClass());
        assertEquals(StyleSpan.class, spans[1].getClass());
    }

    private void printSpans(Spanned spanned) {
        StringBuilder builder = new StringBuilder();
        builder.append(spanned.toString());
        TextUtils.dumpSpans(spanned, new StringBuilderPrinter(builder), "");
        System.out.println(builder.toString());
    }

    @Test
    public void testSetTextHeader2() throws Exception {
        String content = "H1\n" +
                "--";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(2, spans.length);
        assertEquals(RelativeSizeSpan.class, spans[0].getClass());
        assertEquals(StyleSpan.class, spans[1].getClass());
    }

    @Test
    public void testStrike() throws Exception {
        String content = "~~markdown~~";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(1, spans.length);
        assertEquals(StrikethroughSpan.class, spans[0].getClass());
    }

    @Test
    public void testSubList() throws Exception {
        String content = "* item 1\n" +
                "* item 2\n" +
                "    * sublist item 1\n" +
                "    * sublist item 2\n";
        Spanned result = Markdown.fromMarkdown(content);
        printSpans(result);
        Object[] spans = result.getSpans(0, result.length(), Object.class);
        assertEquals(8, spans.length);
        assertEquals(LeadingMarginSpan.Standard.class, spans[0].getClass());
        assertEquals(BulletSpan.class, spans[1].getClass());
        assertEquals(LeadingMarginSpan.Standard.class, spans[2].getClass());
        assertEquals(BulletSpan.class, spans[3].getClass());
        assertEquals(LeadingMarginSpan.Standard.class, spans[4].getClass());
        assertEquals(BulletSpan.class, spans[5].getClass());
        assertEquals(LeadingMarginSpan.Standard.class, spans[6].getClass());
        assertEquals(BulletSpan.class, spans[7].getClass());
    }

}
