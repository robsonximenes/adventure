package adventure.entity;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.AUTO;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
public class Atleta {

	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;

	@NotBlank
	private String email;

	@NotBlank
	private String nome;

	// @NotBlank
	private Date nascimento;

	// @NotBlank
	private String rg;

	// @NotBlank
	private String cpf;

	@NotNull
	@Enumerated(STRING)
	private Sexo sexo;

	@Valid
	@NotNull
	@AttributeOverrides({ @AttributeOverride(name = "area", column = @Column(name = "telefoneCelularArea")),
			@AttributeOverride(name = "numero", column = @Column(name = "telefoneCelularNumero")) })
	@Embedded
	private Telefone telefoneCelular;

	@Valid
	@AttributeOverrides({ @AttributeOverride(name = "area", column = @Column(name = "telefoneResidencialArea")),
			@AttributeOverride(name = "numero", column = @Column(name = "telefoneResidencialNumero")) })
	@Embedded
	private Telefone telefoneResidencial;

	@Valid
	@AttributeOverrides({ @AttributeOverride(name = "area", column = @Column(name = "telefoneComercialArea")),
			@AttributeOverride(name = "numero", column = @Column(name = "telefoneComercialNumero")) })
	@Embedded
	private Telefone telefoneComercial;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getNascimento() {
		return nascimento;
	}

	public void setNascimento(Date nascimento) {
		this.nascimento = nascimento;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Sexo getSexo() {
		return sexo;
	}

	public void setSexo(Sexo sexo) {
		this.sexo = sexo;
	}

	public Telefone getTelefoneCelular() {
		return telefoneCelular;
	}

	public void setTelefoneCelular(Telefone telefoneCelular) {
		this.telefoneCelular = telefoneCelular;
	}

	public Telefone getTelefoneResidencial() {
		return telefoneResidencial;
	}

	public void setTelefoneResidencial(Telefone telefoneResidencial) {
		this.telefoneResidencial = telefoneResidencial;
	}

	public Telefone getTelefoneComercial() {
		return telefoneComercial;
	}

	public void setTelefoneComercial(Telefone telefoneComercial) {
		this.telefoneComercial = telefoneComercial;
	}
}
