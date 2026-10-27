package ucb.piscidata.farm.data.mapper

import ucb.piscidata.farm.data.dto.FarmDto
import ucb.piscidata.farm.domain.model.Farm

fun FarmDto.toModel(): Farm {
    return Farm(
        id = id,
        name = name
    )
}
