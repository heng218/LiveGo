/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.v.heng.library.xlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.v.heng.library_listview_gridview.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class XListView extends ListView implements OnScrollListener {

    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private IXListViewListener mListViewListener;

    // -- header view
    private XListViewHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreashing.

    // -- footer view
    private XListViewFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;

    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.

    private int totalDataCount = 0;

    /**
     * @param context
     */
    public XListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);

        // init header view
        mHeaderView = new XListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView
                .findViewById(R.id.xlistview_header_content);
        mHeaderTimeView = (TextView) mHeaderView
                .findViewById(R.id.xlistview_header_time);
        addHeaderView(mHeaderView);

        // init footer view
        mFooterView = new XListViewFooter(context);

        // init header height
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mHeaderViewHeight = mHeaderViewContent.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
        }
    }

    private String time = "";
    private String date = "";

    public void notifyFootViewTextChange() {
        post(new Runnable() {

            @Override
            public void run() {
                stopRefresh();
                stopLoadMore();
                if (getFooterViewsCount() == 0) {
                    addFooterView(mFooterView);
                    resetFooterHeight();
                }
                SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.dateFormatYMDHMS);
                Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                if ("".equals(date)) {
                    date = formatter.format(curDate);
                }
                time = DateUtil.formatDateStr2Desc(date, DateUtil.dateFormatYMDHMS);
                setRefreshTime(time);
                int count = getAdapter().getCount();
                if (totalDataCount == 0) {
                    mFooterView.setState(XListViewFooter.STATE_NODATA);
                } else if (count < totalDataCount) {
                    mFooterView.setState(XListViewFooter.STATE_NORMAL);
                } else {
                    mFooterView.setState(XListViewFooter.STATE_LOADALL);
                }
                date = formatter.format(curDate);
            }
        });

    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        mHeaderTimeView.setText(time);
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta
                + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                mFooterView.setState(XListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(XListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);

//		setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(XListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    public void startPullRefresh() {
        if (!mPullRefreshing) {
            mHeaderView.setVisiableHeight(300);
            mHeaderView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
            this.setSelection(0);
            updateHeaderHeight(300 / OFFSET_RADIO);
            invokeOnScrolling();
            mPullRefreshing = true;
            mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
            removeFooterView(mFooterView);
            if (mListViewListener != null) {
                mListViewListener.onRefresh();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                }
                if (getLastVisiblePosition() == mTotalItemCount - 1
                        && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (!mPullRefreshing && mEnablePullRefresh
                            && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {

                        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                        mHeaderView.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {

                            }
                        });
                        mFooterView.setState(XListViewFooter.STATE_NORMAL);
                        removeFooterView(mFooterView);

                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                        mPullRefreshing = true;
                    }
                    resetHeaderHeight();
                }
                if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad
                            && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface IXListViewListener {
        public void onRefresh();

        public void onLoadMore();
    }

    public int getTotalDataCount() {
        return totalDataCount;
    }

    public void setTotalDataCount(int totalDataCount) {
        this.totalDataCount = totalDataCount;
    }


    public static class DateUtil {

        /**
         * 时间日期格式化到年月日时分秒.
         */
        public static String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";

        /**
         * 时间日期格式化到年月日.
         */
        public static String dateFormatYMD = "yyyy-MM-dd";

        /**
         * 时间日期格式化到年月.
         */
        public static String dateFormatYM = "yyyy-MM";

        /**
         * 时间日期格式化到年月日时分.
         */
        public static String dateFormatYMDHM = "yyyy-MM-dd HH:mm";

        /**
         * 时间日期格式化到月日.
         */
        public static String dateFormatMD = "MM/dd";

        /**
         * 时分秒.
         */
        public static String dateFormatHMS = "HH:mm:ss";

        /**
         * 时分.
         */
        public static String dateFormatHM = "HH:mm";

        /**
         * 描述：String类型的日期时间转化为Date类型.
         *
         * @param strDate String形式的日期时间
         * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
         * @return Date Date类型日期时间
         */
        public static Date getDateByFormat(String strDate, String format) {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            Date date = null;
            try {
                date = mSimpleDateFormat.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }

        /**
         * 描述：获取偏移之后的Date.
         *
         * @param date          日期时间
         * @param calendarField Calendar属性，对应offset的值，
         *                      如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
         * @param offset        偏移(值大于0,表示+,值小于0,表示－)
         * @return Date 偏移之后的日期时间
         */
        public Date getDateByOffset(Date date, int calendarField, int offset) {
            Calendar c = new GregorianCalendar();
            try {
                c.setTime(date);
                c.add(calendarField, offset);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c.getTime();
        }

        /**
         * 描述：获取指定日期时间的字符串(可偏移).
         *
         * @param strDate       String形式的日期时间
         * @param format        格式化字符串，如："yyyy-MM-dd HH:mm:ss"
         * @param calendarField Calendar属性，对应offset的值，
         *                      如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
         * @param offset        偏移(值大于0,表示+,值小于0,表示－)
         * @return String String类型的日期时间
         */
        public static String getStringByOffset(String strDate, String format,
                                               int calendarField, int offset) {
            String mDateTime = null;
            try {
                Calendar c = new GregorianCalendar();
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
                c.setTime(mSimpleDateFormat.parse(strDate));
                c.add(calendarField, offset);
                mDateTime = mSimpleDateFormat.format(c.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return mDateTime;
        }

        /**
         * 描述：Date类型转化为String类型(可偏移).
         *
         * @param date          the date
         * @param format        the format
         * @param calendarField the calendar field
         * @param offset        the offset
         * @return String String类型日期时间
         */
        public static String getStringByOffset(Date date, String format,
                                               int calendarField, int offset) {
            String strDate = null;
            try {
                Calendar c = new GregorianCalendar();
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
                c.setTime(date);
                c.add(calendarField, offset);
                strDate = mSimpleDateFormat.format(c.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strDate;
        }

        /**
         * 描述：Date类型转化为String类型.
         *
         * @param date   the date
         * @param format the format
         * @return String String类型日期时间
         */
        public static String getStringByFormat(Date date, String format) {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            String strDate = null;
            try {
                strDate = mSimpleDateFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strDate;
        }

        /**
         * 描述：获取指定日期时间的字符串,用于导出想要的格式.
         *
         * @param strDate String形式的日期时间，必须为yyyy-MM-dd HH:mm:ss格式
         * @param format  输出格式化字符串，如："yyyy-MM-dd HH:mm:ss"
         * @return String 转换后的String类型的日期时间
         */
        public static String getStringByFormat(String strDate, String format) {
            String mDateTime = null;
            try {
                Calendar c = new GregorianCalendar();
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
                        dateFormatYMDHMS);
                c.setTime(mSimpleDateFormat.parse(strDate));
                SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(format);
                mDateTime = mSimpleDateFormat2.format(c.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mDateTime;
        }

        public static String getStringByFormat(String strDate, String format1,
                                               String format2) {
            String mDateTime = null;
            try {
                Calendar c = new GregorianCalendar();
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format1);
                c.setTime(mSimpleDateFormat.parse(strDate));
                SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(format2);
                mDateTime = mSimpleDateFormat2.format(c.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mDateTime;
        }

        /**
         * 描述：获取milliseconds表示的日期时间的字符串.
         *
         * @param milliseconds the milliseconds
         * @param format       格式化字符串，如："yyyy-MM-dd HH:mm:ss"
         * @return String 日期时间字符串
         */
        public static String getStringByFormat(long milliseconds, String format) {
            String thisDateTime = null;
            try {
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
                thisDateTime = mSimpleDateFormat.format(milliseconds);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return thisDateTime;
        }

        /**
         * 描述：获取表示当前日期时间的字符串.
         *
         * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
         * @return String String类型的当前日期时间
         */
        public static String getCurrentDate(String format) {
            String curDateTime = null;
            try {
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
                Calendar c = new GregorianCalendar();
                curDateTime = mSimpleDateFormat.format(c.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return curDateTime;

        }

        /**
         * 描述：获取表示当前日期时间的字符串(可偏移).
         *
         * @param format        格式化字符串，如："yyyy-MM-dd HH:mm:ss"
         * @param calendarField Calendar属性，对应offset的值，
         *                      如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
         * @param offset        偏移(值大于0,表示+,值小于0,表示－)
         * @return String String类型的日期时间
         */
        public static String getCurrentDateByOffset(String format,
                                                    int calendarField, int offset) {
            String mDateTime = null;
            try {
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
                Calendar c = new GregorianCalendar();
                c.add(calendarField, offset);
                mDateTime = mSimpleDateFormat.format(c.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mDateTime;

        }

        /**
         * 描述：计算两个日期所差的天数.
         *
         * @param date1 第一个时间的毫秒表示
         * @param date2 第二个时间的毫秒表示
         * @return int 所差的天数
         */
        public static int getOffectDay(long date1, long date2) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(date1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(date2);
            // 先判断是否同年
            int y1 = calendar1.get(Calendar.YEAR);
            int y2 = calendar2.get(Calendar.YEAR);
            int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
            int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
            int maxDays = 0;
            int day = 0;
            if (y1 - y2 > 0) {
                maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
                day = d1 - d2 + maxDays;
            } else if (y1 - y2 < 0) {
                maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
                day = d1 - d2 - maxDays;
            } else {
                day = d1 - d2;
            }
            return day;
        }

        /**
         * 描述：计算两个日期所差的小时数.
         *
         * @param date1 第一个时间的毫秒表示
         * @param date2 第二个时间的毫秒表示
         * @return int 所差的小时数
         */
        public static int getOffectHour(long date1, long date2) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(date1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(date2);
            int h1 = calendar1.get(Calendar.HOUR_OF_DAY);
            int h2 = calendar2.get(Calendar.HOUR_OF_DAY);
            int h = 0;
            int day = getOffectDay(date1, date2);
            h = h1 - h2 + day * 24;
            return h;
        }

        /**
         * 描述：计算两个日期所差的分钟数.
         *
         * @param date1 第一个时间的毫秒表示
         * @param date2 第二个时间的毫秒表示
         * @return int 所差的分钟数
         */
        public static int getOffectMinutes(long date1, long date2) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(date1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(date2);
            int m1 = calendar1.get(Calendar.MINUTE);
            int m2 = calendar2.get(Calendar.MINUTE);
            int h = getOffectHour(date1, date2);
            int m = 0;
            m = m1 - m2 + h * 60;
            return m;
        }

        /**
         * 描述：获取本周一.
         *
         * @param format the format
         * @return String String类型日期时间
         */
        public static String getFirstDayOfWeek(String format) {
            return getDayOfWeek(format, Calendar.MONDAY);
        }

        /**
         * 描述：获取本周日.
         *
         * @param format the format
         * @return String String类型日期时间
         */
        public static String getLastDayOfWeek(String format) {
            return getDayOfWeek(format, Calendar.SUNDAY);
        }

        /**
         * 描述：获取本周的某一天.
         *
         * @param format        the format
         * @param calendarField the calendar field
         * @return String String类型日期时间
         */
        private static String getDayOfWeek(String format, int calendarField) {
            String strDate = null;
            try {
                Calendar c = new GregorianCalendar();
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
                int week = c.get(Calendar.DAY_OF_WEEK);
                if (week == calendarField) {
                    strDate = mSimpleDateFormat.format(c.getTime());
                } else {
                    int offectDay = calendarField - week;
                    if (calendarField == Calendar.SUNDAY) {
                        offectDay = 7 - Math.abs(offectDay);
                    }
                    c.add(Calendar.DATE, offectDay);
                    strDate = mSimpleDateFormat.format(c.getTime());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strDate;
        }

        /**
         * 描述：获取本月第一天.
         *
         * @param format the format
         * @return String String类型日期时间
         */
        public static String getFirstDayOfMonth(String format) {
            String strDate = null;
            try {
                Calendar c = new GregorianCalendar();
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
                // 当前月的第一天
                c.set(Calendar.DAY_OF_MONTH, 1);
                strDate = mSimpleDateFormat.format(c.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strDate;

        }

        /**
         * 描述：获取本月最后一天.
         *
         * @param format the format
         * @return String String类型日期时间
         */
        public static String getLastDayOfMonth(String format) {
            String strDate = null;
            try {
                Calendar c = new GregorianCalendar();
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
                // 当前月的最后一天
                c.set(Calendar.DATE, 1);
                c.roll(Calendar.DATE, -1);
                strDate = mSimpleDateFormat.format(c.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strDate;
        }

        /**
         * 描述：获取表示当前日期的0点时间毫秒数.
         *
         * @return the first time of day
         */
        public static long getFirstTimeOfDay() {
            Date date = null;
            try {
                String currentDate = getCurrentDate(dateFormatYMD);
                date = getDateByFormat(currentDate + " 00:00:00", dateFormatYMDHMS);
                return date.getTime();
            } catch (Exception e) {
            }
            return -1;
        }

        /**
         * 描述：获取表示当前日期24点时间毫秒数.
         *
         * @return the last time of day
         */
        public static long getLastTimeOfDay() {
            Date date = null;
            try {
                String currentDate = getCurrentDate(dateFormatYMD);
                date = getDateByFormat(currentDate + " 24:00:00", dateFormatYMDHMS);
                return date.getTime();
            } catch (Exception e) {
            }
            return -1;
        }

        /**
         * 描述：判断是否是闰年()
         * <p/>
         * (year能被4整除 并且 不能被100整除) 或者 year能被400整除,则该年为闰年.
         *
         * @param year 年代（如2012）
         * @return boolean 是否为闰年
         */
        public static boolean isLeapYear(int year) {
            if ((year % 4 == 0 && year % 400 != 0) || year % 400 == 0) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * 描述：根据时间返回格式化后的时间的描述. 小于1小时显示多少分钟前 大于1小时显示今天＋实际日期，大于今天全部显示实际时间
         *
         * @param strDate   the str date
         * @param outFormat the out format
         * @return the string
         */
        public static String formatDateStr2Desc(String strDate, String outFormat) {

            DateFormat df = new SimpleDateFormat(dateFormatYMDHMS);
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            try {
                c2.setTime(df.parse(strDate));
                c1.setTime(new Date());
                int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
                if (d == 0) {
                    int h = getOffectHour(c1.getTimeInMillis(),
                            c2.getTimeInMillis());
                    if (h > 0) {
                        return "今天" + getStringByFormat(strDate, dateFormatHM);
                        // return h + "小时前";
                    } else if (h < 0) {
                        // return Math.abs(h) + "小时后";
                    } else if (h == 0) {
                        int m = getOffectMinutes(c1.getTimeInMillis(),
                                c2.getTimeInMillis());
                        if (m > 0) {
                            return m + "分钟前";
                        } else if (m < 0) {
                            // return Math.abs(m) + "分钟后";
                        } else {
                            return "刚刚";
                        }
                    }

                } else if (d > 0) {
                    if (d == 1) {
                        // return "昨天"+getStringByFormat(strDate,outFormat);
                    } else if (d == 2) {
                        // return "前天"+getStringByFormat(strDate,outFormat);
                    }
                } else if (d < 0) {
                    if (d == -1) {
                        // return "明天"+getStringByFormat(strDate,outFormat);
                    } else if (d == -2) {
                        // return "后天"+getStringByFormat(strDate,outFormat);
                    } else {
                        // return Math.abs(d) +
                        // "天后"+getStringByFormat(strDate,outFormat);
                    }
                }

                String out = getStringByFormat(strDate, outFormat);
                if (out != null && !"".equals(out)) {
                    return out;
                }
            } catch (Exception e) {
            }

            return strDate;
        }

        public static String formatDateStr3Desc(String strDate, String outFormat) {

            try {
                DateFormat df = new SimpleDateFormat(outFormat);
                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                Date date = df.parse(strDate);
                c2.setTime(date);
                c1.setTime(new Date());
                int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
                int y2 = c2.get(Calendar.YEAR);
                int y1 = c1.get(Calendar.YEAR);
                if (d == 0) {
                    return getStringByFormat(date, dateFormatHM);
                } else if (y1 == y2) {
                    return getStringByFormat(date, "MM-dd");
                } else {
                    return getStringByFormat(date, dateFormatYMD);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }


        /**
         * 取指定日期为星期几.
         *
         * @param strDate  指定日期
         * @param inFormat 指定日期格式
         * @return String 星期几
         */
        public static String getWeekNumber(String strDate, String inFormat) {
            String week = "星期日";
            Calendar calendar = new GregorianCalendar();
            DateFormat df = new SimpleDateFormat(inFormat);
            try {
                calendar.setTime(df.parse(strDate));
            } catch (Exception e) {
                return "错误";
            }
            int intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            switch (intTemp) {
                case 0:
                    week = "星期日";
                    break;
                case 1:
                    week = "星期一";
                    break;
                case 2:
                    week = "星期二";
                    break;
                case 3:
                    week = "星期三";
                    break;
                case 4:
                    week = "星期四";
                    break;
                case 5:
                    week = "星期五";
                    break;
                case 6:
                    week = "星期六";
                    break;
            }
            return week;
        }

        /**
         * The main method.
         *
         * @param args the arguments
         */
        public static void main(String[] args) {
            System.out.println(formatDateStr2Desc("2012-3-2 12:2:20",
                    "MM月dd日  HH:mm"));
        }

        /**
         * @param date 、可自行设置“2013-06-03”格式的日期
         * @return 返回1是星期日、2是星期一、3是星期二、4是星期三、5是星期四、6是星期五、7是星期六
         */
        public static int getDayofweek(String date) {
            Calendar cal = Calendar.getInstance();
            if (date.equals("")) {
                cal.setTime(new Date(System.currentTimeMillis()));
            } else {
                cal.setTime(new Date(getDateByStr2(date).getTime()));
            }
            return cal.get(Calendar.DAY_OF_WEEK);
        }

        public static Date getDateByStr2(String dd) {

            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = sd.parse(dd);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
            return date;
        }
    }
}
