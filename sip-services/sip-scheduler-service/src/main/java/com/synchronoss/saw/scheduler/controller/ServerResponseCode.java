package com.synchronoss.saw.scheduler.controller;

public class ServerResponseCode {
	
	//SPECIFIC ERROR CODES
	public static final int JOB_WITH_SAME_NAME_EXIST = 501;
	public static final int JOB_NAME_NOT_PRESENT = 502;
	
	public static final int JOB_ALREADY_IN_RUNNING_STATE = 510;
	
	public static final int JOB_NOT_IN_PAUSED_STATE = 520;
	public static final int JOB_NOT_IN_RUNNING_STATE = 521;
	
	public static final int JOB_DOESNT_EXIST = 500;

	public static final int ATLEAST_ONE_DISPATCHER_IS_MUST = 400;
	
	//GENERIC ERROR
	public static final int ERROR = 600;
	
	//SUCCESS CODES
	public static final int SUCCESS = 200;
}
