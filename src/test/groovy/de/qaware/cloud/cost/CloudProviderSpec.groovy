package de.qaware.cloud.cost

import spock.lang.Specification

class CloudProviderSpec extends Specification {

    def "Get AWS cost explorer"() {
        setup:
        System.setProperty("aws.access.key", "test")
        System.setProperty("aws.secret.key", "test")

        expect:
        CloudProvider.AMAZON_AWS.costExplorer
    }

    def "Get Google cost explorer"() {
        when:
        CloudProvider.GOOGLE_CLOUD.getCostExplorer()

        then:
        thrown(UnsupportedOperationException)
    }

    def "Get Azure cost explorer"() {
        when:
        CloudProvider.MICROSOFT_AZURE.getCostExplorer()

        then:
        thrown(UnsupportedOperationException)
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
