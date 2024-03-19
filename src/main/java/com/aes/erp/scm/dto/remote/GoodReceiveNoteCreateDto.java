package com.aes.erp.scm.dto.remote;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoodReceiveNoteCreateDto {
    Long poId;
    Long remotePoId;
    Long warehouseId;
    private List<GoodReceiveItemDetailDto> details;
}
