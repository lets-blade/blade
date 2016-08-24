package com.blade.kit.http;

/**
 * 上传进度回调接口
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface UploadProgress {
	/**
	 * 上传数据的回调函数调用
	 * 
	 * @param uploaded	已经上传的字节数
	 * @param total     字节总数
	 */
	void onUpload(long uploaded, long total);

	UploadProgress DEFAULT = new UploadProgress() {
		public void onUpload(long uploaded, long total) {
		}
	};
}