package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;

/**
 * 逻辑删除-自动生成雪花算法
 * @author taogger
 * @date 2022/7/7 17:15
 */
@Data
public class DeletedAssignIdBase extends PrimaryLongAssignIdBase implements Serializable {

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;
}
