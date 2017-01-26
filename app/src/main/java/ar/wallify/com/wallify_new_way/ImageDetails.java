package ar.wallify.com.wallify_new_way;

/**
 * Created by JAYAN on 12/28/2016.
 */

public class ImageDetails
{
    String iName;

    public ImageDetails()
    {
    }

    Integer iRate;

    public String getiName()
    {
        return iName;
    }

    public void setiName(String iName)
    {
        this.iName = iName;
    }

    public Integer getiRate()
    {
        return iRate;
    }

    public void setiRate(Integer iRate)
    {
        this.iRate = iRate;
    }
    public ImageDetails(String iName,Integer iRate)
    {
        this.iName=iName;
        this.iRate=iRate;
    }
}
