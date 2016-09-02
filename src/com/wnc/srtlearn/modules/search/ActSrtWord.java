package com.wnc.srtlearn.modules.search;

import net.widget.act.abs.AutoCompletable;

public class ActSrtWord implements AutoCompletable
{
    private String word;

    @Override
    public String toString()
    {
        return this.word;
    }

    @Override
    public boolean match(String searchStr)
    {
        if (this.getWord() != null
                && this.getWord().trim().startsWith(searchStr))
        {
            return true;
        }

        return false;
    }

    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
    }
}
