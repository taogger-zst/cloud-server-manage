package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 更新时间-雪花算法
 * @author taogger
 * @date 2022/7/7 17:39
 */
@Data
public class UpdateTimeAssignIdAndDeletedBase extends CreateTimeAndDeletedAssignIdBase implements Serializable {

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
