package com.mycompany.echo.AllModels;

import org.springframework.stereotype.Component;

@Component
public class BuildInfoModel {
    private String chart_name;
    private String chart_release_name;
    private String branch;
    private String mode;

    public String getChart_name() {
        return chart_name;
    }

    public void setChart_name(String chart_name) {
        this.chart_name = chart_name;
    }

    public String getChart_release_name() {
        return chart_release_name;
    }

    public void setChart_release_name(String chart_release_name) {
        this.chart_release_name = chart_release_name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
