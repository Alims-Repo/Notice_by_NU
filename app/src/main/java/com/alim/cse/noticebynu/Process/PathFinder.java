package com.alim.cse.noticebynu.Process;

public class PathFinder {

    public String getPath(String PATH) {
        PATH = PATH.replace("%2F","/");
        PATH = PATH.replace("%2520"," ");
        return "/sdcard/"+PATH;
    }
}
