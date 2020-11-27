/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cce.aws;

import de.qaware.cce.CostExplorer;
import de.qaware.cce.TimeRange;
import de.qaware.cce.TimeSeries;
import de.qaware.cce.Usage;
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
    private TimeRange timeRange = TimeRange.LAST_30_DAYS;
    private String instance;
    private String service;

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

    public AwsCostExplorer forInstance(String instance) {
        this.instance = instance;
        return this;
    }

    public AwsCostExplorer forService(String service) {
        this.service = service;
        return this;
    }

    public List<String> getNames() {
        if (instance != null) {
            return getInstances();
        }
        if (service != null) {
            return getServices();
        }
        throw new IllegalStateException("Don't know what names to fetch.");
    }

    private List<String> getInstances() {
        return TagNamesFetcher.withClient(costExplorerClient)
                .searchFor(instance)
                .during(timeRange)
                .fetch();
    }

    private List<String> getServices() {
        return DimensionalValuesFetcher.withClient(costExplorerClient)
                .searchFor(service)
                .during(timeRange)
                .fetchServices();
    }

    public TimeSeries getCosts() {
        if (instance != null) {
            return getInstanceCosts();
        }
        if (service != null) {
            return getServiceCosts();
        }
        return getTotalCosts();
    }

    private TimeSeries getTotalCosts() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .during(timeRange)
                .fetchCost();
    }

    private TimeSeries getServiceCosts() {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .filterByService(service)
                .during(timeRange)
                .fetchCost();
    }

    private TimeSeries getInstanceCosts() {
        List<String> tagNames = TagNamesFetcher.withClient(costExplorerClient)
                    .searchFor(instance)
                    .during(timeRange)
                    .fetch();

        TimeSeries timeSeries = new TimeSeries();

        for (String tagName : tagNames) {
            timeSeries = timeSeries.add(CostAndUsageFetcher.withClient(costExplorerClient)
                    .filterByTagName(tagName)
                    .during(timeRange)
                    .fetchCost());
        }

        return timeSeries;
    }

    public TimeSeries getUsage(Usage usage) {
        return CostAndUsageFetcher.withClient(costExplorerClient)
                .filterByTagName(instance)
                .filterByUsage(usage)
                .during(timeRange)
                .fetchUsage();
    }
}
