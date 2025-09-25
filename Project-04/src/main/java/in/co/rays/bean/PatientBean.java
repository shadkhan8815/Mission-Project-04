package in.co.rays.bean;

import java.util.Date;

public class PatientBean extends BaseBean {
	
	private String name ;
	private String disease ;
	private String mobileNo ;
	private Date dateOfVisit ;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Date getDateOfVisit() {
		return dateOfVisit;
	}

	public void setDateOfVisit(Date dateOfVisit) {
		this.dateOfVisit = dateOfVisit;
	}

	@Override
	public String getKey() {
		return disease;
	}

	@Override
	public String getValue() {
		return disease;
	}

}
