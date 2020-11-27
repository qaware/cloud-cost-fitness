/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cce.aws.fetcher

import de.qaware.cce.TimeRange
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
