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
package de.qaware.cloud.aws.fetcher

import de.qaware.cloud.TimeRange
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
