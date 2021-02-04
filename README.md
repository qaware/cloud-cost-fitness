# Cloud Cost Fitness

[![Build Status](https://github.com/qaware/cloud-cost-fitness/workflows/build/badge.svg?branch=main)](https://github.com/qaware/cloud-cost-fitness/actions?query=workflow%3Abuild)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=qaware_cloud-cost-fitness&metric=alert_status)](https://sonarcloud.io/dashboard?id=qaware_cloud-cost-fitness)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=qaware_cloud-cost-fitness&metric=coverage)](https://sonarcloud.io/dashboard?id=qaware_cloud-cost-fitness)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A library to express and test your cloud costs as architecture fitness functions.

## Usage

### Dependency Definition

If you are using Gradle as a build tool the following dependency needs to be added to your
project dependencies.
```groovy
testImplementation 'de.qaware.cloud.cost:cloud-cost-fitness:1.0.0'
```

If you are using Maven as a build tool the following dependency needs to be added to your project dependencies.

```xml

<dependency>
    <groupId>de.qaware.cloud.cost</groupId>
    <artifactId>cloud-cost-fitness</artifactId>
    <version>1.0.0</version>
    <type>test</type>
</dependency>
```

### Cloud Cost Fitness Tests using Spock

The Cloud Cost Fitness library can be used nicely in combination with the Spock Framework. Simply define a test
specification, create a cost explorer for the desired cloud provider and implement a dedicated test for each cloud cost
fitness function.

```groovy
@Requires({ sys['aws.access.key'] && sys['aws.secret.key'] })
class CloudCostFitnessSpec extends Specification {
    @Shared
    CostExplorer costExplorer = CloudProvider.AMAZON_AWS.getCostExplorer()

    def "checks the total costs"() {
        expect: "the total costs to be less than a limit"
        costExplorer.during(YESTERDAY).getCosts().sum().lessThan(100.0)
    }

    @Unroll
    def "check the costs of EC2 instance #service"() {
        then: "the cost is less than a limit"
        costExplorer.during(LAST_7_DAYS).forService(service).getCosts().sum().lessThan(250.0)

        where:
        service << costExplorer.forService("Amazon Elastic *").getNames()
    }
}
```

For the complete example have a look at `CloudCostFitnessSpec.groovy` found under `src/test/groovy/`.

### Cloud Cost Fitness Tests using JUnit 5

The Cloud Cost Fitness library can also be used nicely in combination with JUnit 5. Simply define a test class, create a
cost explorer for the desired cloud provider and implement a dedicated test for each cloud cost fitness function.

```java

@EnabledIfSystemProperty(named = "aws.access.key", matches = ".*")
@EnabledIfSystemProperty(named = "aws.secret.key", matches = ".*")
class CloudCostFitnessTest {

    private static CostExplorer costExplorer;

    @BeforeAll
    static void setUpAll() {
        costExplorer = CloudProvider.AMAZON_AWS.getCostExplorer();
    }

    @Test
    void checkTheTotalCosts() {
        ValueWithUnit sum = costExplorer.during(YESTERDAY).getCosts().sum();
        assertTrue(sum.lessThan(42.0));
    }

    @ParameterizedTest
    @MethodSource("serviceNameFactory")
    void checkTheCostsOfElasticComputeInstance(String service) {
        ValueWithUnit sum = costExplorer.during(LAST_7_DAYS).forService(service).getCosts().sum();
        assertTrue(sum.lessThan(250.0));
    }

    static List<String> serviceNameFactory() {
        return costExplorer.forService("Amazon Elastic *").getNames();
    }
}
```

For the complete example have a look at `CloudCostFitnessTest.java` found under `src/test/java/`.

## Maintainer

- Tobias Melson (@tmelson), <tobias.melson@qaware.de>
- M.-Leander Reimer (@lreimer), <mario-leander.reimer@qaware.de>

## License

This software is provided under the Apache v2 open source license, read the `LICENSE` file for details.