package com.v.heng.livego.bean;

import java.io.Serializable;

/**
 * Created by heng on 2015/10/5.
 * 邮箱：252764480@qq.com
 */
public class LiveInfo implements Serializable{




    // 直播平台
    private String livePlatform;
    // 游戏类别
    private String gameSort;
    // 主播名字
    private String anchorName;
    // 主播头像
    private String anchorIcon;
    // 主播标题
    private String anchorTitle;
    // 观众人数
    private String viewerNum;
    // 直播地址_页面
    private String livePage;
    // 直播地址_address
    private String liveAddress;
    // 直播地址_htmlData
    private String liveHtmlData;
    // 直播地址_stream
    private String liveStream;
    // 直播缩略图
    private String liveIcon;





    public String getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(String liveStream) {
        this.liveStream = liveStream;
    }

    public String getLiveHtmlData() {
        return liveHtmlData;
    }

    public void setLiveHtmlData(String liveHtmlData) {
        this.liveHtmlData = liveHtmlData;
    }
    public String getLivePage() {
        return livePage;
    }

    public void setLivePage(String livePage) {
        this.livePage = livePage;
    }

    public String getLiveIcon() {
        return liveIcon;
    }

    public void setLiveIcon(String liveIcon) {
        this.liveIcon = liveIcon;
    }


    public String getLivePlatform() {
        return livePlatform;
    }

    public void setLivePlatform(String livePlatform) {
        this.livePlatform = livePlatform;
    }

    public String getLiveAddress() {
        return liveAddress;
    }

    public void setLiveAddress(String liveAddress) {
        this.liveAddress = liveAddress;
    }

    public String getViewerNum() {
        return viewerNum;
    }

    public void setViewerNum(String viewerNum) {
        this.viewerNum = viewerNum;
    }

    public String getAnchorTitle() {
        return anchorTitle;
    }

    public void setAnchorTitle(String anchorTitle) {
        this.anchorTitle = anchorTitle;
    }

    public String getAnchorIcon() {
        return anchorIcon;
    }

    public void setAnchorIcon(String anchorIcon) {
        this.anchorIcon = anchorIcon;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    public String getGameSort() {
        return gameSort;
    }

    public void setGameSort(String gameSort) {
        this.gameSort = gameSort;
    }
}
