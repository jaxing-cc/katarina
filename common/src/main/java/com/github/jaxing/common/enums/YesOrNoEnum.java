package com.github.jaxing.common.enums;

/**
 * @author cjx
 */
public enum YesOrNoEnum {
    YES(1, "是"),
    NO(0, "否");

    private final Integer code;
    private final String info;

    YesOrNoEnum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public String getStringCode() {
        return code.toString();
    }

    public String getInfo() {
        return info;
    }

    public static Integer getByBoolean(boolean yesOrNo){
        return yesOrNo ? YES.getCode() : NO.getCode();
    }

    public static boolean getByCode(Integer code){
        return YES.code.equals(code);
    }
}
