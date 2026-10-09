package com.zzsd.inolibi.utils;

public class LlmResponseParser {

    private static final String CONCLUSION_START = "<<<CONCLUSION>>>";
    private static final String CONCLUSION_END   = "<<<END_CONCLUSION>>>";
    private static final String ECHARTS_START    = "<<<ECHARTS>>>";
    private static final String ECHARTS_END      = "<<<END_ECHARTS>>>";

    public static AnalysisResult parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new AnalysisResult("", "");
        }

        String conclusion = extractBetween(raw, CONCLUSION_START, CONCLUSION_END);
        String echarts    = extractBetween(raw, ECHARTS_START, ECHARTS_END);

        // 兜底：如果 ECharts 段没解析到，尝试从 ``` 代码块里取
        if (echarts.isBlank()) {
            echarts = extractFromMarkdownCodeBlock(raw);
        }

        // 兜底：如果结论段没解析到，取第一个标记之前的所有文本
        if (conclusion.isBlank()) {
            int firstMark = raw.indexOf(ECHARTS_START);
            if (firstMark > 0) {
                conclusion = raw.substring(0, firstMark).trim();
            }
        }

        return new AnalysisResult(conclusion.trim(), cleanEcharts(echarts));
    }

    private static String extractBetween(String text, String start, String end) {
        int s = text.indexOf(start);
        if (s < 0) return "";
        int e = text.indexOf(end, s + start.length());
        if (e < 0) return "";
        return text.substring(s + start.length(), e).trim();
    }

    private static String extractFromMarkdownCodeBlock(String text) {
        // 匹配 ```javascript ... ``` 或 ``` ... ```
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("```(?:javascript|js)?\\s*([\\s\\S]*?)```")
                .matcher(text);
        return m.find() ? m.group(1).trim() : "";
    }

    private static String cleanEcharts(String code) {
        if (code.isBlank()) return "";
        // 去掉可能残留的 markdown 标记
        return code.replaceAll("```[a-zA-Z]*", "").replace("```", "").trim();
    }
}
