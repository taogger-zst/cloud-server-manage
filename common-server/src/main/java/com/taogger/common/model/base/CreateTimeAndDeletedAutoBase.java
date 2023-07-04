package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建时间并且删除-自动递增
 * @author taogger
 * @date 2022/7/7 17:27
 */
@Data
public class CreateTimeAndDeletedAutoBase extends DeletedAutoBase implements Serializable {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
