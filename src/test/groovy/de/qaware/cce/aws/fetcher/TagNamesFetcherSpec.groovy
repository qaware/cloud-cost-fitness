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
import software.amazon.awssdk.services.costexplorer.model.CostExplorerResponse
import software.amazon.awssdk.services.costexplorer.model.GetTagsRequest
import software.amazon.awssdk.services.costexplorer.model.GetTagsResponse
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class TagNamesFetcherSpec extends Specification {
    @Subject
    TagNamesFetcher fetcher
    CostExplorerClient client

    def setup() {
        client = Mock()
        fetcher = TagNamesFetcher.withClient(client)
    }

    @Unroll
    def "fetches data"() {
        given: "a time range"
        fetcher.during(TimeRange.YESTERDAY)

        and: "a search query"
        fetcher.searchFor(inputQuery)

        and: "an example response"
        CostExplorerResponse response = GetTagsResponse.builder()
                .tags("pair-int-solr-cloud-0", "pair-int-solr-cloud-1", "")
                .build()

        when: "the fetcher is invoked"
        def result = fetcher.fetch()

        then: "the client is called with a correct request"
        1 * client.getTags(_) >> { arguments ->
            GetTagsRequest request = arguments.get(0)
            assert request.tagKey() == "Name"
            assert request.searchString() == usedQuery
            return response
        }

        and: "the response is converted correctly"
        result.size() == 2
        result.containsAll(["pair-int-solr-cloud-0", "pair-int-solr-cloud-1"])

        where:
        inputQuery              | usedQuery
        "pair-int-solr-cloud-*" | "pair-int-solr-cloud-*"
        null                    | null
        ""                      | null
    }
}
