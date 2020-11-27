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
import de.qaware.cce.aws.AwsUsage
import software.amazon.awssdk.services.costexplorer.CostExplorerClient
import software.amazon.awssdk.services.costexplorer.model.*
import spock.lang.Specification
import spock.lang.Subject

class CostAndUsageFetcherSpec extends Specification {
    @Subject
    CostAndUsageFetcher fetcher
    CostExplorerClient client

    def setup() {
        client = Mock()
        fetcher = CostAndUsageFetcher.withClient(client)
    }

    def "fetches data without filters"() {
        given: "a time range"
        fetcher.during(TimeRange.YESTERDAY)

        and: "an example response"
        CostExplorerResponse response = GetCostAndUsageResponse.builder()
                .resultsByTime(ResultByTime.builder()
                        .timePeriod(DateInterval.builder()
                                .start("2020-08-01")
                                .end("2020-08-01")
                                .build())
                        .total(Collections.singletonMap("UnblendedCost",
                                MetricValue.builder()
                                        .amount("10")
                                        .unit("EUR")
                                        .build()))
                        .build())
                .build()

        when: "the fetcher is invoked"
        def result = fetcher.fetchCost()

        then: "the client is called with a correct request"
        1 * client.getCostAndUsage(_) >> { arguments ->
            GetCostAndUsageRequest request = arguments.get(0)
            assert request.granularity() == Granularity.DAILY
            assert request.metrics()[0].toString() == "UnblendedCost"
            return response
        }

        and: "the response is converted correctly"
        result.size() == 1
        result.elements[0].value == 10.0d
        result.elements[0].unit == "EUR"
    }

    def "filters by service name"() {
        when: "the filter is set"
        fetcher.filterByService("EC2")

        then: "the filter is applied"
        fetcher.filter.dimensions().key() == Dimension.SERVICE
        fetcher.filter.dimensions().values() == ["EC2"]
    }

    def "filters by tag name"() {
        when: "the filter is set"
        fetcher.filterByTagName("pair-int-solr-cloud-0")

        then: "the filter is applied"
        fetcher.filter.tags().key() == "Name"
        fetcher.filter.tags().values() == ["pair-int-solr-cloud-0"]
    }

    def "filters by tag name and usage"() {
        when: "the first filter is set"
        fetcher.filterByTagName("pair-int-solr-cloud-0")

        and: "the second filter is set"
        fetcher.filterByUsage(AwsUsage.EC2_RUNNING_HOURS)

        then: "both filters are applied"
        fetcher.filter.and().size() == 2
        fetcher.filter.and()[0].tags().key() == "Name"
        fetcher.filter.and()[0].tags().values() == ["pair-int-solr-cloud-0"]
        fetcher.filter.and()[1].dimensions().key() == Dimension.USAGE_TYPE_GROUP
        fetcher.filter.and()[1].dimensions().values() == ["EC2: Running Hours"]
    }
}
