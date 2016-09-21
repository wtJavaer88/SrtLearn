package srt.ex;

public class ReachFileTailException extends SrtException
{

    /**
     * 
     */
    private static final long serialVersionUID = -3479417538766423137L;

    public ReachFileTailException()
    {
        super(SrtErrCode.SRT_REACH_TAIL);
    }
}
