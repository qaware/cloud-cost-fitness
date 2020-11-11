package de.qaware.cce.aws.fetcher;

import de.qaware.cce.aws.TimeRange;
import de.qaware.cce.aws.converter.RequestConverter;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.GetTagsRequest;
import software.amazon.awssdk.services.costexplorer.model.GetTagsResponse;

import java.util.List;
import java.util.stream.Collectors;

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

        if (query != null) {
            requestBuilder.searchString(query);
        }

        GetTagsResponse response = client.getTags(requestBuilder.build());

        return response.tags().stream().filter(tag -> !tag.isBlank()).collect(Collectors.toList());
    }
}
