package com.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
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
import android.content.Intent;
import android.util.Log;

import com.appcontroller.AppController;
import com.google.gson.Gson;
import com.log.MyLog;
import com.settersgetters.ConfigData;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static java.util.GregorianCalendar.AD;

public class WebService{
	private final String DEBUG_TAG = "[WebService]";

	 
    DefaultHttpClient httpClient;
    HttpContext localContext;
    private String ret;
 
    HttpResponse response = null;
    HttpPost httpPost = null;
    HttpGet httpGet = null;
    String webServiceUrl;
	DBHelper db;
 
    //The serviceName should be the name of the Service you are going to be using.
    public WebService(String serviceName){
        HttpParams myParams = new BasicHttpParams();
 
        HttpConnectionParams.setConnectionTimeout(myParams, 15000);
        HttpConnectionParams.setSoTimeout(myParams, 15000);
        httpClient = new DefaultHttpClient(myParams);      
        localContext = new BasicHttpContext();
        webServiceUrl = serviceName;        
    }
 
    //Use this method to do a HttpPost\WebInvoke on a Web Service
    public String webInvoke(String methodName, Map<String, Object> params) {
        try
        {
//			Log.e("WEBINVOKE", "webInvoke: "+methodName + " => "+params );
	        JSONObject jsonObject = new JSONObject();
	        for (Map.Entry<String, Object> param : params.entrySet()){
	            try {
	                jsonObject.put(param.getKey(), param.getValue());
	            }
	            catch (JSONException e) {
	                Log.e(DEBUG_TAG, "JSONException : "+e.toString());
	            }
	        }
	        return webInvoke(methodName,  jsonObject.toString(), "application/json");
        }
        catch(Exception Eww)
        {
        	Log.e(DEBUG_TAG, "1.webInvoke() : "+Eww.toString());
        }
        return "";
    }
 
    private String webInvoke(String methodName, String data, String contentType) {
    	ret = null;
    	try
    	{
			System.out.println("Inside the webInvoke method "+webServiceUrl+methodName);
	        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);
	        httpPost = new HttpPost(webServiceUrl + methodName);
	        response = null;
	 
	        StringEntity tmp = null;
	        //httpPost.setHeader("User-Agent", "SET YOUR USER AGENT STRING HERE");
	        httpPost.setHeader("Accept","text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
	 
	        if (contentType != null) {
	            httpPost.setHeader("Content-Type", contentType);
	        } else {
	            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
	        }
	 
	        try {
	            tmp = new StringEntity(data,"UTF-8");
	        } catch (UnsupportedEncodingException e) {
	            Log.e(DEBUG_TAG, "HttpUtils : UnsupportedEncodingException : "+e.toString());
	        }
	 
	        httpPost.setEntity(tmp);
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
				System.out.println(DEBUG_TAG + "Inside the Exception 1"+Ew);
		    }
		    catch(IOException Eww)
		    {		    
					System.out.println(DEBUG_TAG + "Inside the Exception 2"+Eww);
		    }
	        catch(Exception Eww)
		    {
				System.out.println(DEBUG_TAG + "Inside the Exception 3"+Eww);
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
 
    //Use this method to do a HttpGet/WebGet on the web service
    public String webGet(String methodName, Map<String, String> params) {
    	try
    	{
	        String getUrl = webServiceUrl + methodName;
	        int i = 0;
	        for (Map.Entry<String, String> param : params.entrySet())
	        {
	            if(i == 0){
	                getUrl += "?";
	            }
	            else{
	                getUrl += "&";
	            }
	             
	            try {
	                getUrl += param.getKey() + "=" + URLEncoder.encode(param.getValue(),"UTF-8");
					Log.e("Polling URL : ", "webGet: "+getUrl );
	            } catch (UnsupportedEncodingException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	             
	            i++;
	        }
	         
	        httpGet = new HttpGet(getUrl); 
	        //Log.i("WebGetURL: ",getUrl);
	         
	        try {
	            response = httpClient.execute(httpGet); 

	        } catch (Exception e) {

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

		    }
		    catch(IOException Eww)
		    {
		    	Log.e("Groshie IOException:", Eww.toString());
		    }
	        //---
    	}
    	catch(Exception Eww)
    	{
    		Log.e(DEBUG_TAG, "webGet() : "+Eww.toString());
    	}
        return "";
    }
    public String downloadAdsImage(String url ,JSONObject jsonObject) {
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
    public static JSONObject Object(Object o){
        try {
            return new JSONObject(new Gson().toJson(o));
        } catch (JSONException e) {
            e.printStackTrace();
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
        {}
        return in;    
    }
     
    public void clearCookies() {
        httpClient.getCookieStore().clear();
    }
 
    public void abort() {
        try {
            if (httpClient != null) {
            	Log.e(DEBUG_TAG, "***1.abort()***");
                httpPost.abort();
            }
        } catch (Exception e) {
        	Log.e(DEBUG_TAG, "***!! Exception !! 2.abort()*** :"+e.toString());
        }
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
				String encodedurl = URLEncoder.encode(url,"UTF-8");
				HttpPost httpPost = new HttpPost(encodedurl);
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
				String encodedurl = URLEncoder.encode(url,"UTF-8");
				HttpGet httpGet = new HttpGet(encodedurl);
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
				Log.e("Inside", Ew.toString());
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
}