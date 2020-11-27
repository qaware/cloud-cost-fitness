/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cce

import spock.lang.Specification

import java.time.LocalDate

import static de.qaware.cce.CloudProvider.AMAZON_AWS
import static de.qaware.cce.TimeRange.*
import static de.qaware.cce.aws.AwsUsage.EC2_RUNNING_HOURS

class CloudCostFitness extends Specification {
    CostExplorer costExplorer

    def setup() {
        costExplorer = CloudCostExplorer.forProvider(AMAZON_AWS)
    }

    def "checks the total costs"() {
        expect: "the total costs to be less than a limit"
        costExplorer.during(YESTERDAY).getCosts().sum().lessThan(50.0)
    }

    def "checks the extrapolated costs"() {
        given: "a day next month"
        def day = LocalDate.now().plusDays(30).format(ValueWithUnit.DATE_FORMATTER)

        expect: "the extrapolated total costs to be less than a limit"
        costExplorer.during(LAST_30_DAYS).getCosts().extrapolate(day).lessThan(100.0)
    }

    def "checks that the most expensive day is the first day of the month"() {
        when: "all costs are retrieved"
        def costs = costExplorer.during(LAST_30_DAYS).getCosts()

        then: "the first day of the month is the most expensive"
        costs.max().getDate().toCalendar().get(Calendar.DAY_OF_MONTH) == 1
    }

    def "checks that the most expensive instance in INT is ignite"() {
        when: "the instance names are retrieved"
        def instances = costExplorer.forInstance("pair-int-*").getNames()

        and: "the costs are fetched for each of them"
        def costs = [:]
        instances.forEach() {instance ->
            costs[instance] = costExplorer.forInstance(instance).during(LAST_30_DAYS).getCosts().sum()
        }

        and: "the name of the most expensive instance is extracted"
        String name = costs.max { it.value }.key

        then: "the name matches an ignite instance"
        name.contains("pair-int-ignite")
    }

    def "checks the costs of all EC2 instances"() {
        when: "all services related to EC2 are fetched"
        def services = costExplorer.forService("Amazon Elastic *").getNames()

        then: "their costs are less than a limit"
        services.each {service ->
            assert costExplorer.during(LAST_7_DAYS).forService(service).getCosts().sum().lessThan(200.0)
        }
    }

    def "checks the costs for ignite last week"() {
        when: "all ignite instances are fetched"
        def instances = costExplorer.during(LAST_7_DAYS).forInstance("pair-int-ignite-*").getNames()

        then: "the individual costs are less than a limit"
        instances.each {
            assert costExplorer.during(LAST_7_DAYS).forInstance(it).getCosts().sum().lessThan(20.0)
        }
    }

    def "checks that the ignite instances were up all day"() {
        when: "the usage of the ignite instances are retrieved"
        TimeSeries ignite1 = costExplorer.during(YESTERDAY).forInstance("pair-int-ignite-0").getUsage(EC2_RUNNING_HOURS)
        TimeSeries ignite2 = costExplorer.during(YESTERDAY).forInstance("pair-int-ignite-1").getUsage(EC2_RUNNING_HOURS)

        then: "the average uptime is long enough"
        !ignite1.add(ignite2).min().lessThan(48.0)
    }
}
