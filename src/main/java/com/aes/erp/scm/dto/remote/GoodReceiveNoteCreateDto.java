package com.aes.erp.scm.dto.remote;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class GoodReceiveNoteCreateDto extends GoodReceivedManualRequestDto{
    Long poId;
    Long remotePoId;
    Long warehouseId;
    private List<GoodReceiveItemDetailDto> details;
}
