package com.fei.aitravelassistant.tool;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.fei.aitravelassistant.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class FileOperationTool {
    private static final String FILE_PATH = FileConstant.FILE_SAVE_PATH + "/file";

    @Tool(description = "读取文件中的内容")
    public String readFile(@ToolParam(description = "文件名称") String fileName) {
        String filepath = FILE_PATH +"/"+ fileName;
        try {
            return FileUtil.readUtf8String(filepath);
        } catch (IORuntimeException e) {
            return "文件读取失败" + e.getMessage();
        }
    }

    @Tool(description = "写入内容到文件")
    public String writeFile(@ToolParam(description = "写入的内容")String content,
                            @ToolParam(description = "写入到的文件名")String filename) {
        String filepath = FILE_PATH + "/" + filename;
        try {
            FileUtil.mkdir(FILE_PATH);
            FileUtil.writeUtf8String(content, filepath);
            return "文件写入到" + filepath + "成功";
        } catch (IORuntimeException e) {
            return "文件写入失败" + e.getMessage();
        }


    }
}