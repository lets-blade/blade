package com.blade.websocket;

public interface WebSocketServer {

	void start(int port) throws WebSocketException;
	
	void start(int port, String context) throws WebSocketException;
	
	void stop() throws WebSocketException;
	
	void addEndpoints(Class<?>... endPoints);
	
}