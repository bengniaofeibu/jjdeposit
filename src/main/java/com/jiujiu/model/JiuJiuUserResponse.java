package com.jiujiu.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by t_hz on 2019/5/8.
 */
@Setter
@Getter
public class JiuJiuUserResponse {


    private List<JiuJiuUserResVO> jiuJiuUserResVOList;

    private Long totalNum;

    public JiuJiuUserResponse(List<JiuJiuUserResVO> jiuJiuUserResVOList,Long totalNum){
        this.jiuJiuUserResVOList = jiuJiuUserResVOList;
        this.totalNum = totalNum;

    }

}
