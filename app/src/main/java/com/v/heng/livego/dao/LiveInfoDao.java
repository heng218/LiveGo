package com.v.heng.livego.dao;

import android.text.TextUtils;

import com.v.heng.livego.bean.LiveInfo;
import com.v.heng.livego.net.NetUtils;
import com.v.heng.livego.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by heng on 2015/10/5.
 * 邮箱：252764480@qq.com
 */
public class LiveInfoDao {

//    private static int PAGE_TOTAL = 5;



    // 直播平台
    public enum LivePlatform {
        斗鱼, 虎牙, 龙珠, 战旗, 熊猫, Twitch, 火猫
    }
    // 游戏类别
    public enum GameSort {
        英雄联盟, DOTA2, 魔兽世界, 风暴英雄, 暗黑破坏神3, 星际争霸, 炉石传说,
    }





    /**
     * 获取所有的直播信息
     * by 捞月狗
     * @return
     */
    public static List<LiveInfo> getAllLiveInfosByLaoyuegou(int page) {

        List<LiveInfo> liveInfos_all = getLiveInfosByLaoyuegou("", "",
                "http://www.laoyuegou.com/media/live/online/sec/online/cate/all/page/", page);

        return liveInfos_all;
    }

    public static  List<LiveInfo> getLiveInfosByLaoyuegou_platform(String livePlatform, int page) {

        String url_prefix = "";
        if ("斗鱼".equals(livePlatform)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/live/online/top//p/1/sec/online/page/";
        } else if("战旗".equals(livePlatform)) {
//            url_prefix = "http://www.laoyuegou.com/media/live/online/live/online/top//p/5/sec/online/page/";
//            getZhanqiLiveInfos();
            return null;
        } else if("虎牙".equals(livePlatform)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/live/online/top//p/2/sec/online/page/";
        } else if("龙珠".equals(livePlatform)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/live/online/top//p/6/sec/online/page/";
        } else if("Twitch".equals(livePlatform)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/live/online/top//p/4/sec/online/page/";
        } else if("火猫".equals(livePlatform)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/live/online/top//p/13/sec/online/page/";
        }  else {
            LogUtil.logERROR(LiveInfoDao.class, "livePlatform: " + livePlatform);
        }
        List<LiveInfo> liveInfos__platform = getLiveInfosByLaoyuegou(livePlatform, "",
                url_prefix, page);
        return liveInfos__platform;
    }

    public static  List<LiveInfo> getLiveInfosByLaoyuegou_game(String gameSort, int page) {
        String url_prefix = "";
        if ("英雄联盟".equals(gameSort)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/top/lol/p//sec/online/cate/all/page/";
        } else if("DOTA2".equals(gameSort)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/top/dota2/p//sec/online/cate/all/page/";
        } else if("魔兽世界".equals(gameSort)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/top/wow/p//sec/online/cate/all/page/";
        } else if("风暴英雄".equals(gameSort)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/top/diablo/p//sec/online/cate/all/page/";
        } else if("暗黑破坏神3".equals(gameSort)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/top/lol/p//sec/online/cate/all/page/";
        } else if("星际争霸".equals(gameSort)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/top/sc2/p//sec/online/cate/all/page/";
        } else if("炉石传说".equals(gameSort)) {
            url_prefix = "http://www.laoyuegou.com/media/live/online/top/how/p//sec/online/cate/all/page/";
        } else {
            LogUtil.logERROR(LiveInfoDao.class, "gameSort: " + gameSort);
        }

        List<LiveInfo> liveInfos_game = getLiveInfosByLaoyuegou("", gameSort,
                url_prefix, page);
        return liveInfos_game;
    }

