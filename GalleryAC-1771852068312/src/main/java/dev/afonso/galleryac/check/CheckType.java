package dev.afonso.galleryac.check;

public enum CheckType {
    AIM_ASSIST_A("AimAssistA"),
    AIM_ASSIST_B("AimAssistB"),
    AIM_ASSIST_C("AimAssistC"),
    AIM_ASSIST_D("AimAssistD"),
    AIM_ASSIST_E("AimAssistE"),
    AIM_ASSIST_F("AimAssistF"),
    AIM_ASSIST_G("AimAssistG"),
    AIM_ASSIST_H("AimAssistH"),
    AIM_ASSIST_I("AimAssistI"),
    AIM_ASSIST_J("AimAssistJ"),
    AIM_ASSIST_M("AimAssistM"),
    AIM_ASSIST_N("AimAssistN"),
    ANALYSIS_A("AnalysisA"),
    ANALYSIS_B("AnalysisB"),
    AIM_ROUNDED("AimRounded"),
    MOUSE("Mouse"),
    SENSITIVITY("Sensitivity");

    private final String configName;

    CheckType(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }
}