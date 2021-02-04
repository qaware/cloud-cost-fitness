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
package de.qaware.cloud.cost.aws.fetcher

import de.qaware.cloud.cost.TimeRange
import software.amazon.awssdk.services.costexplorer.CostExplorerClient
import software.amazon.awssdk.services.costexplorer.model.*
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class DimensionalValuesFetcherSpec extends Specification {
    @Subject
    DimensionalValuesFetcher fetcher
    CostExplorerClient client

    def setup() {
        client = Mock()
        fetcher = DimensionalValuesFetcher.withClient(client)
    }

    @Unroll
    def "fetches data"() {
        given: "a time range"
        fetcher.during(TimeRange.YESTERDAY)

        and: "a search query"
        fetcher.searchFor(inputQuery)

        and: "an example response"
        CostExplorerResponse response = GetDimensionValuesResponse.builder()
                .dimensionValues(
                        DimensionValuesWithAttributes.builder().value("EC2").build(),
                        DimensionValuesWithAttributes.builder().value("S3").build()
                )
                .build()

        when: "the fetcher is invoked"
        def result = fetcher.fetchServices()

        then: "the client is called with a correct request"
        1 * client.getDimensionValues(_) >> { arguments ->
            GetDimensionValuesRequest request = arguments.get(0)
            assert request.dimension() == Dimension.SERVICE
            assert request.searchString() == usedQuery
            return response
        }

        and: "the response is converted correctly"
        result.size() == 2
        result.containsAll(["EC2", "S3"])

        where:
        inputQuery | usedQuery
        "query"    | "query"
        null       | null
        ""         | null
    }
}
