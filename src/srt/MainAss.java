package srt;

import java.util.List;

public class MainAss
{
    public static void main(String[] args)
    {
        String srtFile = "C:\\Users\\cpr216\\Downloads\\字幕\\Person.of.Interest.S03"
                + "\\S03E01.ass";
        List<SrtInfo> srtInfos = srt.picker.PickerFactory.getPicker(
                srtFile).getSrtInfos();
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
            e.printStackTrace();
        }
        System.out.println(DataHolder.getNext());
    }
}