    /**
     * 获取 lol live信息
     * by 捞月狗
     * @return
     */
    private static List<LiveInfo> getLiveInfosByLaoyuegou(String livePlatform, String gameSort, String url_prefix, int page) {
        List<LiveInfo> liveInfos = new ArrayList<>();
        try {
            String url = url_prefix + page + ".html";
            Document doc = Jsoup.connect(url).get();

            Elements ListDiv = doc.getElementsByAttributeValue("class","video-pic-list");
            for (Element element :ListDiv) {
                Elements links = element.getElementsByTag("a");
                for (Element link : links) {
                    String anchorTitle = link.attr("title");
                    String liveIcon = link.getElementsByTag("img").attr("src");
                    String href = "http://www.laoyuegou.com" + link.attr("href");
                    String viewerNum = link.getElementsByAttributeValue("class", "look-icon").text();
                    String anchorName = link.getElementsByAttributeValue("class", "person-icon").text();
                    String anchorIcon = link.getElementsByAttributeValue("class", "portrait-img").attr("src");
                    String livePlatform_s = TextUtils.isEmpty(livePlatform)  ?
                            link.getElementsByAttributeValue("class", "tv").text() : livePlatform;

//                    System.out.println(anchorTitle);
//                    System.out.println(liveIcon);
//                    System.out.println(href);
//                    System.out.println(viewerNum);
//                    System.out.println(anchorName);
//                    System.out.println(livePlatform_s);
//                    System.out.println(gameSort);

                    LiveInfo liveInfo = new LiveInfo();
                    liveInfo.setAnchorTitle(anchorTitle);
                    liveInfo.setLiveIcon(liveIcon);
                    liveInfo.setLivePage(href);
                    liveInfo.setViewerNum(viewerNum);
                    liveInfo.setAnchorName(anchorName);
                    liveInfo.setLivePlatform(livePlatform_s);
                    liveInfo.setAnchorIcon(anchorIcon);
                    liveInfo.setGameSort(gameSort);
                    liveInfos.add(liveInfo);
                }
            }

//            if(++page <= PAGE_TOTAL) {
//                getAllLiveInfosByLaoyuegou(livePlatform, gameSort, url_prefix, page);
//            }

        } catch (UnknownHostException e) {
            LogUtil.logVERBOSE(LiveInfoDao.class, e.toString());
        } catch (IOException e) {
            LogUtil.logERROR(LiveInfoDao.class, e);
        }

        return liveInfos;
    }

    public static LiveInfo getLiveAddressByLivePage( LiveInfo liveInfo) {
        try {
            Document doc = Jsoup.connect(liveInfo.getLivePage()).get();
            Elements elements = doc.getElementsByAttributeValue("class", "videl-list");
            String liveAddress = "";

            if ("斗鱼".equals(liveInfo.getLivePlatform())){
                liveAddress = elements.get(0).getElementsByTag("object").attr("data");
            } else if("战旗".equals(liveInfo.getLivePlatform())) {
//                liveAddress = elements.get(0).getElementsByTag("iframe").attr("src");
                return liveInfo;
            } else if("虎牙".equals(liveInfo.getLivePlatform())) {
                liveAddress = elements.get(0).getElementsByTag("embed").attr("src");
            } else if("龙珠".equals(liveInfo.getLivePlatform())) {
//                liveAddress = elements.get(0).getElementsByTag("embed").attr("src");
//                liveAddress = liveAddress + "&" + elements.get(0).getElementsByTag("embed").attr("flashvars");
                return liveInfo;
            } else if("Twitch".equals(liveInfo.getLivePlatform())) {
                liveAddress = elements.get(0).getElementsByTag("object").attr("data");
            } else if("火猫".equals(liveInfo.getLivePlatform())) {
                liveAddress = elements.get(0).getElementsByTag("iframe").attr("src");
            }

            LogUtil.logVERBOSE(LiveInfoDao.class, "liveAddress:  " + liveAddress);
            liveInfo.setLiveAddress(liveAddress);

        } catch (IOException e) {
           LogUtil.logERROR(LiveInfoDao.class, e);
        }
        return liveInfo;
    }


    /**
     * 获取直播data，根据直播page
     * @param liveInfo
     * @param url
     */
    public void getLiveData(LiveInfo liveInfo, String url) {
        try {
            Document doc_detail = Jsoup.connect(url).get();
            String liveAddress = doc_detail.getElementsByAttributeValue("class", "videl-list").get(0)
                    .getElementsByTag("object").attr("data");

            liveInfo.setLiveAddress(liveAddress);
//        System.out.println("liveAddress: " + liveAddress);
        } catch (IOException e) {
            LogUtil.logERROR(getClass(), e);
        }

    }




/**---------------------------------------------------------------------------------------------*/


    /**
     * 获取所有的直播信息
     * 一个一个
     * @return
     */
    public List<LiveInfo> getAllLiveInfos() {
        List<LiveInfo> allLiveInfos = new ArrayList<LiveInfo>() ;

        for (LivePlatform livePlatform : LivePlatform.values()) {
           switch (livePlatform) {
               case 斗鱼:
                   allLiveInfos = addLiveInfos(allLiveInfos,getDouyuLiveInfos());
                   break;
               case 战旗:
//                   allLiveInfos = addLiveInfos(allLiveInfos,getZhanqiLiveInfos());
                   break;
               case 虎牙:
                   allLiveInfos = addLiveInfos(allLiveInfos,getHuyaLiveInfos());
                   break;
               case 龙珠:
//                   allLiveInfos = addLiveInfos(allLiveInfos,getLongzhuLiveInfos());
                   break;
//               case Twitch:
//                   allLiveInfos = addLiveInfos(allLiveInfos,getTwitchLiveInfos());
//                   break;
//               case 火猫:
//                   allLiveInfos = addLiveInfos(allLiveInfos,getHuomaoLiveInfos());
//                   break;
//               case 熊猫:
//                   allLiveInfos = addLiveInfos(allLiveInfos,getPandaLiveInfos());
//                   break;
               default:
                   break;
           }
        }
        return allLiveInfos;
    }

