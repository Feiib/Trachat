package com.fei.aitravelassistant.config;


import com.fei.aitravelassistant.tool.*;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Resource
    private ToolCallback[] amapMcpTools; // 注入 MCP 工具

    @Resource
    private ToolCallback[] railwayMcpTools;
    @Bean
    public ToolCallback[] allTools() {
//        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapeTool webScrapeTool = new WebScrapeTool();
 //       PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool = new TerminateTool();

        // 合并 MCP 工具和本地工具
        ToolCallback[] localTools = ToolCallbacks.from(
//                fileOperationTool,
                webSearchTool,
                webScrapeTool,
//                pdfGenerationTool,
                terminateTool
        );

        // 合并所有工具
        ToolCallback[] allTools = new ToolCallback[localTools.length + amapMcpTools.length + railwayMcpTools.length];
        System.arraycopy(localTools, 0, allTools, 0, localTools.length);
        System.arraycopy(amapMcpTools, 0, allTools, localTools.length, amapMcpTools.length);
        System.arraycopy(railwayMcpTools, 0, allTools, localTools.length + amapMcpTools.length, railwayMcpTools.length);


        return allTools;
    }
}
