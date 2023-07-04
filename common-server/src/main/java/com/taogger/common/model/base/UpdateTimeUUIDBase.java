package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 更新时间-uuid
 * @author taogger
 * @date 2022/7/7 17:37
 */
@Data
public class UpdateTimeUUIDBase extends CreateTimeUUIDBase implements Serializable {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
