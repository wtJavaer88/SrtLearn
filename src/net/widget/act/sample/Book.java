package net.widget.act.sample;


public class Book implements net.widget.act.abs.AutoCompletable
{

    private int id;
    private String name;
    private String author;
    private int price;
    private String pinyin;

    public Book(int id, String name, String author, int price, String pinyin)
    {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.pinyin = pinyin;
    }

    public String getPinyin()
    {
        return pinyin;
    }

    public void setPinyin(String pinyin)
    {
        this.pinyin = pinyin;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public Book()
    {
    }

    @Override
    public boolean match(String str)
    {
        if (getAuthor().contains(str) || getName().contains(str)
                || (getPrice() + "").contains(str) || getPinyin().contains(str))
        {
            return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "Book [id=" + id + ", name=" + name + ", author=" + author
                + ", price=" + price + ", pinyin=" + pinyin + "]";
    }

}
