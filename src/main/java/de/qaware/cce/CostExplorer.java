package de.qaware.cce;

import de.qaware.cce.aws.Usage;

import java.util.List;

/**
 * Representation of a Cost Explorer
 */
public interface CostExplorer {
    /**
     * Sets a time range for the request
     *
     * @param timeRange a time range
     * @return the current instance
     */
    CostExplorer during(TimeRange timeRange);

    /**
     * Sets a filter given a search query
     *
     * @param query a search query
     * @return the current instance
     */
    CostExplorer filterFor(String query);

    /**
     * Sets a filter for a usage category
     *
     * @param usage a usage category
     * @return the current instance
     */
    CostExplorer filterFor(Usage usage);

    /**
     * Fetch the names of all instances
     *
     * @return a list of instance names
     */
    List<String> getInstanceNames();

    /**
     * Fetch a list of all services
     *
     * @return a list of services
     */
    List<String> getServices();

    /**
     * Fetch a list of usage categories
     *
     * @return a list of usage categories
     */
    List<String> getUsageCategories();

    /**
     * Fetch the total costs
     *
     * @return a time series with the total costs
     */
    TimeSeries getTotalCosts();

    /**
     * Fetch the AWS service costs
     *
     * @return a time series with the costs of the service
     */
    TimeSeries getServiceCosts();

    /**
     * Fetch the AWS instance costs
     *
     * @return a time series with the costs of the instance
     */
    TimeSeries getInstanceCosts();

    /**
     * Fetch the AWS instance usage
     *
     * @return a time series with the usage of the instance
     */
    TimeSeries getInstanceUsage();
}
