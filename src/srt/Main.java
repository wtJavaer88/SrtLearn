package srt;

import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        String srtFile = "C:\\Users\\cpr216\\Downloads\\字幕\\[zmk.tw]Person.of.Interest.S01.720p.HDTV.x264-TvT[chs&amp;amp;eng]"
                + "\\Person.of.Interest.S01E01.720p.HDTV.x264-IMMERSE.chs&eng.srt";
        List<SrtInfo> srtInfos = new srt.picker.SrtPicker(srtFile)
                .getSrtInfos();
        DataHolder.appendData(srtFile, srtInfos);
        System.out.println(DataHolder.getClosestSrt(0, 0, 9));
        System.out.println(DataHolder.getNext());
        System.out.println(DataHolder.getNext());
        System.out.println(DataHolder.getNext());
        System.out.println(DataHolder.getPre());
        System.out.println(DataHolder.getPre());
        System.out.println(DataHolder.getPre());

        try
        {
            System.out.println(DataHolder.getPre());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(DataHolder.getNext());
    }
}
