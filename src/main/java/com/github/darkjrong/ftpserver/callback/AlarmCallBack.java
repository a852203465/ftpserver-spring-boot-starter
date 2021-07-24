package com.github.darkjrong.ftpserver.callback;

import java.io.File;

/**
 *  报警回调函数
 * @author  Rong.Jia
 * @date 2019/10/16 23:58
 */
public interface AlarmCallBack {

    /**
     * 回调函数
     * @param file 文件
     * @param hostAddress 地址
     * @date 2019/10/16 23:58
     */
    void invoke(File file, String hostAddress);






}
