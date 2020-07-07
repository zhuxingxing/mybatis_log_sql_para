package com.star;

import java.util.List;

/**
 * sql
 *
 * @author: star.zhu
 * @date: 2020-06-22
 */
public class MybatisLog {

    /**
     * sql
     */
    public String preparing;

    /**
     * 参数
     */
    public List<LogParameters> parameters;

    public List<LogParameters> getParameters() {
        return parameters;
    }

    public String getPreparing() {
        return preparing;
    }

    public void setParameters(List<LogParameters> parameters) {
        this.parameters = parameters;
    }

    public void setPreparing(String preparing) {
        this.preparing = preparing;
    }

}
