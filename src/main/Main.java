package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import service.ServerThread;
import utils.ThreadUtils;

public class Main {
	public static void main(String[] args) {
		ExecutorService executorService = ThreadUtils.newCachedThreadPool();
		try
		{
			@SuppressWarnings("resource")
			ServerSocket ss =new ServerSocket(47423);

			while (true){
				Socket socket =ss.accept();
				executorService.execute(new Runnable() {
					
					@Override
					public void run() {
						new ServerThread(socket).handle();
						
					}
				});
			}
		} 
		catch
		(IOException e) {
			System.out.println(e.getMessage());
		}



	}

}
