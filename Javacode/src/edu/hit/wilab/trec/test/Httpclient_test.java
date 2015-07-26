package edu.hit.wilab.trec.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class Httpclient_test {

	private static CookieStore cookieStore = null;
	private static String cookie_value = new String();

	// private static String local_url = new String();

	public static void main(String[] args) {

		Httpclient_test http_test = new Httpclient_test();

		http_test.get_UMLS();

		http_test.login_UMLS();

	}

	public void get_UMLS() {
		// 初始化httpclient
		DefaultHttpClient httpclient = new DefaultHttpClient();

		httpclient = (DefaultHttpClient) WebClientDevWrapper
				.wrapClient(httpclient);// ssl证书设置

		// 设置httpclient参数：：cookie参数、连接超时参数
		httpclient.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);

		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				60000);

		// 访问登录页面，获取AuthenticityToken
		HttpGet getMethod = new HttpGet(
				"https://utslogin.nlm.nih.gov/cas/login");

		try {
			HttpResponse response = httpclient.execute(getMethod);

			String entity = EntityUtils.toString(response.getEntity());// 获取页面html代码

			cookieStore = httpclient.getCookieStore();// 获取登陆页面的cookie

			cookie_value = cookieStore.getCookies().get(0).getValue();
			
			//获取响应实体  
            HttpEntity httpEntity = response.getEntity();  
            //打印响应状态  
            System.out.println(response.getStatusLine());  
            if (httpEntity != null) {  
                //响应内容的长度  
                long length = httpEntity.getContentLength();  
                //响应内容  
//                String content = EntityUtils.toString(httpEntity);  
  
                System.out.println("Response content length:" + length);  
//                System.out.println("Response content:" + content);  
            }  
			
			System.out.println(cookieStore.toString());
			System.out.println(cookie_value);
			
			System.out.println("----------------------------");
			
		} catch (ClientProtocolException e) {

			System.err.println(e);

		} catch (IOException e) {

			System.err.println(e);

		} finally {

			getMethod.releaseConnection();

			httpclient.getConnectionManager().shutdown();

		}

	}

	public void login_UMLS() {

		// httpclientManager
		PoolingClientConnectionManager pccm = new PoolingClientConnectionManager();

		pccm.setMaxTotal(2000);

		pccm.setDefaultMaxPerRoute(1000);

		// 初始化Httpclient
		DefaultHttpClient httpclient = new DefaultHttpClient(pccm);

		httpclient = (DefaultHttpClient) WebClientDevWrapper
				.wrapClient(httpclient);

		// 设置httpclient参数
		httpclient.setCookieStore(cookieStore);//

		httpclient.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);

		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				60000);

		// post请求
		HttpPost post = new HttpPost(
				"https://utslogin.nlm.nih.gov/cas/login;jsessionid="
						+ cookie_value);

		// 添加post提交参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("_eventId", "submit"));
		nvps.add(new BasicNameValuePair("lt", "e2s1"));
		nvps.add(new BasicNameValuePair("password", "yjfIPADMINI050425"));
		nvps.add(new BasicNameValuePair("submit", "登录"));
		nvps.add(new BasicNameValuePair("username", "yangjinfeng"));

		post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		post.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Cookie", "JSESSIONID="+cookie_value);
		post.addHeader("Host", "utslogin.nlm.nih.gov");
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:39.0) Gecko/20100101 Firefox/39.0");
		post.addHeader("Cache-Control", "max-age=0");
		post.addHeader("Content-Length", "97");
		post.addHeader("Origin", "https://utslogin.nlm.nih.gov");
		post.addHeader("Referer", "https://utslogin.nlm.nih.gov/cas/login");
		
		
		try {

			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

			HttpResponse response = httpclient.execute(post);

			System.out.println(response.toString());

			String entity = EntityUtils.toString(response.getEntity());// 获取页面html代码

			System.out.println("entitysssss::" + entity);

			cookieStore = httpclient.getCookieStore();// 获取登陆页面的cookie

			System.out.println(cookieStore.toString());
			
			int statusCode = response.getStatusLine().getStatusCode();// 获取状态码

			System.out.println("statusCode::" + statusCode);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 这是组装头部
	public static Header[] assemblyHeader(Map<String, String> hashMap) {
		Header[] allHeader = new BasicHeader[hashMap.size()];
		int i = 0;
		for (String str : hashMap.keySet()) {
			Header header = new BasicHeader(str, hashMap.get(str));
			allHeader[i++] = header;
			// i++;
			// i = i +1;s
		}
		return allHeader;
	}

	// 这是组装cookie
	public static String assemblyCookie(List<Cookie> cookies) {
		StringBuffer sbu = new StringBuffer();
		for (Cookie cookie : cookies) {
			sbu.append(cookie.getName()).append("=").append(cookie.getValue())
					.append(";");
		}
		if (sbu.length() > 0)
			sbu.deleteCharAt(sbu.length() - 1);
		return sbu.toString();
	}

	// https://uts.nlm.nih.gov//uts.html
	public void get_UMLS_page(String url) {
		// 初始化httpclient
		DefaultHttpClient httpclient = new DefaultHttpClient();

		httpclient = (DefaultHttpClient) WebClientDevWrapper
				.wrapClient(httpclient);// ssl证书设置

		// 设置httpclient参数：：cookie参数、连接超时参数

		httpclient.setCookieStore(cookieStore);//
		httpclient.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);

		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);

		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				60000);

		// 访问登录页面，获取AuthenticityToken
		HttpGet getMethod = new HttpGet(url);

		try {
			HttpResponse response = httpclient.execute(getMethod);

			int statusCode = response.getStatusLine().getStatusCode();// 获取状态码
			System.out.println(statusCode);
			String entity = EntityUtils.toString(response.getEntity());// 获取页面html代码
			System.out.println("entity::" + entity);
			cookieStore = httpclient.getCookieStore();// 获取登陆页面的cookie

			cookie_value = cookieStore.getCookies().get(0).getValue();

		} catch (ClientProtocolException e) {

			System.err.println(e);

		} catch (IOException e) {

			System.err.println(e);

		} finally {

			getMethod.releaseConnection();

			httpclient.getConnectionManager().shutdown();

		}

	}

}
