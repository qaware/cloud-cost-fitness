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
import de.qaware.cloud.cost.TimeSeries;
import de.qaware.cloud.cost.Usage;
import de.qaware.cloud.cost.aws.converter.RequestConverter;
import de.qaware.cloud.cost.aws.converter.ResponseConverter;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.*;
import software.amazon.awssdk.utils.StringUtils;

/**
 * Fetcher for the CostAndUsage AWS API
 */
public class CostAndUsageFetcher {
    private static final Granularity GRANULARITY = Granularity.DAILY;
    private static final String METRIC_COST = "UnblendedCost";
    private static final String METRIC_USAGE = "UsageQuantity";
    private static final String TAG_NAME = "Name";

    private final RequestConverter requestConverter = new RequestConverter();
    private final ResponseConverter responseConverter = new ResponseConverter();

    private CostExplorerClient client;
    private TimeRange timeRange;
    private Expression filter;

    /**
     * Set the cost explorer client
     *
     * @param client the cost explorer client
     * @return a new instance of the fetcher
     */
    public static CostAndUsageFetcher withClient(CostExplorerClient client) {
        CostAndUsageFetcher fetcher = new CostAndUsageFetcher();
        fetcher.client = client;
        return fetcher;
    }

    /**
     * Sets the time range
     *
     * @param timeRange the time range
     * @return the current instance of the fetcher
     */
    public CostAndUsageFetcher during(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    /**
     * Sets a filter for the AWS service
     *
     * @param service the AWS service
     * @return the current instance of the fetcher
     */
    public CostAndUsageFetcher filterByService(String service) {
        if (StringUtils.isEmpty(service)) {
            return this;
        }
        Expression expr = Expression.builder()
                .dimensions(DimensionValues.builder().key(Dimension.SERVICE).values(service).build())
                .build();
        if (filter == null) {
            filter = expr;
        } else {
            filter = Expression.builder().and(filter, expr).build();
        }
        return this;
    }

    /**
     * Sets a filter for the tag "Name" (=instance name)
     *
     * @param tagName the tag "Name"
     * @return the current instance of the fetcher
     */
    public CostAndUsageFetcher filterByTagName(String tagName) {
        if (StringUtils.isEmpty(tagName)) {
            return this;
        }
        Expression expr = Expression.builder()
                .tags(TagValues.builder().key(TAG_NAME).values(tagName).build())
                .build();
        if (filter == null) {
            filter = expr;
        } else {
            filter = Expression.builder().and(filter, expr).build();
        }
        return this;
    }

    /**
     * Sets a filter for the usage
     *
     * @param usage a usage category to filter for
     * @return the current instance of the fetcher
     */
    public CostAndUsageFetcher filterByUsage(Usage usage) {
        if (usage == null) {
            return this;
        }
        Expression expr = Expression.builder()
                .dimensions(DimensionValues.builder().key(Dimension.USAGE_TYPE_GROUP).values(usage.toString()).build())
                .build();
        if (filter == null) {
            filter = expr;
        } else {
            filter = Expression.builder().and(filter, expr).build();
        }
        return this;
    }

    /**
     * Fetch all costs given the filters
     *
     * @return the resulting time series
     */
    public TimeSeries fetchCost() {
        return fetch(METRIC_COST);
    }

    /**
     * Fetch all usage data given the filters
     *
     * @return the resulting time series
     */
    public TimeSeries fetchUsage() {
        return fetch(METRIC_USAGE);
    }

    protected Expression getFilter() {
        return filter;
    }

    private TimeSeries fetch(String metric) {
        GetCostAndUsageRequest.Builder requestBuilder = GetCostAndUsageRequest.builder()
                .granularity(GRANULARITY)
                .metrics(metric)
                .timePeriod(requestConverter.createDateInterval(timeRange));

        if (filter != null) {
            requestBuilder.filter(filter);
        }

        GetCostAndUsageResponse response = client.getCostAndUsage(requestBuilder.build());

        return responseConverter.extractValues(response.resultsByTime(), metric);
    }
}
