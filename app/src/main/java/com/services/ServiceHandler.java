package com.services;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ServiceHandler {
 
	    DefaultHttpClient httpClient;
	    HttpContext localContext;
	    HttpResponse response = null;
	    HttpPost httpPost = null;
	    HttpGet httpGet = null;
	    String webServiceUrl;
	    
   // static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
 
    public ServiceHandler(String serviceName) {
    	 HttpParams myParams = new BasicHttpParams();
         HttpConnectionParams.setConnectionTimeout(myParams, 25000);
         HttpConnectionParams.setSoTimeout(myParams, 25000);
         httpClient = new DefaultHttpClient(myParams);      
         localContext = new BasicHttpContext();
         webServiceUrl = serviceName;
    }
    public String webInvoke(String url ,Map<String, Object> params) {
    	String ret = null;
    	JSONObject jsonObject = new JSONObject();
    	try
    	{
    		 for (Map.Entry<String, Object> param : params.entrySet()){
 	            try {
 	                jsonObject.put(param.getKey(), param.getValue());
 	            }
 	            catch (JSONException e) {
 	               
 	            }
 	        }
	        httpPost = new HttpPost(url);
	        response = null;      
	        try {
	        	if (params != null) {
		        	 StringEntity se = new StringEntity(jsonObject.toString());  
	                 se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	                 httpPost.setEntity(se);
		        }
	        } 
	        catch (UnsupportedEncodingException e) {
	           
	        }
	        try
	        {
	        	response = httpClient.execute(httpPost,localContext);
		        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		        StringBuilder builder = new StringBuilder();
		        for (String line = null; (line = reader.readLine()) != null;) {
		            builder.append(line).append("\n");
		        }		        		        
		        //Log.v("Groshie :", builder.toString());		        
		        return builder.toString();
	        }
	        catch(UnsupportedEncodingException Ew)
		    {
		    	//Log.e("Groshie :Invoke UnsupportedEncodingException:", Ew.toString());
		    	
		    }
		    catch(IOException Eww)
		    {		    
		    	//Log.e("Groshie :Invoke IOException:", Eww.toString());
		    }
	        catch(Exception Eww)
		    {		    
		    	//Log.e("Groshie :Invoke Exception:", Eww.toString());
		    }
	        //---
    	}
    	catch(Exception Eww)
    	{
    		try
    		{
    			
    		}
    		catch(IllegalArgumentException Et)
    		{
    			
    		}
    	}    	
    	
    	return ret;
    }
    public String pollingService(String url ,JSONObject jsonObject) {
    	String ret = null;
    	//JSONObject jsonObject1 = new JSONObject();
    	try
    	{
	        httpPost = new HttpPost(url);
	        response = null;      
	        try {
	        	
		        	 StringEntity se = new StringEntity(jsonObject.toString());  
	                 se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	                 httpPost.setEntity(se);
	            } 
	        catch (UnsupportedEncodingException e) {
	           
	        }
	        try
	        {
	        	response = httpClient.execute(httpPost,localContext);
	        	int statusCode=response.getStatusLine().getStatusCode();
	        	System.out.println("Response code="+statusCode);
		        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		        StringBuilder builder = new StringBuilder();
		        for (String line = null; (line = reader.readLine()) != null;) {
		            builder.append(line).append("\n");
		        }		        		        
		        //Log.v("Groshie :", builder.toString());		        
		        return builder.toString();
	        }
	        catch(UnsupportedEncodingException Ew)
		    {
		    	//Log.e("Groshie :Invoke UnsupportedEncodingException:", Ew.toString());
		    	
		    }
		    catch(IOException Eww)
		    {		    
		    	//Log.e("Groshie :Invoke IOException:", Eww.toString());
		    	
		    }
	        catch(Exception Eww)
		    {		    
		    	//Log.e("Groshie :Invoke Exception:", Eww.toString());
		    	
		    }
	        //---
    	}
    	catch(Exception Eww)
    	{
    		try
    		{
    			
    		}
    		catch(IllegalArgumentException Et)
    		{
    			
    		}
    	}    	
    	
    	return ret;
    }
    public String makeServiceCall(String url, int method,
            List<NameValuePair> params) {
        try {
            if (method == POST) {
                	if (params != null) {
                        String paramString = URLEncodedUtils
                                .format(params, "utf-8");
                        url += "?" + paramString;
                    }
                HttpPost httpPost = new HttpPost(url);
                response = httpClient.execute(httpPost);
                String status_code=response.getStatusLine().toString();
                System.out.println("Status code=>"+status_code);
 
            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);
                try
                {
                	response = httpClient.execute(httpGet);
                }
                catch(Exception e){
                	
                }
 
            }
        
        try
        {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	        StringBuilder builder = new StringBuilder();
	        for (String line = null; (line = reader.readLine()) != null;) {
	            builder.append(line).append("\n");
	        }
	        //Log.v("Groshie :", builder.toString());
	        return builder.toString();
        }
        catch(UnsupportedEncodingException Ew)
	    {
	    	//Log.e("Groshie UnsupportedEncodingException:", Ew.getMessage());	    	
        	//Log.e("Groshie UnsupportedEncodingException:", Ew.toString());
	    }
	    catch(IOException Eww)
	    {
	    	Log.e("Groshie IOException:", Eww.toString());
	    }
        
        }
        catch(Exception Eww)
    	{
    		Log.e("Inside the webservic", "webGet() : "+Eww.toString());
    	}
        return null;
      
    }
    
    public InputStream getHttpStream(String urlString) throws IOException {
    	InputStream in = null;
    	try
    	{	        
	        int response = -1;
	        URL url = new URL(urlString);
	        URLConnection conn = url.openConnection();
	 
	        if (!(conn instanceof HttpURLConnection))                    
	            throw new IOException("Not an HTTP connection");
	 
	        try{
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            httpConn.setAllowUserInteraction(false);
	            httpConn.setInstanceFollowRedirects(true);
	            httpConn.setRequestMethod("GET");
	            httpConn.connect();
	            response = httpConn.getResponseCode();                
	            if (response == HttpURLConnection.HTTP_OK) {
	                in = httpConn.getInputStream();                                
	            }                    
	        } catch (Exception e) {
	            throw new IOException("Error connecting");           
	        } // end try-catch
    	}
        catch(Exception Eww)
        {
        	
        }
        return in;    
    }
    public InputStream getHttpStream1(String urlString,String bitMapURL) throws IOException {
    	InputStream in = null;
    	Bitmap bitmap=null;
    	try
    	{	        
	        int response = -1;
	        URL url = new URL(urlString);
	        URLConnection conn = url.openConnection();
	        DataOutputStream outputStream;
	        if (!(conn instanceof HttpURLConnection))                    
	            throw new IOException("Not an HTTP connection");
	 
	        try{
	            HttpURLConnection httpConn = (HttpURLConnection) conn;
	            httpConn.setDoOutput(true);
	            httpConn.setDoInput(true);
	            httpConn.setAllowUserInteraction(false);
	            httpConn.setInstanceFollowRedirects(true);
	            httpConn.setRequestMethod("POST");
	            httpConn.setRequestProperty("Content-Type", "image/png");
	            httpConn.setUseCaches(false);
	            httpConn.connect();
	            outputStream = new DataOutputStream(httpConn.getOutputStream());
	            final BitmapFactory.Options options = new BitmapFactory.Options();
				System.out.println("Upload 4");
				options.inSampleSize=3;
				bitmap = BitmapFactory.decodeFile(bitMapURL);
				if (bitmap != null)
				{
					System.out.println("Upload 5");
					int width = 300;
					int height = 200;
					int photo_width = bitmap.getWidth();
					int photo_height = bitmap.getHeight();
					if (photo_width > width)
						photo_width = width;
					if (photo_height > height)
						photo_height = height;
					bitmap = Bitmap.createScaledBitmap(bitmap, photo_width,photo_height, false);
					System.out.println("Upload 6");
				}
				bitmap.compress(CompressFormat.PNG,50, outputStream);
	            response = httpConn.getResponseCode();                
	            if (response == HttpURLConnection.HTTP_OK) {
	                in = httpConn.getInputStream();                                
	            }                    
	        } catch (Exception e) {
	            throw new IOException("Error connecting");           
	        } // end try-catch
    	}
        catch(Exception Eww)
        {
        	
        }
        return in;    
    }
    public void clearCookies() {
        httpClient.getCookieStore().clear();
    }
 
    public void abort() {
        try {
            if (httpClient != null) {
            	Log.e("Inside the abort", "***1.abort()***");
                httpPost.abort();
            }
        } catch (Exception e) {
        	Log.e("Inside the abort", "***!! Exception !! 2.abort()*** :"+e.toString());
        }
    }
}