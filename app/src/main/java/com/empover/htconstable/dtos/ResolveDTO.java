package com.empover.htconstable.dtos;

public class ResolveDTO extends DTO {

	private String autoComplaintsId;
	private String comments;
	private int complaintResolveCategoryId;
	private String loginId;
	private int status;
	public String getAutoComplaintsId() {
		return autoComplaintsId;
	}
	public void setAutoComplaintsId(String autoComplaintsId) {
		this.autoComplaintsId = autoComplaintsId;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public int getComplaintResolveCategoryId() {
		return complaintResolveCategoryId;
	}
	public void setComplaintResolveCategoryId(int complaintResolveCategoryId) {
		this.complaintResolveCategoryId = complaintResolveCategoryId;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