    /**
     * 添加直播信息
     * @param allLiveInfos
     * @param liveInfos
     * @return
     */
    private List<LiveInfo> addLiveInfos(List<LiveInfo> allLiveInfos, List<LiveInfo> liveInfos) {
        if(liveInfos != null && liveInfos.size() != 0) {
            allLiveInfos .addAll(liveInfos);
        }
        return allLiveInfos;
    }

    /**
     * 获取 斗鱼 直播信息
     * @return
     */
    public List<LiveInfo>  getDouyuLiveInfos() {

        return null;
    }

    /**
     * 获取 战旗 直播信息
     * @return
     */
    public static List<LiveInfo>  getZhanqiLiveInfos(int page) {
        List<LiveInfo> liveInfos = new ArrayList<LiveInfo>();
        try {
            int litmit = 12;
//            int start_index =  (page-1) * litmit;
            String url = "http://www.zhanqi.tv/api/static/live.hots/" + litmit + "-" + page +".json";
            byte[] bytes = NetUtils.getByteArrayFromNetwork(url);
//            LogUtil.logINFO(LiveInfoDao.class, "bytes: " + new String(bytes));
            JSONObject jsonObject = new JSONObject(new String(bytes));
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("rooms");
            for (int i = 0; i < jsonArray.length() ; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String anchorTitle = object.getString("title");
                String liveIcon = object.getString("spic");
                String livePage = "http://www.zhanqi.tv" +object.getString("url");
                String viewerNum = object.getString("online");
                String anchorName =  object.getString("nickname");
                String anchorIcon = object.getString("avatar");
                String livePlatform = "战旗";
                String gameSort = object.getString("gameName");

                String roomId = object.getString("id");
                String gameId = object.getString("gameId");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.hh");
                String pv = sdf.format(new Date());
                String liveHtmlData = "<object type=\"application/x-shockwave-flash\" id=\"BFPlayerID\" data=\"http://dlstatic.cdn.zhanqi.tv/assets/swf/shell.swf?20151021.01\" width=\"100%\" height=\"100%\">\n" +
                        "<param name=\"menu\" value=\"false\">\n" +
                        "<param name=\"scale\" value=\"noScale\">\n" +
                        "<param name=\"allowFullscreen\" value=\"true\">\n" +
                        "<param name=\"allowScriptAccess\" value=\"always\">\n" +
                        "<param name=\"allowFullScreenInteractive\" value=\"true\">\n" +
                        "<param name=\"wmode\" value=\"transparent\"><param name=\"flashvars\" value=\"Servers=eyJsb2ciOnsiaXAiOiIxMTMuMzEuODcuODYiLCJwb3J0IjoxNTAwMX0sImxpc3QiOlt7ImlwIjoiMTEyLjEyNi44NC45MiIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6ODAsImlkIjo4MH0seyJpcCI6IjExMi4xMjQuNDQuMjA1IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjoyMywiaWQiOjIzfSx7ImlwIjoiMTEyLjEyNC42MS4xMDQiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjQ3LCJpZCI6NDd9LHsiaXAiOiIxMTIuMTI0LjI1LjI0OSIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6NTcsImlkIjo1N30seyJpcCI6IjEyMS40Mi4xOTUuMTY2IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo2NiwiaWQiOjY2fSx7ImlwIjoiMTE0LjIxNS4xMTcuNjAiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjI3LCJpZCI6Mjd9LHsiaXAiOiIxMjAuNTUuMTE2LjIxOSIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6OTgsImlkIjo5OH0seyJpcCI6IjExMi4xMjYuODQuNjYiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjkzLCJpZCI6OTN9LHsiaXAiOiIxMTUuMjkuMTk0LjE3OSIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6OTYsImlkIjo5Nn0seyJpcCI6IjEyMC4yNi4xNC43MiIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6MTAzLCJpZCI6MTAzfSx7ImlwIjoiMTEyLjEyNi44NC42NSIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6ODMsImlkIjo4M30seyJpcCI6IjQyLjk2LjE0My45IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo5NSwiaWQiOjk1fSx7ImlwIjoiMTIwLjI2LjEzLjIiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjEwOCwiaWQiOjEwOH0seyJpcCI6IjEyMC4yNi4xMi41NSIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6MTA3LCJpZCI6MTA3fSx7ImlwIjoiMTE1LjI4LjE3My4zNiIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6OTQsImlkIjo5NH0seyJpcCI6IjEyMC41NS43Mi4yNTQiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjQ0LCJpZCI6NDR9LHsiaXAiOiIxMjMuNTYuMTUyLjE4MyIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6MzEsImlkIjozMX0seyJpcCI6IjEyMC41NS43Mi4xNjQiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjQzLCJpZCI6NDN9LHsiaXAiOiIxMTUuMjguMTMxLjE5NyIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6ODQsImlkIjo4NH0seyJpcCI6IjEyMC41NS4xMTcuMTEzIiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo5NywiaWQiOjk3fSx7ImlwIjoiMTIxLjQzLjE5Ny4xNTIiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjQyLCJpZCI6NDJ9LHsiaXAiOiIxMjMuNTYuNDcuMTU1IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo4MSwiaWQiOjgxfSx7ImlwIjoiMTEyLjEyNC4zOC4yMjkiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjY0LCJpZCI6NjR9LHsiaXAiOiIxMjMuNTcuMjEyLjQ2IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjozMywiaWQiOjMzfSx7ImlwIjoiMTEyLjEyNi44NS4xMjQiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjYxLCJpZCI6NjF9LHsiaXAiOiIxMTIuMTI2Ljg0LjY0IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo5MSwiaWQiOjkxfSx7ImlwIjoiMTIzLjU2LjE1Mi4yNTMiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjQwLCJpZCI6NDB9LHsiaXAiOiIxMTUuMjkuMjQ1LjE0NCIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6NzksImlkIjo3OX0seyJpcCI6IjExNS4yOS4yMDEuMjUzIiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo4NiwiaWQiOjg2fSx7ImlwIjoiMTgyLjkyLjIzMi4xOTAiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjkyLCJpZCI6OTJ9LHsiaXAiOiIxMTIuMTI0LjE5LjIwMyIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6NDYsImlkIjo0Nn0seyJpcCI6IjEyMC4yNi4yLjk4IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjoxMDksImlkIjoxMDl9LHsiaXAiOiIxMjAuMjYuNjMuMTc2IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjoyMSwiaWQiOjIxfSx7ImlwIjoiMTEyLjEyNC45LjM5IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjoyMiwiaWQiOjIyfSx7ImlwIjoiMTIwLjI2LjE2LjM4IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo4NywiaWQiOjg3fSx7ImlwIjoiMTIxLjQzLjE5Ni43NyIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6NDEsImlkIjo0MX0seyJpcCI6IjEyMy41Ni4xNTIuMTkyIiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjozMiwiaWQiOjMyfSx7ImlwIjoiMTIwLjI2LjEzLjIxNCIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6NTgsImlkIjo1OH0seyJpcCI6IjExMi4xMjQuMTAwLjM2IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo3OCwiaWQiOjc4fSx7ImlwIjoiMTIwLjI2LjEzLjc1IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjoxMDUsImlkIjoxMDV9LHsiaXAiOiI0Mi45Ni4xNjguOTciLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjY1LCJpZCI6NjV9LHsiaXAiOiIxMjAuMjYuMTYuMTc2IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo4OSwiaWQiOjg5fSx7ImlwIjoiMTIxLjQzLjIzMC44MiIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6MjQsImlkIjoyNH0seyJpcCI6IjE4Mi45Mi4yMTcuMTQzIiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo2MiwiaWQiOjYyfSx7ImlwIjoiMTIzLjU3LjQ2LjIyMyIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6ODIsImlkIjo4Mn0seyJpcCI6IjIxOC4yNDQuMTI4LjQ4IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo3NiwiaWQiOjc2fSx7ImlwIjoiMTIwLjI2LjAuMTM4IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo1OSwiaWQiOjU5fSx7ImlwIjoiMTEyLjEyNi44NC4yMTQiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjkwLCJpZCI6OTB9LHsiaXAiOiIxMTUuMjguMjI2LjEzMyIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6MjksImlkIjoyOX0seyJpcCI6IjExMi4xMjQuNjEuMTA2IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo0OCwiaWQiOjQ4fSx7ImlwIjoiMTE0LjIxNS4xNDQuMTgyIiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjoyOCwiaWQiOjI4fSx7ImlwIjoiMTIzLjU2LjE0NS45MCIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6MzQsImlkIjozNH0seyJpcCI6IjEyMy41Ny4xMC4xOTciLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjYwLCJpZCI6NjB9LHsiaXAiOiIxMjAuMjYuMTQuNjIiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjEwNiwiaWQiOjEwNn0seyJpcCI6IjEyMC4yNi4xNC4zOSIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6MTA0LCJpZCI6MTA0fSx7ImlwIjoiMTIxLjQyLjE0Mi4xNjAiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjI2LCJpZCI6MjZ9LHsiaXAiOiIxMTIuMTI0LjIwLjM0IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo0NSwiaWQiOjQ1fSx7ImlwIjoiNDIuOTYuMTU0LjEyOSIsInBvcnQiOjE1MDEwLCJjaGF0cm9vbV9pZCI6ODUsImlkIjo4NX0seyJpcCI6IjE4Mi45Mi4xMjguMjQ5IiwicG9ydCI6MTUwMTAsImNoYXRyb29tX2lkIjo2MywiaWQiOjYzfSx7ImlwIjoiMjE4LjI0NC4xMzAuMjUiLCJwb3J0IjoxNTAxMCwiY2hhdHJvb21faWQiOjg4LCJpZCI6ODh9XX0=&amp;\n" +
                        "ServerIp=&amp;\n" +
                        "ServerPort=&amp;\n" +
                        "ChatRoomId=&amp;\n" +
                        "VideoLevels=eyJtdWxyYXRlIjpbWyJ3c19saXZlIiwiaGRsIiwiMjM2ODlfWjFDZ3UuZmx2IiwiTElWRSIsIjQiLDEsIiJdXSwicmV2aWV3IjpbWyJ6cV9yZXZpZXciLCJobHMiLCIyMzY4OV9aMUNndVwvcGxheWxpc3QubTN1OCIsIkxJVkUiLCI0IiwxLCIiXV0sIm11bHJhdGVIbHMiOltbIndzX2xpdmUiLCJobHMiLCIyMzY4OV9aMUNndVwvcGxheWxpc3QubTN1OCIsIkxJVkUiLCI0IiwxLCIiXV19&amp;\n" +
                        "cdns=eyJ2aWQiOiIyMzY4OV9aMUNndSIsImJyb2Nhc3QiOiI0IiwiY2RucyI6IjIxfDMyfDYxIiwicmF0ZSI6IiIsInJldmlldyI6IjEzIiwiYWlkY2RucyI6IjIxfDE0MS0xNTIiLCJvdXRjZG5zIjoiMzJ8MTExIiwiYWsiOiIxLWZBOUxDRXhKT0NoOUp4dHRSbGtXTFE9PXwyLWZBOUxDRXhKT0NoOUp4dHRSbGtXTFE9PXwzLWR3Z1JTMWx1UFNob05WUjhDMWdvRkE9PXw0LWR3Z1JTMWx1UFNob05WUjhDMWdvRkE9PXw1LWVHNXRhSDFtQkF4SWEwNXBTeDBuTGc9PXw2LWVHNXRhSDFtQkF4SWEwNXBTeDBuTGc9PXw3LWFSeHJTd3hFS1NSWFBYSlJEbWN4TXc9PXw4LVJCb1RXWGhqUFM5dU9YZFFmRzBiUGc9PXw5LVJBbFVjQVlTSnhwdEdYWnJTbTRvS3c9PXwxMC18MTEtYVNwYUNsZHBLVHQwS0ZwMURrZ1hPdz09fDEyLWRHcEdBdzUrRUNSM0hIUmpjVThWRWc9PXwxMy1YV3hDZVVkUERTWnhHa0pxWFZNOU53PT18MTQtU1FsdlhFa2FEQjVKWjF0NVVXRU5Idz09fDE1LVNXcEdEa2RaREF4dkszWm9DUjhxR2c9PSIsImFrMiI6IjEtWVNwT0hWeGRSM3hnQWg1NFZrMXBlUT09fDItWVNwT0hWeGRSM3hnQWg1NFZrMXBlUT09fDMtYWkwVVhrbDZRbngxRUZGcEcweFhRQT09fDQtYWkwVVhrbDZRbngxRUZGcEcweFhRQT09fDUtWlV0b2ZXMXllMWhWVGt0OFd3bFllZz09fDYtWlV0b2ZXMXllMWhWVGt0OFd3bFllZz09fDctZERsdVhoeFFWbkJLR0hkRUhuTk9adz09fDgtV1Q4V1RHaDNRbnR6SEhKRmJIbGthZz09fDktV1N4UlpSWUdXRTV3UEhOK1ducFhmdz09fDEwLXwxMS1kQTlmSDBkOVZtOXBEVjlnSGx4b2J3PT18MTItYVU5REZoNXFiM0JxT1hGMllWdHFSZz09fDEzLVFFbEhiRmRiY25Kc1AwZFwvVFVkQ1l3PT18MTQtVkN4cVNWa09jMHBVUWw1c1FYVnlTdz09fDE1LVZFOURHMWROYzFoeURuTjlHUXRWVGc9PSIsImFjYyI6eyJjZG4iOjcyLCJhZGQiOiIiLCJ2dCI6IiIsInBsYXRmb3JtIjoxLCJhc3QiOjEsImFkdCI6IjE6OTAiLCJjc3QiOjAsImNkdCI6IiJ9fQ==&amp;\n" +
                        "Status=4&amp;\n" +
                        "RoomId="+ roomId +"&amp;\n" +
                        "ComLayer=true&amp;\n" +
                        "VideoTitle="+ anchorTitle +"&amp;\n" +
                        "WebHost=http://www.zhanqi.tv&amp;\n" +
                        "VideoType=LIVE&amp;\n" +
                        "GameId="+ gameId +"&amp;\n" +
                        "Online="+ viewerNum +"&amp;\n" +
                        "StarRoom=0&amp;\n" +
                        "pv="+ pv +"&amp;\n" +
                        "TuristRate=0&amp;\n" +
                        "UID=0\">\n" +
                        "</object>";

                LogUtil.logINFO(LiveInfoDao.class, "anchorTitle: " + anchorTitle);
                LogUtil.logINFO(LiveInfoDao.class, "liveIcon: " + liveIcon);
                LogUtil.logINFO(LiveInfoDao.class, "livePage: " + livePage);
                LogUtil.logINFO(LiveInfoDao.class, "viewerNum: " + viewerNum);
                LogUtil.logINFO(LiveInfoDao.class, "anchorName: " + anchorName);
                LogUtil.logINFO(LiveInfoDao.class, "livePlatform: " + livePlatform);
                LogUtil.logINFO(LiveInfoDao.class, "gameSort: " + gameSort);
                LogUtil.logINFO(LiveInfoDao.class, "liveHtmlData: " + liveHtmlData);

                LiveInfo liveInfo = new LiveInfo();
                liveInfo.setAnchorTitle(anchorTitle);
                liveInfo.setLiveIcon(liveIcon);
                liveInfo.setLivePage(livePage);
                liveInfo.setViewerNum(viewerNum);
                liveInfo.setAnchorName(anchorName);
                liveInfo.setLivePlatform(livePlatform);
                liveInfo.setAnchorIcon(anchorIcon);
                liveInfo.setGameSort(gameSort);
                liveInfo.setLiveHtmlData(liveHtmlData);
                liveInfos.add(liveInfo);
            }

        }catch (JSONException e) {
            LogUtil.logERROR(LiveInfoDao.class, e);
        } catch (Exception e) {
            LogUtil.logERROR(LiveInfoDao.class, e);
        }
        return liveInfos;
    }

