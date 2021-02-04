# Cloud Cost Fitness

[![Build Status](https://github.com/qaware/cloud-cost-fitness/workflows/build/badge.svg?branch=main)](https://github.com/qaware/cloud-cost-fitness/actions?query=workflow%3Abuild)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=qaware_cloud-cost-fitness&metric=alert_status)](https://sonarcloud.io/dashboard?id=qaware_cloud-cost-fitness)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=qaware_cloud-cost-fitness&metric=coverage)](https://sonarcloud.io/dashboard?id=qaware_cloud-cost-fitness)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A library to express and test your cloud costs as architecture fitness functions.

## Usage

### Dependencies

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
_TODO_

### Cloud Cost Fitness Tests using JUnit 5
_TODO_

## Maintainer

- Tobias Melson (@tmelson), <tobias.melson@qaware.de>
- M.-Leander Reimer (@lreimer), <mario-leander.reimer@qaware.de>

## License

This software is provided under the Apache v2 open source license, read the `LICENSE` file for details.