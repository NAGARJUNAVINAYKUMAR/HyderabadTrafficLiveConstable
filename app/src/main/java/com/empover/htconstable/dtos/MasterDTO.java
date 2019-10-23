package com.empover.htconstable.dtos;

import java.util.List;

public class MasterDTO extends DTO {

	private List<ComplaintsDTO> complaintsDTO;

	public List<ComplaintsDTO> getComplaintsDTO() {
		return complaintsDTO;
	}

	public void setComplaintsDTO(List<ComplaintsDTO> complaintsDTO) {
		this.complaintsDTO = complaintsDTO;
	}

}
