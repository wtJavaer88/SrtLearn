package common.utils;

import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

public class Mp3Utils
{
    /**
     * 获取MP3的时长,以秒为单位
     * 
     * @param mp3
     * @return
     */
    public static int getTime(String mp3)
    {
        MP3File file = null;
        try
        {
            file = new MP3File(mp3);
            MP3AudioHeader audioHeader = (MP3AudioHeader) file.getAudioHeader();
            return audioHeader.getTrackLength();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }
}
