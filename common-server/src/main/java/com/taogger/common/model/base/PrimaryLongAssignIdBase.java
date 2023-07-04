package com.taogger.common.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 主键雪花
 * @author taogger
 * @date 2022/7/7 17:09
 */
@Data
public class PrimaryLongAssignIdBase implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
}
