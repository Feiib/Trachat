package com.fei.aitravelassistant.tool;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebSearchTool {
    private final String apikey;
    public WebSearchTool(String apikey) {
        this.apikey = apikey;
    }

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";
    @Tool(description = "搜索网页")
    public String search(String query) {
        try {
            HttpResponse response = HttpRequest.get(SEARCH_API_URL)
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + apikey)
                    .form(Map.of(
                            "q", query,
                            "engine", "bing"
                    ))
                    .execute();

            JSONObject jsonObject = JSONUtil.parseObj(response.body());
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            if (organicResults == null) {
                return "No results or API error: " + response.body();
            }

            List<Object> objects = organicResults.subList(0, Math.min(5, organicResults.size()));
            return objects.stream()
                    .map(obj -> ((JSONObject) obj).toString())
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            return "search failed: " + e.getMessage();
        }

    }
}
