package com.easychat.entity.query;

import java.util.Date;


/**
 *@Description: 联系人查询
 *@date: 2025/03/31
 */
public class UserContactQuery extends BaseQuery {
	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 联系人ID或群组ID
	 */
	private String contactId;

	private String contactIdFuzzy;

	/**
	 * 联系人类型 0:好友 1:群组
	 */
	private Integer contactType;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private Date createTimeStart;

	private Date createTimeEnd;

	/**
	 * 状态 0:非好友 1:好友 2:已删除 3:拉黑
	 */
	private Integer status;

	/**
	 * 最后更新时间
	 */
	private Date lastUpdateTime;

	// 是否查询关联
	private Boolean queryUserInfo;

	private Boolean queryGroupInfo;

	private Boolean queryContactUserInfo;

	private Boolean excludeMyGroup;

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public Boolean getExcludeMyGroup() {
		return excludeMyGroup;
	}

	public void setExcludeMyGroup(Boolean excludeMyGroup) {
		this.excludeMyGroup = excludeMyGroup;
	}

	public Boolean getQueryContactUserInfo() {
		return queryContactUserInfo;
	}

	public void setQueryContactUserInfo(Boolean queryContactUserInfo) {
		this.queryContactUserInfo = queryContactUserInfo;
	}

	public Boolean getQueryGroupInfo() {
		return queryGroupInfo;
	}

	public void setQueryGroupInfo(Boolean queryGroupInfo) {
		this.queryGroupInfo = queryGroupInfo;
	}

	public Boolean getQueryUserInfo() {
		return queryUserInfo;
	}

	public void setQueryUserInfo(Boolean queryUserInfo) {
		this.queryUserInfo = queryUserInfo;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setContactId(String contactId){
		this.contactId = contactId;
	}

	public String getContactId(){
		return this.contactId;
	}

	public void setContactType(Integer contactType){
		this.contactType = contactType;
	}

	public Integer getContactType(){
		return this.contactType;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

	public Date getCreateTime(){
		return this.createTime;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setLastUpdateTime(Date lastUpdateTime){
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getLastUpdateTime(){
		return this.lastUpdateTime;
	}

	public void setUserIdFuzzy(String userIdFuzzy){
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy(){
		return this.userIdFuzzy;
	}

	public void setContactIdFuzzy(String contactIdFuzzy){
		this.contactIdFuzzy = contactIdFuzzy;
	}

	public String getContactIdFuzzy(){
		return this.contactIdFuzzy;
	}

}