    /**
     * 获取 虎牙 直播信息
     * @return
     */
    public static List<LiveInfo>  getHuyaLiveInfos() {

        return null;
    }

    /**
     * 获取 龙珠 直播信息
     * page 起始页 1
     */
    public static List<LiveInfo>  getLongzhuLiveInfos(int page) {
        List<LiveInfo> liveInfos = new ArrayList<LiveInfo>();
        try {
            int litmit = 12;
            int start_index =  (page-1) * litmit;
            String url = "http://api.plu.cn/tga/streams/?game=0&max-results=" + litmit +
                    "&start-index=" + start_index  + "&sort-by=top&filter=0&";
            byte[] bytes = NetUtils.getByteArrayFromNetwork(url);
//            LogUtil.logINFO(LiveInfoDao.class, "bytes: " + new String(bytes));
            JSONObject jsonObject = new JSONObject(new String(bytes));
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("items");
            for (int i = 0; i < jsonArray.length() ; i++) {
                JSONObject channel = jsonArray.getJSONObject(i).getJSONObject("channel");
                String anchorTitle = channel.getString("status");
                String liveIcon = jsonArray.getJSONObject(i).getString("preview");
                String livePage = channel.getString("url");
                String viewerNum = jsonArray.getJSONObject(i).getString("viewers");
                String anchorName =  channel.getString("name");
                String anchorIcon = channel.getString("avatar");
                String livePlatform = "龙珠";
                String gameSort = jsonArray.getJSONObject(i).getJSONArray("game").getJSONObject(0).getString("Name");
                int vid =  channel.getInt("vid");
                String liveHtmlData = vid > 0 ? "<embed \n" +
                        "wmode=\"opaque\" \n" +
                        "flashvars=\"vid="+ vid +"&amp;\n" +
                        "\tloadingswf=http://imgcache.qq.com/minivideo_v1/vd/res/skins/longzhu_loading.swf&amp;\n" +
                        "\tvurl=http://zb.v.qq.com:1863/?progid="+ vid +"&amp;\n" +
                        "\tsktype=live&amp;\n" +
                        "\tfunCnlInfo=TenVideo_FlashLive_GetChannelInfo&amp;\n" +
                        "\tfunTopUrl=TenVideo_FlashLive_GetTopUrl&amp;\n" +
                        "\tfunLogin=TenVideo_FlashLive_IsLogin&amp;\n" +
                        "\tfunOpenLogin=TenVideo_FlashLive_OpenLogin&amp;\n" +
                        "\tfunSwitchPlayer=TenVideo_FlashLive_SwitchPlayer&amp;\n" +
                        "\ttxvjsv=2.0&amp;\n" +
                        "\tshowcfg=1&amp;\n" +
                        "\tshare=1&amp;\n" +
                        "\tfull=1&amp;\n" +
                        "\tautoplay=1&amp;\n" +
                        "\tp=true\" \n" +
                        "src=\"http://imgcache.qq.com/minivideo_v1/vd/res/TencentPlayerLive.swf?max_age=86400&amp;v="+ vid +" \n" +
                        "quality=\"high\" \n" +
                        "name=\"tenvideo_flash_player_1429855293134\" \n" +
                        "id=\"tenvideo_flash_player_1429855293134\" \n" +
                        "bgcolor=\"#000000\" width=\"100%\" height=\"100%\" align=\"middle\" \n" +
                        "\tallowscriptaccess=\"always\" allowfullscreen=\"true\" \n" +
                        "type=\"application/x-shockwave-flash\" pluginspage=\"http://get.adobe.com/cn/flashplayer/\"/>"
                        :
                        "<embed \n" +
                        "width=\"100%\" \n" +
                        "height=\"100%\" \n" +
                        "align=\"none\" name=\"videoFlash\" \n" +
                        "allowfullscreen=\"true\" \n" +
                        "allowscriptaccess=\"always\" \n" +
                        "loop=\"false\" \n" +
                        "menu=\"false\" \n" +
                        "play=\"true\" \n" +
                        "pluginspage=\"http://www.macromedia.com/go/getflashplayer\" " +
                        "src=\"http://r.plures.net/proton/flash/streaming-ifp2rgic.swf?hasNextBtn=0&amp;hasMovieBtn=0&amp;autoPlay=1&amp;roomId="+ channel.getString("id") + "&amp;title="+ anchorTitle +"\" " +
                        "type=\"application/x-shockwave-flash\" \n" +
                        "wmode=\"transparent\"/>";


                LogUtil.logINFO(LiveInfoDao.class, "anchorTitle: " + anchorTitle);
                LogUtil.logINFO(LiveInfoDao.class, "liveIcon: " + liveIcon);
                LogUtil.logINFO(LiveInfoDao.class, "livePage: " + livePage);
                LogUtil.logINFO(LiveInfoDao.class, "viewerNum: " + viewerNum);
                LogUtil.logINFO(LiveInfoDao.class, "anchorName: " + anchorName);
                LogUtil.logINFO(LiveInfoDao.class, "livePlatform: " + livePlatform);
                LogUtil.logINFO(LiveInfoDao.class, "gameSort: " + gameSort);
                LogUtil.logINFO(LiveInfoDao.class, "liveHtmlData: " + liveHtmlData);

                LiveInfo liveInfo = new LiveInfo();
                liveInfo.setAnchorTitle(anchorTitle);
                liveInfo.setLiveIcon(liveIcon);
                liveInfo.setLivePage(livePage);
                liveInfo.setViewerNum(viewerNum);
                liveInfo.setAnchorName(anchorName);
                liveInfo.setLivePlatform(livePlatform);
                liveInfo.setAnchorIcon(anchorIcon);
                liveInfo.setGameSort(gameSort);
                liveInfo.setLiveHtmlData(liveHtmlData);
                liveInfos.add(liveInfo);
            }

        }catch (JSONException e) {
            LogUtil.logERROR(LiveInfoDao.class, e);
        } catch (Exception e) {
            LogUtil.logERROR(LiveInfoDao.class, e);
        }
        return liveInfos;
    }

