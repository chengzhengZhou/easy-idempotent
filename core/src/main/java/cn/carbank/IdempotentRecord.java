package cn.carbank;

import java.io.Serializable;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月14日
 */
public class IdempotentRecord implements Serializable {

    private String key;

    private String value;

    private Date expireTime;

    private Date addTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "IdempotentRecord{" + "key='" + key + '\'' + ", value='" + value + '\'' + ", expireTime=" + expireTime + ", addTime=" + addTime + '}';
    }
}
