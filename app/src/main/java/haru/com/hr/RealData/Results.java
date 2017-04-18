package haru.com.hr.RealData;

/**
 * Created by myPC on 2017-04-17.
 */

public class Results
{
    private String created_date;

    private int status_code;

    private String content;

    private int id;

    private Author author;

    private String title;

    private String image_link;

    public String getCreated_date ()
    {
        return created_date;
    }

    public void setCreated_date (String created_date)
    {
        this.created_date = created_date;
    }

    public int getStatus_code ()
    {
        return status_code;
    }

    public void setStatus_code (int status_code)
    {
        this.status_code = status_code;
    }

    public String getContent ()
    {
        return content;
    }

    public void setContent (String content)
    {
        this.content = content;
    }

    public int getId ()
    {
        return id;
    }

    public void setId (int id)
    {
        this.id = id;
    }

    public Author getAuthor ()
    {
        return author;
    }

    public void setAuthor (Author author)
    {
        this.author = author;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getImage_link ()
    {
        return image_link;
    }

    public void setImage_link (String image_link)
    {
        this.image_link = image_link;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [created_date = "+created_date+", status_code = "+status_code+", content = "+content+", id = "+id+", author = "+author+", title = "+title+", image_link = "+image_link+"]";
    }
}

