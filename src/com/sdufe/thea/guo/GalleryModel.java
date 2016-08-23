package com.sdufe.thea.guo;

import android.graphics.drawable.Drawable;

public class GalleryModel {

	private int imageView;
	private String text;

	public GalleryModel(int imageView, String text) {
		super();
		this.imageView = imageView;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getImageView() {
		return imageView;
	}

	public void setImageView(int imageView) {
		this.imageView = imageView;
	}

}
