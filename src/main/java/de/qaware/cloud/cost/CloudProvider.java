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

import java.util.ServiceLoader;

/**
 * The cloud provider enum used to get CostExplorer instances.
 */
public enum CloudProvider {
    AMAZON_AWS, GOOGLE_CLOUD, MICROSOFT_AZURE;

    /**
     * Return a new CostExplorer instance.
     * 
     * @return a cost explorer
     * @throws UnsupportedOperationException if no cost explorer could be found
     */
    public CostExplorer getCostExplorer() {
        ServiceLoader<CostExplorer> loader = ServiceLoader.load(CostExplorer.class);
        
        // this here only allows one implementation on the classpath
        return loader.findFirst().orElseThrow(UnsupportedOperationException::new);
    }
}
