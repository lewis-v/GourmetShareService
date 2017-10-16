package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import net.sf.json.JSONObject;


/**
 * 处理线程
 * @author yw
 *
 */
public class ServerThread{
	Socket socket;
	public ServerThread(Socket socket){
		this.socket = socket;
	}
	
	public void handle() {
		String log = "---------start-----------\n";//传输的数据
		BufferedReader bd;
		try {
			bd = new BufferedReader(new
					InputStreamReader(socket.getInputStream()));
			/**

			 * 接受HTTP请求

			 */

			String requestHeader;

			int contentLength=0;
			while
				((requestHeader=bd.readLine())!=null
				&&!requestHeader.isEmpty()){
				log = log + requestHeader+"\n";

				/**

				 * 获得GET参数

				 */

				if(requestHeader.startsWith("GET")){
					log = log + "GET:\n";
					int begin = requestHeader.indexOf("?")+1;
					String condition = "";
					if (begin != 0){
						int end = requestHeader.indexOf("HTTP/");
						condition =requestHeader.substring(begin, end);//传来的数据,以&分隔
					}
					JSONObject jsonObject;
					if(requestHeader.substring(4).startsWith("/Login")){
						jsonObject = stringToJson(condition);
						log = log + "登录接口:"+jsonObject+"\n";
//						LoginApi loginApi = new LoginApi(condition,socket);
//						loginApi.handler();
//						log = log + loginApi.getLog();
					}
				}

				/**

				 * 获得POST参数
				 * 1.获取请求内容长度

				 */

				if(requestHeader.startsWith("Content-Length")){

					int begin=requestHeader.indexOf("Content-Lengh:")+"Content-Length:".length()+1;
					String postParamterLength =requestHeader.substring(begin).trim();
					contentLength =Integer.parseInt(postParamterLength);
				}
			}
			StringBuffer sb =new StringBuffer();

			if(contentLength>0){
				for(int i = 0; i < contentLength; i++) {
					sb.append((char)bd.read());
				}
				log = log + "POST消息体:\n"+sb.toString()+"\n";
				String[] contents = sb.toString().split("\n");
				JSONObject jsonObject = new JSONObject();//post获取的数据
				for(int i = 0;i<contents.length;i++){
					String content = contents[i];
					if (content.startsWith("Content-Disposition:")){
						String name = content.substring(content.indexOf("name=\"")+"name=\"".length(),content.length()-2).trim();
						String value = contents[i+3].trim();
						jsonObject.put(name, value);
						log = log + name+":"+value+"\n";
						i = i+3;
					}
				}
				log = log+"json:"+jsonObject.toString()+"\n";
			}
			//发送回执

			if(!socket.isClosed()){
				ServerResult.result(socket,0,"无此接口访问", "");
			}
		} catch (IOException e) {
			log = log + "err:"+e.getMessage();
		}
		log = log + "---------end-----------\n";
		System.out.println(log);
	}
	
	/**
	 * GET方式下获取的字符串转换成json格式
	 * @param str
	 * @return
	 */
	public JSONObject stringToJson(String str){
		JSONObject jsonObject = new JSONObject();
		for(String content :str.split("&")){
			String[] value = content.split("=");
			if(value.length == 2){
				jsonObject.put(value[0], value[1]);
			}
		}
		return jsonObject;
	}
}
