package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 主键UUid
 * @author taogger
 * @date 2022/7/7 17:11
 */
@Data
public class PrimaryStringUUIDBase implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
}
