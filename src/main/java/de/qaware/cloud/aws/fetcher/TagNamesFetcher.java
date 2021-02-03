/*
 *    Copyright (C) 2021 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.qaware.cloud.aws.fetcher;

import de.qaware.cloud.TimeRange;
import de.qaware.cloud.aws.converter.RequestConverter;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.GetTagsRequest;
import software.amazon.awssdk.services.costexplorer.model.GetTagsResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Fetcher for the Tags AWS API
 */
public class TagNamesFetcher {
    private static final String TAG_NAME = "Name";
    private final RequestConverter requestConverter = new RequestConverter();

    private CostExplorerClient client;
    private TimeRange timeRange;
    private String query;

    /**
     * Set the cost explorer client
     *
     * @param client the cost explorer client
     * @return a new instance of the fetcher
     */
    public static TagNamesFetcher withClient(CostExplorerClient client) {
        TagNamesFetcher fetcher = new TagNamesFetcher();
        fetcher.client = client;
        return fetcher;
    }

    /**
     * Sets the time range
     *
     * @param timeRange the time range
     * @return the current instance of the fetcher
     */
    public TagNamesFetcher during(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    /**
     * Sets a search query
     *
     * @param query a search query for the tag
     * @return the current instance of the fetcher
     */
    public TagNamesFetcher searchFor(String query) {
        this.query = query;
        return this;
    }

    /**
     * Fetch the tag names given the filters
     *
     * @return a list of tag names
     */
    public List<String> fetch() {
        GetTagsRequest.Builder requestBuilder = GetTagsRequest.builder()
                .tagKey(TAG_NAME)
                .timePeriod(requestConverter.createDateInterval(timeRange));

        if (query != null && query.length() > 0) {
            requestBuilder.searchString(query);
        }

        GetTagsResponse response = client.getTags(requestBuilder.build());

        return response.tags().stream().filter(tag -> !tag.isBlank()).collect(Collectors.toList());
    }
}
