package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import net.sf.json.JSONObject;


/**
 * �����߳�
 * @author yw
 *
 */
public class ServerThread{
	Socket socket;
	public ServerThread(Socket socket){
		this.socket = socket;
	}
	
	public void handle() {
		String log = "---------start-----------\n";//���������
		BufferedReader bd;
		try {
			bd = new BufferedReader(new
					InputStreamReader(socket.getInputStream()));
			/**

			 * ����HTTP����

			 */

			String requestHeader;

			int contentLength=0;
			while
				((requestHeader=bd.readLine())!=null
				&&!requestHeader.isEmpty()){
				log = log + requestHeader+"\n";

				/**

				 * ���GET����

				 */

				if(requestHeader.startsWith("GET")){
					log = log + "GET:\n";
					int begin = requestHeader.indexOf("?")+1;
					String condition = "";
					if (begin != 0){
						int end = requestHeader.indexOf("HTTP/");
						condition =requestHeader.substring(begin, end);//����������,��&�ָ�
					}
					JSONObject jsonObject;
					if(requestHeader.substring(4).startsWith("/Login")){
						jsonObject = stringToJson(condition);
						log = log + "��¼�ӿ�:"+jsonObject+"\n";
//						LoginApi loginApi = new LoginApi(condition,socket);
//						loginApi.handler();
//						log = log + loginApi.getLog();
					}
				}

				/**

				 * ���POST����
				 * 1.��ȡ�������ݳ���

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
				log = log + "POST��Ϣ��:\n"+sb.toString()+"\n";
				String[] contents = sb.toString().split("\n");
				JSONObject jsonObject = new JSONObject();//post��ȡ������
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
			//���ͻ�ִ

			if(!socket.isClosed()){
				ServerResult.result(socket,0,"�޴˽ӿڷ���", "");
			}
		} catch (IOException e) {
			log = log + "err:"+e.getMessage();
		}
		log = log + "---------end-----------\n";
		System.out.println(log);
	}
	
	/**
	 * GET��ʽ�»�ȡ���ַ���ת����json��ʽ
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
