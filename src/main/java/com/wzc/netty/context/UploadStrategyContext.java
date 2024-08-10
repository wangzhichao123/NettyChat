package com.wzc.netty.context;

import com.wzc.netty.enums.UploadModeEnum;
import com.wzc.netty.service.UploadStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Map;

import static com.wzc.netty.enums.UploadModeEnum.QNY;

@Component
public class UploadStrategyContext {
    
    @Resource
    private Map<String, UploadStrategy> uploadStrategyMap;

    public String executeUploadStrategy(MultipartFile file, String path) {
        return uploadStrategyMap.get(QNY.getStrategy()).uploadFile(file, path);
    }

    public String executeUploadStrategy(String fileName, InputStream inputStream, String path) {
        return uploadStrategyMap.get(QNY.getStrategy()).uploadFile(fileName, inputStream, path);
    }
}
