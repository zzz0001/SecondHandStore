package com.zzz.Util;

import cn.hutool.json.JSONUtil;
import com.zzz.pojo.entity.ResultMessage;

/**
 * @author zzz
 * @date 2022/3/23 19:13
 */
public class MessageUtils {

    /**
     * 封装响应的消息，注意:<b>系统消息的发送fromName为null</b>;封装好的响应如下，例如
     * 1.系统消息： {“systemMsgFlag”: true, "fromName": null, "message": ["Name1", "Name2"]}.
     * 2.非系统消息 {“systemMsgFlag”: false, "fromName": "YYJ", "message": “你在哪里呀？”}.
     *
     * @param systemMsgFlag 是否是系统消息
     * @param fromName      发送方名称
     * @param message       发送的消息内容
     * @return java.lang.String
     */
    public static String getMessage(boolean systemMsgFlag, String fromName, Object message) {
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setSystemMsgFlag(systemMsgFlag);
        resultMessage.setMessage(message);
        //如果不是系统消息，就传入fromName
        if (!systemMsgFlag) {
            resultMessage.setFromName(fromName);
        }
        String result = JSONUtil.toJsonStr(resultMessage);
        return result;
    }
}

