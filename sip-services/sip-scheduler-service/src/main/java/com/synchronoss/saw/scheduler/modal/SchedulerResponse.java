package com.synchronoss.saw.scheduler.modal;

public class SchedulerResponse {


	private int statusCode;
	private Object data;

    /**
     * @return
     */
	public int getStatusCode() {
		return statusCode;
	}

    /**
     *
     * @param statusCode
     */
	public void setStatusCode(int statusCode)
    {
		this.statusCode = statusCode;
	}

    /**
     *
     * @return
     */
	public Object getData()
    {
		return data;
	}

    /**
     *
     * @param data
     */
	public void setData(Object data)
    {
		this.data = data;
	}
}

