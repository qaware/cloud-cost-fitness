/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cce.aws.fetcher;

import de.qaware.cce.TimeRange;
import de.qaware.cce.aws.converter.RequestConverter;
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
        return fetch(Dimension.SERVICE);
    }

    /**
     * Fetch all usage categories given the filters
     *
     * @return a list of usage categories
     */
    public List<String> fetchUsage() {
        return fetch(Dimension.USAGE_TYPE_GROUP);
    }

    private List<String> fetch(Dimension dimension) {
        GetDimensionValuesRequest.Builder requestBuilder = GetDimensionValuesRequest.builder()
                .dimension(dimension)
                .timePeriod(requestConverter.createDateInterval(timeRange));

        if (query != null && query.length() > 0) {
            requestBuilder.searchString(query);
        }

        GetDimensionValuesResponse response = client.getDimensionValues(requestBuilder.build());

        return response.dimensionValues().stream().map(DimensionValuesWithAttributes::value).collect(Collectors.toList());
    }
}
