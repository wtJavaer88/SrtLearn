package com.wnc.srtlearn.tts;

public interface BdTextToSpeech
{
    public static final String APP_ID = "8483663";// 请更换为自己创建的应用
    public static final String API_KEY = "9YM9iZpG45u67k4GFLpr1VNG";// 请更换为自己创建的应用
    public static final String SECRET_KEY = "3b517268f52cb6ae123f3eb4ee305d38";// 请更换为自己创建的应用

    public int speak(String content);
}