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

class TimeRangeSpec extends Specification {
    def "Check time range values"() {
        expect:
        TimeRange.values().length == 4
    }

    def "Check valueOf"() {
        expect:
        TimeRange.valueOf(provider)

        where:
        provider << ['YESTERDAY', 'LAST_7_DAYS', 'LAST_30_DAYS', 'LAST_6_MONTHS']
    }
}