    /**
     * 获取 Twitch 直播信息
     * @return
     */
    public static List<LiveInfo>  getTwitchLiveInfos() {

        return null;
    }

    /**
     * 获取 火猫 直播信息
     * @return
     */
    public static List<LiveInfo>  getHuomaoLiveInfos() {

        return null;
    }

    /**
     * 获取 熊猫 直播信息
     * @return
     */
    public static List<LiveInfo>  getPandaLiveInfos(int page) {
        List<LiveInfo> liveInfos = new ArrayList<LiveInfo>();
        try {
            int litmit = 12;
//            int start_index =  (page-1) * litmit;
            String url = "http://www.panda.tv/live_lists?status=2&order=person_num&token=&pageno="+
                    page +"&pagenum="+ litmit ;
            byte[] bytes = NetUtils.getByteArrayFromNetwork(url);
//            LogUtil.logINFO(LiveInfoDao.class, "bytes: " + new String(bytes));
            JSONObject jsonObject = new JSONObject(new String(bytes));
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("items");
            for (int i = 0; i < jsonArray.length() ; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String anchorTitle = object.getString("name");
                String liveIcon = object.getJSONObject("pictures").getString("img");
                String livePage = "http://www.panda.tv/room/" + object.getString("id");
                String viewerNum = object.getString("person_num");
                String anchorName =  object.getJSONObject("userinfo").getString("nickName");
                String anchorIcon = object.getJSONObject("userinfo").getString("avatar");
                String livePlatform = "熊猫";
                String gameSort = object.getJSONObject("classification").getString("cname");

                String roomId =  object.getString("id");
                String room_key = object.getString("room_key");
                String liveHtmlData = "<object type=\"application/x-shockwave-flash\" data=\"http://s2.qhimg.com/static/959e0bdf5c40f82a.swf\" width=\"100%\" height=\"100%\" id=\"live_player_swf\" style=\"visibility: visible;\">\n" +
                        "<param name=\"allowFullScreen\" value=\"true\">\n" +
                        "<param name=\"wMode\" value=\"opaque\">\n" +
                        "<param name=\"allowScriptAccess\" value=\"always\">\n" +
                        "<param name=\"allowFullScreenInteractive\" value=\"true\">\n" +
                        "<param name=\"flashvars\" value=\"resource=http://s2.qhimg.com/static/959e0bdf5c40f82a.swf&amp;width=100%&amp;height=100%&amp;sign=false&amp;videoList=true|true|false&amp;videoId="+ room_key+"&amp;isLogin=false&amp;isAnchor=false&amp;roomId="+roomId+"&amp;plflag=2_3&amp;flashId=live_player_swf\">\n" +
                        "</object>";

                LogUtil.logINFO(LiveInfoDao.class, "anchorTitle: " + anchorTitle);
                LogUtil.logINFO(LiveInfoDao.class, "liveIcon: " + liveIcon);
                LogUtil.logINFO(LiveInfoDao.class, "livePage: " + livePage);
                LogUtil.logINFO(LiveInfoDao.class, "viewerNum: " + viewerNum);
                LogUtil.logINFO(LiveInfoDao.class, "anchorName: " + anchorName);
                LogUtil.logINFO(LiveInfoDao.class, "livePlatform: " + livePlatform);
                LogUtil.logINFO(LiveInfoDao.class, "gameSort: " + gameSort);
                LogUtil.logINFO(LiveInfoDao.class, "liveHtmlData: " + liveHtmlData);

                LiveInfo liveInfo = new LiveInfo();
                liveInfo.setAnchorTitle(anchorTitle);
                liveInfo.setLiveIcon(liveIcon);
                liveInfo.setLivePage(livePage);
                liveInfo.setViewerNum(viewerNum);
                liveInfo.setAnchorName(anchorName);
                liveInfo.setLivePlatform(livePlatform);
                liveInfo.setAnchorIcon(anchorIcon);
                liveInfo.setGameSort(gameSort);
                liveInfo.setLiveHtmlData(liveHtmlData);
                liveInfos.add(liveInfo);
            }

        }catch (JSONException e) {
            LogUtil.logERROR(LiveInfoDao.class, e);
        } catch (Exception e) {
            LogUtil.logERROR(LiveInfoDao.class, e);
        }
        return liveInfos;
    }








}
