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
package de.qaware.cloud.cost.aws.fetcher;

import de.qaware.cloud.cost.TimeRange;
import de.qaware.cloud.cost.aws.converter.RequestConverter;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.Dimension;
import software.amazon.awssdk.services.costexplorer.model.DimensionValuesWithAttributes;
import software.amazon.awssdk.services.costexplorer.model.GetDimensionValuesRequest;
import software.amazon.awssdk.services.costexplorer.model.GetDimensionValuesResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Fetcher for the DimensionalValues AWS API
 */
public class DimensionalValuesFetcher {
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
    public static DimensionalValuesFetcher withClient(CostExplorerClient client) {
        DimensionalValuesFetcher fetcher = new DimensionalValuesFetcher();
        fetcher.client = client;
        return fetcher;
    }

    /**
     * Sets the time range
     *
     * @param timeRange the time range
     * @return the current instance of the fetcher
     */
    public DimensionalValuesFetcher during(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    /**
     * Sets a search query
     *
     * @param query a search query for filtering the values
     * @return the current instance of the fetcher
     */
    public DimensionalValuesFetcher searchFor(String query) {
        this.query = query;
        return this;
    }

    /**
     * Fetch all services given the filters
     *
     * @return a list of service names
     */
    public List<String> fetchServices() {
        GetDimensionValuesRequest.Builder requestBuilder = GetDimensionValuesRequest.builder()
                .dimension(Dimension.SERVICE)
                .timePeriod(requestConverter.createDateInterval(timeRange));

        if (query != null && query.length() > 0) {
            requestBuilder.searchString(query);
        }

        GetDimensionValuesResponse response = client.getDimensionValues(requestBuilder.build());

        return response.dimensionValues().stream().map(DimensionValuesWithAttributes::value).collect(Collectors.toList());
    }
}
