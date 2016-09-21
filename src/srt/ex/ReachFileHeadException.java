package srt.ex;

public class ReachFileHeadException extends SrtException
{

    /**
     * 
     */
    private static final long serialVersionUID = -4973102507820087104L;

    public ReachFileHeadException()
    {
        super(SrtErrCode.SRT_REACH_HEAD);
    }
}
