/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
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
     * Wildcards are allowed when calling {@link #getNames()} or {@link #getCosts()}
     *
     * @param instance an instance name
     * @return the current CostExplorer
     */
    CostExplorer forInstance(String instance);

    /**
     * Sets a filter for a cloud provider service
     * Wildcards are allowed when calling {@link #getNames()}
     *
     * @param service a cloud provider service
     * @return the current CostExplorer
     */
    CostExplorer forService(String service);

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
