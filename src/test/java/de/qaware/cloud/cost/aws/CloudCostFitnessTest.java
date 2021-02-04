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
package de.qaware.cloud.cost.aws;

import de.qaware.cloud.cost.CloudProvider;
import de.qaware.cloud.cost.CostExplorer;
import de.qaware.cloud.cost.TimeSeries;
import de.qaware.cloud.cost.ValueWithUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;

import static de.qaware.cloud.cost.TimeRange.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(sum.lessThan(100.0));
    }

    @Test
    void checkTheExtrapolatedCosts() {
        String day = LocalDate.now().plusDays(30).format(ValueWithUnit.DATE_FORMATTER);
        ValueWithUnit costsForDay = costExplorer.during(LAST_30_DAYS).getCosts().extrapolate(day);
        assertTrue(costsForDay.lessThan(200.0));
    }

    @Test
    void checkTheMostExpensiveDay() {
        TimeSeries costs = costExplorer.during(LAST_30_DAYS).getCosts();
        assertEquals(costs.max().getDate().getDayOfMonth(), 1);
    }

    static List<String> serviceNameFactory() {
        return costExplorer.forService("Amazon Elastic *").getNames();
    }

    @ParameterizedTest
    @MethodSource("serviceNameFactory")
    void checkTheCostsOfElasticComputeInstance(String service) {
        ValueWithUnit sum = costExplorer.during(LAST_7_DAYS).forService(service).getCosts().sum();
        assertTrue(sum.lessThan(250.0));
    }
}
