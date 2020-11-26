package de.qaware.cce


import static TimeRange.LAST_SIX_MONTHS
import static TimeRange.LAST_WEEK
import static de.qaware.cce.CloudProvider.AMAZON_AWS
import static de.qaware.cce.aws.Usage.EC2_RUNNING_HOURS

class Costs {
    static void main(String[] args) {
        //'Show the most expensive day last month'()
        //'Show the costs of all AWS services during last week'()
        //'Show the most expensive instance'()
        //'Show the usage of an instance'()
        //'Show the costs of multiple instances'()
        'Extrapolate the costs of an instance'()
    }

    static def 'Show the most expensive day last month'() {
        def costs = CloudCostExplorer.forProvider(AMAZON_AWS).getTotalCosts()
        println("Maximum: " + costs.max())
    }

    static def 'Show the costs of all AWS services during last week'() {
        def client = CloudCostExplorer.forProvider(AMAZON_AWS)
        def services = client.getServices()

        services.each {service ->
            def costs = client.during(LAST_WEEK).filterFor(service).getServiceCosts()
            println(service + ": " + costs.sum())
        }
    }

    static def 'Show the most expensive instance'() {
        def client = CloudCostExplorer.forProvider(AMAZON_AWS)
        def instances = client.getInstanceNames()

        def costs = [:]
        instances.forEach() {instance ->
            costs[instance] = client.filterFor(instance).during(LAST_SIX_MONTHS).getInstanceCosts().sum()
        }

        println(costs.max { it.value })
    }

    static def 'Show the usage of an instance'() {
        def instanceName = "pair-int-ignite-0"
        println(CloudCostExplorer.forProvider(AMAZON_AWS).filterFor(instanceName).filterFor(EC2_RUNNING_HOURS).during(LAST_WEEK).getInstanceUsage())
    }

    static def 'Show the costs of multiple instances'() {
        def client = CloudCostExplorer.forProvider(AMAZON_AWS)

        def instanceCosts1 = client.filterFor("pair-int-ignite-0").getInstanceCosts()
        def instanceCosts2 = client.filterFor("pair-int-ignite-1").getInstanceCosts()
        println(instanceCosts1.add(instanceCosts2))
    }

    static def 'Extrapolate the costs of an instance'() {
        println(CloudCostExplorer.forProvider(AMAZON_AWS).filterFor("pair-int-ignite-0").getInstanceCosts().extrapolate("2020-12-06"))
    }
}

