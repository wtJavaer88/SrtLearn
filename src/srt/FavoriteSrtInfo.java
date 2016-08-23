package srt;

public class FavoriteSrtInfo extends SrtInfo
{
	private String favoriteTime;
	private String srtFile;

	public String getFavoriteTime()
	{
		return favoriteTime;
	}

	public void setFavoriteTime(String favoriteTime)
	{
		this.favoriteTime = favoriteTime;
	}

	public String getSrtFile()
	{
		return srtFile;
	}

	public void setSrtFile(String srtFile)
	{
		this.srtFile = srtFile;
	}

	@Override
	public String toString()
	{
		return "FavoriteSrtInfo [favoriteTime=" + favoriteTime + ", srtFile=" + srtFile + ", fromTime=" + fromTime + ", toTime=" + toTime + ", srtIndex=" + srtIndex + ", chs=" + chs + ", eng=" + eng + "]";
	}
}
