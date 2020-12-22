package cn.carbank.utils;

import cn.carbank.constant.DateField;

import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
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
