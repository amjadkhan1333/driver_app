package com.connectors;

/*
 * 
 * @author Sreekanth <sreekanth.reddy@visiontek.co.in>
 *
 */

public class BTConnectionException extends Exception {

	private static final long serialVersionUID = 1L;

	String error = "";

	public BTConnectionException(String msg) {
		super(msg);

		error = msg;
	}

	public String getError() {
		return error;
	}

}