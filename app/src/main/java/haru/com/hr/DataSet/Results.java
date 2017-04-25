package haru.com.hr.DataSet;

/**
 * Created by myPC on 2017-04-17.
 */
public class Results
{
    private String content;

    private int id;

    private int author;

    private String title;

    private int status;

    private String image;

    private String day;

    private String url;

    private String email;

    private String realImage;

    public String getRealImage() {
        return realImage;
    }

    public void setRealImage(String realImage) {
        this.realImage = realImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public int getAuthor ()
    {
        return author;
    }

    public void setAuthor (int author)
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

    public int getStatus ()
    {
        return status;
    }

    public void setStatus (int status)
    {
        this.status = status;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public String getDay ()
    {
        return day;
    }

    public void setDay (String day)
    {
        this.day = day;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [content = "+content+", id = "+id+", author = "+author+", title = "+title+", status = "+status+", image = "+image+", day = "+day+", url = "+url+"]";
    }
}

