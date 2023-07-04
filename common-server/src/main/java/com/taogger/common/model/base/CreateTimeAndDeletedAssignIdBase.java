package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建时间并且删除-雪花算法
 * @author taogger
 * @date 2022/7/7 17:31
 */
@Data
public class CreateTimeAndDeletedAssignIdBase extends DeletedAssignIdBase implements Serializable {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
