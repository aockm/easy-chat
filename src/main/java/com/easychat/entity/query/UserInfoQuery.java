package com.easychat.entity.query;

import java.util.Date;


/**
 *@Description: 查询
 *@date: 2025/03/28
 */
public class UserInfoQuery extends BaseQuery {
	private String userId;

	private String userIdFuzzy;

	private String email;

	private String emailFuzzy;

	private String nickName;

	private String nickNameFuzzy;

	private Integer joinType;

	private String password;

	private String passwordFuzzy;

	private String personalSignature;

	private String personalSignatureFuzzy;

	private Integer sex;

	private Integer status;

	private Date createTime;

	private String crateTimeStart;

	private String crateTimeEnd;

	public String getCrateTimeStart() {
		return crateTimeStart;
	}

	public void setCrateTimeStart(String crateTimeStart) {
		this.crateTimeStart = crateTimeStart;
	}

	public String getCrateTimeEnd() {
		return crateTimeEnd;
	}

	public void setCrateTimeEnd(String crateTimeEnd) {
		this.crateTimeEnd = crateTimeEnd;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return this.email;
	}

	public void setNickName(String nickName){
		this.nickName = nickName;
	}

	public String getNickName(){
		return this.nickName;
	}

	public void setJoinType(Integer joinType){
		this.joinType = joinType;
	}

	public Integer getJoinType(){
		return this.joinType;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return this.password;
	}

	public void setPersonalSignature(String personalSignature){
		this.personalSignature = personalSignature;
	}

	public String getPersonalSignature(){
		return this.personalSignature;
	}

	public void setSex(Integer sex){
		this.sex = sex;
	}

	public Integer getSex(){
		return this.sex;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

	public Date getCreateTime(){
		return this.createTime;
	}

	public void setUserIdFuzzy(String userIdFuzzy){
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy(){
		return this.userIdFuzzy;
	}

	public void setEmailFuzzy(String emailFuzzy){
		this.emailFuzzy = emailFuzzy;
	}

	public String getEmailFuzzy(){
		return this.emailFuzzy;
	}

	public void setNickNameFuzzy(String nickNameFuzzy){
		this.nickNameFuzzy = nickNameFuzzy;
	}

	public String getNickNameFuzzy(){
		return this.nickNameFuzzy;
	}

	public void setPasswordFuzzy(String passwordFuzzy){
		this.passwordFuzzy = passwordFuzzy;
	}

	public String getPasswordFuzzy(){
		return this.passwordFuzzy;
	}

	public void setPersonalSignatureFuzzy(String personalSignatureFuzzy){
		this.personalSignatureFuzzy = personalSignatureFuzzy;
	}

	public String getPersonalSignatureFuzzy(){
		return this.personalSignatureFuzzy;
	}

}