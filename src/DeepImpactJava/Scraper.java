package DeepImpactJava;

import java.awt.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by oliver on 11/04/2015.
 */
public class Scraper {
    public final String pageUrl = "http://spaceweathergallery.com/";

    // patterns
    Pattern linkPattern;
    Matcher linkMatcher;
    int positionStart = -1;
    int positionEnd = -1;

    // images
    ArrayList<ImageDescriptor> images;

    // constructor
    public Scraper()
    {
        linkPattern = Pattern.compile(".*(<a href=\"indiv_upload.php).*");
        linkPattern = Pattern.compile(".*(<a href=\'indiv_upload.php).*");
        linkPattern = Pattern.compile("(?<=.*<a href).*(?=(PHPSESSID).*)");
        linkPattern = Pattern.compile("^(?<=.*(<a href))[a-z_.?=0-9&;A-Z]*.*(style).*");
        linkPattern = Pattern.compile("(a href)");
        //linkPattern = Pattern.compile(".*(<!DOCTYPE html PUBLIC).*");

        images = new ArrayList<ImageDescriptor>(10);
    }

    // download the website and parse
    public void Parse() {

        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        ImageDescriptor image;

        try {
            url = new URL(pageUrl);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            Boolean matched = false;

            String thumbUrl;
            String imageUrl;

            // line by line...
            while ((line = br.readLine()) != null) {

                // search each line for something like:
                //<a href="indiv_upload.php?upload_id=111474&amp;PHPSESSID=onib1a52ppeis24p40d864nus5" style="text-decoration: none;"><div class="caption none" style="width: 183px;"><img src="http://0e33611cb8e6da737d5c-e13b5a910e105e07f9070866adaae10b.r15.cf1.rackcdn.com/Senol-SANLI-IMG_3646-Acopy_1428767451_fpthumb.jpg" border="0" alt="<font class=&quot;tempImageTitleThumbText&quot;>Venus With Pleiades And Taurus</font><br>Senol SANLI<br>Apr 11 10:51am<br>TURKEY BURSA"><p><font class="tempImageTitleThumbText">Venus With Pleiades And Taurus</font><br>Senol SANLI<br>Apr 11 10:51am<br>TURKEY BURSA</p></div></a>
                //linkMatcher = linkPattern.matcher(line);
                //matched = linkMatcher.matches();

                // just search for the string
                positionStart = line.indexOf("<a href=\'indiv_upload.php");

                // found?
                if (positionStart >= 0) {

                    // now search for the closing bit
                    positionEnd = line.indexOf("\' style=", positionStart);

                    // found?
                    if (positionEnd >= 0)
                    {
                        // extract the url
                        thumbUrl = line.substring(positionStart+9, positionEnd);
                        System.out.println("Found!");

                        // now visit it and extract the image url
                        image = visitThumbnail(thumbUrl);

                        // not blank?
                        if (image != null)
                        {
                            images.add(image);
                        }
                    }
                }
            }




        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
    }

    // visit the link of the image, and get the real photo url
    // takes the url like "indiv_upload.php?upload_id=111486&PHPSESSID=bru56386p0dlfrhggmf616crp7"
    // returns url
    public ImageDescriptor visitThumbnail(String subPart)
    {
        // stuff
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;


        // the image found
        Boolean foundSomething = false;
        String imageUrl = "";
        String imageTitle = "";
        String imageDescription = "";

        // make full
        String fullUrl = pageUrl + subPart;


        // return
        ImageDescriptor image = null;

        // start and end of title
        int titleStart = -1;
        int titleEnd = -1;
        // start and end of description
        int descriptionStart = -1;
        int descriptionEnd = -1;

        //positions
        int positionFirst = -1;

        try {
            url = new URL(fullUrl);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            // line by line...
            while ((line = br.readLine()) != null) {

                // search for the image url
                // first earch for the line above, which as something like "<a href="full_image.php?image_name=Jake-Stehli-aurora_1428774882.jpg" id="main_pic_link" class="image" target="blank" style="text-decoration: none;"><img src="http://0e33611cb8e6da737d5c-e13b5a910e105e07f9070866adaae10b.r15.cf1.rackcdn.com/Jake-Stehli-aurora_1428774882_lg.jpg" border="0" class="imgborder" name="main_pic" id="main_pic"></a>"

                ///////////////////////////////////////////////////////////////////////////////
                // search for the title
                descriptionStart = line.indexOf("<span class=\"photoTitleText\">");
                if (descriptionStart >= 0)
                {
                    descriptionEnd = line.indexOf("</span>", descriptionStart);
                    if (descriptionEnd >= 0)
                    {
                        // extract it
                        imageTitle = line.substring(descriptionStart+28, descriptionEnd);
                        //foundSomething = true;
                    }
                }

                ///////////////////////////////////////////////////////////////////////////////
                // search for the location
                descriptionStart = line.indexOf("<span class=\"photoLocationText\">");
                if (descriptionStart >= 0)
                {
                    descriptionEnd = line.indexOf("</span>", descriptionStart);
                    if (descriptionEnd >= 0)
                    {
                        // extract it
                        imageTitle = line.substring(descriptionStart+31, descriptionEnd);
                        //foundSomething = true;
                    }
                }


                ///////////////////////////////////////////////////////////////////////////////
                // search for the description
                descriptionStart = line.indexOf("<span class=\"imageDescriptionText\">");
                if (descriptionStart >= 0)
                {
                    descriptionEnd = line.indexOf("</span>", descriptionStart);
                    if (descriptionEnd >= 0)
                    {
                        // extract it
                        imageTitle = line.substring(descriptionStart+34, descriptionEnd);
                        //foundSomething = true;
                    }
                }


                /////////////////////////////////////////////////////////////////////////////////
                if (positionFirst == -1) positionFirst = line.indexOf("<a href=\"full_image.php");
                //if (positionFirst >= 0) continue;

                // found the first?
                if (positionFirst >= 0) {
                    positionStart = line.indexOf("<img src=\'");

                    // found?
                    if (positionStart >= 0) {

                        // now search for the closing bit
                        positionEnd = line.indexOf("\' border=", positionStart);

                        // found?
                        if (positionEnd >= 0) {
                            // extract the url
                            imageUrl = line.substring(positionStart + 10, positionEnd);
                            System.out.println("Found Image!");

                            foundSomething = true;
                        }
                    }
                    positionFirst = -1;
                }

            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }

        // if we found something, bundle it up
        if (foundSomething)
        {
            image = new ImageDescriptor();
            image.imageUrl = imageUrl;
            image.description  = imageDescription;
        }

        return image;
    }
}
