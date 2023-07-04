package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 更新时间-自动递增
 * @author taogger
 * @date 2022/7/7 17:33
 */
@Data
public class UpdateTimeAutoBase extends CreateTimeAutoBase implements Serializable {

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
