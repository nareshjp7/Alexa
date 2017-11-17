package com.moxieit.orderplatform.lambda.response;

public class AudioPlayer extends CommonAlexaResponse {

	private String playBehavior;

	AudioItem audioItem;

	public String getPlayBehavior() {
		return playBehavior;
	}

	public void setPlayBehavior(String playBehavior) {
		this.playBehavior = playBehavior;
	}

	public AudioItem getAudioItem() {
		return audioItem;
	}

	public void setAudioItem(AudioItem audioItem) {
		this.audioItem = audioItem;
	}

}
