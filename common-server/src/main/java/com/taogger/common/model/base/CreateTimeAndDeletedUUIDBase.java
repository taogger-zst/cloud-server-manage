package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建时间并且删除-uuid
 * @author taogger
 * @date 2022/7/7 17:29
 */
@Data
public class CreateTimeAndDeletedUUIDBase extends DeletedUUIDBase implements Serializable {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
