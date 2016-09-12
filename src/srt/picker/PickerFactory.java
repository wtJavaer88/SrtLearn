package srt.picker;

public class PickerFactory
{
    public static Picker getPicker(String srtFile)
    {
        if (srtFile.endsWith(".srt"))
        {
            return new SrtPicker(srtFile);
        }
        else if (srtFile.endsWith(".ass") || srtFile.endsWith(".ssa"))
        {
            return new AssPicker(srtFile);
        }
        else if (srtFile.endsWith(".cnpy"))
        {
            return new CnpySrtPicker(srtFile);
        }
        else if (srtFile.endsWith(".lrc"))
        {
            return new LrcPicker(srtFile);
        }
        return null;
    }
}
