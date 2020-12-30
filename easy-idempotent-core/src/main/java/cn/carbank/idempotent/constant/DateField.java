package cn.carbank.idempotent.constant;

import java.util.concurrent.TimeUnit;

/**
 * 时间字段枚举
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
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
