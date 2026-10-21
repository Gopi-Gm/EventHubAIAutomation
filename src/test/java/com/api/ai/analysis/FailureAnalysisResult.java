package com.api.ai.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Structured output from the AI failure analysis.
 *
 * This POJO is:
 *  - Populated by parsing the JSON returned from OpenAI
 *  - Validated against src/test/resources/ai/schemas/failure-analysis.schema.json
 *  - Written to target/ai-analysis/ai-report.json at end of run
 *
 * Fields:
 *  - scenario        : the test/scenario name (filled from FailureContext if AI omits it)
 *  - category        : raw string category from AI (normalized to FailureCategory enum)
 *  - summary         : 1–2 sentence plain-English summary of the failure
 *  - likelyCause     : the single most likely root cause
 *  - suggestedChecks : ordered list of actionable checks/fixes to investigate
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FailureAnalysisResult {

    @JsonProperty("scenario")
    private String scenario;

    @JsonProperty("category")
    private String category;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("likelyCause")
    private String likelyCause;

    @JsonProperty("suggestedChecks")
    private List<String> suggestedChecks = new ArrayList<>();

    // Derived field — set by FailureAnalyzer after normalization
    private FailureCategory resolvedCategory;

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public String getScenario()                    { return scenario; }
    public void   setScenario(String scenario)     { this.scenario = scenario; }

    public String getCategory()                    { return category; }
    public void   setCategory(String category)     { this.category = category; }

    public String getSummary()                     { return summary; }
    public void   setSummary(String summary)       { this.summary = summary; }

    public String getLikelyCause()                 { return likelyCause; }
    public void   setLikelyCause(String cause)     { this.likelyCause = cause; }

    public List<String> getSuggestedChecks()                         { return suggestedChecks; }
    public void         setSuggestedChecks(List<String> checks)      { this.suggestedChecks = checks != null ? checks : new ArrayList<>(); }

    public FailureCategory getResolvedCategory()                     { return resolvedCategory; }
    public void            setResolvedCategory(FailureCategory cat)  { this.resolvedCategory = cat; }

    @Override
    public String toString() {
        return "FailureAnalysisResult{" +
                "scenario='" + scenario + '\'' +
                ", category='" + category + '\'' +
                ", resolvedCategory=" + resolvedCategory +
                ", summary='" + summary + '\'' +
                ", likelyCause='" + likelyCause + '\'' +
                ", suggestedChecks=" + suggestedChecks +
                '}';
    }
}
