package de.stocard.markdown_to_spanned;

/*
 * Copyright 2016 Stocard GmbH
 * Copyright 2015 Florian Barth
 * Copyright 2013-2015 Dominik Schürmann <dominik@dominikschuermann.de>
 * Copyright 2013-2015 Juha Kuitunen
 * Copyright 2013 Mohammed Lakkadshaw
 * Copyright 2007 The Android Open Source Project
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

import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;

import org.xml.sax.XMLReader;

import java.util.Stack;

// https://raw.githubusercontent.com/sufficientlysecure/html-textview

/**
 * Html-Tag Handler that extends the native TagHandler to handle additional tags
 */
public class HTMLTagHandler implements Html.TagHandler {
    public static final String OL_TAG = "orderedlist";
    public static final String UL_TAG = "unorderedlist";
    public static final String LI_TAG = "listiem";

    /**
     * List indentation in pixels. Nested lists use multiple of this.
     */
    private static final int        INDENT_PX           = 10;
    private static final int        LIST_ITEM_INDENT_PX = INDENT_PX * 2;
    private static final BulletSpan BULLET_SPAN         = new BulletSpan(INDENT_PX);
    public static final  String     CODE                = "code";
    public static final  String     CENTER              = "center";
    public static final  String     STRIKE              = "strike";
    public static final  String     STRIKE_SHORT        = "s";

    /**
     * Keeps track of lists (ol, ul). On bottom of Stack is the outermost list
     * and on top of Stack is the most nested list
     */
    private final Stack<ListTag> lists = new Stack<ListTag>();


    /**
     * Abstract super class for {@link Ul} and {@link Ol}.
     */
    private abstract static class ListTag {
        /**
         * Opens a new list item.
         *
         * @param text
         */
        public void openItem(final Editable text) {
            if (text.length() > 0 && text.charAt(text.length() - 1) != '\n') {
                text.append("\n");
            }
            final int len = text.length();
            text.setSpan(this, len, len, Spanned.SPAN_MARK_MARK);
        }

