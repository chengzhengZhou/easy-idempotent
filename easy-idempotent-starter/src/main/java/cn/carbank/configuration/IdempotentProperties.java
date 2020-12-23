package cn.carbank.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 请填写类注释
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月23日
 */
@ConfigurationProperties(prefix = "idempotent.lock")
public class IdempotentProperties {

    private Boolean enable = true;

    private String groupName;

    private Integer namespace;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getNamespace() {
        return namespace;
    }

    public void setNamespace(Integer namespace) {
        this.namespace = namespace;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
