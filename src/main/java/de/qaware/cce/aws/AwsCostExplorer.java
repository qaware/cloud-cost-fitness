package de.qaware.cce.aws;

import de.qaware.cce.CostExplorer;
import de.qaware.cce.TimeRange;
import de.qaware.cce.TimeSeries;
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
 * Collect data from the AWS API
 */
public class AwsCostExplorer implements CostExplorer {
    private CostExplorerClient costExplorerClient;
    private TimeRange timeRange = TimeRange.LAST_MONTH;
    private String query;
    private Usage usage;

    public AwsCostExplorer() {
        initCostExplorerClient(System.getProperty("aws.access.key"), System.getProperty("aws.secret.key"));
    }

    public AwsCostExplorer(String accessKey, String secretKey) {
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

    public AwsCostExplorer during(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    public AwsCostExplorer filterFor(String query) {
        this.query = query;
        return this;
    }

    public AwsCostExplorer filterFor(Usage usage) {
        this.usage = usage;
        return this;
    }

    public List<String> getInstanceNames() {
        return TagNamesFetcher.withClient(costExplorerClient)
                .searchFor(query)
                .during(timeRange)
                .fetch();
    }

    public List<String> getServices() {
        return DimensionalValuesFetcher.withClient(costExplorerClient)
                .searchFor(query)
                .during(timeRange)
                .fetchServices();
    }

    public List<String> getUsageCategories() {
        return DimensionalValuesFetcher.withClient(costExplorerClient)
                .searchFor(query)
                .during(timeRange)
                .fetchUsage();
    }

    public TimeSeries getTotalCosts() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .during(timeRange)
                .fetchCost();
    }

    public TimeSeries getServiceCosts() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .filterByService(query)
                .during(timeRange)
                .fetchCost();
    }

    public TimeSeries getInstanceCosts() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .filterByTagName(query)
                .during(timeRange)
                .fetchCost();
    }

    public TimeSeries getInstanceUsage() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .filterByTagName(query)
                .filterByUsage(usage)
                .during(timeRange)
                .fetchUsage();
    }
}
