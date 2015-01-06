package adventure.entity;

import static javax.persistence.EnumType.STRING;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Athlete implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@OneToOne
	private Account user;

	@Enumerated(STRING)
	private BloodType bloodType;

	@Embedded
	private Address address;

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public BloodType getBloodType() {
		return bloodType;
	}

	public void setBloodType(BloodType bloodType) {
		this.bloodType = bloodType;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
