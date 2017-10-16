package service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import net.sf.json.JSONObject;


public class ServerResult {

	/**
	 * 服务器返回数据
	 * @param socket 连接的socket
	 * @param status 状态码 1：成功 0：接口错误 -1：失败
	 * @param message 返回提示
	 * @param data 返回js数据
	 * @throws IOException
	 */
	public static void result(Socket socket,int status,String message,String data) throws IOException{
		JSONObject js = new JSONObject();
		js.put("stutus", status);
		byte[] bMessage = message.getBytes("UTF-8");
		js.put("message", new String(bMessage,"UTF-8"));
		byte[] bData = data.getBytes("UTF-8");		
		js.put("data", new String(bData,"UTF-8"));
		PrintWriter pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));

		pw.println("HTTP/1.1 200 OK");
		pw.println("Content-type:application/json; charset=utf-8");
		pw.println("Keep-Alive:timeout=10");
		pw.println("Pragma:no-cache");
		//		pw.println("Transfer-Encoding:chunked");
		pw.println("Content-Lengh:"+js.toString().length());
		pw.println("Date:"+new Date());
		pw.println();
		pw.print(js.toString());
		
		pw.flush();
		pw.close();
		socket.close();
	}
}
