package com.ktsedu.code.base;

import com.google.gson.Gson;
import com.ktsedu.code.util.ModelParser;

import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;
import java.net.URLDecoder;

/**
 * Created by aohas on 2015/11/4.
 * 进行序列 化或窗体间参数传递使用该项为基类
 */
public abstract class BaseModel implements Serializable {
    public int code = -1;
    public String msg = "";

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    //    public Selector getSelectBook(){//获取选 数据表操作的
//        Selector selector =Selector.from(getClass());
//
//
//        return selector;
//    }
//    Code: 	0	/1000	/1001	 /1002	/1003		/1004       /1009
//    Msg:	成功/系统错误/输入错误/未找到/验证码错误！/帐号重复 /已绑定其他账户
    public boolean CheckCodeMsg() {
//        if(!CheckUtil.isEmpty(msg))
//            ToastUtil.toast(msg);
        switch (code) {
            case 0:
                return true;
//                break;
            case 1000:
                break;
            case 1001:
                break;
            case 1002:
                break;
            case 1003:
                break;
            case 1004:
                break;
            case 1009:
                break;
            case 1013:
                break;
            case 2003:// 重新登录  2002
                break;
        }
        return false;
    }

    public boolean CheckCode() {
        switch (code) {
            case 0:
                return true;
//                break;
            case 1000:
                break;
            case 1001:
                break;
            case 1002:
                break;
            case 1003:
                break;
            case 1004:
                break;
            case 1009:
                break;
            case 1019:
                break;
            case 1013:
                break;
            case 2003:// 重新登录
                break;
        }
        return false;
    }

    public int getCode() {
        return code;
    }

    //    public static SentenceScoreXML getXMLData1(String strXML) {
//        SentenceScoreXML sentenceXMLs = null;
////        SentenceXML sentenceXML = null;//
//        try {
//            InputStream in_withcode = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
//            XmlPullParser parser = Xml.newPullParser();//得到Pull解析器
//            parser.setInput(in_withcode,"UTF-8");//
//
//            int eventType = parser.getEventType();//得到第一个事件类型
//            while (eventType!= XmlPullParser.END_DOCUMENT){
//                switch (eventType) {
//                    case (XmlPullParser.START_DOCUMENT)://如果是文档开始事件
//                        sentenceXMLs = new SentenceScoreXML();//创建一个person集合
//                        break;
//                    case (XmlPullParser.START_TAG)://如果遇到标签开始
//                        String tagName = parser.getName();// 获得解析器当前元素的名称
//                        switch (tagName){
//                            case "data":
////                            tempUnit = new UnitXML();//创建一个person
//                                break;
//                            case "number":
//                                sentenceXMLs.number =new List<Integer>();
//                                break;
//                        }
//                        break;
//                    case (XmlPullParser.END_TAG)://如果遇到标签结束
//                        if("item".equals(parser.getName())){
//
//                        } else  if ("data".equals(parser.getName())) {//如果是person标签结束
//
//                        }
//                        break;
//                }
//                eventType=parser.next();//进入下一个事件处理
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return sentenceXMLs;
//    }

    protected static String getAttributeValue(XmlPullParser parser, String id) {
        String temp = "";
        try {
            temp = parser.getAttributeValue(null, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    protected static String strDecode(String str){// 对编码进行反编，URLDecoderdecode
        try {
//           return URLEncoder.encode(str,   "utf-8");
            return URLDecoder.decode(str,   "utf-8");
        }catch (Exception e){
            return str;
        }
    }

    //返回当前对象的json
    public String getJson(){
        return ModelParser.toModeJson(this);
    }
}
