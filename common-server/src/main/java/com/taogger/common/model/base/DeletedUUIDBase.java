package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;

/**
 * 逻辑删除-雪花算法uuid版
 * @author taogger
 * @date 2022/7/7 17:17
 */
@Data
public class DeletedUUIDBase extends PrimaryStringUUIDBase implements Serializable {

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;
}
