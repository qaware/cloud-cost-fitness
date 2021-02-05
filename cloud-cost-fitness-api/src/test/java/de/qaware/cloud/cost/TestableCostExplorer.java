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
package de.qaware.cloud.cost;

import java.util.Collections;
import java.util.List;

public class TestableCostExplorer implements CostExplorer {
    @Override
    public CostExplorer during(TimeRange timeRange) {
        return this;
    }

    @Override
    public CostExplorer forInstance(String instance) {
        return this;
    }

    @Override
    public CostExplorer forService(String service) {
        return this;
    }

    @Override
    public List<String> getNames() {
        return Collections.emptyList();
    }

    @Override
    public TimeSeries getCosts() {
        return null;
    }

    @Override
    public TimeSeries getUsage(Usage usage) {
        return null;
    }

    @Override
    public boolean supports(CloudProvider cloudProvider) {
        return CloudProvider.AMAZON_AWS.equals(cloudProvider);
    }
}
