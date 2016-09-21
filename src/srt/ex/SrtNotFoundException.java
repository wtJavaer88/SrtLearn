package srt.ex;

public class SrtNotFoundException extends SrtException
{

    /**
     * 
     */
    private static final long serialVersionUID = 4188869110979560840L;

    public SrtNotFoundException()
    {
        super(SrtErrCode.SRT_NOT_FOUND);
    }

}
