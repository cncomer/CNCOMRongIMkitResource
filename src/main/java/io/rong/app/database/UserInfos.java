package io.rong.app.database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table USER_INFOS.
 */
public class UserInfos {

    private Long id;
    /** Not-null value. */
    private String userid;
    /** Not-null value. */
    private String username;
    private String usernickname;
    private String portrait;
    /** Not-null value. */
    private String status;

    public String mCompany="",mTitle="", mAddress="",mFrindid="",mLabels="";

    public UserInfos() {
    }

    public UserInfos(Long id) {
        this.id = id;
    }

    public UserInfos(Long id, String userid, String username, String usernickname, String portrait, String status) {
        this.id = id;
        this.userid = userid;
        this.username = username;
        this.usernickname = usernickname;
        this.portrait = portrait;
        this.status = status;
    }

    public UserInfos(Long id,
                     String userid,
                     String username,
                     String usernickname,
                     String portrait,
                     String status,
                     String company,
                     String title,
                     String address,
                     String friendid,
                     String labels) {
        this.id = id;
        this.userid = userid;
        this.username = username;
        this.usernickname = usernickname;
        this.portrait = portrait;
        this.status = status;

        mCompany = company;
        mTitle = title;
        mAddress = address;
        mFrindid = friendid;
        mLabels = labels;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUserid() {
        return userid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /** Not-null value. */
    public String getUsername() {
        return username;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUsername(String username) {
        this.username = username;
    }

    /** Not-null value. */
    public String getUserNickName() {
        return usernickname;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserNickName(String usernickname) {
        this.usernickname = usernickname;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    /** Not-null value. */
    public String getStatus() {
        return status;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setStatus(String status) {
        this.status = status;
    }

}