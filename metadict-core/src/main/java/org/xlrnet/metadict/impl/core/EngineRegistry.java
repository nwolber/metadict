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

package org.xlrnet.metadict.impl.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.engine.SearchProvider;
import org.xlrnet.metadict.api.language.Dictionary;
import org.xlrnet.metadict.api.metadata.EngineDescription;
import org.xlrnet.metadict.api.metadata.FeatureSet;
import org.xlrnet.metadict.impl.exception.UnknownSearchEngineException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Class for loading and managing all available {@link SearchProvider}.
 * <p>
 * Since this object is {@link javax.enterprise.context.ApplicationScoped}, only one instance will be running at the
 * same time.
 */
@ApplicationScoped
public class EngineRegistry {

    private static final Logger logger = LoggerFactory.getLogger(EngineRegistry.class);

    @Inject
    Instance<SearchProvider> searchProviderInstances;

    Multimap<Dictionary, String> dictionaryEngineNameMap = ArrayListMultimap.create();

    Map<String, EngineDescription> engineDescriptionMap = new HashMap<>();

    Map<String, FeatureSet> featureSetMap = new HashMap<>();

    Map<String, SearchEngine> searchEngineMap = new HashMap<>();

    /**
     * Returns the amount of currently registered search engines. Search engines are provided by implementations of
     * {@link SearchProvider} and can be registered using {@link #registerSearchProvider(SearchProvider)}.
     *
     * @return the amount of currently registered search engines.
     */
    public int countRegisteredEngines() {
        return searchEngineMap.size();
    }

    /**
     * Returns the concrete {@link SearchEngine} that is registered under the given name. This name should be the
     * canonical class name of the engine. The registered engines can be queried by using {@link
     * #getRegisteredEngineNames()}.
     *
     * @param engineName
     *         Name of the registered engine
     * @return a concrete {@link SearchEngine}.
     * @throws UnknownSearchEngineException
     *         Will be thrown, if no engine is registered under the given name.
     */
    @NotNull
    public SearchEngine getEngineByName(String engineName) throws UnknownSearchEngineException {
        if (!searchEngineMap.containsKey(engineName)) {
            throw new UnknownSearchEngineException(engineName);
        }
        return searchEngineMap.get(engineName);
    }

    /**
     * Returns the concrete {@link EngineDescription} for the {@link SearchEngine} that is registered under the given
     * name. This name should be the canonical class name of the engine. The registered engines can be queried by using
     * {@link #getRegisteredEngineNames()}.
     *
     * @param engineName
     *         Name of the registered engine
     * @return a concrete {@link EngineDescription}.
     * @throws UnknownSearchEngineException
     *         Will be thrown, if no engine is registered under the given name.
     */
    @NotNull
    public EngineDescription getEngineDescriptionByName(String engineName) {
        if (!engineDescriptionMap.containsKey(engineName)) {
            throw new UnknownSearchEngineException(engineName);
        }
        return engineDescriptionMap.get(engineName);
    }

    /**
     * Returns the concrete {@link FeatureSet} for the {@link SearchEngine} that is registered under the given
     * name. This name should be the canonical class name of the engine. The registered engines can be queried by using
     * {@link #getRegisteredEngineNames()}.
     *
     * @param engineName
     *         Name of the registered engine
     * @return a concrete {@link EngineDescription}.
     * @throws UnknownSearchEngineException
     *         Will be thrown, if no engine is registered under the given name.
     */
    @NotNull
    public FeatureSet getFeatureSetByName(String engineName) {
        if (!featureSetMap.containsKey(engineName)) {
            throw new UnknownSearchEngineException(engineName);
        }
        return featureSetMap.get(engineName);
    }

    /**
     * Returns an unmodifiable set of the currently registered search engine names. The registered names should be the
     * canonical class name of the {@link SearchEngine} implementation.
     *
     * @return an unmodifiable set of the currently registered search engine names.
     */
    @NotNull
    public Set<String> getRegisteredEngineNames() {
        return Collections.unmodifiableSet(searchEngineMap.keySet());
    }

    /**
     * Returns the names of all engines that support the given {@link Dictionary}.
     *
     * @param dictionary
     *         The dictionary to look for.
     * @return a collection of engines that support the given dictionary.
     */
    @NotNull
    public Collection<String> getSearchEngineNamesByDictionary(@NotNull Dictionary dictionary) {
        return Collections.unmodifiableCollection(dictionaryEngineNameMap.get(dictionary));
    }

