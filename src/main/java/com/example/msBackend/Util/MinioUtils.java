package com.example.msBackend.Util;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public class MinioUtils {

    // MinIO服务的地址
    private static final String ENDPOINT = "http://42.121.255.254:9000";
    // MinIO的访问密钥
    private static final String ACCESS_KEY = "WRYQ9P6A37W1TGZI4LGR";
    // MinIO的秘密密钥
    private static final String SECRET_KEY = "wZjoV0hFtaJt+yHOn8vOUvEDs+3+MI3wIwpEy+68";
    // 存储桶名称，需提前在MinIO创建好
    private static final String BUCKET_NAME = "skill";

    /**
     * 上传前端传来的文件到MinIO，并返回文件的访问URL
     * @param file 前端传来的文件
     * @param objectName MinIO中存储的对象名称（文件名）
     * @return 文件在MinIO中的访问URL
     * @throws Exception 上传过程中可能抛出的异常
     */
    public static String uploadMultipartFile(MultipartFile file, String objectName) throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();

        // 检查存储桶是否存在，不存在则创建
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        }

        // 获取文件后缀并设置contentType
        String originalFilename = file.getOriginalFilename();
        String fileSuffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        String contentType = null;
        System.out.println(fileSuffix.toLowerCase());
        if (contentType == null) {
            // 如果文件没有提供contentType，则根据后缀设置
            switch (fileSuffix.toLowerCase()) {

                case "jpg", "jpeg":
                    contentType = "image/jpeg";
                    break;
                case "png":
                    contentType = "image/png";
                    break;
                case "txt":
                    contentType = "text/plain";
                    break;
                case "mp3":
                    contentType = "audio/mpeg";
                    break;
                case "mp4":
                    contentType = "video/mp4";
                    break;
                default:
                    contentType = "application/octet-stream";
                    break;
            }
        }

        // 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), 10485760)
                    .contentType(contentType)
                    .build();

            minioClient.putObject(putObjectArgs);
        }

        // 生成文件访问URL
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(BUCKET_NAME)
                .object(objectName)
                .method(Method.GET)
                .build();
        String url = minioClient.getPresignedObjectUrl(args);
        return url.toString();
    }
}
