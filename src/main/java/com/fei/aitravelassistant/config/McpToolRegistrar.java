package com.fei.aitravelassistant.config;


import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class McpToolRegistrar {

    @Value("${amap.api-key}")
    private String amapApiKey;

    /**
     * 初始化 MCP 工具客户端并注册为 ToolCallback[]
     */
    @Bean
    public ToolCallback[] amapMcpTools() {
        try {
            /*ServerParameters params = ServerParameters.builder("cmd")
                    .args("/c", "npx", "-y", "@amap/amap-maps-mcp-server")
                    .env(Map.of("AMAP_MAPS_API_KEY", amapApiKey))
                    .build();

            StdioClientTransport transport = new StdioClientTransport(params);
            McpSyncClient client = McpClient.sync(transport)
                    .requestTimeout(Duration.ofSeconds(10))
                    .build();*/
            String os = System.getProperty("os.name").toLowerCase();
            boolean isWindows = os.contains("win");

            ServerParameters params = isWindows
                    ? ServerParameters.builder("cmd")
                    .args("/c", "npx", "-y", "@amap/amap-maps-mcp-server")
                    .env(Map.of("AMAP_MAPS_API_KEY", amapApiKey))
                    .build()
                    : ServerParameters.builder("sh")
                    .args("-lc", "npx -y @amap/amap-maps-mcp-server")
                    .env(Map.of("AMAP_MAPS_API_KEY", amapApiKey))
                    .build();

            StdioClientTransport transport = new StdioClientTransport(params);
            McpSyncClient client = McpClient.sync(transport)
                    .requestTimeout(Duration.ofSeconds(100))
                    .build();

            client.initialize();
            McpSchema.ListToolsResult listToolsResult = client.listTools();

            log.info("加载amap MCP 工具 {} 个", listToolsResult.tools().size());

            return listToolsResult.tools().stream()
                    .map(tool -> createToolCallback(tool, client))
                    .toArray(ToolCallback[]::new);

        } catch (Exception e) {
            log.error("初始化amap MCP 工具失败: {}", e.getMessage(), e);
            return new ToolCallback[0];
        }
    }

    @Bean
    public ToolCallback[] railwayMcpTools() {
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

           /* ServerParameters params = ServerParameters.builder("cmd")
                    .args("/c", "npx", "-y", "12306-mcp")
                    .build();*/
            ServerParameters params = isWindows
                    ? ServerParameters.builder("cmd")
                    .args("/c", "npx", "-y", "12306-mcp")
                    .build()
                    : ServerParameters.builder("sh")
                    .args("-lc", "npx -y 12306-mcp")
                    .build();
            StdioClientTransport transport = new StdioClientTransport(params);
            McpSyncClient client = McpClient.sync(transport)
                    .requestTimeout(Duration.ofSeconds(100))
                    .build();

            client.initialize();
            McpSchema.ListToolsResult listToolsResult = client.listTools();

            log.info("加载12306 MCP 工具 {} 个", listToolsResult.tools().size());

            return listToolsResult.tools().stream()
                    .map(tool -> createToolCallback(tool, client))
                    .toArray(ToolCallback[]::new);

        } catch (Exception e) {
            log.error("初始化12306 MCP 工具失败: {}", e.getMessage(), e);
            return new ToolCallback[0];
        }
    }
    /** 工具映射逻辑 */
    private ToolCallback createToolCallback(McpSchema.Tool tool, McpSyncClient client) {
        return new ToolCallback() {
            @Override
            public ToolDefinition getToolDefinition() {
                return ToolDefinition.builder()
                        .name(tool.name())
                        .description(tool.description())
                        .inputSchema(JSONUtil.toJsonStr(tool.inputSchema()))
                        .build();
            }

            @Override
            public String call(String toolInput) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode inputJson = mapper.readTree(toolInput);
                    Map<String, Object> arguments = convertJsonToMap(inputJson);

                    McpSchema.CallToolRequest request =
                            new McpSchema.CallToolRequest(tool.name(), arguments);

                    McpSchema.CallToolResult result = client.callTool(request);
                    return extractContentFromResult(result);
                } catch (Exception e) {
                    return "调用 MCP 工具 " + tool.name() + " 失败: " + e.getMessage();
                }
            }
        };
    }

    private Map<String, Object> convertJsonToMap(JsonNode jsonNode) {
        Map<String, Object> map = new HashMap<>();
        if (jsonNode.isObject()) {
            jsonNode.fields().forEachRemaining(entry ->
                    map.put(entry.getKey(), convertJsonValue(entry.getValue())));
        }
        return map;
    }

    private Object convertJsonValue(JsonNode jsonNode) {
        if (jsonNode.isTextual()) return jsonNode.asText();
        if (jsonNode.isNumber()) return jsonNode.asDouble();
        if (jsonNode.isBoolean()) return jsonNode.asBoolean();
        if (jsonNode.isArray()) {
            List<Object> list = new ArrayList<>();
            jsonNode.forEach(node -> list.add(convertJsonValue(node)));
            return list;
        }
        if (jsonNode.isObject()) return convertJsonToMap(jsonNode);
        return null;
    }

    private String extractContentFromResult(McpSchema.CallToolResult result) {
        if (result == null) return "工具调用返回空结果";
        if (result.content() != null) return result.content().toString();
        return result.toString();
    }
}
