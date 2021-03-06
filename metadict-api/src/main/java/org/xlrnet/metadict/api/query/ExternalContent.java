/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.xlrnet.metadict.api.query;

import org.xlrnet.metadict.api.engine.SearchEngine;

import java.net.URL;

/**
 * The {@link ExternalContent} interface is used to describe any content provided by a {@link
 * SearchEngine} that does not represent a lexicographic element. This may e.g. be external
 * links to forums or other useful resources.
 * To build a new instance, you can use the {@link ExternalContentBuilder}.
 */
public interface ExternalContent {

    /**
     * Returns a description for the external content. This should be longer than the title of the content.
     *
     * @return a description for the external content.
     */
    String getDescription();

    /**
     * Returns a link to the external content.
     *
     * @return a link to the external content.
     */
    URL getLink();

    /**
     * Returns the title of the external content. This should be shorter than the content's description.
     *
     * @return the title of the external content.
     */
    String getTitle();

}
