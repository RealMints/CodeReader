package com.morganabard.mintstv.codereader;

public class savedQRCodes {

    //Now to setup every type of email
    public String codeType;

    public String codeData;

    public savedQRCodes(){ }

    public savedQRCodes(String codeType, String codeData)
    {
        this.codeData = codeData;
        this.codeType = codeType;
    }

}
