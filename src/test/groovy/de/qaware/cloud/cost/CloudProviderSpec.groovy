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
package de.qaware.cloud.cost

import spock.lang.Specification

class CloudProviderSpec extends Specification {


    def "Get AWS cost explorer"() {
        setup:
        System.setProperty("aws.access.key", "test")
        System.setProperty("aws.secret.key", "test")

        expect:
        CloudProvider.AMAZON_AWS.costExplorer

        cleanup:
        System.setProperty("aws.access.key", "")
        System.setProperty("aws.secret.key", "")
    }

    def "Get Google cost explorer"() {
        when:
        CloudProvider.GOOGLE_CLOUD.getCostExplorer()

        then:
        thrown(ServiceConfigurationError)
    }

    def "Get Azure cost explorer"() {
        when:
        CloudProvider.MICROSOFT_AZURE.getCostExplorer()

        then:
        thrown(ServiceConfigurationError)
    }

    def "Supported Cloud Providers"() {
        expect:
        CloudProvider.values().length == 3
    }

    def "Check valueOf"() {
        expect:
        CloudProvider.valueOf(provider)

        where:
        provider << ['AMAZON_AWS', 'GOOGLE_CLOUD', 'MICROSOFT_AZURE']
    }
}
