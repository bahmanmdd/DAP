package pgraph.anchorage;

import pgraph.anchorage.policy.AnchorPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 02.12.2013
 * Time: 10:55
 * To change this template use File | Settings | File Templates.
 */
public class AnchorageExperimentConfig {
    private AnchorPolicy anchorPolicy;
    private String outputSummaryFile;
    private String outputFile;
    private String configHeader;
    private int maxIntersection = -1;

    public double getRiskFactor() {
        return riskFactor;
    }

    public void setRiskFactor(double riskFactor) {
        this.riskFactor = riskFactor;
    }

    private double riskFactor=0;

    public AnchorageExperimentConfig(AnchorPolicy anchorPolicy, String outputSummaryFile, String outputFile,String configHeader) {
        this.anchorPolicy = anchorPolicy;
        this.outputSummaryFile = outputSummaryFile;
        this.outputFile = outputFile;
        this.configHeader = configHeader;
    }

    public AnchorageExperimentConfig(AnchorPolicy anchorPolicy, String outputSummaryFile, String outputFile, String configHeader, double  riskFactor) {
        this.anchorPolicy = anchorPolicy;
        this.outputSummaryFile = outputSummaryFile;
        this.outputFile = outputFile;
        this.configHeader = configHeader;
        this.riskFactor = riskFactor;
    }

    public String getConfigHeader() {
        return configHeader;
    }

    public void setConfigHeader(String configHeader) {
        this.configHeader = configHeader;
    }

    public AnchorPolicy getAnchorPolicy() {
        return anchorPolicy;
    }

    public void setAnchorPolicy(AnchorPolicy anchorPolicy) {
        this.anchorPolicy = anchorPolicy;
    }


    public String getOutputSummaryFile() {
        return outputSummaryFile;
    }

    public void setOutputSummaryFile(String outputSummaryFile) {
        this.outputSummaryFile = outputSummaryFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public int getMaxIntersection() {
        return maxIntersection;
    }
}
