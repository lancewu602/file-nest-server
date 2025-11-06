package com.example.bean.response;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/10/22 14:37
 */
@Data
public class MergeResultResp {

    private String status;
    private double progress;
    private String error;

    public MergeResultResp() {
    }

    public static MergeResultResp merging(double progress) {
        MergeResultResp resp = new MergeResultResp();
        resp.setStatus("MERGING");
        resp.setProgress(progress);
        return resp;
    }

    public static MergeResultResp success() {
        MergeResultResp resp = new MergeResultResp();
        resp.setStatus("SUCCESS");
        resp.setProgress(1);
        return resp;
    }

    public static MergeResultResp failed(String error) {
        MergeResultResp resp = new MergeResultResp();
        resp.setStatus("FAILED");
        resp.setError(error);
        return resp;
    }

}
