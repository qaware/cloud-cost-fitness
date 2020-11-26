package de.qaware.cce;

import java.util.List;

/**
 * Representation of a Cost Explorer
 */
public interface CostExplorer {
    /**
     * Sets a time range for the request
     *
     * @param timeRange a time range
     * @return the current CostExplorer
     */
    CostExplorer during(TimeRange timeRange);

    /**
     * Sets a filter for an instance name
     *
     * @param instance an instance name
     * @return the current CostExplorer
     */
    CostExplorer forInstance(String instance);

    /**
     * Sets a filter for a cloud provider service
     *
     * @param service a cloud provider service
     * @return the current CostExplorer
     */
    CostExplorer forService(String service);

    /**
     * Sets a filter for all instances
     *
     * @return the current CostExplorer
     */
    CostExplorer forAllInstances();

    /**
     * Sets a filter for all cloud provider services
     *
     * @return the current CostExplorer
     */
    CostExplorer forAllServices();

    /**
     * Fetch the names
     *
     * @return a list of names according to the filters
     */
    List<String> getNames();

    /**
     * Fetch the costs
     *
     * @return a time series containing costs
     */
    TimeSeries getCosts();

    /**
     * Fetch the instance usage
     *
     * @return a time series containing usage
     */
    TimeSeries getUsage(Usage usage);
}
