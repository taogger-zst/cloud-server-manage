package com.taogger.common.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author taogger
 * @description baseQuery
 * @date 2023-05-24 14:30
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseQuery implements Serializable {

    @Min(value = 1, message = "页码错误")
    @NotNull(message = "页码不能为空")
    private Integer page = 1;
    @Max(value = 1000, message = "条数过大")
    @Min(value = 1, message = "条数有误")
    @NotNull(message = "条数不能为空")
    private Integer size = 10;

}
