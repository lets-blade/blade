package com.blade.patchca;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.patchca.color.ColorFactory;
import org.patchca.filter.FilterFactory;
import org.patchca.filter.predefined.DiffuseRippleFilterFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.utils.encoder.EncoderHelper;
import org.patchca.word.RandomWordFactory;
import org.patchca.word.WordFactory;

import com.blade.web.http.Response;
import com.blade.web.http.wrapper.Session;

import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;

/**
 * PatchcaService
 */
public class PatchcaService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PatchcaService.class);

	private ConfigurableCaptchaService cs = null;
	private static Random random = new Random();
	
	private PatchcaService() {
		cs = new ConfigurableCaptchaService();
		// cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
		cs.setColorFactory(new ColorFactory() {
			@Override
			public Color getColor(int x) {
				int[] c = new int[3];
				int i = random.nextInt(c.length);
				for (int fi = 0; fi < c.length; fi++) {
					if (fi == i) {
						c[fi] = random.nextInt(71);
					} else {
						c[fi] = random.nextInt(256);
					}
				}
				return new Color(c[0], c[1], c[2]);
			}
		});
		RandomWordFactory wf = new RandomWordFactory();
		wf.setCharacters("23456789abcdefghigkmnpqrstuvwxyzABCDEFGHIGKLMNPQRSTUVWXYZ");
		wf.setMaxLength(4);
		wf.setMinLength(4);
		cs.setWordFactory(wf);
		cs.setFilterFactory(new DiffuseRippleFilterFactory());
	}
	
	public static PatchcaService get(){
		return new PatchcaService();
	}
	
	public PatchcaService color(ColorFactory colorFactory){
		cs.setColorFactory(colorFactory);
		return this;
	}
	
	public PatchcaService word(WordFactory wordFactory){
		cs.setWordFactory(wordFactory);
		return this;
	}
	
	public PatchcaService filter(FilterFactory filterFactory){
		cs.setFilterFactory(filterFactory);
		return this;
	}
	
	public void session(Session session, Response response, String patchca){
		try {
			setResponseHeaders(response);
			String token = EncoderHelper.getChallangeAndWriteImage(cs, "png", response.outputStream());
			session.attribute(patchca, token);
			LOGGER.debug("current sessionid = {}, token = {}", session.id(), token);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setResponseHeaders(Response response) {
        response.contentType("image/png");
        response.header("Cache-Control", "no-cache, no-store");
        response.header("Pragma", "no-cache");
        long time = System.currentTimeMillis();
		response.header("Last-Modified", time + "");
		response.header("Date", time + "");
		response.header("Expires", time + "");
    }
	
	public String token(String imgType, Response response){
		try {
			String token = EncoderHelper.getChallangeAndWriteImage(cs, imgType, response.outputStream());
			return token;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String token(Response response){
		try {
			String token = EncoderHelper.getChallangeAndWriteImage(cs, "png", response.outputStream());
			return token;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public File create(String imgPath, String imgType) {
		try {
			FileOutputStream fos = new FileOutputStream(imgPath);
			EncoderHelper.getChallangeAndWriteImage(cs, imgType, fos);
			fos.close();
			return new File(imgPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