    /**
     * Returns a collection of all currently registered dictionaries that are supported by at least one search engine.
     * If a engine registered a bothWay-dictionary, this method will also return one-way configurations of this
     * dictionary.
     * <p>
     * Example:
     * If an engine registered itself as <code>de-en__bothWay</code>, this method will return
     * <code>de-en__bothWay</code>, <code>de-en</code> and <code>en-de</code>
     *
     * @return a collection of all currently registered dictionaries that are supported by at least one search engine.
     */
    @NotNull
    public Collection<Dictionary> getSupportedDictionaries() {
        return Collections.unmodifiableCollection(dictionaryEngineNameMap.keySet());
    }

    @PostConstruct
    void initialize() {
        logger.info("Registering search providers...");
        for (SearchProvider searchProvider : searchProviderInstances) {

            try {
                registerSearchProvider(searchProvider);
            } catch (Exception e) {
                logger.error("Registering search provider {} failed: {}", searchProvider.getClass().getCanonicalName(), e.getMessage());
            }
        }
    }

    /**
     * Register the given {@link SearchProvider} in the internal registry. The registration may fail, if any ob the
     * mandatory methods of the {@link SearchProvider} return null or a provider with the same class name is already
     * registered.
     *
     * @param searchProvider
     *         The {@link SearchProvider} that should be registered.
     * @throws Exception
     *         may be thrown at any time
     */
    void registerSearchProvider(@NotNull SearchProvider searchProvider) throws Exception {
        String canonicalProviderName = searchProvider.getClass().getCanonicalName();

        checkState(!searchEngineMap.containsKey(canonicalProviderName), "Provider %s is already registered", canonicalProviderName);

        EngineDescription engineDescription = searchProvider.getEngineDescription();
        FeatureSet featureSet = searchProvider.getFeatureSet();
        validateSearchProvider(canonicalProviderName, engineDescription, featureSet);

        SearchEngine searchEngine = searchProvider.newEngineInstance();
        checkNotNull(searchEngine, "Search provider returned null engine", canonicalProviderName);

        String canonicalEngineName = searchEngine.getClass().getCanonicalName();

        engineDescriptionMap.put(canonicalEngineName, engineDescription);
        featureSetMap.put(canonicalEngineName, featureSet);
        searchEngineMap.put(canonicalEngineName, searchEngine);

        // Register supported dictionaries for engine
        registerDictionariesFromFeatureSet(canonicalEngineName, featureSet);

        logger.info("Registered engine {} from provider {}", canonicalEngineName, canonicalProviderName);
    }

    private void registerDictionariesFromFeatureSet(@NotNull String canonicalEngineName, @NotNull FeatureSet featureSet) {
        for (Dictionary dictionary : featureSet.getSupportedDictionaries()) {
            registerDictionary(canonicalEngineName, dictionary);
        }
    }

    private void registerDictionary(@NotNull String canonicalEngineName, @NotNull Dictionary dictionary) {
        if (!dictionaryEngineNameMap.containsEntry(dictionary, canonicalEngineName)) {
            dictionaryEngineNameMap.put(dictionary, canonicalEngineName);
        }
        if (dictionary.isBidirectional()) {
            // Register inverted bidirectional dictionary
            Dictionary inverseBidirectional = Dictionary.inverse(dictionary);
            if (!dictionaryEngineNameMap.containsEntry(inverseBidirectional, canonicalEngineName)) {
                dictionaryEngineNameMap.put(inverseBidirectional, canonicalEngineName);
            }
            // Register as non-bidirectional to improve lookup speeds
            Dictionary simpleDictionary = Dictionary.fromLanguages(dictionary.getInput(), dictionary.getOutput(), false);
            if (!dictionaryEngineNameMap.containsEntry(simpleDictionary, canonicalEngineName)) {
                dictionaryEngineNameMap.put(simpleDictionary, canonicalEngineName);
            }
            Dictionary inverseSimpleDictionary = Dictionary.inverse(simpleDictionary);
            if (!dictionaryEngineNameMap.containsEntry(inverseSimpleDictionary, canonicalEngineName)) {
                dictionaryEngineNameMap.put(inverseSimpleDictionary, canonicalEngineName);
            }
        }
    }

    private void validateSearchProvider(@NotNull String canonicalName, @Nullable EngineDescription engineDescription, @Nullable FeatureSet featureSet) {
        checkNotNull(engineDescription, "Search provider %s returned null description", canonicalName);
        checkNotNull(featureSet, "Search provider %s returned null feature set", canonicalName);

        checkNotNull(featureSet.getSupportedDictionaries(), "Feature set of search provider %s has null on supported dictionaries", canonicalName);

        for (Dictionary dictionary : featureSet.getSupportedDictionaries()) {
            checkNotNull(dictionary, "Dictionary from search provider %s may not be null", canonicalName);
            checkNotNull(dictionary.getInput(), "Input language in dictionary from search provider %s may not be null", canonicalName);
            checkNotNull(dictionary.getOutput(), "Output language in dictionary from search provider %s may not be null", canonicalName);
        }
    }

}
