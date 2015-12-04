package org.blade.patchca;

import java.io.IOException;

import com.blade.patchca.PatchcaService;

public class AppTest {
	
	public static void main(String[] args) throws IOException {
		
		PatchcaService service = PatchcaService.get();
		// 生成一个验证码到本地
		service.create("F:/aaa.png", "png");
	}
}
