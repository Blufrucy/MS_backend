package com.example.msBackend;

import com.example.msBackend.Util.MinioUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsBackendApplicationTests {

	@Test
	void contextLoads() throws Exception {
//        MinioUtils.uploadFile("D:/Minio/测试音频M50/0004D7rhs4dNgN8 (1).mp3","0004D7rhs4dNgN8 (1)");
		System.out.println( MinioUtils.uploadFile("D:\\图片\\65c96827bbb7440b97.jpg","0004D7rhs4dNgN8"));
	}

}
