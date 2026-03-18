/*
 * Copyright 2020 chengzhengZhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.carbank.idempotent.constant;

import java.util.concurrent.TimeUnit;

/**
 * 时间字段枚举
 *
 * @author chengzhengZhou
 * @since 2020年12月14日
 */
public enum DateField {
    YEAR(1),
    MONTH(2),
    WEEK_OF_YEAR(3),
    WEEK_OF_MONTH(4),
    DAY_OF_MONTH(5),
    DAY_OF_YEAR(6),
    DAY_OF_WEEK(7),
    DAY_OF_WEEK_IN_MONTH(8),
    AM_PM(9),
    HOUR(10),
    HOUR_OF_DAY(11),
    MINUTE(12),
    SECOND(13),
    MILLISECOND(14);

    private int value;

    private DateField(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static DateField of(TimeUnit timeUnit) {
        switch(timeUnit) {
            case DAYS:
                return DAY_OF_YEAR;
            case HOURS:
                return HOUR;
            case MINUTES:
                return MINUTE;
            case SECONDS:
                return SECOND;
            case MILLISECONDS:
                return MILLISECOND;
            case NANOSECONDS:
            case MICROSECONDS:
            default:
                return null;
        }
    }
}
