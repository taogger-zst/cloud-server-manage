package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;

/**
 * 逻辑删除-自动生成id-integer
 * @author taogger
 * @date 2022/7/7 17:12
 */
@Data
public class DeletedAutoBase extends PrimaryIntegerAutoBase implements Serializable {

    /**
     * 逻辑删除字段 0.不删除 ,1.删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;
}
