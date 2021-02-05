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
package de.qaware.cloud.cost.aws

import de.qaware.cloud.cost.CloudProvider
import de.qaware.cloud.cost.TimeRange
import de.qaware.cloud.cost.TimeSeries
import software.amazon.awssdk.services.costexplorer.CostExplorerClient
import spock.lang.Specification
import spock.lang.Title

@Title("Test specification for the AWS cost explorer implementation")
class AwsCostExplorerSpec extends Specification {

    CostExplorerClient client
    AwsCostExplorer costExplorer

    void setup() {
        client = Mock(CostExplorerClient)
        costExplorer = Spy(new AwsCostExplorer(client))
    }

    def "Check during filter"() {
        expect:
        costExplorer.during(TimeRange.YESTERDAY) == costExplorer
    }

    def "Check forInstance filter"() {
        expect:
        costExplorer.forInstance("test-service") == costExplorer
    }

    def "Check forService filter"() {
        expect:
        costExplorer.forService("EC2") == costExplorer
    }

    def "Check getNames empty"() {
        when:
        costExplorer.getNames()
        then:
        thrown(IllegalStateException)
    }

    def "Check getNames for instance"() {
        given:
        costExplorer.during(TimeRange.YESTERDAY).forInstance("test")
        costExplorer.getInstances() >> ["test"]

        when:
        def names = costExplorer.getNames()

        then:
        names.size() == 1
    }

    def "Check get costs for service"() {
        given:
        costExplorer.during(TimeRange.YESTERDAY).forService("test")
        costExplorer.getServiceCosts() >> new TimeSeries()

        expect:
        costExplorer.getServiceCosts()
    }

    def "Check get costs for instance"() {
        given:
        costExplorer.during(TimeRange.YESTERDAY).forInstance("test")
        costExplorer.getInstanceCosts() >> new TimeSeries()

        expect:
        costExplorer.getCosts()
    }

    def "Check get total costs"() {
        given:
        costExplorer.during(TimeRange.YESTERDAY)
        costExplorer.getTotalCosts() >> new TimeSeries()

        expect:
        costExplorer.getCosts()
    }

    def "Check supports AWS only"() {
        expect:
        costExplorer.supports(CloudProvider.AMAZON_AWS)
        and:
        !costExplorer.supports(CloudProvider.GOOGLE_CLOUD)
        and:
        !costExplorer.supports(CloudProvider.MICROSOFT_AZURE)
    }
}
