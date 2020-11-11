package de.qaware.cce.aws;

import de.qaware.cce.aws.data.TimeSeries;
import de.qaware.cce.aws.fetcher.CostAndUsageFetcher;
import de.qaware.cce.aws.fetcher.DimensionalValuesFetcher;
import de.qaware.cce.aws.fetcher.TagNamesFetcher;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;

import java.util.List;

/**
 * Http client to connect to the AWS API
 */
public class CloudCostExplorer {
    private CostExplorerClient costExplorerClient;
    private TimeRange timeRange = TimeRange.LAST_MONTH;
    private String query;
    private Usage usage;

    public CloudCostExplorer() {
        initCostExplorerClient(System.getProperty("aws.access.key"), System.getProperty("aws.secret.key"));
    }

    public CloudCostExplorer(String accessKey, String secretKey) {
        initCostExplorerClient(accessKey, secretKey);
    }

    private void initCostExplorerClient(String accessKey, String secretKey) {
        AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        StaticCredentialsProvider credentialProvider = StaticCredentialsProvider.create(awsCredentials);

        costExplorerClient = CostExplorerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialProvider)
                .build();
    }

    /**
     * Sets a time range for the request
     *
     * @param timeRange a time range
     * @return the current instance
     */
    public CloudCostExplorer during(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    /**
     * Sets a filter given a search query
     *
     * @param query a search query
     * @return the current instance
     */
    public CloudCostExplorer filterFor(String query) {
        this.query = query;
        return this;
    }

    /**
     * Sets a filter for a usage category
     *
     * @param usage a usage category
     * @return the current instance
     */
    public CloudCostExplorer filterFor(Usage usage) {
        this.usage = usage;
        return this;
    }

    /**
     * Fetch the names of the AWS instances
     *
     * @return a list of instance names
     */
    public List<String> getInstanceNames() {
        return TagNamesFetcher.withClient(costExplorerClient)
                .searchFor(query)
                .during(timeRange)
                .fetch();
    }

    /**
     * Fetch a list of AWS services
     *
     * @return a list of services
     */
    public List<String> getServices() {
        return DimensionalValuesFetcher.withClient(costExplorerClient)
                .searchFor(query)
                .during(timeRange)
                .fetchServices();
    }

    /**
     * Fetch a list of usage categories
     *
     * @return a list of usage categories
     */
    public List<String> getUsageCategories() {
        return DimensionalValuesFetcher.withClient(costExplorerClient)
                .searchFor(query)
                .during(timeRange)
                .fetchUsage();
    }

    /**
     * Fetch the total costs
     *
     * @return a time series with the total costs
     */
    public TimeSeries getTotalCosts() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .during(timeRange)
                .fetchCost();
    }

    /**
     * Fetch the AWS service costs
     *
     * @return a time series with the costs of the AWS service
     */
    public TimeSeries getServiceCosts() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .filterByService(query)
                .during(timeRange)
                .fetchCost();
    }

    /**
     * Fetch the AWS instance costs
     *
     * @return a time series with the costs of the AWS instance
     */
    public TimeSeries getInstanceCosts() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .filterByTagName(query)
                .during(timeRange)
                .fetchCost();
    }

    /**
     * Fetch the AWS instance usage
     *
     * @return a time series with the usage of the AWS instance
     */
    public TimeSeries getInstanceUsage() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .filterByTagName(query)
                .filterByUsage(usage)
                .during(timeRange)
                .fetchUsage();
    }
}