        /**
         * Closes a list item.
         *
         * @param text
         * @param indentation
         */
        public final void closeItem(final Editable text, final int indentation) {
            if (text.length() > 0 && text.charAt(text.length() - 1) != '\n') {
                text.append("\n");
            }
            final Object[] replaces = getReplaces(text, indentation);
            final int len = text.length();
            final ListTag listTag = getLast(text);
            final int where = text.getSpanStart(listTag);
            text.removeSpan(listTag);
            if (where != len) {
                for (Object replace : replaces) {
                    text.setSpan(replace, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        protected abstract Object[] getReplaces(final Editable text, final int indentation);

        /**
         * Note: This knows that the last returned object from getSpans() will be the most recently added.
         *
         * @see Html
         */
        private ListTag getLast(final Spanned text) {
            final ListTag[] listTags = text.getSpans(0, text.length(), ListTag.class);
            if (listTags.length == 0) {
                return null;
            }
            return listTags[listTags.length - 1];
        }
    }

    /**
     * Class representing the unordered list ({@code <ul>}) HTML tag.
     */
    private static class Ul extends ListTag {

        @Override
        protected Object[] getReplaces(final Editable text, final int indentation) {
            // Nested BulletSpans increases distance between BULLET_SPAN and text, so we must prevent it.
            int bulletMargin = INDENT_PX;
            if (indentation > 1) {
                bulletMargin = INDENT_PX - BULLET_SPAN.getLeadingMargin(true);
                if (indentation > 2) {
                    // This get's more complicated when we add a LeadingMarginSpan into the same line:
                    // we have also counter it's effect to BulletSpan
                    bulletMargin -= (indentation - 2) * LIST_ITEM_INDENT_PX;
                }
            }
            return new Object[]{
                    new LeadingMarginSpan.Standard(LIST_ITEM_INDENT_PX * (indentation - 1)),
                    new BulletSpan(bulletMargin)
            };
        }
    }

    /**
     * Class representing the ordered list ({@code <ol>}) HTML tag.
     */
    private static class Ol extends ListTag {
        private int nextIdx;

        /**
         * Creates a new {@code <ul>} with start index of 1.
         */
        public Ol() {
            this(1); // default start index
        }

        /**
         * Creates a new {@code <ul>} with given start index.
         *
         * @param startIdx
         */
        public Ol(final int startIdx) {
            this.nextIdx = startIdx;
        }

        @Override
        public void openItem(final Editable text) {
            super.openItem(text);
            text.append(Integer.toString(nextIdx++)).append(". ");
        }

        @Override
        protected Object[] getReplaces(final Editable text, final int indentation) {
            int numberMargin = LIST_ITEM_INDENT_PX * (indentation - 1);
            if (indentation > 2) {
                // Same as in ordered lists: counter the effect of nested Spans
                numberMargin -= (indentation - 2) * LIST_ITEM_INDENT_PX;
            }
            return new Object[]{new LeadingMarginSpan.Standard(numberMargin)};
        }
    }

    private static class Code {
    }

    private static class Center {
    }

    private static class Strike {
    }

    @Override
    public void handleTag(final boolean opening, final String tag, Editable output, final XMLReader xmlReader) {

        Log.d(Config.TAG, tag);
        if (UL_TAG.equalsIgnoreCase(tag)) {
            if (opening) {   // handle <ul>
                lists.push(new Ul());
            } else {   // handle </ul>
                lists.pop();
            }
        } else if (OL_TAG.equalsIgnoreCase(tag)) {
            if (opening) {   // handle <ol>
                lists.push(new Ol()); // use default start index of 1
            } else {   // handle </ol>
                lists.pop();
            }
        } else if (LI_TAG.equalsIgnoreCase(tag)) {
            if (opening) {   // handle <li>
                lists.peek().openItem(output);
            } else {   // handle </li>
                lists.peek().closeItem(output, lists.size());
            }
        } else if (tag.equalsIgnoreCase(CODE)) {
            if (opening) {   // handle <code>
                start(output, new Code());
            } else {   // handle </code>
                end(output, Code.class, false, new TypefaceSpan("monospace"));
            }
        } else if (tag.equalsIgnoreCase(CENTER)) {
            if (opening) {   // handle <center>
                start(output, new Center());
            } else {   // handle </center>
                end(output, Center.class, true, new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER));
            }

        } else if (tag.equalsIgnoreCase(STRIKE_SHORT) || tag.equalsIgnoreCase(STRIKE)) {
            if (opening) {   // handle <strike>
                start(output, new Strike());
            } else {   // handle </strike>
                end(output, Strike.class, false, new StrikethroughSpan());
            }


        } else {
            Log.d(Config.TAG, "ingnoring unsupported closing tag: " + tag);
        }

    }

    /**
     * Mark the opening tag by using private classes
     */
    private void start(Editable output, Object mark) {
        int len = output.length();
        output.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);

        if (Config.DEBUG) {
            Log.d(Config.TAG, "len: " + len);
        }
    }

    /**
     * Modified from {@link android.text.Html}
     */
    private void end(Editable output, Class kind, boolean paragraphStyle, Object... replaces) {
        Object obj = getLast(output, kind);
        // start of the tag
        int where = output.getSpanStart(obj);
        // end of the tag
        int len = output.length();

        output.removeSpan(obj);

        if (where != len) {
            int thisLen = len;
            // paragraph styles like AlignmentSpan need to end with a new line!
            if (paragraphStyle) {
                output.append("\n");
                thisLen++;
            }
            if (kind.equals(Code.class)) {
                String outputString = output.toString();
                Log.d(Config.TAG, "pre: " + outputString);
            }

            for (Object replace : replaces) {
                output.setSpan(replace, where, thisLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (Config.DEBUG) {
                Log.d(Config.TAG, "where: " + where);
                Log.d(Config.TAG, "thisLen: " + thisLen);
            }
        }
    }

    /**
     * Get last marked position of a specific tag kind (private class)
     */
    private static Object getLast(Editable text, Class kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        } else {
            for (int i = objs.length; i > 0; i--) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1];
                }
            }
            return null;
        }
    }

}