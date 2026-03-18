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
package cn.carbank.idempotent.utils;

import cn.carbank.idempotent.constant.DateField;

import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具
 *
 * @author chengzhengZhou
 * @since 2020年12月14日
 */
public abstract class DateUtil {
    /**
     * 时间偏移
     * @param date 指定日期
     * @param dateField 偏移字段
     * @param offset 偏移量
     * @return
     */
    public static Date offset(Date date, DateField dateField, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(dateField.getValue(), offset);
        return calendar.getTime();
    }

}
