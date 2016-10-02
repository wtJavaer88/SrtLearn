package net.widget.sdufe.thea.guo;

public class GalleryModel
{

	private int imageView;
	private String text;
	private String id;

	public GalleryModel(int imageView, String text)
	{
		super();
		this.imageView = imageView;
		this.text = text;
	}

	public GalleryModel(int imageView, String text, String id)
	{
		super();
		this.imageView = imageView;
		this.text = text;
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public int getImageView()
	{
		return imageView;
	}

	public void setImageView(int imageView)
	{
		this.imageView = imageView;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

}
