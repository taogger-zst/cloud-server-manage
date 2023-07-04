package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 主键-integer
 * @author taogger
 * @date 2022/7/7 17:06
 */
@Data
public class PrimaryIntegerAutoBase implements Serializable {

    /**
     * 自动递增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
}
