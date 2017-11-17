package com.moxieit.orderplatform.lambda.response;

import java.util.Map;

public class Directives {

	Map<String, Object> renderTemplate;

	Map<String, Object> audioPlayer;

	Map<String, Object> videoApp;

	public Map<String, Object> getRenderTemplate() {
		return renderTemplate;
	}

	public void setRenderTemplate(Map<String, Object> renderTemplate) {
		this.renderTemplate = renderTemplate;
	}

	public Map<String, Object> getAudioPlayer() {
		return audioPlayer;
	}

	public void setAudioPlayer(Map<String, Object> audioPlayer) {
		this.audioPlayer = audioPlayer;
	}

	public Map<String, Object> getVideoApp() {
		return videoApp;
	}

	public void setVideoApp(Map<String, Object> videoApp) {
		this.videoApp = videoApp;
	}

}
