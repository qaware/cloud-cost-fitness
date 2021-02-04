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
package de.qaware.cloud;

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
     * @param usage a usage filter
     * @return a time series containing usage
     */
    TimeSeries getUsage(Usage usage);
}
