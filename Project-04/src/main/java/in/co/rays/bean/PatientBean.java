package in.co.rays.bean;

import java.util.Date;

public class PatientBean extends BaseBean {
	
	private String name ;
	private String desease ;
	private String mobileNo ;
	private Date dateOfVisit ;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesease() {
		return desease;
	}

	public void setDesease(String desease) {
		this.desease = desease;
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
		return desease;
	}

	@Override
	public String getValue() {
		return desease;
	}

}
