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

package org.xlrnet.metadict.impl.query;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Dictionary;
import org.xlrnet.metadict.impl.aggregation.GroupingType;
import org.xlrnet.metadict.impl.aggregation.OrderType;
import org.xlrnet.metadict.impl.core.MetadictCore;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for creating new {@link QueryRequest} objects.
 */
public class QueryRequestBuilder {

    private final MetadictCore metadictCore;

    private String queryString;

    private List<Dictionary> queryDictionaries = new ArrayList<>();

    private GroupingType groupingType = GroupingType.NONE;

    private OrderType orderType = OrderType.RELEVANCE;

    protected QueryRequestBuilder(@NotNull MetadictCore metadictCore) {
        this.metadictCore = metadictCore;
    }

    /**
     * Adds a new {@link Dictionary} to the current query.
     *
     * @param newDictionary
     *         The new dictionary to add.
     * @return the current builder
     */
    public QueryRequestBuilder addQueryDictionary(@NotNull Dictionary newDictionary) {
        checkNotNull(newDictionary);

        this.queryDictionaries.add(newDictionary);
        return this;
    }

    public QueryRequest build() {
        return new QueryRequestImpl(metadictCore, queryString, queryDictionaries, groupingType, orderType);
    }

    /**
     * Set the grouping type that should be used for the result set. If none is set, the grouping type {@link
     * GroupingType#NONE} will be used as default.
     *
     * @param groupingType
     *         The grouping type that should be used for the result set.
     * @return the current builder
     */
    public QueryRequestBuilder setGroupBy(GroupingType groupingType) {
        checkNotNull(groupingType);

        this.groupingType = groupingType;
        return this;
    }

    /**
     * Set the order type that should be used for the result set. If none is set, the order type {@link
     * OrderType#RELEVANCE} will be used as default.
     *
     * @param orderType
     *         The order type that should be used for the result set.
     * @return the current builder
     */
    public QueryRequestBuilder setOrderBy(OrderType orderType) {
        checkNotNull(orderType);

        this.orderType = orderType;
        return this;
    }

    /**
     * Sets all {@link Dictionary} objects that should be queried.
     *
     * @param dictionaries
     *         The dictionaries to query.
     * @return the current builder
     */
    public QueryRequestBuilder setQueryDictionaries(@NotNull List<Dictionary> dictionaries) {
        checkNotNull(dictionaries);

        for (Dictionary dictionary : dictionaries)
            checkNotNull(dictionary);

        this.queryDictionaries.addAll(dictionaries);
        return this;
    }

    /**
     * Set the query string for this request. This is usually the string that will be forwarded to the search backend.
     *
     * @param queryString
     *         The query string for this request.
     * @return the current builder
     */
    public QueryRequestBuilder setQueryString(@NotNull String queryString) {
        checkNotNull(queryString);

        this.queryString = queryString;
        return this;
    }
}
